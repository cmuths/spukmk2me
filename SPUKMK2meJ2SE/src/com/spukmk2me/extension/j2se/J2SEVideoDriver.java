package com.spukmk2me.extension.j2se;

import java.io.IOException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.RenderInfo;
import com.spukmk2me.video.ICFontRenderer;
import com.spukmk2me.video.IVideoDriver;
import java.io.FileInputStream;
import java.io.InputStream;

public final class J2SEVideoDriver extends JPanel
    implements IVideoDriver, Scrollable
{
    public J2SEVideoDriver()
    {
        InitialiseDriver();
    }

    public void paint( Graphics g )
    {
        if ( g != null )
            g.drawImage( m_buffer, 0, 0, this );
    }

    public void update( Graphics g )
    {
        paint( g );
    }

    public void repaint()
    {
        paint( this.getGraphics() );
    }

    public Dimension getPreferredScrollableViewportSize()
    {
        return this.getPreferredSize();
    }

    public int getScrollableUnitIncrement( Rectangle visibleRect,
        int orientation, int direction )
    {
        int pos = ( orientation == SwingConstants.HORIZONTAL )?
            visibleRect.x : visibleRect.y;

        if ( direction < 0 )
        {
            return pos - (pos / 10) * 10;
        }
        else
            return (pos / 10 + 1) * 10 - pos;
    }

    public int getScrollableBlockIncrement( Rectangle visibleRect,
        int orientation, int direction )
    {
        if ( orientation == SwingConstants.HORIZONTAL )
        {
            return visibleRect.width - 10;
        }
        else
            return visibleRect.height - 10;
    }

    public boolean getScrollableTracksViewportWidth()
    {
        return false;
    }

    public boolean getScrollableTracksViewportHeight()
    {
        return false;
    }

    public Dimension getPreferredSize()
    {
        if ( m_buffer != null )
            return new Dimension( m_buffer.getWidth(), m_buffer.getHeight() );

        return super.getPreferredSize();
    }

    public boolean IsSupported()
    {
        return true;
    }

    public void PrepareRenderingContext()
    {
        m_buffer = new BufferedImage( 640, 480, BufferedImage.TYPE_INT_ARGB );
        m_g = m_buffer.getGraphics();
        m_fontRenderer = new J2SEFontRenderer( this );
    }

    public void CleanupRenderingContext()
    {
        m_buffer = null;
    }

    public void StartInternalClock()
    {
        m_lastTime = System.currentTimeMillis();
    }

    public void StartRendering( boolean clearScreen, int clearColor,
        long deltaMilliseconds )
    {
        m_g.setClip( m_clipX, m_clipY, m_clipWidth, m_clipHeight );

        if ( clearScreen )
        {
            m_g.setColor( new Color( clearColor ) );
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
        repaint();
    }

    public Object GetProperty( String property )
    {
        if ( property.equals( PROPERTY_GRAPHICS ) )
            return m_g;
        else
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
        return (short)this.getWidth();
    }

    public short GetScreenHeight()
    {
        return (short)this.getHeight();
    }

    public void SetOrigin( int x0, int y0 )
    {
        m_x0 = x0;
        m_y0 = y0;
    }

    public int GetOrigin()
    {
        return (m_x0 << 16) | (m_y0 & 0x0000FFFF);
    }

    public void SetClipping( int x, int y, int width, int height )
    {
        m_clipX         = x;
        m_clipY         = y;
        m_clipWidth     = width;
        m_clipHeight    = height;
        
        if ( m_g != null )
            m_g.setClip( x, y, width, height );
    }

    public long GetClipping()
    {
        return ((long)m_clipX << 48) |
            ((long)(m_clipY & 0x0000FFFF) << 32) |
            ((long)(m_clipWidth & 0x0000FFFF) << 16) |
            (long)(m_clipHeight & 0x0000FFFF);
    }
    
    public void DrawLine( int x1, int y1, int x2, int y2, int color )
    {
        m_g.setColor( new Color( color ) );
        m_g.drawLine( x1, y1, x2, y2 );
    }

    public IImageResource CreateImageResource( InputStream is, String proxyname )
        throws IOException
    {
        return new J2SEImageResource( is, proxyname );
    }
    
    public IImageResource CreateImageResource( String filename, String proxyname )
        throws IOException
    {
        return new J2SEImageResource( new FileInputStream( filename ), proxyname );
    }

    public ISubImage CreateSubImage( IImageResource imageResource,
        int x, int y, int width, int height,
        int rotationDegree, int flippingFlag, String proxyname )
    {
        return new J2SESubImage( (J2SEImageResource)imageResource,
            x, y, width, height, rotationDegree, flippingFlag, proxyname );
    }

    public ISubImage[] CreateSubImages( IImageResource imageResource,
        int width, int height, String[] proxynames )
    {
        return J2SESubImage.CreateSubImagesFromResource(
            (J2SEImageResource)imageResource, width, height,
            proxynames );
    }

    public ISubImage CreateSubImage( String filename, String proxyname ) throws IOException
    {
        J2SEImageResource res =
            new J2SEImageResource( new FileInputStream( filename ), null );
        
        return new J2SESubImage( res, (short)0, (short)0,
            res.GetWidth(), res.GetHeight(), 0, (byte)0, proxyname );
    }
    
    private void InitialiseDriver()
    {
        this.setDoubleBuffered( true );
        SetClipping( (short)0, (short)0, (short)32767, (short)32767 );
        m_renderInfo = new RenderInfo();
    }
    
    public static final String PROPERTY_GRAPHICS = "j2me_graphics";

    private static final long   MAX_TIME_PER_STEP = 100;

    private Graphics            m_g;
    private RenderInfo          m_renderInfo;
    private BufferedImage       m_buffer;
    private J2SEFontRenderer    m_fontRenderer;

    private long    m_lastTime;
    private int     m_clipX, m_clipY, m_clipWidth, m_clipHeight, m_x0, m_y0;
}
