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

public final class SceneTreeLoader
{
    public SceneTreeLoader() {}

    public ISceneNode GetSceneNode( String name )
    {
        if ( name == null )
            return m_root;

        for ( int i = 0; i != m_exportedNodeNames.length; ++i )
        {
            if ( m_exportedNodeNames[ i ].equals( name ) )
                return m_exportedNodes[ i ];
        }

        return null;
    }

    void Load( InputStream is ) throws IOException
    {
        LoadHeader( is );
        m_resourceManager.LoadResources( is );
        LoadNodeNamesMappingTable( is );
        ConstructSceneTree( is );
    }

    private void LoadHeader( InputStream is ) throws IOException
    {
    }

    private void LoadNodeNamesMappingTable( InputStream is ) throws IOException
    {
    }

    private void ConstructSceneTree( InputStream is ) throws IOException
    {
        DataInputStream dis = new DataInputStream( is );

        // Load traversal data
        int     nTraversalBits   = dis.readInt() << 1;

        // Empty tree
        if ( nTraversalBits == 0 )
            return;

        byte[]  traversalData;

        if ( (nTraversalBits & 0x00000007) == 0 )
            traversalData = new byte[ nTraversalBits >> 3 ];
        else
            traversalData = new byte[ (nTraversalBits >> 3) + 1 ];

        int     prefetchedIndex = 0, direction = 0;
        int     exportedIndex = 0, currentNodeIndex = 0;
        byte    bitCounter = 0, nodeType;
        
        // Construct stack with the size equal to tree height.
        ISceneNode[]    stack = new ISceneNode[ dis.readShort() ];
        int             topStack = 0;

        stack[ 0 ] = m_root = new NullSceneNode();

        while ( topStack != -1 )
        {
            if ( bitCounter == 0 )
            {
                bitCounter  = 8;
                direction   = traversalData[ prefetchedIndex++ ];
            }

            if ( (direction & 0x00000001) == 0 )
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
                imageIndex, MinimizedResourceLoader.RT_IMAGE ) ) );

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
                dis.readByte(), MinimizedResourceLoader.RT_IMAGE ) );
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
            properties = new byte[ nProperties ];

        dis.read( properties );
        flags = dis.readByte();

        StringSceneNode node = new StringSceneNode();

        node.SetupString(
            (ICFont)m_resourceManager.GetResource(
                fontIndex, MinimizedResourceLoader.RT_FONT ),
            dis.readUTF(), properties, alignment,
            width, height, (flags & 0x01) != 0 );

        node.SetPosition( x, y );
        node.c_visible  = (flags & 0x80) != 0;
        node.c_enable   = (flags & 0x40) != 0;

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

    private String[]                m_exportedNodeNames;
    private int[]                   m_exportedNodeIndexes;
    private ISceneNode[]            m_exportedNodes;
    private MinimizedResourceLoader m_resourceManager;
    private ISceneNode              m_root;
}
