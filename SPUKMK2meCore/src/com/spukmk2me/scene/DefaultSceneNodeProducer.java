package com.spukmk2me.scene;

import java.io.DataInputStream;
import java.io.IOException;

import com.spukmk2me.debug.Logger;
import com.spukmk2me.resource.IResource;
import com.spukmk2me.resource.ResourceSet;
import com.spukmk2me.scene.complex.ClippingSceneNode;
import com.spukmk2me.video.ICFont;
import com.spukmk2me.video.ISubImage;

public final class DefaultSceneNodeProducer implements ISceneNodeProducer
{
    public ISceneNode ConstructSceneNode( DataInputStream dis, byte nodeType )
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
                
            case 7:
                // $if SPUKMK2ME_DEBUG$ */
                Logger.Log( "Constructing LineSceneNode..." );
                /* $endif$ */
                return ConstructLineSceneNode( dis );

            /* $if SPUKMK2ME_DEBUG$ */
            default:
                Logger.Log( "Unknown node type." );
            /* $endif$ */
        }
        
        return null;
    }
    
    public void UpdateResourceSet( ResourceSet resourceSet )
    {
        m_resourceSet = resourceSet;
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

        int     mode, msPerFrame, nImages, startIndex, firstIndex, lastIndex, nFrameToStop;
        short   x, y;
        byte    flags;

        mode        = dis.readInt();
        msPerFrame  = dis.readInt();
        x           = dis.readShort();
        y           = dis.readShort();
        nImages     = dis.readUnsignedByte();

        images = new ISubImage[ nImages ];

        for ( int i = 0; i != nImages; ++i )
        {
            images[ i ] = (ISubImage)(m_resourceSet.GetResource(
                dis.readUnsignedByte(), IResource.RT_IMAGE ) );
        }

        startIndex      = dis.readUnsignedByte();
        firstIndex      = dis.readUnsignedByte();
        lastIndex       = dis.readUnsignedByte();
        nFrameToStop    = dis.readUnsignedByte();
        flags           = dis.readByte();

        SpriteSceneNode node = new SpriteSceneNode( images );

        node.SetAnimating(
            mode, firstIndex, lastIndex, msPerFrame, nFrameToStop );
        node.SetFrameIndex( startIndex );
        node.SetPosition( x, y );
        node.c_visible  = (flags & 0x80) != 0;
        node.c_enable   = (flags & 0x40) != 0;

        /* $if SPUKMK2ME_SCENESAVER$ */
        SpriteSceneNode.SpriteSceneNodeInfoData infoData =
            node.new SpriteSceneNodeInfoData();

        infoData.c_startIndex   = startIndex;
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
        int         viewSpdX, viewSpdY;
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
        //viewSpdX = viewSpdY = 0;
        viewSpdX    = dis.readInt();
        viewSpdY    = dis.readInt();
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
        node.SetupRepeatedView( viewX, viewY, viewWidth, viewHeight, viewSpdX, viewSpdY,
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
        infoData.c_viewSpdX     = viewSpdX;
        infoData.c_viewSpdY     = viewSpdY;
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
    
    private ISceneNode ConstructLineSceneNode( DataInputStream dis )
        throws IOException
    {
        short   x       = dis.readShort();
        short   y       = dis.readShort();
        byte    flags   = dis.readByte();
        short   deltaX  = dis.readShort();
        short   deltaY  = dis.readShort();
        int     color   = dis.readInt();
        LineSceneNode lineNode = new LineSceneNode();
        
        lineNode.c_x        = x;
        lineNode.c_y        = y;
        lineNode.c_visible  = (flags & 0x80) != 0;
        lineNode.c_enable   = (flags & 0x40) != 0;
        lineNode.SetData( deltaX, deltaY, color );
        return lineNode;
    }
    
    private ResourceSet m_resourceSet;
}
