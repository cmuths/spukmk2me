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
        m_g = (Graphics)renderTool.c_rAPI;
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

        int rasterX = x;

        char character;

        while ( offset != border )
        {
            character = s[ offset ];

            if ( character == '\n' )
            {
                rasterX = x;
                y      += font.GetLineHeight();
            }
            else
            {
                if ( character != ' ' ) // Spaces should be invisible, right?
                {
                    m_g.drawRGB( (int[])font.GetBitmapData( character ), 0,
                        font.GetBitmapDataDimension() >> 16,
                        rasterX, y,
                        font.GetCharWidth( character ), font.GetLineHeight(),
                        true );

                    rasterX += font.GetSpaceBetweenCharacters() +
                        font.GetCharWidth( character );
                }
                else
                    rasterX += font.GetCharWidth( ' ' );
            }

            ++offset;
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

    private Graphics m_g;
}
