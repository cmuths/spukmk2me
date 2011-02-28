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
 *  along with SPUKMK2me.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spukmk2me.extension.midp;

import javax.microedition.lcdui.Graphics;

import com.spukmk2me.video.ICFont;
import com.spukmk2me.video.ICFontRenderer;
import com.spukmk2me.video.RenderTool;

/**
 *  A font renderer which use MIDP API as the main rendering API.
 *  \details Currently, it can only render font with maximum character
 * width/height is 18/23. You can extends the range of rendering up to
 * 32/any_number by changing the constant of this class. Because of current
 * implement, if the character width is large than 32, some of the essential
 * part must be re-written.
 *  @see spukmk2me.video.BitmapFont
 */
public final class FontRenderer_MIDP implements ICFontRenderer
{    
    public FontRenderer_MIDP() {}

    public void SetRenderTool( RenderTool renderTool )
    {
        m_g             = (Graphics)renderTool.c_rAPI;
        m_renderTool    = renderTool;
    }

    public void RenderString( char[] s, ICFont font, int offset, int length,
        int x, int y )
    {
        if ( s == null )
            return;

        int border = offset + length;

        if ( border > s.length )
            border = s.length;

        if ( offset >= border )
            return;

        font.RequestFont();

        try
        {
            int     rasterX     = x;
            int     charHeight  = font.GetLineHeight();
            int     charWidth;
            int     clipX, clipY, clipW, clipH;
            char    character;
            // Small space after each non-space character.
            boolean additionalSpace = false;

            {
                long clipping = m_renderTool.GetClipping();
            
                clipX = (int)(clipping >> 48);
                clipY = (int)((clipping >> 32) & 0x0000FFFF);
                clipW = (int)((clipping >> 16) & 0x0000FFFF);
                clipH = (int)(clipping & 0x0000FFFF);
            }
        
            while ( offset != border )
            {
                character = s[ offset ];

                if ( character == '\n' )
                {
                    rasterX         = x;
                    y              += charHeight;
                    additionalSpace = false;
                }
                else
                {
                    // Spaces should be invisible, right?
                    if ( character != ' ' )
                    {
                        charWidth = font.GetCharWidth( character );
                    
                        if ( RectangleCollide(
                                rasterX, y, charWidth, charHeight,
                                clipX, clipY, clipW, clipH ) )
                        {
                            m_g.drawRGB(
                                (int[])font.GetBitmapData( character ), 0,
                                font.GetBitmapDataDimension() >> 16,
                                rasterX, y, charWidth, charHeight, true );
                        }

                        rasterX += font.GetSpaceBetweenCharacters() +
                            font.GetCharWidth( character );
                        additionalSpace = true;
                    }
                    else
                    {
                        if ( additionalSpace )
                            rasterX -= font.GetSpaceBetweenCharacters();

                        rasterX        += font.GetCharWidth( ' ' );
                        additionalSpace = false;
                    }
                }

                ++offset;
            }
        } finally {
            font.ReleaseFont();
        }
    }
    
    public void RenderString( String s, ICFont font, int offset, int length,
        int x, int y )
    {
        if ( s == null )
            return;

        int borderOffset    = Math.min( offset + length, s.length() );
        char[] buffer       = new char[ borderOffset - offset ];
        
        s.getChars( offset, borderOffset, buffer, 0 );
        RenderString( buffer, font, offset, borderOffset - offset, x, y );
    }

    private boolean RectangleCollide( int x1, int y1, int w1, int h1,
        int x2, int y2, int w2, int h2 )
    {
        return  (x1 < x2 + w2) && (y1 < y2 + h2) &&
                (x2 < x1 + w1) && (y2 < y1 + h1);
    }

    private Graphics    m_g;
    private RenderTool  m_renderTool;
}
