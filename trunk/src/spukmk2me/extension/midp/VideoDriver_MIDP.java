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
import javax.microedition.lcdui.*;

import spukmk2me.video.IVideoDriver;
import spukmk2me.video.BitmapFont;
import spukmk2me.video.IFontRenderer;
import spukmk2me.video.RenderTool;
import spukmk2me.video.IImage;

/**
 *  So-so implement of IVideoDriver, which use MIDP rendering API to render.
 *  \details This is only a simple implement (as the name SI), but it should be
 * used on low-configuration devices.
 */
public final class VideoDriver_MIDP extends InputMonitor_MIDP
    implements IVideoDriver
{
    public VideoDriver_MIDP()
    {
    }

    public void paint( Graphics g ) {}

    public boolean IsSupported()
    {
        // Let's assume that every mobile phones supports MIDP 2.0.
        return true;
    }

    public void SetOrigin( short x0, short y0 )
    {
        m_x0 = x0;
        m_y0 = y0;
    }

    public int GetOrigin()
    {
        return ( m_x0 << 16 ) | m_y0;
    }

    public void SetClipping( short x, short y, short width, short height )
    {
        m_clipX         = x;
        m_clipY         = y;
        m_clipWidth     = width;
        m_clipHeight    = height;
        ((Graphics)m_renderTool.c_rAPI).setClip( x, y, width, height );
    }

    public long GetClipping()
    {
        return ((long)m_clipX << 48) |
            ((long)(m_clipY & 0x0000FFFF) << 32) |
            ((long)(m_clipWidth & 0x0000FFFF) << 16) |
            (long)(m_clipHeight & 0x0000FFFF);
    }

    public void StartInternalClock()
    {
        m_lastTime = System.currentTimeMillis();        
    }    

    public void StartRendering( boolean clearScreen, int clearColor,
        long deltaMilliseconds )
    {
        m_renderTool.c_scrWidth     = (short)this.getWidth();
        m_renderTool.c_scrHeight    = (short)this.getHeight();

        if ( clearScreen )
        {
            Graphics g = (Graphics)m_renderTool.c_rAPI;
            g.setColor( clearColor );
            g.fillRect(
                0, 0, m_renderTool.c_scrWidth, m_renderTool.c_scrHeight );
        }

        long passedTime;

        if ( deltaMilliseconds < 0 )
        {
            passedTime = System.currentTimeMillis() - m_lastTime;

            if ( passedTime < 0 )
                 passedTime = 0;
            else if ( passedTime > MAX_TIME_PER_STEP )
                passedTime = MAX_TIME_PER_STEP;
        }
        else
            passedTime = deltaMilliseconds;

        m_renderTool.c_timePassed = ((int)passedTime << 16) / 1000;
        m_lastTime += passedTime;
    }

    public void FinishRendering()
    {
        //#ifdef __SPUKMK2ME_DEBUG
        long fpsDeltaTime = System.currentTimeMillis() - m_fpsLastTime;

        m_fpsLastTime = System.currentTimeMillis();

        if ( fpsDeltaTime <= 0 )
            m_fpsString[ 5 ] = m_fpsString[ 6 ] =
                m_fpsString[ 8 ] = m_fpsString[ 9 ] = '-';
        else
        {
            fpsDeltaTime = 100000 / fpsDeltaTime;            
            m_fpsString[ 5 ] = (char)(fpsDeltaTime / 1000 + 0x30);
            m_fpsString[ 6 ] = (char)(fpsDeltaTime % 1000 / 100 + 0x30);
            fpsDeltaTime %= 100;
            m_fpsString[ 8 ] = (char)(fpsDeltaTime / 10 + 0x30);
            m_fpsString[ 9 ] = (char)(fpsDeltaTime % 10 + 0x30);
        }
        
        ((Graphics)m_renderTool.c_rAPI).setColor( 0xFF7F7F7F );
        ((Graphics)m_renderTool.c_rAPI).drawChars( m_fpsString, 0, 10, 0, 0,
            Graphics.TOP | Graphics.LEFT );
        //#endif

        this.flushGraphics();        
    }

    public RenderTool GetRenderTool()
    {
        return m_renderTool;
    }

    public Displayable GetMIDPDisplayable()
    {
        return this;
    }

    public Graphics GetMIDPGraphics()
    {
        return (Graphics)m_renderTool.c_rAPI;
    }

    public IFontRenderer GetFontRenderer()
    {
        return m_fontRenderer;
    }

    public int GetScreenWidthHeight()
    {
        return (this.getWidth() << 16) | (this.getHeight() & 0x0000FFFF);
    }

    public short GetScreenHeight()
    {
        return (short)this.getHeight();
    }

    public void PrepareRenderingContext()
    {
        // Initialise
        m_lastTime  = 0;

        m_fontRenderer = new FontRenderer_MIDP();
        m_renderTool = new RenderTool( this );
        m_renderTool.c_rAPI         = this.getGraphics();
        m_renderTool.c_fontRenderer = m_fontRenderer;
        m_renderTool.c_vdriverID    = VIDEODRIVER_MIDP;

        m_fontRenderer.SetRenderTool( m_renderTool );

        m_x0 = m_y0 = 0;
        this.setFullScreenMode( true );

        //#ifdef __SPUKMK2ME_DEBUG
        m_fpsLastTime   = 0;
        m_fpsString     = new char[]{
            'F', 'P', 'S', ':', ' ', '-', '-', '.', '-', '-' };
        //#endif

        SetClipping( (short)0, (short)0,
            (short)this.getWidth(), (short)this.getHeight() );
        KeyCodeAdapter.StartFilteringKeyCodes( this );
    }
    
    public void CleanupRenderingContext() {}

    public IImage LoadImage( String filename ) throws IOException
    {
        return new MIDPImage( filename );
    }

    public IImage[] LoadImages( String filename, short width, short height )
        throws IOException
    {
        return MIDPImage.LoadImagesFromFile( filename, width, height );
    }    
    
    private static final long   MAX_TIME_PER_STEP = 100;

    private IFontRenderer   m_fontRenderer;
    private RenderTool      m_renderTool;
    private long            m_lastTime;
    private short           m_x0, m_y0, m_clipX, m_clipY, m_clipWidth,
                            m_clipHeight;

    //#ifdef __SPUKMK2ME_DEBUG
    private char[]      m_fpsString;
    private long        m_fpsLastTime;
    //#endif
}
