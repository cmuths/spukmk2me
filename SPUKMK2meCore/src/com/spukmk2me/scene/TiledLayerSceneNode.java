package com.spukmk2me.scene;

import com.spukmk2me.Util;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.RenderInfo;
import com.spukmk2me.video.ISubImage;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */

public class TiledLayerSceneNode extends ISceneNode
{
    public TiledLayerSceneNode() {}

    public void Render( IVideoDriver driver )
    {
        RenderInfo ri = driver.GetRenderInfo();
        short oldX = ri.c_rasterX;
        short oldY = ri.c_rasterY;
        
        CalculateViewMovement( driver.GetRenderInfo().c_passedTime );
        
        if ( m_sprites != null )
            CalculateSprites( driver.GetRenderInfo().c_passedTime );
        
        if ( m_repeatedView )
        {
            long oldClip = driver.GetClipping();
            int nX, nY;
            int displayW = c_stepX * c_tableWidth;
            int displayH = c_stepY * c_tableHeight;
            
            {
                int rangeX = m_viewWidth - Util.FPRound( m_viewX );

                nX = rangeX / displayW;

                if ( rangeX % displayW != 0 )
                    ++nX;
            }
            
            {
                int rangeY = m_viewHeight - Util.FPRound( m_viewY );

                nY = rangeY / displayH;

                if ( rangeY % displayH != 0 )
                    ++nY;
            }
            
            short oldClipX, oldClipY, oldClipW, oldClipH, clipX, clipY, clipW, clipH;
            
            oldClipX = (short)(oldClip >>> 48);
            oldClipY = (short)(oldClip >> 32 & 0x000000000000FFFFL);
            oldClipW = (short)(oldClip >> 16 & 0x000000000000FFFFL);
            oldClipH = (short)(oldClip & 0x000000000000FFFFL);
            
            if ( Util.RectIntersect(
                oldClipX, oldClipY, oldClipW, oldClipH,
                ri.c_rasterX, ri.c_rasterY, m_viewWidth, m_viewHeight ) )
            {
                clipX = (short)Math.max( ri.c_rasterX, oldClipX );
                clipY = (short)Math.max( ri.c_rasterY, oldClipY );
                clipW = (short)(Math.min(
                    ri.c_rasterX + m_viewWidth,
                    oldClipX + oldClipW ) - clipX);
                clipH = (short)(Math.min(
                    ri.c_rasterY + m_viewHeight,
                    oldClipY + oldClipH ) - clipY);
            }
            else
                clipX = clipY = clipW = clipH = 0;
            
            driver.SetClipping( clipX, clipY, clipW, clipH );
            
            short x, y = (short)(Util.FPRound( m_viewY ) + oldY);
            
            for ( ; nY != 0; --nY )
            {
                x = (short)(Util.FPRound( m_viewX ) + oldX);
                
                for ( int i = nX; i != 0; --i )
                {
                    RenderOnce( driver, x, y );
                    x += displayW;
                }
                
                y += displayH;
            }
            
            driver.SetClipping( oldClipX, oldClipY, oldClipW, oldClipH );
        }
        else
            RenderOnce( driver, oldX, oldY );
        
        ri.c_rasterX = oldX;
        ri.c_rasterY = oldY;
    }
    
    public short GetAABBX()
    {
        return 0;
    }
    
    public short GetAABBY()
    {
        return 0;
    }

    public short GetAABBWidth()
    {
        return ( m_repeatedView )?
            m_viewWidth : (short)(c_stepX * c_tableWidth);
    }

    public short GetAABBHeight()
    {
        return ( m_repeatedView )?
            m_viewHeight : (short)(c_stepY * c_tableHeight);
    }

    public void SetupTiledLayer( ISubImage[] images, ISubImage[][] sprites,
        int[] spriteSpeed, byte[] terrainData,
        short tableWidth, short tableHeight, short stepX, short stepY )
    {
        /* $if SPUKMK2ME_DEBUG$ */
        if ( (tableWidth == 0) || (tableHeight == 0)  )
            Logger.Trace( "WARNING: TiledLayer: zero dimension." );
        else if ( terrainData == null )
        {
            Logger.Trace(
                "ERROR: TiledLayer: null pointer passed to terrain." );
        }
        else if ( terrainData.length != tableWidth * tableHeight )
            Logger.Trace( "WARNING: TiledLayer: terrain size mismatch" );

        if ( images == null )
            Logger.Trace( "WARNING: null pointer was passed to image data." );

        if ( sprites == null )
            Logger.Trace( "WARNING: null pointer was passed to sprite data." );
        else if ( spriteSpeed == null )
        {
            Logger.Trace(
                "WARNING: null pointer was passed to sprite speed." );
        }
        else if ( sprites.length != spriteSpeed.length )
        {
            Logger.Trace(
                "WARNING: number of sprites/sprite speed mismatch." );
        }

        /* $endif$ */

        m_images        = images;
        m_sprites       = sprites;
        m_spriteSpeed   = spriteSpeed;
        c_terrainData   = terrainData;
        c_tableWidth    = tableWidth;
        c_tableHeight   = tableHeight;
        c_stepX         = stepX;
        c_stepY         = stepY;

        if ( m_sprites != null )
        {
            m_spriteRealIndexes = new int[ m_sprites.length ];
            m_spriteIndexes     = new int[ m_sprites.length ];
        }
    }
    
