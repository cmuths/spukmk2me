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

package com.spukmk2me.video;

import java.io.InputStream;
import java.io.IOException;

/**
 *  Interface for video driver, an essential part of SPUKMK2ME engine.
 *  \details IVideoDriver is the common interface for video drivers, which take
 * responsibility for rendering.\n
 *  A video driver can be unusable before the call to
 * PrepareRenderingContext(). The constructor of any video driver must only do
 * platform-independent work to ensure the stability of SPUKMK2me engine. After
 * construction, user can check if the driver is supported on the current
 * platform or not.
 *  A video driver must create it's suitable font renderer for rendering.\n\n
 *
 *  Question: Why J2ME need JSRs thing?
 *  Answer: Because J2ME can't do any thing without called-by-many-people
 * unsafe native code. It doesn't allow you to surpass any thing. How about
 * "faster than native code" JIT compiler? Hah, on my PC it needs 40MB of RAM
 * just to output "Hello world!", quiet a show.
 *  @see com.spukmk2me.SPUKMK2Device
 *  @see com.spukmk2me.scene.SceneManager
 *  @see com.spukmk2me.video.IFontRenderer
 */
public interface IVideoDriver
{
    /**
     *  Check if the driver is supported on current platform.
     *  \details MIDP driver is supposed to run on a wide range of devices,
     * but OpenGL ES driver isn't. You should check the driver before using it.
     *  @return The possibility of using the driver on the current platform.
     */
    public boolean IsSupported();
    
    /**
     *  For initialization.
     *  \details Some API require drawing and initializing must be in the same
     * thread, such as OpenGL. This function is used for later
     * initialization.\n
     *  Notice that this function <b>should</b> be called after the driver has
     * somehow acquired the "visible" rendering surface, especially that
     * applies for MIDP driver. If you're using class from
     * com.spukmk2me.gameflow, you need not to worry about this.
     */
    public void PrepareRenderingContext();

    /**
     *  Do the cleanup.
     *  \details This function is opposed to PrepareRenderingContext().
     */
    public void CleanupRenderingContext();

    /**
     *  Restart the internal clock.
     *  \details Rendering sometime requires time-based computing, like drawing
     * animated sprite. User should call this function as near game routine as
     * possible.\n
     *  This function is ignored if timing mode is set to manual.
     */
    public void StartInternalClock();

    /**
     *  Start the rendering sequence.
     *  \details If you called this function, you shouldn't do any game status
     * calculation until the next FinishRendering() call.
     *  @param clearScreen Clear the screen or not?
     *  @param clearColor The color tho clear screen, ignored if clearScreen is
     * false.
     *  @param deltaMilliseconds Tell the driver the amount of time has passed
     * since the last rendering sequence. Passing negative value to this
     * argument causes the driver to use its own timing mechanism.
     */
    public void StartRendering( boolean clearScreen, int clearColor,
        long deltaMilliseconds );

    /**
     *  Tell the driver that the current rendering sequence is finished.
     *  \details The driver may do things like: swap the buffer, re-calculate
     * timing, etc...
     */
    public void FinishRendering();

    /**
     *  Get a platform-dependent property.
     *  \details More details can be seen in each dependent implement
     * if IVideoDriver.
     *  @param propertyName Name of property.
     *  @return Object represent property name.
     */
    public Object GetProperty( String propertyName );

    /**
     *  Get the font renderer associated with this video driver.
     *  @return The font renderer for text rendering.
     */
    public ICFontRenderer GetFontRenderer();

    public RenderInfo GetRenderInfo();

    /**
     *  Get the width of screen, in pixel.
     *  @return Width of screen.
     */
    public short GetScreenWidth();
    
    /**
     *  Get the height of screen, in pixel.
     *  @return Width of screen.
     */
    public short GetScreenHeight();

    /**
     *  Set the origin of rendering coordinates.
     *  @param x0 The absolute X coordinate of the origin.
     *  @param y0 The absolute Y coordinate of the origin.
     */
    public void SetOrigin( int x0, int y0 );

    /**
     *  Get the current absolute position of origin.
     *  @return An integer that holds the position. The highest 16 bits
     * hold X value, and the lowest 16 bits hold Y value.
     */
    public int GetOrigin();

    /**
     *  Setup the clipping area.
     *  @param x1
     *  @param y1
     *  @param width
     *  @param height
     */
    public void SetClipping( int x, int y, int width, int height );

    /**
     *  Get the clipping area.
     *  x, y, width, height (as the parameters of SetClipping()) will be put in
     * a long. Counting from the MSB to the LSB, each two bytes contain one
     * element: x, y, width, height, respectively.
     */
    public long GetClipping();
    
    /**
     *  Draw a line.
     *  @param x1
     *  @param y1
     *  @param x2
     *  @param y2
     */
    public void DrawLine( int x1, int y1, int x2, int y2, int color );

    /**
     *  Create an image resources from an input stream.
     *  @param inputStream Image file name.
     *  @param proxyname Leave null if you don't plan on using ResourceSet.
     *  @return Image resource created from file.
     *  @throws IOException If loading sequence got error.
     */
    public IImageResource CreateImageResource( InputStream inputStream, String proxyname )
        throws IOException;
    
    /**
     *  Create an image resources from file.
     *  @param filename Image file name.
     *  @param proxyname Leave null if you don't plan on using ResourceSet.
     *  @return Image resource created from file.
     *  @throws IOException If loading sequence got error.
     *  @deprecated This function is deprecated due to the ambiguity of
     * "filename". 
     */
    public IImageResource CreateImageResource( String filename, String proxyname )
        throws IOException;
    
    /**
     *  Create a regional sub image from image resource.
     *  @param imgResource Resource data.
     *  @param x Crop x.
     *  @param y Crop y
     *  @param width Crop width.
     *  @param height Crop height
     *  @param rotationDegree Rotation degree (16-16 fixed point)
     *  @param flippingFlag Check the constants (flipping is applied before rotation).
     *  @param proxyname Leave null if you don't plan on using ResourceSet.
     *  @return Created sub image.
     */
    public ISubImage CreateSubImage( IImageResource imgResource,
        int x, int y, int width, int height,
        int rotationDegree, int flippingFlag, String proxyname );
    
    /**
     *  Create a sub image from file name.
     *  \details The result can be created equally by creating image resource
     * explicitly and then create a sub image from it with full size and no
     * rotation/flipping.
     *  @param filename File name to load image.
     *  @param proxyname Leave null if you don't plan on using ResourceSet.
     *  @return Created sub image.
     *  @throws IOException If loading sequence got error.
     *  @deprecated This function is deprecated due to the ambiguity of
     * "filename". 
     */
    public ISubImage CreateSubImage( String filename, String proxyname )
        throws IOException;
    
    /**
     *  Create batch of sub images from an image resources.
     *  \details This function will create batch of sub images which have
     * the same width and height, no rotation or flipping is applied. Sub
     * images will be taken from top to down, left to right.
     *  @param imgResource Resource to create batch if sub images.
     *  @param width Width of each sub image.
     *  @param height Height of each sub image.
     *  @param proxynames Leave null if you don't plan on using ResourceSet.
     */
    public ISubImage[] CreateSubImages( IImageResource imgResource,
        int width, int height, String[] proxynames );
    
    public static final byte VIDEODRIVER_MIDP   = 1; //!< MIDP driver.
    public static final byte VIDEODRIVER_GLES   = 2; //!< OpenGL ES driver.

    public static final byte FLIP_HORIZONTAL    = 0x01;
    public static final byte FLIP_VERTICAL      = 0x02;
}
