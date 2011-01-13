/*
 *  SPUKMK2me - SPUKMK2 Engine for J2ME platform
 *  Copyright 2010 - 2011  HNYD Team
 *
 *   SPUKMK2me is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *   SPUKMK2me is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package spukmk2me.extension.midp;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;

import spukmk2me.video.BitmapFont;
import spukmk2me.video.IFontRenderer;
import spukmk2me.video.RenderTool;

/**
 *  A font renderer which use MIDP API as the main rendering API.
 *  \details Currently, it can only render font with maximum character
 * width/height is 18/23. You can extends the range of rendering up to
 * 32/any_number by changing the constant of this class. Because of current
 * implement, if the character width is large than 32, some of the essential
 * part must be re-written.
 *  @see spukmk2me.video.BitmapFont
 */
public final class FontRenderer_MIDP implements IFontRenderer
{    
    public FontRenderer_MIDP()
    {
        m_argb          = new int[ MAX_CHARWIDTH * MAX_CHARHEIGHT ];
        m_cache         = new int[ MAX_CHARHEIGHT ];
    }

    private void RenderChar( int charIndex )
    {
        CacheCharacterData( charIndex );
        
        int argbIndex, argbFirst, currentInt, i, j, width;

        argbFirst   = 0;
        i           = 0;
        width       = m_rasterCharWidth[ charIndex ];

        while ( i != m_rasterHeight )
        {
            currentInt  = m_cache[ i++ ];
            argbIndex   = argbFirst;
            
            for ( j = width; j != 0; --j )
            {
                m_argb[ argbIndex++ ] = ((currentInt & 0x80000000) == 0)?
                    0x00000000 : m_rasterColor;
                currentInt <<= 1;
            }
            
            argbFirst += MAX_CHARWIDTH;
        }

        PushCharacter( charIndex );
    }

    public void SetRenderTool( RenderTool renderTool )
    {
        m_g = (Graphics)renderTool.c_rAPI;
    }

    public void RenderString( char[] s, int offset, int length, int color,
        short x, short y )
    {
        if ( s == null )
            return;

        int border = offset + length;

        if ( border > s.length )
            border = s.length;

        if ( offset >= border )
            return;

        m_rasterColor   = color;
        m_rasterX       = x;
        m_rasterY       = y;

        char character;

        while ( offset != border )
        {
            character = s[ offset ];

            if ( character == '\n' )
            {
                if ( (m_rasterStyle & STYLE_UNDERLINE) != 0 )
                    DrawUnderline( x, m_rasterX - m_rasterDistance );

                m_rasterX   = x;
                m_rasterY  += m_rasterHeight;
            }
            else
            {
                if ( character != ' ' ) // Spaces should be invisible, right?
                {
                    RenderChar( m_rasterFont.GetIndex( character ) );
                    m_rasterX += m_rasterDistance +
                        m_rasterCharWidth[ m_rasterFont.GetIndex( character ) ] +
                        m_characterAdditionalWidth;
                }
                else
                    m_rasterX += m_rasterSpace;
            }

            ++offset;
        }

        if ( (m_rasterStyle & STYLE_UNDERLINE) != 0 )
            DrawUnderline( x, m_rasterX - m_rasterDistance );
    }
    
    public void RenderString( String s, int offset, int length, int color,
        short x, short y )
    {
        if ( s == null )
            return;

        int borderOffset    = Math.min( offset + length, s.length() );
        char[] buffer       = new char[ borderOffset - offset ];
        
        s.getChars( offset, borderOffset, buffer, 0 );
        RenderString( buffer, offset, borderOffset - offset, color, x, y );
    }

    public void RenderString( char[] s, int offset, int length, int color,
        byte alignment, short x, short y, short width, short height )
    {
        if ( s == null )
            return;

        if ( (alignment & (ALIGN_CENTERX | ALIGN_RIGHT)) != 0 )
        {
            int strWidth = m_rasterFont.GetStringWidth(
                s, offset, length, m_rasterStyle );
        
            if ( (alignment & ALIGN_CENTERX) != 0 )
                x += (width - strWidth) / 2;
            else if ( (alignment & ALIGN_RIGHT) != 0 )
                x += width - strWidth;
        }

        if ( (alignment & ALIGN_CENTERY) != 0 )
            y += (height - m_rasterHeight) / 2;
        else if ( (alignment & ALIGN_BOTTOM) != 0 )
            y += height - m_rasterHeight;

        RenderString( s, offset, length, color, x, y );
    }

