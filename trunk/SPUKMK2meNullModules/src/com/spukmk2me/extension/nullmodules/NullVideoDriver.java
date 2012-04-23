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

package com.spukmk2me.extension.nullmodules;

import java.io.InputStream;
import java.io.IOException;

import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.ICFontRenderer;
import com.spukmk2me.video.RenderInfo;;

public final class NullVideoDriver implements IVideoDriver
{
    public boolean IsSupported()
    {
        return true;
    }
    
    public void PrepareRenderingContext() {}

    public void CleanupRenderingContext() {}

    public void StartInternalClock() {}

    public void StartRendering( boolean clearScreen, int clearColor,
        long deltaMilliseconds ) {}

    public void FinishRendering() {}

    public Object GetProperty( String propertyName )
    {
        return null;
    }

    public ICFontRenderer GetFontRenderer()
    {
        return m_fontRenderer;
    }

    public RenderInfo GetRenderInfo()
    {
        return m_renderInfo;
    }

    public short GetScreenWidth()
    {
        return 0;
    }
    
    public short GetScreenHeight()
    {
        return 0;
    }

    public void SetOrigin( short x0, short y0 )
    {
        m_x0 = x0;
        m_y0 = y0;
    }

    public int GetOrigin()
    {
        return (m_x0 << 16) | (m_y0 & 0xFFFF0000);
    }

    public void SetClipping( short x, short y, short width, short height )
    {
        m_clipX = x;
        m_clipY = y;
        m_clipW = width;
        m_clipH = height;
    }

    public long GetClipping()
    {
        return  ((long)m_clipX << 48) |
                ((long)m_clipY & 0x000000000000FFFF << 32) |
                ((long)m_clipW & 0x000000000000FFFF << 16) |
                ((long)m_clipH & 0x000000000000FFFF);
    }

    public IImageResource CreateImageResource(
        InputStream inputStream, String proxyname )
        throws IOException
    {
        return null;
    }
    
    public IImageResource CreateImageResource(
        String filename, String proxyname )
        throws IOException
    {
        return null;
    }
    
    public ISubImage CreateSubImage( IImageResource imgResource,
        short x, short y, short width, short height,
        int rotationDegree, byte flippingFlag, String proxyname )
    {
        return null;
    }
    
    public ISubImage CreateSubImage(
        String filename, String proxyname ) throws IOException
    {
        return null;
    }
    
    public ISubImage[] CreateSubImages( IImageResource imgResource,
        short width, short height, String[] proxynames )
    {
        return null;
    }
    
    private ICFontRenderer  m_fontRenderer;
    private RenderInfo      m_renderInfo;
    private short           m_x0, m_y0, m_clipX, m_clipY, m_clipW, m_clipH;
}
