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

/**
 *  Most abstract interface for a font renderer.  
 */
public interface IFontRenderer
{
    public void SetRenderTool( RenderTool renderTool );

    /**
     *  Render a string.
     *  \details This function render a character sequence from s[ offset ] to
     * s[ offset + length - 1 ]. If offset + length is greater than the
     * string's length, this function only renders to the end of string. If
     * offset is somehow greater than s.length(), or length is smaller than 0,
     * this function does nothing.\n
     *  Implements of this function must not create any new object
     * (for performance issue).
     *  @param s The string.
     *  @param offset Index for rendering to start.
     *  @param length The number of rendered characters.
     *  @param color The color of characters (8A8R8G8B format).
     *  @param size Size of characters.
     *  @param style Style for drawing. Can be combined from STYLE_BOLD,
     * STYLE_ITALIC, STYLE_UNDERLINE. Use STYLE_PLAIN for normal text.
     *  @param x X coordinate.
     *  @param y Y coordinate.
     */
    public void RenderString( char[] s, int offset, int length, int color,
        short x, short y );

    /**
     *  Alternative version of RenderString(), which takes String as input.
     *  @deprecated
     */
    public void RenderString( String s, int offset, int length, int color,
        short x, short y );

    /**
     *  Render a string.
     *  \details This function render a character sequence from s[ offset ] to
     * s[ offset + length - 1 ]. If offset + length is greater than the
     * string's length, this function only renders to the end of string. If
     * offset is somehow greater than s.length(), or length is smaller than 0,
     * this function does nothing.\n
     *  Alignment is also applied.
     *  @param s The string.
     *  @param offset Index for rendering to start.
     *  @param length The number of rendered characters.
     *  @param color The color of characters (8A8R8G8B format).
     *  @param size Size of characters.
     *  @param style Style for drawing. See RenderString() for more
     * information.
     *  @param alignment Alignment for drawing. See RenderString() for more
     * information.
     *  @param x X coordinate.
     *  @param y Y coordinate.
     *  @param width Width of displayed text, used for aligning.
     *  @param height Height of displayed text, used for aligning.
     */
    public void RenderString( char[] s, int offset, int length,
        int color, byte alignment, short x, short y,
        short width, short height );
    
    /**
     *  Alternative version of RenderString(), which takes String as input.
     *  @deprecated
     */
    public void RenderString( String s, int offset, int length,
        int color, byte alignment, short x, short y,
        short width, short height );

    public void PresetSettings( BitmapFont font, byte style );

    public static final byte STYLE_PLAIN        = 0x00;
    public static final byte STYLE_BOLD         = 0x01;
    public static final byte STYLE_ITALIC       = 0x02;
    public static final byte STYLE_UNDERLINE    = 0x04;

    public static final byte ALIGN_NONE     = 0x00;
    public static final byte ALIGN_LEFT     = 0x01;
    public static final byte ALIGN_RIGHT    = 0x02;
    public static final byte ALIGN_TOP      = 0x04;
    public static final byte ALIGN_BOTTOM   = 0x08;
    public static final byte ALIGN_CENTERX  = 0x10;
    public static final byte ALIGN_CENTERY  = 0x20;
}
