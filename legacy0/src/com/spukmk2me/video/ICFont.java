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

/**
 *  Interface for fonts used in SPUKMK2me engine.
 *  \details This interface is suitable only for character-based fonts. Any
 * font with variables glyph, such as fonts that depend on sequences of
 * character, may be unable to be implemented through this interface.\n
 *  All ICFont are designed to be no thread-safe (to increase processing
 * speed).
 *  ICFont uses two-dimension buffer or IImage to represent its characters. For
 * now, due to the limit of J2ME API, only integer array and IImage should be
 * returned.\n
 *  There is an exception about space (0x20). Space is considered as a no-glyph
 * character.
 */
public abstract class ICFont
{
    /**
     *  Get the render data type of this font.
     *  \details Currently, developments on J2ME platform heavily depends on
     * MIDP API. Integer array should solves many case in font rendering, but
     * not for all. E.g. image-based font should not be converted to integer
     * array because it significantly reduces performance as well as double the
     * memory required on many mobile device. So the font should indicate which
     * type of data it's using, for the font renderer to render. Two common
     * data types are integer array and IImage.
     *  @return Render data type.
     */
    public abstract byte GetRenderDataType();

    /**
     *  Check if the specified character is supported by this font or not.
     *  @param ch Character to test.
     *  @return True if current font supports this character. Otherwise, return
     * false.
     */
    public abstract boolean IsSupported( char ch );

    /**
     *  Get the width of rendered character.
     *  @param ch Character to take the width.
     *  @return The width of character in pixels.
     */
    public abstract int GetCharWidth( char ch );

    /**
     *  Return the number of pixel to jump after one line.
     *  @return The jump-height of line.
     */
    public abstract int GetLineHeight();

    public abstract int GetSpaceBetweenCharacters();

    /**
     *  @param ch The character to render.
     *  @return The bitmap data, represented by a integer array, or an IImage,
     * whatever that match the returned value of GetRenderDataType().
     */
    public abstract Object GetBitmapData( char ch );

    /**
     *  Get the dimension of bitmap data returned by previous GetBitmapData().
     *  \details If this function is called before any call to GetBitmapData(),
     * implements can return anything.
     *  @return The width and the height of bitmap data. As usual, two higher
     * bytes is the width and two lower bytes is the height.
     */
    public abstract int GetBitmapDataDimension();

    /**
     *  Suggest font object to cache the specified character.
     *  \details Since some font require some data preprocessing before
     * rendering, caching frequently used characters should reduce the overhead
     * of data processing.
     *  @param ch Character to cache.
     */
    public abstract void CacheCharacter( char ch );

    /**
     *  Preset the properties.
     *  \details Any call to functions of this font after the call to
     * PresetProperties() will implicitly use this properties.
     * @param properties Properties to be used.
     */
    public abstract void PresetProperties( byte[] properties );

    /**
     *  Get width of string.
     *  \details If the string contains more than one line, then the longest
     * width will be returned. Lines are separated by '\n'.
     *  @param s The string you want to examine.
     *  @param offset Index for rendering to start.
     *  @param length The number of rendered characters.
     *  @return The width of rendered string.
     */
    public final int GetStringWidth( char[] s, int offset, int length )
    {
        if ( s == null )
            return 0;

        int border = offset + length;

        if ( border > s.length )
            border = s.length;

        if ( border <= offset )
            return 0;

        int     lineWidth, maxWidth;
        char    ch;
        // Small space added after each non-space character.
        boolean additionalSpace = false;

        lineWidth = maxWidth = 0;

        while ( offset != border )
        {
            ch = s[ offset ];

            if ( ch == '\n' )
            {
                lineWidth -= GetSpaceBetweenCharacters();

                if ( lineWidth > maxWidth )
                    maxWidth = lineWidth;

                lineWidth       = 0;
                additionalSpace = false;
            }
            else if ( ch != ' ' )
            {
                lineWidth += GetCharWidth( ch ) + GetSpaceBetweenCharacters();
                additionalSpace = true;
            }
            else
            {
                lineWidth += GetCharWidth( ' ' );
                
                if ( additionalSpace )
                    lineWidth -= GetSpaceBetweenCharacters();
                
                additionalSpace = false;
            }

            ++offset;
        }

        if ( additionalSpace )
            lineWidth -= GetSpaceBetweenCharacters();

        if ( lineWidth > maxWidth )
            maxWidth = lineWidth;

        return maxWidth;
    }

    /**
     *  Alternative version of GetStringWidth(), which takes String as input.
     */
    public final int GetStringWidth( String s, int offset, int length )
    {
        if ( s == null )
            return 0;

        int borderOffset    = Math.min( offset + length, s.length() );
        char[] buffer       = new char[ borderOffset - offset ];

        s.getChars( offset, borderOffset, buffer, 0 );
        return GetStringWidth( buffer, offset, length );
    }

    //!< Bitmap data is represented as an integer array (int[]).
    public static final byte RDT_INTARRAY   = 0;
    //!< Bitmap data is represented as an IImage.
    public static final byte RDT_IIMAGE     = 1;
}
