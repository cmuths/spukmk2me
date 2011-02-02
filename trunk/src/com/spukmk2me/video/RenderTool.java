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

package com.spukmk2me.video;

/**
 *  Tool for rendering.
 */
public final class RenderTool
{
    public RenderTool( IVideoDriver vdriver )
    {
        m_vdriver = vdriver;
    }

    public void SetOrigin( short x0, short y0 )
    {
        m_vdriver.SetOrigin( x0, y0 );
    }

    public int GetOrigin()
    {
        return m_vdriver.GetOrigin();
    }

    public void SetClipping( short x1, short y1, short x2, short y2 )
    {
        m_vdriver.SetClipping( x1, y1, x2, y2 );
    }

    public long GetClipping()
    {
        return m_vdriver.GetClipping();
    }

    public Object           c_rAPI; //!< Object that hold the rendering API.
    public Object           c_reserved1;    //!< For reservation.
    public Object           c_reserved2;    //!< For reservation.
    public ICFontRenderer   c_fontRenderer; //<! Tool for font rendering.

    public int      c_timePassed; //!< Time since the last rendering sequence.
    public short    c_rasterX,      //!< Current raster X, relative to screen.
                    c_rasterY,      //!< Current raster Y, relative to screen.                    
                    c_scrWidth,     //!< Current screen width.
                    c_scrHeight;    //!< Current screen height.
    public byte     c_vdriverID;    //!< ID of current video driver.

    private IVideoDriver m_vdriver;
}
