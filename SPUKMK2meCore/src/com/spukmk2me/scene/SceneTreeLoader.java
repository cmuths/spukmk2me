package com.spukmk2me.scene;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */
import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.NamedList;
import com.spukmk2me.resource.ResourceSet;
import com.spukmk2me.resource.IResourceProducer;
import com.spukmk2me.scene.complex.ComplexSceneNode;

public final class SceneTreeLoader
{
    public SceneTreeLoader()
    {
        m_exportedNodes = new NamedList();
        m_resourceSet   = new ResourceSet();
    }
    
    public void changeNodeNames( String[] originalNames, String[] newNames )
    {
        /* $if SPUKMK2ME_DEBUG$ */
        if ( (originalNames == null) || (newNames == null) )
        {
            Logger.Trace( "Null names were passed" );
            return;
        }
            
        if ( (originalNames.length != newNames.length) )
        {
            Logger.Trace( "Number of new names and original names are not equal." );
            return;
        }
        /* $endif$ */
        
        NamedList newNodeList = new NamedList();
        DoublyLinkedList.Iterator namei = m_exportedNodes.getNameIterator();
        DoublyLinkedList.Iterator nodei = m_exportedNodes.getObjectIterator();
        String name;
        
        for ( int i = m_exportedNodes.length(); i != 0; --i )
        {
            name = (String)namei.data();
            
            for ( int j = 0; j != originalNames.length; ++j )
            {
                if ( name.equals( originalNames[ j ]) )
                {
                    name = newNames[ j ];
                    break;
                }
            }
            
            if ( name != null )
                newNodeList.add( nodei.data(), name );
            
            namei.fwrd();
            nodei.fwrd();
        }
        
        m_exportedNodes = newNodeList;
    }

    /**
     *  Get the scene node associated with specified proxy name.
     *  @param name Proxy name
     *  @return The corresponding node, null if there's no such node.
     */
    public ISceneNode Get( String name )
    {
        if ( name.equals( ROOTNODE_NAME ) )
            return m_root;

        return (ISceneNode)m_exportedNodes.get( name );
    }
    
    /**
     *  Get all exported names, except "root".
     *  @return An array of exported names. Null if there's no name exported.
     */
    public String[] GetExportedNames()
    {
        int length = m_exportedNodes.length();
        
        if ( length == 0 )
            return null;
        
        String[] ret = new String[ length ];
        DoublyLinkedList.Iterator itr = m_exportedNodes.getNameIterator();
        
        for ( int i = 0; i != length; ++i )
        {
            ret[ i ] = (String)itr.data();
            itr.fwrd();
        }
        
        return ret;
    }

    public boolean Load( InputStream is, IResourceProducer producer,
        ISceneNodeProducer nodeProducer ) throws IOException
    {
        DataInputStream dis = new DataInputStream( is );

        if ( !CheckHeader( dis ) )
        {
            /* $if SPUKMK2ME_DEBUG$ */
            Logger.Trace( "Invalid file header" );
            /* $endif$ */
            return false;
        }
        
        m_resourceSet.Load( is, producer );
        nodeProducer.UpdateResourceSet( m_resourceSet );
        LoadNodeNamesMappingTable( dis );
        ConstructSceneTree( dis, nodeProducer );

        return true;
    }
    
    public ResourceSet GetResourceSet()
    {
        return m_resourceSet;
    }

    private boolean CheckHeader( DataInputStream dis ) throws IOException
    {
        char[] validationString = new char[ 24 ];
        
        for ( int i = 0; i != 24; ++i )
            validationString[ i ] = (char)dis.readByte();
        
        return new String( validationString ).equals( VALID_STRING );
    }

    private void LoadNodeNamesMappingTable( DataInputStream dis )
        throws IOException
    {
        int nExportedNodes = dis.readInt();
        
        /* $if SPUKMK2ME_DEBUG$ */
        Logger.Log( "Number of exported nodes: " + nExportedNodes + '\n' );
        /* $endif$ */

        // Load exported node indexes
        {
            m_exportedNodeIndexes = new int[ nExportedNodes ];
            
            /* $if SPUKMK2ME_DEBUG$ */
            Logger.Log( "Exported indexes: " );
            /* $endif$ */

            for ( int i = 0; i != nExportedNodes; ++i )
            {
                m_exportedNodeIndexes[ i ] = dis.readInt();
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Log( m_exportedNodeIndexes[ i ] + " " );
                /* $endif$ */
            }
        }

        // Load proxy names
        {
            m_exportedNodeNames = new String[ nExportedNodes ];
            
            /* $if SPUKMK2ME_DEBUG$ */
            Logger.Log( "Exported proxy names:\n" );
            /* $endif$ */
            
            for ( int i = 0; i != nExportedNodes; ++i )
            {
                m_exportedNodeNames[ i ] = dis.readUTF();
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Log( m_exportedNodeNames[ i ] + ' ' );
                /* $endif$ */
            }
            
            /* $if SPUKMK2ME_DEBUG$ */
            Logger.Log( "--------\n" );
            /* $endif$ */
        }
    }

