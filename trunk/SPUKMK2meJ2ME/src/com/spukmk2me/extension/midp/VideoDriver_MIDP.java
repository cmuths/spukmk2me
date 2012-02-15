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

import java.io.InputStream;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */

import com.spukmk2me.io.IFileSystem;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.ICFontRenderer;
import com.spukmk2me.video.RenderInfo;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.IImageResource;

/**
 *  A So-so implement of IVideoDriver, which use MIDP rendering API to render.
 *  \details This is only a simple implement (as the name SI), but it should be
 * used on low-configuration devices.
 */
public final class VideoDriver_MIDP extends InputMonitor_MIDP
    implements IVideoDriver
{
    public VideoDriver_MIDP( IFileSystem fileSystem )
    {
        m_fileSystem = fileSystem;
        this.setFullScreenMode( true );
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
        m_g.setClip( x, y, width, height );
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
        /* $if SPUKMK2ME_DEBUG$ */
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
        /* $endif$ */

        if ( clearScreen )
        {
            m_g.setColor( clearColor );
            m_g.fillRect( 0, 0, getWidth(), getHeight() );
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

        m_renderInfo.c_passedTime = ((int)passedTime << 16) / 1000;
        m_lastTime += passedTime;
    }

    public void FinishRendering()
    {
        /* $if SPUKMK2ME_DEBUG$ */
        m_g.setColor( 0xFF7F7F7F );
        m_g.drawChars( m_fpsString, 0, 10, m_x0, m_y0,
            Graphics.TOP | Graphics.LEFT );
        /* $endif$ */

        this.flushGraphics();        
    }
    
    /**
     *  Get a MIDP-dependent property.
     *  @param propertyName Can be PROPERTY_MIDPGRAPHICS or
     * PROPERTY_MIDPDISPLAYABLE.
     *  @return A Graphics object or a Displayable object (in
     * javax.microedition.lcdui package).
     */
    public Object GetProperty( String propertyName )
    {
        if ( propertyName.equals( PROPERTY_MIDPGRAPHICS ) )
            return m_g;
        else if ( propertyName.equals( PROPERTY_MIDPDISPLAYABLE ) )
            return this;
        
        return null;
    }
    
    /**
     *  Specific function made for MIDP driver internal use.
     *  @return A Graphics object used for drawing.
     */
    Graphics GetMIDPGraphics()
    {
        return m_g;
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
        return (short)this.getWidth();
    }

    public short GetScreenHeight()
    {
        return (short)this.getHeight();
    }

    public void PrepareRenderingContext()
    {
        // Small hack to wait for the canvas to go to fullscreen mode.
        // See the constructor.
        while ( !this.isShown() )
            Thread.yield();

        // Initialise
        m_lastTime  = 0;

        m_g             = this.getGraphics();
        m_fontRenderer  = new FontRenderer_MIDP( this );
        m_renderInfo    = new RenderInfo();
        
        m_x0 = m_y0 = m_renderInfo.c_rasterX = m_renderInfo.c_rasterY = 0;

        /* $if SPUKMK2ME_DEBUG$ */
        m_fpsLastTime   = 0;
        m_fpsString     = new char[]{
            'F', 'P', 'S', ':', ' ', '-', '-', '.', '-', '-' };
        /* $endif$ */

        SetClipping( (short)0, (short)0,
            (short)this.getWidth(), (short)this.getHeight() );
    }
    
    public void CleanupRenderingContext() {}
    
    public IImageResource CreateImageResource( InputStream inputStream )
        throws IOException
    {
        return new MIDPImageResource( inputStream );
    }

    public IImageResource CreateImageResource( String filename )
        throws IOException
    {
        /* $if SPUKMK2ME_DEBUG$ */
        if ( filename.charAt( 0 ) != '/' )
            Logger.Trace( "Warning: image filename does not start with '." );

        Logger.Log( "Loading image: " + filename + "..." );
        /* $endif$ */
        
        InputStream is =
            m_fileSystem.OpenFile( filename, IFileSystem.LOCATION_AUTODETECT );
        IImageResource res = new MIDPImageResource( is );
        
        is.close();
        
        /* $if SPUKMK2ME_DEBUG$ */
        Logger.Log( " Loaded.\n" );
        /* $endif$ */
        
        return res;
    }

    public ISubImage CreateSubImage( IImageResource imgResource,
        short x, short y, short width, short height,
        int rotationDegree, byte flippingFlag )
    {
        /* $if SPUKMK2ME_DEBUG$ */
        if ( rotationDegree % 0x0005A0000 != 0 ) // Not divisible by 90
        {
            Logger.Trace( "MIDP driver currently does not support " +
                "non-90-degree rotation, rotation degree will be reset to 0.");
            rotationDegree = 0;

            try
            {
                MIDPImageResource test = (MIDPImageResource)imgResource;
            } catch ( ClassCastException e ) {
                Logger.Trace(
                    "This isn't image resource created by MIDP driver" );
            }
        }
        /* $endif$ */

        return new MIDPSubImage( (MIDPImageResource)imgResource,
            x, y, width, height, rotationDegree, flippingFlag );
    }
    
    public ISubImage CreateSubImage( String filename ) throws IOException
    {
        MIDPImageResource resource =
            (MIDPImageResource)CreateImageResource( filename );
        
        return new MIDPSubImage( resource,
            (short)0, (short)0, resource.GetWidth(), resource.GetHeight(),
            0, (byte)0 );
    }

    public ISubImage[] CreateSubImages( IImageResource imgResource,
        short width, short height )
    {
        /* $if SPUKMK2ME_DEBUG$ */
        try
        {
            MIDPImageResource test = (MIDPImageResource)imgResource;
        } catch ( ClassCastException e ) {
            Logger.Trace( "This isn't image resource created by MIDP driver" );
        }
        /* $endif$ */
        return MIDPSubImage.CreateSubImagesFromResource(
            (MIDPImageResource)imgResource, width, height );
    }
    
    public static final String PROPERTY_MIDPGRAPHICS    = "midp_graphics";
    public static final String PROPERTY_MIDPDISPLAYABLE = "midp_displayable";

    private static final long   MAX_TIME_PER_STEP = 100;

    private IFileSystem     m_fileSystem;
    private Graphics        m_g;
    private ICFontRenderer  m_fontRenderer;
    private RenderInfo      m_renderInfo;
    private long            m_lastTime;
    private short           m_x0, m_y0, m_clipX, m_clipY,
                            m_clipWidth, m_clipHeight;

    /* $if SPUKMK2ME_DEBUG$ */
    private char[]      m_fpsString;
    private long        m_fpsLastTime;
    /* $endif$ */
}