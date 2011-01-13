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

package spukmk2me.video;

import java.io.IOException;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

/**
 *  Interface for video driver, an essential part of SPUKMK2ME engine.
 *  \details IVideoDriver is the common interface for video drivers, which take
 * responsibility about rendering.\n
 *  A video driver can be usable before the call to PrepareRenderingContext().
 * The constructor of any video driver must only do platform-independent work
 * to ensure the stability of SPUKMK2ME engine. After construction, user can
 * check if the driver is supported on the current platform or not.
 *  A video driver must create it's propriety font renderer for rendering.\n\n
 *  Question: Why J2ME need JSRs thing?
 *  Answer: Because J2ME can't do any thing without called-by-many-people
 * unsafe native code. It doesn't allow you to surpass any thing. How about
 * "faster than native code" JIT compiler? Hah, on my PC it needs 40MB of RAM
 * just to output "Hello world!", quiet a show.
 *  @see spukmk2me.SPUKMK2Device
 *  @see spukmk2me.scene.SceneManager
 *  @see spukmk2me.video.IFontRenderer
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
     *  thread, such as OpenGL. This function is used for later initialization.
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

    public RenderTool GetRenderTool();

    /**
     *  Get the displayable to display.
     *  \details Since MIDP API relies on Displayable, all things are put to
     * screen must be Displayable. Of course you must put a Displayable object
     * to your screen via javax.microedition.lcdui.Display.setCurrent().
     *  @return The representative MIDP Displayable object.
     */
    public Displayable GetMIDPDisplayable();

    /**
     *  Get the Graphics for manual drawing.
     *  \details SPUKMK2ME engine isn't completed, even if it's completed,
     * sometimes we must do some manual drawing.
     *  @return The Graphics object to draw to the screen.
     */
    public Graphics GetMIDPGraphics();

    /**
     *  Get the font renderer associated with this video driver.
     *  @return The font renderer for text rendering.
     */
    public IFontRenderer GetFontRenderer();

    /**
     *  Get the width/height of rendering surface.
     *  @return The width/height of rendering surface. The two higher bytes
     * indicates the width, and the other twos hold the value of the height.
     */
    public int GetScreenWidthHeight();

    /**
     *  Set the origin of rendering coordinates.
     *  @param x0 The absolute X coordinate of the origin.
     *  @param y0 The absolute Y coordinate of the origin.
     */
    public void SetOrigin( short x0, short y0 );

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
     *  @param x2
     *  @param y2
     */
    public void SetClipping( short x1, short y1, short x2, short y2 );

    /**
     *  Get the clipping area.
     *  x1, y1, x2, y2 (as the parameters of SetClipping()) will be put in
     * a long. Counting from the MSB to the LSB, each two bytes contain one
     * element: x1, y1, x2, y2, respectively.
     */
    public long GetClipping();

    /**
     *  Load an image.
     *  \details Depend on the API used, the content of any image must be
     * suitable to that rendering API and vary from an API to another API.
     * Example: OpenGL ES developers may to use 16-bit RGB/RGBA/BGRA or
     * 8-bit indexed format instead of the familiar 32-bit ARGB format. So the
     * video driver should be the one who decide how images are stored.
     *  @param filename The image filename.
     *  @return The image.
     *  @throws IOException If the loading sequence fails due to I/O problem.
     */
    public IImage LoadImage( String filename ) throws IOException;

    /**
     *  Load a set of image.
     *  \details The data of returned images are acquired by loading a large
     * image, and then divide it into smaller pieces. If the divisions have
     * remainders, then redundant parts are ignored; e.g you have a 55 x 47
     * image, and you want to divide them into 10 x 10 pieces, so you have
     * 5 x 4 = 20 pieces, the last 5 columns and the last 7 lines are not
     * extracted.\n
     *  Why you should load images via video driver? See LoadImage() for more
     * details.
     *  @param filename The image filename.
     *  @param width The width of an image.
     *  @param height The height of an image.
     *  @return
     *  @throws IOException If the loading sequence fails due to I/O problem.
     *  @see LoadImage()
     */
    public IImage[] LoadImages( String filename, short width, short height )
        throws IOException;

    //#ifdef __SPUKMK2ME_MIDP
//#     public static final byte VIDEODRIVER_MIDP   = 1; //!< MIDP driver.
    //#endif

    //#ifdef __SPUKMK2ME_GLES
//#     public static final byte VIDEODRIVER_GLES   = 2; //!< OpenGL ES driver.
    //#endif
}