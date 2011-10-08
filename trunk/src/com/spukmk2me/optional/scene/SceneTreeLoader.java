package com.spukmk2me.optional.scene;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.ICFont;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.NullSceneNode;
import com.spukmk2me.scene.ImageSceneNode;
import com.spukmk2me.scene.SpriteSceneNode;
import com.spukmk2me.scene.StringSceneNode;
import com.spukmk2me.scene.TiledLayerSceneNode;
//#ifdef __SPUKMK2ME_SCENESAVER
//# import com.spukmk2me.scene.SpriteSceneNodeInfoData;
//# import com.spukmk2me.scene.StringSceneNodeInfoData;
//# import com.spukmk2me.scene.TiledLayerSceneNodeInfoData;
//#endif

public final class SceneTreeLoader
{
    public SceneTreeLoader( ResourceManager resourceManager )
    {
        m_resourceManager = resourceManager;
    }

    public ISceneNode GetSceneNode( String name )
    {
        if ( name.equals( ROOTNODE_NAME ) )
            return m_root;

        for ( int i = 0; i != m_exportedNodeNames.length; ++i )
        {
            if ( m_exportedNodeNames[ i ].equals( name ) )
                return m_exportedNodes[ i ];
        }

        return null;
    }

    public boolean Load( InputStream is, String pathToSceneFile,
        char dstPathSeparator ) throws IOException
    {
        DataInputStream dis = new DataInputStream( is );

        if ( !CheckHeader( dis ) )
            return false;

        m_resourceManager.LoadResources( dis, pathToSceneFile,
            dstPathSeparator );
        LoadNodeNamesMappingTable( dis );
        ConstructSceneTree( dis );

        return true;
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

        // Load exported node indexes
        {
            m_exportedNodeIndexes = new int[ nExportedNodes ];

            for ( int i = 0; i != nExportedNodes; ++i )
                m_exportedNodeIndexes[ i ] = dis.readInt();
        }

        // Load proxy names
        {
            m_exportedNodeNames = new String[ nExportedNodes ];

            for ( int i = 0; i != nExportedNodes; ++i )
                m_exportedNodeNames[ i ] = dis.readUTF();
        }
    }

