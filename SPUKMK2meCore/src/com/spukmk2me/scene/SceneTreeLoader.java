package com.spukmk2me.scene;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */
import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.NamedList;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.ICFont;
import com.spukmk2me.resource.IResource;
import com.spukmk2me.resource.ResourceSet;
import com.spukmk2me.resource.IResourceProducer;
import com.spukmk2me.scene.complex.ComplexSceneNode;
import com.spukmk2me.scene.complex.ClippingSceneNode;
import com.spukmk2me.scene.complex.ViewportSceneNode;

public final class SceneTreeLoader
{
    public SceneTreeLoader()
    {
        m_exportedNodes = new NamedList();
        m_resourceSet   = new ResourceSet();
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

    public boolean Load( InputStream is, IResourceProducer producer ) throws IOException
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
        LoadNodeNamesMappingTable( dis );
        ConstructSceneTree( dis );

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

    private void ConstructSceneTree( DataInputStream dis ) throws IOException
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
                Logger.Log(
                    Integer.toBinaryString( traversalData[ i ] | 0xFFFFFF00 ).
                    substring( 24 ) );
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
                stack[ ++topStack ] = ConstructSceneNode( dis, nodeType );
                
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
    private ISceneNode ConstructSceneNode( DataInputStream dis, byte nodeType )
        throws IOException
    {
        switch ( nodeType )
        {
            case 0:
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Log( "Constructing NullSceneNode..." );
                /* $endif$ */
                return ConstructNullSceneNode( dis );

            case 1:
                return ConstructImageSceneNode( dis );

            case 2:
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Log( "Constructing SpriteSceneNode..." );
                /* $endif$ */
                return ConstructSpriteSceneNode( dis );

            case 3:
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Log( "Constructing StringSceneNode..." );
                /* $endif$ */
                return ConstructStringSceneNode( dis );
                
            case 4:
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Log( "Constructing TiledLayerSceneNode..." );
                /* $endif$ */
                return ConstructTiledLayerSceneNode( dis );

            case 5:
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Log( "Constructing ClippingSceneNode..." );
                /* $endif$ */
                return ConstructClippingSceneNode( dis );

            case 6:
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Log( "Constructing ViewportSceneNode..." );
                /* $endif$ */
                return ConstructViewportSceneNode( dis );

            /* $if SPUKMK2ME_DEBUG$ */
            default:
                Logger.Log( "Unknown node type." );
            /* $endif$ */
        }
        
        return null;
    }

    private NullSceneNode ConstructNullSceneNode( DataInputStream dis )
        throws IOException
    {
        short   x       = dis.readShort();
        short   y       = dis.readShort();
        byte    flags   = dis.readByte();

        NullSceneNode node = new NullSceneNode();

        node.SetPosition( x, y );
        node.c_visible  = (flags & 0x80) != 0;
        node.c_enable   = (flags & 0x40) != 0;

        return node;
    }

    private ImageSceneNode ConstructImageSceneNode( DataInputStream dis )
        throws IOException
    {
        /* $if SPUKMK2ME_DEBUG$ */
        Logger.Log( "Constructing ImageSceneNode..." );
        /* $endif$ */
        
        int     imageIndex;
        short   x, y;
        byte    flags;

        x               = dis.readShort();
        y               = dis.readShort();
        imageIndex      = dis.readUnsignedShort();
        flags           = dis.readByte();

        ImageSceneNode node = new ImageSceneNode(
            (ISubImage)(m_resourceSet.GetResource(
                imageIndex, IResource.RT_IMAGE ) ) );

        node.SetPosition( x, y );
        node.c_visible  = (flags & 0x80) != 0;
        node.c_enable   = (flags & 0x40) != 0;
        
        /* $if SPUKMK2ME_DEBUG$ */
        Logger.Log( "X, Y, index, flags: " + x + ' ' + y + ' ' + imageIndex + 
            ' ' + Integer.toBinaryString( flags | 0xFFFFFF00 ).
                substring( 24 ) + ". " );
        /* $endif$ */
        
        return node;
    }

    private SpriteSceneNode ConstructSpriteSceneNode( DataInputStream dis )
        throws IOException
    {
        ISubImage[] images;

        int     mode, msPerFrame, nImages, firstIndex, lastIndex, nFrameToStop;
        short   x, y;
        byte    flags;

        mode        = dis.readInt();
        msPerFrame  = dis.readInt();
        x           = dis.readShort();
        y           = dis.readShort();
        nImages     = dis.readUnsignedByte();

        images = new ISubImage[ nImages ];

        for ( int i = nImages; i != 0; --i )
        {
            images[ i ] = (ISubImage)(m_resourceSet.GetResource(
                dis.readByte(), IResource.RT_IMAGE ) );
        }

        firstIndex      = dis.readUnsignedByte();
        lastIndex       = dis.readUnsignedByte();
        nFrameToStop    = dis.readUnsignedByte();
        flags           = dis.readByte();

        SpriteSceneNode node = new SpriteSceneNode( images );

        node.SetAnimating(
            mode, firstIndex, lastIndex, msPerFrame, nFrameToStop );
        node.SetPosition( x, y );
        node.c_visible  = (flags & 0x80) != 0;
        node.c_enable   = (flags & 0x40) != 0;

        /* $if SPUKMK2ME_SCENESAVER$ */
        SpriteSceneNode.SpriteSceneNodeInfoData infoData =
            node.new SpriteSceneNodeInfoData();

        infoData.c_firstIndex   = firstIndex;
        infoData.c_lastIndex    = lastIndex;
        infoData.c_mode         = mode;
        infoData.c_msPerFrame   = msPerFrame;
        infoData.c_nFrameToStop = nFrameToStop;
        infoData.c_nImages      = nImages;
        infoData.c_images       = images;

        node.c_infoData = infoData;
        /* $endif$ */

        return node;
    }

    private StringSceneNode ConstructStringSceneNode( DataInputStream dis )
        throws IOException
    {
        byte[]  properties;
        int     alignment, fontIndex, nProperties;
        short   x, y, width, height;
        byte    flags;

        alignment   = dis.readInt();
        x           = dis.readShort();
        y           = dis.readShort();
        width       = dis.readShort();
        height      = dis.readShort();
        fontIndex   = dis.readByte();
        nProperties = dis.readUnsignedByte();

        if ( nProperties == 0 )
            properties = null;
        else
        {
            properties = new byte[ nProperties ];
            dis.read( properties );
        }
        
        flags = dis.readByte();

        StringSceneNode node    = new StringSceneNode();
        String content          = dis.readUTF();
        ICFont font;
        
        if ( fontIndex == -1 )
            font = null;
        else
        {
            font = (ICFont)m_resourceSet.GetResource(
                fontIndex, IResource.RT_BITMAPFONT );
        }

        node.SetupString( font,
            content, properties, alignment,
            width, height, (flags & 0x01) != 0 );

        node.SetPosition( x, y );
        node.c_visible  = (flags & 0x80) != 0;
        node.c_enable   = (flags & 0x40) != 0;

        /* $if SPUKMK2ME_SCENESAVER$ */
        StringSceneNode.StringSceneNodeInfoData infoData =
            node.new StringSceneNodeInfoData();

        infoData.c_font         = font;
        infoData.c_alignment    = alignment;
        infoData.c_width        = width;
        infoData.c_height       = height;
        infoData.c_properties   = properties;
        infoData.c_nProperties  = nProperties;
        infoData.c_string       = content;
        infoData.c_truncate     = (flags & 0x01) != 0;

        node.c_infoData         = infoData;
        /* $endif$ */

        return node;
    }

    private ISceneNode ConstructTiledLayerSceneNode( DataInputStream dis )
        throws IOException
    {
        short[]     imageIndexes;
        short[][]   spriteIndexes;
        int[]       spriteSpeed;
        byte[]      terrainData;
        short       x, y, width, height, stepX, stepY,
                    viewWidth, viewHeight, viewX, viewY;
        byte        flags;

        // Width, height and steps
        x           = dis.readShort();
        y           = dis.readShort();
        width       = dis.readShort();
        height      = dis.readShort();
        stepX       = dis.readShort();
        stepY       = dis.readShort();
        viewWidth   = dis.readShort();
        viewHeight  = dis.readShort();
        viewX       = dis.readShort();
        viewY       = dis.readShort();
        flags       = dis.readByte();

        // Terrain data
        terrainData = new byte[ width * height ];
        dis.read( terrainData );

        // Images
        short nSprites, nImages;

        nImages = dis.readShort();
        imageIndexes    = new short[ nImages ];

        for ( int i = 0; i != nImages; ++i )
            imageIndexes[ i ] = dis.readShort();

        // Sprites
        nSprites = dis.readShort();
        spriteIndexes   = new short[ nSprites ][];
        spriteSpeed     = new int[ nSprites ];

        short[] sprite;

        for ( int i = 0; i != nSprites; ++i )
        {
            sprite = spriteIndexes[ i ] = new short[ dis.readShort() ];

            for ( int j = 0; j != sprite.length; ++j )
                sprite[ j ] = dis.readShort();

            spriteSpeed[ i ] = dis.readInt();
        }

        // Construct
        ISubImage[][]   sprites = new ISubImage[ nSprites ][];
        ISubImage[]     images  = new ISubImage[ nImages ];

        for ( int i = 0; i != nImages; ++i )
        {
            images[ i ] = (ISubImage)m_resourceSet.GetResource(
                imageIndexes[ i ], IResource.RT_IMAGE );
        }

        for ( int i = 0; i != nSprites; ++i )
        {
            sprite = spriteIndexes[ i ];
            sprites[ i ] = new ISubImage[ sprite.length ];

            for ( int j = 0; j != sprite.length; ++j )
            {
                sprites[ i ][ j ] = (ISubImage)m_resourceSet.
                    GetResource( sprite[ j ], IResource.RT_IMAGE );
            }
        }

        TiledLayerSceneNode node = new TiledLayerSceneNode();

        node.SetupTiledLayer( images, sprites, spriteSpeed, terrainData,
            width, height, stepX, stepY );
        node.SetupRepeatedView( viewX, viewY, viewWidth, viewHeight,
            (flags & 0x20) != 0 );

        node.c_x = x;
        node.c_y = y;
        node.c_visible  = (flags & 0x80) != 0;
        node.c_enable   = (flags & 0x40) != 0;

        /* $if SPUKMK2ME_SCENESAVER$ */
        TiledLayerSceneNode.TiledLayerSceneNodeInfoData infoData =
            node.new TiledLayerSceneNodeInfoData();

        infoData.c_sprites      = sprites;
        infoData.c_images       = images;
        infoData.c_tableWidth   = width;
        infoData.c_tableHeight  = height;
        infoData.c_stepX        = stepX;
        infoData.c_stepY        = stepY;
        infoData.c_viewWidth    = viewWidth;
        infoData.c_viewHeight   = viewHeight;
        infoData.c_viewX        = viewX;
        infoData.c_viewY        = viewY;
        infoData.c_repeatedView = (flags & 0x20) != 0;
        infoData.c_spriteSpeed  = spriteSpeed;
        infoData.c_terrainData  = terrainData;
        
        node.c_infoData = infoData;
        /* $endif$ */

        return node;
    }

    private ISceneNode ConstructClippingSceneNode( DataInputStream dis )
        throws IOException
    {
        short x     = dis.readShort();
        short y     = dis.readShort();
        short clipX = dis.readShort();
        short clipY = dis.readShort();
        short clipW = dis.readShort();
        short clipH = dis.readShort();
        byte  flags = dis.readByte(); 
        ClippingSceneNode node = new ClippingSceneNode();
        
        node.c_x        = x;
        node.c_y        = y;
        node.c_visible  = (flags & 0x80) != 0;
        node.c_enable   = (flags & 0x40) != 0;
        node.SetClipping( clipX, clipY, clipW, clipH );
        
        /* $if SPUKMK2ME_SCENESAVER$ */
        ClippingSceneNode.ClippingSceneNodeInfoData infoData =
            node.new ClippingSceneNodeInfoData();

        infoData.c_x        = clipX;
        infoData.c_y        = clipY;
        infoData.c_width    = clipW;
        infoData.c_height   = clipH;
        
        node.c_infoData = infoData;
        /* $endif$ */
        
        return node;
    }

    private ISceneNode ConstructViewportSceneNode( DataInputStream dis )
        throws IOException
    {
        return null;
    }
    
    private static final String ROOTNODE_NAME   = "root";
    private static final String VALID_STRING    = "SPUKMK2me_SCENE-FILE_0.1";

    private int[]           m_exportedNodeIndexes;
    private String[]        m_exportedNodeNames;
    private NamedList       m_exportedNodes;
    private ResourceSet     m_resourceSet;
    private ISceneNode      m_root;
}
