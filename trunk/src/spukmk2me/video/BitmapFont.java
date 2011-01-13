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

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *  Storage for bitmap fonts.
 *  \details This is only a storage place, all members are public so feel free
 * to access.\n
 *  The font layout still heavily depends on the mapping table.\n
 *  One more thing, "bitmap font" here means each pixel is represented by
 * a bit. This could save the memory, but obviously creates a moderate overhead
 * of rendering sequence.
 *  @see spukmk2me.video.midp.FontRenderer_MIDP
 */
public final class BitmapFont
{
    public BitmapFont( String filename ) throws IOException
    {
        LoadFontFromFile( filename );
    }

    // Currently this function only supports BitmapFont file format version 0.1
    private void LoadFontFromFile( String filename ) throws IOException
    {
        InputStream is =
            (InputStream)this.getClass().getResourceAsStream( filename );

        DataInputStream dis = new DataInputStream( is );

        // Header
        // Skip the first two bytes, and "SPUKMK2me_BITMAPFONT_0.1"
        // Assume that the bit/little endian mode is compatible
        dis.skipBytes( 26 );
        c_nChar         = dis.readUnsignedShort();
        c_width         = dis.readUnsignedShort();
        c_height        = dis.readUnsignedShort();
        c_charDistance  = dis.readUnsignedShort();
        c_space         = dis.readUnsignedShort();
        c_yUnderline    = dis.readUnsignedShort();
        c_italicStride  = dis.readUnsignedShort();
        // Skip 120 reserved bytes
        dis.skip( 120 );

        // Trunk
        if ( c_nChar > 94 )
        {
            c_extraCharMap = new int[ c_nChar - 94 ];

            int charIterator = 0;

            for ( int i = c_nChar - 94; i != 0; --i )
                c_extraCharMap[ charIterator++ ] = dis.readInt();
        }

        c_bytesPerLine = (byte)(c_width >> 3);

        if ( (c_width & 0x07) != 0 )
            ++c_bytesPerLine;

        c_charWidth = new int[ c_nChar ];

        c_data = new byte[ c_nChar * c_bytesPerLine * c_height ];
        dis.read( c_data );

        FindCharactersWidth();
    }

    /**
     *  Get with of string.
     *  @param s The string you want to examine.
     *  @param offset Index for rendering to start.
     *  @param length The number of rendered characters.
     *  @return The width of rendered string.
     */
    public int GetStringWidth( char[] s, int offset, int length, byte style )
    {
        if ( s == null )
            return 0;

        int border = offset + length;

        if ( border > s.length )
            border = s.length;

        if ( border <= offset )
            return 0;

        int     lineWidth, maxWidth, additionalCharWidth = 0;
        char    ch;

        lineWidth           = maxWidth = 0;

        if ( (style & IFontRenderer.STYLE_BOLD) != 0 )
            ++additionalCharWidth;

        if ( (style & IFontRenderer.STYLE_ITALIC) != 0 )
            additionalCharWidth += c_italicStride;

        while ( offset != border )
        {
            ch = s[ offset ];

            if ( ch == '\n' )
            {
                lineWidth -= c_charDistance;

                if ( lineWidth > maxWidth )
                    maxWidth = lineWidth;

                lineWidth = 0;
            }
            else if ( ch != ' ' )
                lineWidth += c_charWidth[ GetIndex( ch ) ] +
                    additionalCharWidth + c_charDistance;
            else
                lineWidth += c_space;

            ++offset;
        }

        lineWidth -= c_charDistance;

        if ( lineWidth > maxWidth )
            maxWidth = lineWidth;

        return maxWidth;
    }

    /**
     *  Alternative version of GetStringWidth(), which takes String as input.
     *  @deprecated
     */
    public int GetStringWidth( String s, int offset, int length, byte style )
    {
        if ( s == null )
            return 0;

        int borderOffset    = Math.min( offset + length, s.length() );
        char[] buffer       = new char[ borderOffset - offset ];

        s.getChars( offset, borderOffset, buffer, 0 );
        return GetStringWidth( buffer, offset, length, style );
    }

    /**
     *  Get the index of character on the font table.
     *  @param c The character.
     *  @return The index on the font table. Returns -1 if it isn't supported
     * by this font.
     */
    public int GetIndex( char c )
    {
        if ( (c >= '!') && (c <= '~') )
            return c - '!';
        else
        {
            // Linear searching, is there any better solution?
            int ch = (int)c;

            for ( int i = c_extraCharMap.length - 1; i != -1; --i )
            {
                if ( c_extraCharMap[ i ] == ch )
                    return i + 94;
            }

            return -1;
        }
    }

    private void FindCharactersWidth()
    {
        int     dataIndexFirst = 0, dataIndex;
        int     j, k, bitRemain, tailBitRemain;
        int     currentWidth, currentByte, maxWidth;
        boolean stopLine;

        tailBitRemain = (byte)(c_width & 0x07);

        if ( tailBitRemain == 0 )
            tailBitRemain = 8;

        //c_charWidth[ 0 ] = (byte)(c_space - c_charDistance);
        
        for ( int i = 0; i != c_nChar; ++i )
        {
            maxWidth = 0;            

            for ( j = c_height; j != 0; --j )
            {
                bitRemain       = tailBitRemain;
                dataIndex       = dataIndexFirst + c_bytesPerLine - 1;
                stopLine        = false;
                currentWidth    = c_width;

                for ( k = c_bytesPerLine; k != 0; --k )
                {
                    currentByte     = c_data[ dataIndex ];
                    currentByte   >>= (8 - bitRemain);

                    while ( bitRemain != 0 )
                    {
                        if ( (currentByte & 0x01) != 0 )
                        {
                            if ( currentWidth > maxWidth )
                                maxWidth = currentWidth;
                        
                            stopLine = true;
                            break;
                        }
                    
                        --currentWidth;
                        --bitRemain;
                        currentByte >>= 1;
                    }

                    if ( stopLine )
                        break;

                    bitRemain = 8;
                    --dataIndex;
                }

                dataIndexFirst += c_bytesPerLine;
            }

            //sSystem.out.println( maxWidth );
            c_charWidth[ i ] = maxWidth;
        }
    }

    public int[]    c_extraCharMap, //!< Mapping table for extra characters.
                    c_charWidth;    //!< The width of each character.
    public byte[]   c_data;         //!< Hold the "pixel" data.
    
    //! The number of characters supported by this font.
    public int  c_nChar,
                c_width,    //!< The common width of characters when loading.
                c_height,   //!< The common height of characters.
                c_space,    //!< Space width.
                c_charDistance, //!< Distance between character.
                c_bytesPerLine, //!< Number of bytes for one line.
                c_yUnderline,   //!< Y coordinate for underlining.
                c_italicStride; //!< Additional pixel/italic character.
}