    private void ConstructSceneTree( DataInputStream dis, ISceneNodeProducer producer )
        throws IOException
    {
        byte[]  traversalData;

        m_root = new NullSceneNode();
        
        /* $if SPUKMK2ME_SCENESAVER$ */
        m_root.c_proxyName = "root";
        m_root.c_exportFlag = true;
        /* $endif$ */
        m_exportedNodes.clear();

        // Load traversal data
        {
            int nNodes = dis.readInt();
            
            /* $if SPUKMK2ME_DEBUG$ */
            Logger.Log( "Number of nodes: " + nNodes + ". " );
            /* $endif$ */

            // Empty tree
            if ( nNodes == 1 )
                return;

            int nTraversalBits = (nNodes - 1 << 1) + 1;

            if ( (nTraversalBits & 0x00000007) == 0 )
                traversalData = new byte[ nTraversalBits >> 3 ];
            else
                traversalData = new byte[ (nTraversalBits >> 3) + 1 ];

            dis.read( traversalData );
            
            /* $if SPUKMK2ME_DEBUG$ */
            Logger.Log( "Traversal data: " );
            
            for ( int i = 0; i != traversalData.length; ++i )
            {
                Logger.Log(
                    Integer.toBinaryString( traversalData[ i ] | 0xFFFFFF00 ).
                    substring( 24 ) );
            }
            /* $endif$ */
        }

        // Tree construction sequence
        int     prefetchedIndex = 0, direction = 0;
        int     exportedIndex = 0, currentNodeIndex = 0;
        short   treeHeight = (short)dis.readUnsignedShort();
        byte    bitCounter = 0, nodeType;
        
        // Construct stack with the size equal to tree height.
        ISceneNode[]    stack = new ISceneNode[ treeHeight ];
        int             topStack = 0;

        stack[ 0 ] = m_root;

        while ( topStack != -1 )
        {
            if ( bitCounter == 0 )
            {
                bitCounter  = 8;
                direction   = traversalData[ prefetchedIndex++ ];
            }

            if ( (direction & 0x80) == 0 )
            {
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Log( "Goes up. " );
                /* $endif$ */
                stack[ topStack-- ] = null;
            }
            else
            {
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Log( "Goes down. " );
                /* $endif$ */
                
                nodeType = dis.readByte();
                stack[ ++topStack ] = producer.ConstructSceneNode( dis, nodeType );
                
                if ( stack[ topStack - 1 ] instanceof ComplexSceneNode )
                {
                    ((ComplexSceneNode)stack[ topStack - 1 ]).GetEntryNode().
                        AddChild( stack[ topStack ] );
                }
                else
                    stack[ topStack - 1 ].AddChild( stack[ topStack ] );

                if ( exportedIndex < m_exportedNodeIndexes.length )
                {
                    if ( currentNodeIndex ==
                        m_exportedNodeIndexes[ exportedIndex ] )
                    {
                        /* $if SPUKMK2ME_SCENESAVER$ */
                        stack[ topStack ].c_proxyName =
                            m_exportedNodeNames[ exportedIndex ];
                        stack[ topStack ].c_exportFlag = true;
                        /* $endif$ */
                        m_exportedNodes.add( stack[ topStack ],
                            m_exportedNodeNames[ exportedIndex ] );
                        ++exportedIndex;
                    }
                }

                ++currentNodeIndex;
            }

            direction <<= 1;
            --bitCounter;
        }
    }

    // Node types:
    // 0: Null
    // 1: Image
    // 2: Sprite
    // 3: String
    // 4: Tiled layer
    // 5: Clipping
    // 6: Viewport
    
    
    private static final String ROOTNODE_NAME   = "root";
    private static final String VALID_STRING    = "SPUKMK2me_SCENE-FILE_0.1";

    private int[]           m_exportedNodeIndexes;
    private String[]        m_exportedNodeNames;
    private NamedList       m_exportedNodes;
    private ResourceSet     m_resourceSet;
    private ISceneNode      m_root;
}
