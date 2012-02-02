package com.spukmk2me.scene;

import com.spukmk2me.Util;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.RenderInfo;
import com.spukmk2me.video.ISubImage;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */

public class TiledLayerSceneNode extends ITopLeftOriginSceneNode
{
    public TiledLayerSceneNode() {}

    public void Render( IVideoDriver driver )
    {
        RenderInfo ri = driver.GetRenderInfo();
        short oldX = ri.c_rasterX;
        short oldY = ri.c_rasterY;
        
        if ( m_sprites != null )
            CalculateSprites( driver.GetRenderInfo().c_passedTime );
        
        if ( m_repeatedView )
        {
            long oldClip = driver.GetClipping();
            int nX, nY;
            int displayW = m_stepX * m_tableWidth;
            int displayH = m_stepY * m_tableHeight;
            
            {
                int rangeX = m_viewWidth - m_viewX;

                nX = rangeX / displayW;

                if ( rangeX % displayW != 0 )
                    ++nX;
            }
            
            {
                int rangeY = m_viewHeight - m_viewY;

                nY = rangeY / displayH;

                if ( rangeY % displayH != 0 )
                    ++nY;
            }
            
            driver.SetClipping( oldX, oldY, m_viewWidth, m_viewHeight );
            
            short x, y = (short)(m_viewY + oldY);
            
            for ( ; nY != 0; --nY )
            {
                x = (short)(m_viewX + oldX);
                
                for ( int i = nX; i != 0; --i )
                {
                    RenderOnce( driver, x, y );
                    x += displayW;
                }
                
                y += displayH;
            }
            
            driver.SetClipping(
                (short)(oldClip >>> 48), (short)(oldClip >>> 32),
                (short)(oldClip >>> 16), (short)oldClip );
        }
        else
            RenderOnce( driver, oldX, oldY );
        
        ri.c_rasterX = oldX;
        ri.c_rasterY = oldY;
    }

    public short GetAABBWidth()
    {
        return ( m_repeatedView )?
            m_viewWidth : (short)(m_stepX * m_tableWidth);
    }

    public short GetAABBHeight()
    {
        return ( m_repeatedView )?
            m_viewHeight : (short)(m_stepY * m_tableHeight);
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
        m_terrainData   = terrainData;
        m_tableWidth    = tableWidth;
        m_tableHeight   = tableHeight;
        m_stepX         = stepX;
        m_stepY         = stepY;

        if ( m_sprites != null )
        {
            m_spriteRealIndexes = new int[ m_sprites.length ];
            m_spriteIndexes     = new int[ m_sprites.length ];
        }
    }
    
    public void SetupRepeatedView( short startX, short startY,
        short width, short height, boolean repeatedView )
    {
        if ( m_repeatedView = repeatedView )
        {
            m_viewWidth     = width;
            m_viewHeight    = height;
            // viewX, viewY are modified to become negative values
            
            short w = (short)(m_stepX * m_tableWidth);
            short h = (short)(m_stepY * m_tableHeight);
            
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
        }
    }
    
    private void RenderOnce( IVideoDriver driver, short x, short y )
    {
        RenderInfo ri = driver.GetRenderInfo();
        int i, j, index, data;

        index = 0;

        for ( i = 0; i != m_tableHeight; ++i )
        {
            ri.c_rasterX = x;
            ri.c_rasterY = y;
            
            for ( j = 0; j != m_tableWidth; ++j )
            {
                data = m_terrainData[ index++ ];

                if ( data >= 0 )
                    m_images[ data ].Render( driver );
                else if ( data != (byte)0xFF ) // -1 means null cell
                {
                    data = ~data; //data = -1 - data;
                    m_sprites[ data ][ m_spriteIndexes[ data ] ].
                        Render( driver );
                }

                ri.c_rasterX += m_stepX;
            }

            y += m_stepY;
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

    private ISubImage[][]   m_sprites;
    private ISubImage[]     m_images;
    private int[]           m_spriteSpeed;
    private byte[]          m_terrainData;
    private short           m_tableWidth, m_tableHeight, m_stepX, m_stepY;
    private short           m_viewX, m_viewY,
                            m_viewWidth, m_viewHeight;
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
        public boolean          c_repeatedView;
    }
    /* $endif$ */
}
