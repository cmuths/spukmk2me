package com.spukmk2me.extension.j2se;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.spukmk2me.video.ICFont;
import com.spukmk2me.video.ICFontRenderer;
import com.spukmk2me.video.IVideoDriver;

final class J2SEFontRenderer extends ICFontRenderer
{
    public J2SEFontRenderer( IVideoDriver vdriver )
    {
        super( vdriver );
        m_g = (Graphics)vdriver.GetProperty( J2SEVideoDriver.PROPERTY_GRAPHICS );
        m_buffer = new BufferedImage(
            BUFFER_WIDTH, BUFFER_HEIGHT, BufferedImage.TYPE_INT_ARGB );
    }

    protected void RenderCharacter( ICFont font, char character )
    {
        Object  rasterData  = font.GetBitmapData( character );
        int     dimension   = font.GetBitmapDataDimension();
        
        switch ( font.GetRenderDataType() )
        {
            case ICFont.RDT_INTARRAY:
                m_buffer.setRGB( 0, 0, m_charWidth, m_charHeight,
                    (int[])rasterData, 0, dimension >>> 16 );
                m_g.drawImage( m_buffer, m_rasterX, m_rasterY,
                    m_rasterX + m_charWidth,
                    m_rasterY + m_charHeight,
                    0, 0, m_charWidth, m_charHeight, null );
                break;
        }
    }

    private static final int BUFFER_WIDTH   = 32;
    private static final int BUFFER_HEIGHT  = 32;

    private Graphics        m_g;
    private BufferedImage   m_buffer;
}