    public void SetupRepeatedView( short startX, short startY,
        short width, short height, int spdX, int spdY, boolean repeatedView )
    {
        if ( m_repeatedView = repeatedView )
        {
            m_viewWidth     = width;
            m_viewHeight    = height;
            m_viewSpdX      = spdX;
            m_viewSpdY      = spdY;
            // viewX, viewY are modified to become 0 or negative values
            
            short w = (short)(c_stepX * c_tableWidth);
            short h = (short)(c_stepY * c_tableHeight);
            
            m_viewX = startX;
            m_viewY = startY;
            
            while ( m_viewX > 0 )
                m_viewX -= w;
            
            while ( m_viewX <= -w )
                m_viewX += w;
            
            while ( m_viewY > 0 )
                m_viewY -= h;
            
            while ( m_viewY <= -h )
                m_viewY += h;
            
            // Convert viewX and viewY to 16-16 fixed point
            m_viewX <<= 16;
            m_viewY <<= 16;
        }
    }
    
    public void changeSpeed( int spdX, int spdY )
    {
        m_viewSpdX = spdX;
        m_viewSpdY = spdY;
    }
    
    private void RenderOnce( IVideoDriver driver, short x, short y )
    {
        RenderInfo ri = driver.GetRenderInfo();
        int i, j, index, data;

        index = 0;

        for ( i = 0; i != c_tableHeight; ++i )
        {
            ri.c_rasterX = x;
            ri.c_rasterY = y;
            
            for ( j = 0; j != c_tableWidth; ++j )
            {
                data = c_terrainData[ index++ ];

                if ( data >= 0 )
                    m_images[ data ].Render( driver );
                else if ( data != (byte)0xFF ) // -1 means null cell
                {
                    data = ~data; //data = -1 - data;
                    m_sprites[ data ][ m_spriteIndexes[ data ] ].
                        Render( driver );
                }

                ri.c_rasterX += c_stepX;
            }

            y += c_stepY;
        }
    }
    
    private void CalculateSprites( int deltaTime )
    {
        int n = m_sprites.length;

        for ( int i = 0; i != n; ++i )
        {
            m_spriteRealIndexes[ i ] +=
                Util.FPMul( m_spriteSpeed[ i ], deltaTime );
            m_spriteIndexes[ i ] = Util.FPRound( m_spriteRealIndexes[ i ] );

            m_spriteIndexes[ i ] %= m_sprites[ i ].length;
        }
    }
    
    private void CalculateViewMovement( int deltaTime )
    {
        if ( (m_viewSpdX == 0) && (m_viewSpdY == 0) )
            return;
        
        m_viewX += Util.FPMul( m_viewSpdX, deltaTime );
        m_viewY += Util.FPMul( m_viewSpdY, deltaTime );
        
        int w = (c_stepX * c_tableWidth) << 16;
        int h = (c_stepY * c_tableHeight) << 16;
        
        while ( m_viewX > 0 )
            m_viewX -= w;
        
        while ( m_viewX <= -w )
            m_viewX += w;
        
        while ( m_viewY > 0 )
            m_viewY -= h;
        
        while ( m_viewY <= -h )
            m_viewY += h;
    }

    public byte[]   c_terrainData;
    public short    c_tableWidth, c_tableHeight, c_stepX, c_stepY;
    
    private ISubImage[][]   m_sprites;
    private ISubImage[]     m_images;
    private int[]           m_spriteSpeed;
    private int             m_viewX, m_viewY;
    private short           m_viewWidth, m_viewHeight;
    private int             m_viewSpdX, m_viewSpdY;
    private boolean         m_repeatedView;

    private int[]           m_spriteRealIndexes;
    private int[]           m_spriteIndexes;
    
    /* $if SPUKMK2ME_SCENESAVER$ */
    public class TiledLayerSceneNodeInfoData
    {
        public ISubImage[][]    c_sprites;
        public ISubImage[]      c_images;
        public int[]            c_spriteSpeed;
        public byte[]           c_terrainData;
        public short            c_tableWidth, c_tableHeight,
                                c_stepX, c_stepY,
                                c_viewX, c_viewY, c_viewWidth, c_viewHeight;
        public int              c_viewSpdX, c_viewSpdY;
        public boolean          c_repeatedView;
    }
    /* $endif$ */
}
