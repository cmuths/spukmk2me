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

package com.spukmk2me.extension.gles;

import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import com.spukmk2me.Util;
import com.spukmk2me.input.IInputMonitor;
import com.spukmk2me.video.RenderTool;
import com.spukmk2me.video.IImage;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IFontRenderer;
import com.spukmk2me.extension.midp.FontRenderer_MIDP;
import com.spukmk2me.extension.midp.InputMonitor_MIDP;

/**
 *  An implement of IVideoDriver which use OpenGL ES 1.0 and EGL 1.0 to render.
 *  \details Right now it won't output any thing to your screen other than some
 * error messages. So don't email asking me about how to use it.
 */
public class VideoDriver_GLES10 extends InputMonitor_MIDP
    implements IVideoDriver, IInputMonitor
{
    public VideoDriver_GLES10()
    {        
        m_fontRenderer  = new FontRenderer_MIDP();
        m_g             = this.getGraphics();
    }

    public boolean IsSupported()
    {
        try
        {
            Class.forName( "javax.microedition.khronos.opengles.GL10" );
        } catch ( ClassNotFoundException e ) {
            //#ifdef __SPUKMK2ME_DEBUG
            System.out.println( "OpenGL ES isn't supported." );
            //#endif
            return false;
        }

        //#ifdef __SPUKMK2ME_DEBUG
        System.out.println( "OpenGL ES driver is supported." );
        //#endif

        return true;
    }

    public int GetScreenWidthHeight()
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
        return (m_x0 << 16) | (m_y0 & 0x0000FFFF);
    }

    public void SetClipping( short x1, short y1, short x2, short y2 )
    {
        m_gl.glScissor( x1, y2, x2 - x1 + 1, y2 - y1 + 1);
    }

    public long GetClipping()
    {
        return 0;
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
        m_renderTool.c_fontRenderer = m_fontRenderer;

        m_egl.eglWaitNative( EGL10.EGL_CORE_NATIVE_ENGINE, m_g );

        if ( clearScreen )
        {
            m_gl.glClearColorx(
                Util.FPDiv( clearColor & 0x00FF0000, 0x01000000 ),
                Util.FPDiv( (clearColor << 8) & 0x00FF0000, 0x01000000 ),
                Util.FPDiv( (clearColor << 16) & 0x00FF0000, 0x01000000 ),
                Util.FPDiv( (clearColor >> 8) & 0x00FF0000, 0x01000000 ) );
            m_gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
        }

        m_gl.glFinish();

        long passedTime;

        if ( m_autoTiming )
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
        m_gl.glFinish();
        System.out.println( m_gl.glGetError() );
        m_egl.eglWaitGL();
        this.flushGraphics();
    }    

    public Displayable GetMIDPDisplayable()
    {
        return this;
    }

    public Graphics GetMIDPGraphics()
    {
        return this.getGraphics();
    }

    public IFontRenderer GetFontRenderer()
    {
        return m_fontRenderer;
    }

    public short GetScreenWidth()
    {
        return (short)this.getWidth();
    }

    public short GetScreenHeight()
    {
        return (short)this.getHeight();
    }

    public void ConfigureDriver( boolean autoTiming )
    {
        m_autoTiming = autoTiming;
    }

    public RenderTool GetRenderTool()
    {
        return m_renderTool;
    }

    public void PrepareRenderingContext()
    {
        m_egl = (EGL10)EGLContext.getEGL();

        m_egl.eglWaitNative( EGL10.EGL_CORE_NATIVE_ENGINE, m_g );
        
        // GL context configuration
        int[] attributes = {
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_ALPHA_SIZE, 8,
            EGL10.EGL_DEPTH_SIZE, EGL10.EGL_DONT_CARE,
            EGL10.EGL_STENCIL_SIZE, EGL10.EGL_DONT_CARE,
            EGL10.EGL_NONE };
        EGLConfig[] config      = new EGLConfig[ 1 ];
        m_eglDisplay = m_egl.eglGetDisplay( EGL10.EGL_DEFAULT_DISPLAY );
        int[] numConfig = new int[ 1 ];
        
        m_egl.eglGetConfigs( m_eglDisplay, null, 0, numConfig );
        m_egl.eglChooseConfig( m_eglDisplay, attributes, config, 1,
            numConfig );

        m_eglContext = m_egl.eglCreateContext( m_eglDisplay, config[ 0 ],
            EGL10.EGL_NO_CONTEXT, null );
        m_eglSurface = m_egl.eglCreateWindowSurface( m_eglDisplay, config[ 0 ],
            this.getGraphics(), null );
        m_egl.eglMakeCurrent( m_eglDisplay, m_eglSurface, m_eglSurface,
            m_eglContext );
        System.out.println( m_egl.eglGetError() );

        // GL states configuration
        m_gl = (GL10)m_eglContext.getGL();
        m_gl.glEnable( GL10.GL_TEXTURE_2D );
        m_gl.glEnable( GL10.GL_CULL_FACE );
        m_gl.glFrontFace( GL10.GL_CCW );
        m_gl.glCullFace( GL10.GL_BACK );

        m_gl.glDisable( GL10.GL_DITHER );
        m_gl.glDisable( GL10.GL_MULTISAMPLE );

        m_gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
        m_gl.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );

        
        // GL texture configuration
        int[] texCoords = {
            0x00000000, 0x00000000,
            0x00010000, 0x00000000,
            0x00000000, 0x00010000,
            0x00010000, 0x00010000 };
        IntBuffer textureCoords =
            ByteBuffer.allocateDirect( 64 ).asIntBuffer();

        textureCoords.put( texCoords, 0, 8 );
        textureCoords.rewind();

        m_gl.glTexCoordPointer( 2, GL10.GL_FIXED, 0, textureCoords );

        m_gl.glMatrixMode( GL10.GL_MODELVIEW );
        m_gl.glLoadIdentity();
        m_gl.glMatrixMode( GL10.GL_PROJECTION );
        m_gl.glLoadIdentity();
        m_gl.glViewport( 0, 0, this.getWidth(), this.getHeight() );
        m_gl.glOrthox( 0, this.getWidth() << 16, 0, this.getHeight() << 16,
            0x00010000, 0xFFFF0000 );
        
        m_texList = new int[ 1 ];
        m_gl.glGenTextures( 1, m_texList, 0 );
        m_gl.glBindTexture( GL10.GL_TEXTURE_2D, m_texList[ 0 ] );
        m_gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
            GL10.GL_REPLACE );
        m_gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_COLOR,
            GL10.GL_RGBA );

        m_gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
            GL10.GL_NEAREST );
        m_gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
            GL10.GL_NEAREST );
        m_gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
            GL10.GL_REPEAT );
        m_gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
            GL10.GL_REPEAT );

        m_gl.glPixelStorei( GL10.GL_UNPACK_ALIGNMENT, 4 );
        m_gl.glPixelStorei( GL10.GL_PACK_ALIGNMENT, 4 );

        
        // RenderTool configuration
        m_renderTool = new RenderTool( this );
        m_renderTool.c_fontRenderer = null;
        m_renderTool.c_rAPI         = m_gl;
        m_renderTool.c_scrWidth     = (short)this.getWidth();
        m_renderTool.c_scrHeight    = (short)this.getHeight();

        // RenderTool.c_reserved1 will be used as the vertex buffer
        IntBuffer vertexBuffer =
            ByteBuffer.allocateDirect( 32 ).asIntBuffer();

        vertexBuffer.rewind();
        m_gl.glVertexPointer( 2, GL10.GL_FIXED, 0, vertexBuffer );
        m_renderTool.c_reserved1 = vertexBuffer;

        m_gl.glFinish();
        m_egl.eglWaitGL();
        this.setFullScreenMode( true );
    }

    public void CleanupRenderingContext()
    {
        m_gl.glBindTexture( GL10.GL_TEXTURE_2D, 0 );
        m_gl.glDeleteTextures( 1, m_texList, 0 );
        m_egl.eglDestroySurface( m_eglDisplay, m_eglSurface );
        m_egl.eglDestroyContext( m_eglDisplay, m_eglContext );
        m_egl.eglTerminate( m_eglDisplay );
    }

    public IImage LoadImage( String filename ) throws IOException
    {
        return new BufferedImage32( filename );
    }

    public IImage[] LoadImages( String filename, short width, short height )
        throws IOException
    {
        return BufferedImage32.LoadImagesFromFile( filename, width, height );
    }

    public IImage CreateRegionalImage( IImage srcImage,
        short x, short y, short width, short height )
    {
        //#ifdef __SPUKMK2ME_DEBUG
        try
        {
        //#endif
        return BufferedImage32.CreateRegionalImage( (BufferedImage32)srcImage,
            x, y, width, height );
        //#ifdef __SPUKMK2ME_DEBUG
        } catch ( ClassCastException e ) {
            e.printStackTrace();
        }

        return null;
        //#endif
    }


    private static final int MAX_TIME_PER_STEP  = 100;

    private RenderTool      m_renderTool;
    private IFontRenderer   m_fontRenderer;
    private int[]           m_texList; // List of texture handles.

    private EGL10       m_egl;
    private EGLDisplay  m_eglDisplay;
    private EGLContext  m_eglContext;
    private EGLSurface  m_eglSurface;
    private GL10        m_gl;
    private Graphics    m_g;
    
    private long    m_lastTime;

    private short   m_x0, m_y0;
    private boolean m_autoTiming;
}