    private void ConstructSceneTree( DataInputStream dis ) throws IOException
    {
        byte[]  traversalData;

        m_root = new NullSceneNode();

        // Load traversal data
        {
            int nNodes = dis.readInt();

            // Empty tree
            if ( nNodes == 1 )
                return;

            int nTraversalBits = (nNodes - 1 << 1) + 1;

            if ( (nTraversalBits & 0x00000007) == 0 )
                traversalData = new byte[ nTraversalBits >> 3 ];
            else
                traversalData = new byte[ (nTraversalBits >> 3) + 1 ];

            dis.read( traversalData );
        }

        // Tree construction sequence
        int     prefetchedIndex = 0, direction = 0;
        int     exportedIndex = 0, currentNodeIndex = 0;
        byte    bitCounter = 0, nodeType;
        
        // Construct stack with the size equal to tree height.
        ISceneNode[]    stack = new ISceneNode[ dis.readUnsignedShort() ];
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
                stack[ topStack-- ] = null;
            else
            {
                nodeType = dis.readByte();
                stack[ ++topStack ] = ConstructSceneNode( dis, nodeType );
                ISceneNode.AddSceneNode(
                    stack[ topStack ], stack[ topStack - 1 ] );

                if ( currentNodeIndex ==
                    m_exportedNodeIndexes[ exportedIndex ] )
                {
                    m_exportedNodes[ exportedIndex++ ] = stack[ topStack ];
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
    // 4: Clipping
    // 5: Viewport
    private ISceneNode ConstructSceneNode( DataInputStream dis, byte nodeType )
        throws IOException
    {
        switch ( nodeType )
        {
            case 0:
                return ConstructNullSceneNode( dis );

            case 1:
                return ConstructImageSceneNode( dis );

            case 2:
                return ConstructSpriteSceneNode( dis );

            case 3:
                return ConstructStringSceneNode( dis );

            case 4:
                return ConstructClippingSceneNode( dis );

            case 5:
                return ConstructViewportSceneNode( dis );

            case 6:
                return ConstructTiledLayerSceneNode( dis );
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
        int     imageIndex;
        short   x, y;
        byte    flags;

        x               = dis.readShort();
        y               = dis.readShort();
        imageIndex      = dis.readUnsignedShort();
        flags           = dis.readByte();

        ImageSceneNode node = new ImageSceneNode(
            (ISubImage)(m_resourceManager.GetResource(
                imageIndex, ResourceManager.RT_IMAGE ) ) );

        node.SetPosition( x, y );
        node.c_visible  = (flags & 0x80) != 0;
        node.c_enable   = (flags & 0x40) != 0;

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
            images[ i ] = (ISubImage)(m_resourceManager.GetResource(
                dis.readByte(), ResourceManager.RT_IMAGE ) );
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

        //#ifdef __SPUKMK2ME_SCENESAVER
//#         SpriteSceneNodeInfoData infoData = new SpriteSceneNodeInfoData();
//# 
//#         infoData.c_firstIndex   = firstIndex;
//#         infoData.c_lastIndex    = lastIndex;
//#         infoData.c_mode         = mode;
//#         infoData.c_msPerFrame   = msPerFrame;
//#         infoData.c_nFrameToStop = nFrameToStop;
//#         infoData.c_nImages      = nImages;
//#         infoData.c_images       = images;
//# 
//#         node.c_infoData = infoData;
        //#endif

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
        fontIndex   = dis.readUnsignedByte();
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

        node.SetupString(
            (ICFont)m_resourceManager.GetResource(
                fontIndex, ResourceManager.RT_FONT ),
            content, properties, alignment,
            width, height, (flags & 0x01) != 0 );

        node.SetPosition( x, y );
        node.c_visible  = (flags & 0x80) != 0;
        node.c_enable   = (flags & 0x40) != 0;

        //#ifdef __SPUKMK2ME_SCENESAVER
//#         StringSceneNodeInfoData infoData = new StringSceneNodeInfoData();
//# 
//#         infoData.c_font         = (ICFont)m_resourceManager.GetResource(
//#             fontIndex, ResourceManager.RT_FONT );
//#         infoData.c_alignment    = alignment;
//#         infoData.c_width        = width;
//#         infoData.c_height       = height;
//#         infoData.c_properties   = properties;
//#         infoData.c_nProperties  = nProperties;
//#         infoData.c_string       = content;
//#         infoData.c_truncate     = (flags & 0x01) != 0;
//# 
//#         node.c_infoData         = infoData;
        //#endif

        return node;
    }

    private ISceneNode ConstructTiledLayerSceneNode( DataInputStream dis )
        throws IOException
    {
        short[]     imageIndexes;
        short[][]   spriteIndexes;
        int[]       spriteSpeed;
        byte[]      terrainData;
        short       x, y, width, height, startX, startY,
                    step1X, step1Y, step2X, step2Y;
        byte        flags;

        // Width, height and steps
        x       = dis.readShort();
        y       = dis.readShort();
        width   = dis.readShort();
        height  = dis.readShort();
        startX  = dis.readShort();
        startY  = dis.readShort();
        step1X  = dis.readShort();
        step1Y  = dis.readShort();
        step2X  = dis.readShort();
        step2Y  = dis.readShort();
        flags   = dis.readByte();

        // Terrain data
        terrainData = new byte[ width * height ];
        dis.read( terrainData );

        // Images
        short nSprites, nImages;

        nImages = dis.readShort();
        imageIndexes    = new short[ nImages ];

        for ( int i = nImages; i != 0; --i )
            imageIndexes[ i ]   = dis.readShort();

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
            images[ i ] = (ISubImage)m_resourceManager.GetResource(
                imageIndexes[ i ], ResourceManager.RT_IMAGE );
        }

        for ( int i = 0; i != nSprites; ++i )
        {
            sprite = spriteIndexes[ i ];
            sprites[ i ] = new ISubImage[ sprite.length ];

            for ( int j = 0; j != sprite.length; ++j )
            {
                sprites[ i ][ j ] = (ISubImage)m_resourceManager.
                    GetResource( sprite[ j ], ResourceManager.RT_IMAGE );
            }
        }

        TiledLayerSceneNode node = new TiledLayerSceneNode();

        node.SetupTiledLayer( images, sprites, spriteSpeed, terrainData,
            startX, startY, width, height, step1X, step1Y, step2X, step2Y );

        node.c_x = x;
        node.c_y = y;
        node.c_visible  = (flags & 0x80) != 0;
        node.c_enable   = (flags & 0x40) != 0;

        //#ifdef __SPUKMK2ME_SCENESAVER
//#         TiledLayerSceneNodeInfoData infoData =
//#             new TiledLayerSceneNodeInfoData();
//# 
//#         infoData.c_sprites  = sprites;
//#         infoData.c_images   = images;
//#         infoData.c_startX   = startX;
//#         infoData.c_startY   = startY;
//#         infoData.c_width    = width;
//#         infoData.c_height   = height;
//#         infoData.c_step1X   = step1X;
//#         infoData.c_step1Y   = step1Y;
//#         infoData.c_step2X   = step1X;
//#         infoData.c_step2Y   = step1Y;
//#         infoData.c_spriteSpeed = spriteSpeed;
//#         infoData.c_terrainData = terrainData;
        //#endif

        return node;
    }

    private NullSceneNode ConstructClippingSceneNode( DataInputStream dis )
    {
        return null;
    }

    private NullSceneNode ConstructViewportSceneNode( DataInputStream dis )
    {
        return null;
    }

    private static final String ROOTNODE_NAME   = "root";
    private static final String VALID_STRING    = "SPUKMK2me_SCENE-FILE_0.1";

    private String[]        m_exportedNodeNames;
    private int[]           m_exportedNodeIndexes;
    private ISceneNode[]    m_exportedNodes;
    private ResourceManager m_resourceManager;
    private ISceneNode      m_root;
}
