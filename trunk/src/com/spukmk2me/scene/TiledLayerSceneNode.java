package com.spukmk2me.scene;

import com.spukmk2me.Util;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.RenderInfo;
import com.spukmk2me.video.ISubImage;

//#ifdef __SPUKMK2ME_DEBUG
//# import com.spukmk2me.debug.Logger;
//#endif

public class TiledLayerSceneNode extends ISceneNode
{
    public TiledLayerSceneNode()
    {
    }

    public void Render( IVideoDriver driver )
    {
        RenderInfo ri = driver.GetRenderInfo();

        if ( m_sprites != null )
            CalculateSprites( ri.c_passedTime );

        int i, j, index, data;
        short oldX = ri.c_rasterX;
        short oldY = ri.c_rasterY;
        short startX = oldX, startY = oldY;

        ri.c_rasterX   += m_startX;
        ri.c_rasterY   += m_startY;
        index           = 0;

        for ( i = 0; i != m_height; ++i )
        {
            for ( j = 0; j != m_width; ++j )
            {
                data = m_terrainData[ index++ ];

                if ( data >= 0 )
                    m_images[ data ].Render( driver );
                else
                {
                    data = ~data; //data = -1 - data;
                    m_sprites[ data ][ m_spriteIndexes[ data ] ].
                        Render( driver );
                }

                ri.c_rasterX += m_step1X;
                ri.c_rasterY += m_step1Y;
            }

            startX += m_step2X;
            startY += m_step2Y;
            ri.c_rasterX = startX;
            ri.c_rasterY = startY;
        }
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
        return 0;
    }

    public short GetAABBHeight()
    {
        return 0;
    }

    public void SetupTiledLayer( ISubImage[] images, ISubImage[][] sprites,
        int[] spriteSpeed, byte[] terrainData,
        short startX, short startY, short width, short height,
        short step1X, short step1Y, short step2X, short step2Y )
    {
        //#ifdef __SPUKMK2ME_DEBUG
//#         if ( (width == 0) || (height == 0)  )
//#             Logger.Log( "WARNING: TiledLayer: zero dimension." );
//#         else if ( terrainData == null )
//#             Logger.Log( "ERROR: TiledLayer: null pointer passed to terrain." );
//#         else if ( terrainData.length != width * height )
//#             Logger.Log( "WARNING: TiledLayer: terrain size mismatch" );
//# 
//#         if ( images == null )
//#             Logger.Log( "WARNING: null pointer was passed to image data." );
//# 
//#         if ( sprites == null )
//#             Logger.Log( "WARNING: null pointer was passed to sprite data." );
//#         else if ( spriteSpeed == null )
//#             Logger.Log( "WARNING: null pointer was passed to sprite speed." );
//#         else if ( sprites.length != spriteSpeed.length )
//#             Logger.Log( "WARNING: number of sprites/sprite speed mismatch." );
//# 
        //#endif

        m_images        = images;
        m_sprites       = sprites;
        m_spriteSpeed   = spriteSpeed;
        m_terrainData   = terrainData;
        m_startX        = startX;
        m_startY        = startY;
        m_width         = width;
        m_height        = height;
        m_step1X        = step1X;
        m_step1Y        = step1Y;
        m_step2X        = step2X;
        m_step2Y        = step2Y;

        if ( m_sprites != null )
        {
            m_spriteRealIndexes = new int[ m_sprites.length ];
            m_spriteIndexes     = new int[ m_sprites.length ];
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
    private short           m_width, m_height, m_startX, m_startY,
                            m_step1X, m_step1Y, m_step2X, m_step2Y;

    private int[]           m_spriteRealIndexes;
    private int[]           m_spriteIndexes;
}
