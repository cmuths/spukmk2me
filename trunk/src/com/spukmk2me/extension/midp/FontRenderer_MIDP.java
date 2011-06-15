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
import com.spukmk2me.video.IVideoDriver;

/**
 *  A font renderer which use MIDP API as the main rendering API.
 */
public final class FontRenderer_MIDP extends ICFontRenderer
{    
    public FontRenderer_MIDP( IVideoDriver vdriver )
    {
        super( vdriver );
        m_g = (Graphics)vdriver.GetMIDPGraphics();
    }

    protected void RenderCharacter( ICFont font, char character )
    {
        m_g.drawRGB( (int[])font.GetBitmapData( character ), 0,
            font.GetBitmapDataDimension() >> 16,
            m_rasterX, m_rasterY,
            font.GetCharWidth( character ), font.GetLineHeight(), true );
    }

    private Graphics m_g;
}
