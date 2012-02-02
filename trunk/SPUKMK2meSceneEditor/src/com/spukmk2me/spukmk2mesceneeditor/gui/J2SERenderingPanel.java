package com.spukmk2me.spukmk2mesceneeditor.gui;

import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JPanel;

import com.spukmk2me.Device;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.extension.j2se.J2SEVideoDriver;

public class J2SERenderingPanel extends JPanel
    implements SceneDisplaySurface, Runnable
{
    public J2SERenderingPanel()
    {
        m_mspf      = STANDARD_MSPF;
        m_x0        = STANDARD_ROOT_X;
        m_y0        = STANDARD_ROOT_Y;
        m_width     = STANDARD_WIDTH;
        m_height    = STANDARD_HEIGHT;
        setDisplayedSize( STANDARD_WIDTH, STANDARD_HEIGHT );

        m_renderingMode = RENDERINGMODE_PASSIVE;
    }

    @Override
    public void update( Graphics g )
    {
        paint( g );
    }

    @Override
    public void paint( Graphics g )
    {
        if ( m_renderingMode == RENDERINGMODE_PASSIVE )
        {
            super.paint( g );
            //g.setColor( Color.LIGHT_GRAY );
            //g.fillRect( 0, 0, this.getWidth(), this.getHeight() );

            RenderScene( 0 );
        }
    }

    ///////////////////////////////////
    //

    public void run()
    {
        long lastRenderingTime = System.currentTimeMillis();
        long waitTime, processTime;

        while ( m_renderingMode == RENDERINGMODE_ACTIVE )
        {
            processTime = System.currentTimeMillis() - lastRenderingTime;
            RenderScene( processTime );
            waitTime = m_mspf - System.currentTimeMillis() + lastRenderingTime;
            lastRenderingTime += processTime;

            if ( waitTime > 0 )
            {
                try
                {
                    Thread.sleep( waitTime );
                } catch ( InterruptedException e ) {
                }
            }
            else
                Thread.yield();
        }
    }

    ///////////////////////////////////
    //
    public void setDevice( Device device )
    {
        m_currentDevice = device;
        this.removeAll();
        this.add( (JPanel)device.GetVideoDriver() );
    }

    public void setDisplayedNode( ISceneNode node )
    {
        m_currentNode = node;
    }

    public void setDisplayedSize( int width, int height )
    {
        m_width     = width;
        m_height    = height;
        this.setSize( width, height );
        //this.setPreferredSize( new Dimension( width, height ) );
    }

    public int getDisplayedWidth()
    {
        return m_width;
    }

    public int getDisplayedHeight()
    {
        return m_height;
    }

    public void setOrigin( int x, int y )
    {
        m_x0 = x;
        m_y0 = y;
    }

    public int getOriginX()
    {
        return m_x0;
    }

    public int getOriginY()
    {
        return m_y0;
    }

    public void setRenderingMode( byte mode, long mspf )
    {
        if ( mspf > 0 )
            m_mspf = mspf;

        if ( mode != m_renderingMode )
        {
            m_renderingMode = mode;

            if ( mode == RENDERINGMODE_ACTIVE )
            {
                m_renderingThread = new Thread( this );
                m_renderingThread.start();
            }
            else
                m_renderingThread = null;
        }
    }

    protected final void RenderScene( long timePassed )
    {
        if ( m_currentNode != null )
        {
            m_currentDevice.GetVideoDriver().StartRendering(
                true, 0xFF7F7F7F, timePassed );

            if ( m_currentDevice.GetVideoDriver().
                GetProperty( J2SEVideoDriver.PROPERTY_GRAPHICS ) != null )
            {
                m_currentDevice.GetSceneManager().RenderSceneNode(
                    m_currentNode,
                    (short)-m_x0, (short)-m_y0, false );
            }
            
            Graphics g = (Graphics)m_currentDevice.GetVideoDriver().
                GetProperty( J2SEVideoDriver.PROPERTY_GRAPHICS );
            
            long boundingRect = m_currentNode.GetHierarchicalBoundingRect();
            g.setColor( Color.RED );
            g.drawRect(
                m_currentNode.c_x + (short)(boundingRect >>> 48),
                m_currentNode.c_y + (short)((boundingRect >>> 32) & 0x0000FFFF),
                (short)((boundingRect >>> 16) & 0x0000FFFF),
                (short)(boundingRect & 0x0000FFFF) );

            g.setColor( Color.BLUE );
            g.drawRect(
                m_currentNode.c_x + m_currentNode.GetAABBX(),
                m_currentNode.c_y + m_currentNode.GetAABBY(),
                m_currentNode.GetAABBWidth(),
                m_currentNode.GetAABBHeight() );

            m_currentDevice.GetVideoDriver().FinishRendering();
        }
    }

    public static final long    STANDARD_MSPF       = 33;
    public static final int     STANDARD_ROOT_X     = 0;
    public static final int     STANDARD_ROOT_Y     = 0;
    public static final int     STANDARD_WIDTH      = 800;
    public static final int     STANDARD_HEIGHT     = 600;

    protected   long    m_mspf;
    protected   byte    m_renderingMode;

    private Thread      m_renderingThread;
    private Device      m_currentDevice;
    private ISceneNode  m_currentNode;
    private int         m_width, m_height, m_x0, m_y0;
}