    public void RenderString( String s, int offset, int length, int color,
        byte alignment, short x, short y, short width, short height )
    {
        if ( s == null )
            return;

        int borderOffset    = Math.min( offset + length, s.length() );
        char[] buffer       = new char[ borderOffset - offset ];

        s.getChars( offset, borderOffset, buffer, 0 );
        RenderString( buffer, offset, length, color, alignment, x, y,
            width, height );
    }

    public void PresetSettings( BitmapFont font, byte style )
    {
        m_rasterFont    = font;
        m_rasterStyle   = style;
        m_rasterData    = font.c_data;
        m_rasterWidth   = font.c_width;
        m_rasterHeight  = font.c_height;
        m_rasterBytesPerLine    = font.c_bytesPerLine;
        m_rasterCharWidth       = font.c_charWidth;
        m_rasterDistance        = font.c_charDistance;

        m_rasterBytesPerCharacter   = m_rasterBytesPerLine * m_rasterHeight;
        m_rasterSpace               = font.c_space;
        m_characterAdditionalWidth = 0;

        byte masked  = (byte)(m_rasterStyle & (STYLE_BOLD | STYLE_ITALIC));

        if ( masked != 0 )
        {
            ++m_rasterSpace;
            ++m_characterAdditionalWidth;

            if ( masked == (STYLE_BOLD | STYLE_ITALIC) )
            {
                ++m_rasterSpace;
                ++m_characterAdditionalWidth;
            }
        }
    }

    // Draw a underline from startX to endX
    private void DrawUnderline( int startX, int endX )
    {
        if ( endX > startX )
        {
            m_g.setColor( m_rasterColor );
            m_g.drawLine( startX,
                m_rasterY + m_rasterFont.c_yUnderline, endX,
                m_rasterY + m_rasterFont.c_yUnderline );
        }
    }

    private void CacheCharacterData( int charIndex )
    {
        int shiftRange, j, fetchedData;

        charIndex *= m_rasterBytesPerCharacter;

        for ( int i = 0; i != m_rasterHeight; )
        {
            shiftRange  = 24;
            fetchedData = 0x00000000;

            for ( j = m_rasterBytesPerLine; j-- != 0; )
            {
                fetchedData |=
                    ((int)m_rasterData[ charIndex++ ] & 0x000000FF) <<
                    shiftRange;
                shiftRange  -= 8;
            }

            m_cache[ i++ ] = fetchedData;
        }
    }

    private void PushCharacter( int charIndex )
    {
        int nRender = ( (m_rasterStyle & STYLE_BOLD) == 0 )? 0 : 1;
        int upperItalic = m_rasterHeight * 2 / 3;

        while ( nRender-- != -1 )
        {
            if ( (m_rasterStyle & STYLE_ITALIC) != 0 )
            {
                m_g.drawRGB( m_argb, 0, MAX_CHARWIDTH, m_rasterX + 1 + nRender,
                    m_rasterY, m_rasterCharWidth[ charIndex ], upperItalic,
                    true );
                m_g.drawRGB( m_argb, (upperItalic << 4) + (upperItalic << 1),
                    MAX_CHARWIDTH, m_rasterX + nRender,
                    m_rasterY + upperItalic,
                    m_rasterCharWidth[ charIndex ],
                    m_rasterHeight - upperItalic, true );
            }
            else
                m_g.drawRGB( m_argb, 0, MAX_CHARWIDTH, m_rasterX + nRender,
                    m_rasterY, m_rasterCharWidth[ charIndex ],
                    m_rasterHeight, true );
        }
    }

    private static final int    MAX_CHARWIDTH   = 18;
    private static final int    MAX_CHARHEIGHT  = 28;

    private Graphics     m_g;

    private int[]   m_argb, m_cache, m_rasterCharWidth;
    private byte[]  m_rasterData;
    private int     m_rasterX, m_rasterY, m_rasterColor,
                    m_rasterDistance, m_rasterSpace,
                    m_rasterBytesPerCharacter,
                    m_rasterWidth, m_rasterHeight,
                    m_rasterBytesPerLine,
                    m_characterAdditionalWidth;

    private byte        m_rasterStyle;
    private BitmapFont  m_rasterFont;
}
