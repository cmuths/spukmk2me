package com.spukmk2me.animation;

import java.io.IOException;
import java.io.InputStream;

import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.NamedList;
import com.spukmk2me.io.IFileSystem;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.sound.ISoundMonitor;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.SceneTreeLoader;
import com.spukmk2me.resource.IResourceProducer;
import com.spukmk2me.resource.DefaultResourceProducer;

public final class Animation
{
    public Animation( IVideoDriver vdriver, ISoundMonitor smonitor,
        IFileSystem fsystem, boolean reversed )
    {
        m_vdriver   = vdriver;
        m_smonitor  = smonitor;
        m_fsystem   = fsystem;
        m_commands  = new DoublyLinkedList();
        m_nodes     = new NamedList();
        m_objects   = new NamedList();
        m_revertedNodeOriginalInfos = new NamedList();
    }
    
    /**
     *  Load a material files.
     *  \details Material files won't be added if there's name collision.
     * Name collision occurs if there's at least 2 nodes in all material files
     * that have the same name (except "root").
     *  @param materialFile Material filename (absolute path).
     *  @return True if there's no name collision.
     *  @throws IOException If IO error occurs.
     */
    public boolean AddMaterialFile( String materialFile, boolean revert )
        throws IOException
    {
        SceneTreeLoader     loader = new SceneTreeLoader();
        IResourceProducer   producer;
        String              path = materialFile.substring(
            0, materialFile.lastIndexOf( m_fsystem.GetPathSeparator() ) );
        
        if ( revert )
            producer = new ReversedResourceProducer( m_vdriver, m_smonitor, m_fsystem, path );
        else
            producer = new DefaultResourceProducer( m_vdriver, m_smonitor, m_fsystem, path );
        
        InputStream is = m_fsystem.OpenFile(
            materialFile, IFileSystem.LOCATION_AUTODETECT );
        
        loader.Load( is, producer );
        
        if ( revert )
            PrepareReversedTree( loader );

        is.close();
        
        String[] nameList = loader.GetExportedNames();
        
        for ( int i = 0; i != nameList.length; ++i )
        {
            if ( m_nodes.exist( nameList[ i ] ) != -1 )
                return false;
        }
        
        for ( int i = 0; i != nameList.length; ++i )
            m_nodes.add( loader.Get( nameList[ i ] ), nameList[ i ] );

        return true;
    }
    
    /**
     *  Add a new object to animation.
     *  \details If there's name collision, object won't be added.
     *  @param name Object name.
     *  @return True if and only if the name collision doesn't occur.
     */
    public boolean AddAnimationObject( String name )
    {
        return m_objects.add( new AnimationObject(), name );
    }
    
    public AnimationObject GetAnimationObject( String name )
    {
        return (AnimationObject)m_objects.get( name );
    }
    
    public void SetCommands( DoublyLinkedList commands )
    {
        m_commands = commands;
    }
    
    public DoublyLinkedList GetCommands()
    {
        return m_commands;
    }
    
    public void AddNode( ISceneNode node, String name )
    {
        m_nodes.add( node, name );
    }
    
    public String[] GetNodeNameList()
    {
        String[] list = new String[ m_nodes.length() ];
        DoublyLinkedList.Iterator itr = m_nodes.getNameIterator();
        int length = m_nodes.length();
        
        for ( int i = 0; i != length; ++i )
        {
            list[ i ] = (String)itr.data();
            itr.fwrd();
        }
        
        return list;
    }
    
    public ISceneNode GetNode( String name )
    {
        return (ISceneNode)m_nodes.get( name );
    }
    
    /**
     *  Clear every things.
     */
    public void Clear()
    {
        m_commands.clear();
        m_nodes.clear();
        m_objects.clear();
    }
    
    NamedList GetOriginalPositionList()
    {
        return m_revertedNodeOriginalInfos;
    }
    
    void RealignAnimationObjects()
    {
        DoublyLinkedList.Iterator itr = m_objects.getObjectIterator();
        
        for ( int i = m_objects.length(); i != 0; --i )
        {
            ((AnimationObject)itr.data()).RealignNodes();
            itr.fwrd();
        }
    }
    
    private void PrepareReversedTree( SceneTreeLoader loader )
    {
        {
            String[] nodeNameList   = loader.GetExportedNames();
            NodePositionInfo info;
            ISceneNode node;
            
            for ( int i = 0; i != nodeNameList.length; ++i )
            {
                info        = new NodePositionInfo();
                node        = loader.Get( nodeNameList[ i ] );
                info.c_x    = node.c_x;
                info.c_y    = node.c_y;
                m_revertedNodeOriginalInfos.add( info, nodeNameList[ i ] );
            }
        }
        
        DoublyLinkedList stack = new DoublyLinkedList();
        DoublyLinkedList finish = new DoublyLinkedList();
        ISceneNode childNode, finishNode;
        long hbb;
        int px = 0, pw = 0, nx, nw;
        ISceneNode root = loader.Get( "root" );;
        ISceneNode node;

        if ( root.c_children != null )
        {
            stack.push_back( root );
            finish.push_back( root.c_next );
        }   

        while ( stack.length() != 0 )
        {
            node = (ISceneNode)stack.pop_back();

            // Reached all the nodes
            if ( node == finish.peek_back() )
                finish.pop_back();
            else
            {
                stack.push_back( node.c_next );
                
                if ( node.c_children != null )
                {
                    finishNode = node.c_children;
                    childNode = finishNode.c_next;
                    stack.push_back( childNode );
                    finish.push_back( finishNode );
                    
                    if ( node != root )
                    {
                        hbb = node.GetHierarchicalBoundingRect();
                        px = (int)((hbb >> 48) & 0x0000FFFF);
                        pw = (int)((hbb >> 16) & 0x0000FFFF);
                    }
                    
                    while ( childNode != finishNode )
                    {
                        hbb = childNode.GetHierarchicalBoundingRect();
                        nx = (int)((hbb >> 48) & 0x0000FFFF);
                        nw = (int)((hbb >> 16) & 0x0000FFFF);
                        childNode.c_x = (short)(-childNode.c_x + pw - nw + ((px - nx) << 1));
                        childNode = childNode.c_next;
                    }
                }
            }
        }
    }
    
    private DoublyLinkedList    m_commands;
    private IVideoDriver        m_vdriver;
    private ISoundMonitor       m_smonitor;
    private IFileSystem         m_fsystem;
    private NamedList           m_nodes;
    private NamedList           m_objects;
    private NamedList           m_revertedNodeOriginalInfos;
}
