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

package com.spukmk2me.optional.font;

import com.spukmk2me.video.ICFont;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

//#ifdef __SPUKMK2ME_DEBUG
import com.spukmk2me.debug.SPUKMK2meException;
//#endif

/**
 *  One-bit resolution font; save memory, but has moderate rendering overhead.
 *  \details Properties of bitmap font consist of five bytes: the first four
 * bytes is the alpha, red, green, blue channel of text's color,
 * respectively. The fifth byte is the "style" of the text. See the constants
 * for more information.\n
 *  @note Current implement only allow fonts with the maximum sum of character
 * width and italic stride value is 32 pixels.
 */
public final class BitmapFont extends ICFont
{
    public BitmapFont( String filename ) throws IOException
    {
        LoadFontFromFile( filename );
    }

    public byte GetRenderDataType()
    {
        return RDT_INTARRAY;
    }

    public boolean IsSupported( char ch )
    {
        if ( (ch >= ' ') && (ch < 127) )
            return true;

        int ich = (int)ch;

        for ( int i = m_extraCharMap.length - 1; i != -1; --i )
        {
            if ( m_extraCharMap[ i ] == ich )
                return true;
        }

        return false;
    }

    public int GetCharWidth( char ch )
    {
        //#ifdef __SPUKMK2ME_DEBUG
        if ( !IsSupported( ch ) )
        {
            new SPUKMK2meException( "No such character stored in this font:" +
                ch ).printStackTrace();
            return -1;
        }
        //#endif

        return (( ch == ' ' )? m_space : m_charWidth[ GetIndex( ch ) ]) +
            m_additionalCharWidth;
    }

    public int GetLineHeight()
    {
        return m_height;
    }

    public int GetSpaceBetweenCharacters()
    {
        return m_charDistance;
    }

    public Object GetBitmapData( char ch )
    {
        PreprocessCharacterData( GetIndex( ch ) );

        if ( (m_style & STYLE_BOLD) == 0 )
            ExportPlainCharacterData( GetIndex( ch ) );
        else
            ExportBoldCharacterData( GetIndex( ch ) );

        if ( (m_style & STYLE_UNDERLINE) != 0 )
            DrawUnderline( GetCharWidth( ch ) );

        return m_buffer;
    }

    public int GetBitmapDataDimension()
    {
        return (m_bufferWidth << 16) | (m_height & 0x0000FFFF);
    }

    // Caching hasn't been implemented.
    public void CacheCharacter( char ch ) {}

    public void PresetProperties( byte[] properties )
    {
        m_color =   properties[ 0 ] << 24   |
                    ((properties[ 1 ] << 16) & 0x00FF0000) |
                    ((properties[ 2 ] << 8) & 0x0000FF00) |
                    (properties[ 3 ] & 0x000000FF);
        m_style =   properties[ 4 ];

        m_additionalCharWidth   = 0;

        int masked  = m_style & (STYLE_BOLD | STYLE_ITALIC);

        if ( masked != 0 )
        {
            ++m_additionalCharWidth;

            if ( masked == (STYLE_BOLD | STYLE_ITALIC) )
                ++m_additionalCharWidth;
        }
    }

    /**
     *  Create a byte array represents properties for BitmapFont.
     *  \details The arrays are newly created, so don't overuse it.
     *  @param color Color of the text, in ARGB8888 format.
     *  @param style Style of the text, see the constants for more information.
     *  @return An array represents properties for BitmapFont
     */
    public static byte[] CreateProperties( int color, byte style )
    {
        byte[] returnedArray = new byte[ 5 ];

        returnedArray[ 0 ]  = (byte)((color >> 24) & 0x000000FF);
        returnedArray[ 1 ]  = (byte)((color >> 16) & 0x000000FF);
        returnedArray[ 2 ]  = (byte)((color >> 8) & 0x000000FF);
        returnedArray[ 3 ]  = (byte)(color & 0x000000FF);
        returnedArray[ 4 ]  = style;

        return returnedArray;
    }

    private void DrawUnderline( int width )
    {
        int index = m_bufferWidth * m_yUnderline;

        for ( ; width != 0; --width )
            m_buffer[ index++ ] = m_color;
    }

    // Convert bytes to int, perform italic stride.
    private void PreprocessCharacterData( int charIndex )
    {
        int shiftRange, j, fetchedData;
        int italicStride, italicCountdown, italicStep;

        if ( (m_style & STYLE_ITALIC) == 0 )
        {
            italicStride    = 0;
            italicStep      = m_height;
        }
        else
        {
            if ( (m_height % (m_italicStride + 1)) == 0 )
                italicStep = m_height / (m_italicStride + 1);
            else
                italicStep = m_height / (m_italicStride + 1) + 1;

            italicStride = m_italicStride;
        }

        italicCountdown = italicStep;
        charIndex *= m_bytesPerChar;

        for ( int i = 0; i != m_height; )
        {
            shiftRange  = 24;
            fetchedData = 0x00000000;

            for ( j = m_bytesPerLine; j-- != 0; )
            {
                fetchedData |=
                    ((int)m_data[ charIndex++ ] & 0x000000FF) <<
                    shiftRange;
                shiftRange  -= 8;
            }

            m_preprocessedData[ i++ ] = fetchedData >> italicStride;

            if ( --italicCountdown == 0 )
            {
                --italicStride;
                italicCountdown = italicStep;
            }
        }
    }

    private void ExportPlainCharacterData( int charIndex )
    {
        int argbIndex, argbFirst, currentInt, i, j, width;

        argbFirst   = 0;
        i           = 0;
        width       = m_charWidth[ charIndex ];

        while ( i != m_height )
        {
            currentInt  = m_preprocessedData[ i++ ];
            argbIndex   = argbFirst;

            for ( j = width; j != 0; --j )
            {
                m_buffer[ argbIndex++ ] = ((currentInt & 0x80000000) == 0)?
                    0x00000000 : m_color;
                currentInt <<= 1;
            }

            argbFirst += m_bufferWidth;
        }
    }

    private void ExportBoldCharacterData( int charIndex )
    {
        int argbIndex, argbFirst, currentInt, i, j, width;
        int color;
        boolean isPrevBold;

        argbFirst   = 0;
        i           = 0;
        width       = m_charWidth[ charIndex ];

        while ( i != m_height )
        {
            currentInt  = m_preprocessedData[ i++ ];
            argbIndex   = argbFirst;
            isPrevBold  = false;

            for ( j = width; j != 0; --j )
            {
                color = ((currentInt & 0x80000000) == 0)? 0x00000000 : m_color;
                
                if ( isPrevBold )
                    m_buffer[ ++argbIndex ] = color;
                else
                {
                    m_buffer[ argbIndex++ ] = color;
                    m_buffer[ argbIndex ]   = color;
                }
                    
                isPrevBold = color != 0;
                currentInt <<= 1;
            }

            argbFirst += m_bufferWidth;
        }
    }

    // Currently this function only supports file format version 0.1
    private void LoadFontFromFile( String filename ) throws IOException
    {
        InputStream is =
            (InputStream)this.getClass().getResourceAsStream( filename );

        DataInputStream dis = new DataInputStream( is );

        // Header
        // Skip the first two bytes, and "SPUKMK2me_BITMAPFONT_0.1"
        // Assume that the bit/little endian mode is compatible
        dis.skipBytes( 26 );
        m_nChar         = dis.readUnsignedShort();
        m_width         = dis.readUnsignedShort();
        m_height        = dis.readUnsignedShort();
        m_charDistance  = dis.readUnsignedShort();
        m_space         = dis.readUnsignedShort();
        m_yUnderline    = dis.readUnsignedShort();
        m_italicStride  = dis.readUnsignedShort();
        // Skip 120 reserved bytes
        dis.skip( 120 );

        // Trunk
        if ( m_nChar > 94 )
        {
            m_extraCharMap = new int[ m_nChar - 94 ];

            int charIterator = 0;

            for ( int i = m_nChar - 94; i != 0; --i )
                m_extraCharMap[ charIterator++ ] = dis.readInt();
        }

        m_bytesPerLine = (byte)(m_width >> 3);

        if ( (m_width & 0x07) != 0 )
            ++m_bytesPerLine;

        m_charWidth = new int[ m_nChar ];

        m_data = new byte[ m_nChar * m_bytesPerLine * m_height ];
        dis.read( m_data );
        dis.close();

        FindCharactersWidth();

        m_bytesPerChar      = m_bytesPerLine * m_height;
        m_bufferWidth       = m_width + 1 + m_italicStride;
        m_buffer            = new int[ m_bufferWidth * m_height ];
        m_preprocessedData  = new int[ m_bytesPerChar ];
    }

    private void FindCharactersWidth()
    {
        int     dataIndexFirst = 0, dataIndex;
        int     j, k, bitRemain, tailBitRemain;
        int     currentWidth, currentByte, maxWidth;
        boolean stopLine;

        tailBitRemain = (byte)(m_width & 0x07);

        if ( tailBitRemain == 0 )
            tailBitRemain = 8;

        //c_charWidth[ 0 ] = (byte)(c_space - c_charDistance);
        
        for ( int i = 0; i != m_nChar; ++i )
        {
            maxWidth = 0;            

            for ( j = m_height; j != 0; --j )
            {
                bitRemain       = tailBitRemain;
                dataIndex       = dataIndexFirst + m_bytesPerLine - 1;
                stopLine        = false;
                currentWidth    = m_width;

                for ( k = m_bytesPerLine; k != 0; --k )
                {
                    currentByte     = m_data[ dataIndex ];
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

                dataIndexFirst += m_bytesPerLine;
            }

            //sSystem.out.println( maxWidth );
            m_charWidth[ i ] = maxWidth;
        }
    }

    private int GetIndex( char c )
    {
        if ( (c >= '!') && (c <= '~') )
            return c - '!';
        else
        {
            // Linear searching, is there any better solution?
            int ch = (int)c;

            for ( int i = m_extraCharMap.length - 1; i != -1; --i )
            {
                if ( m_extraCharMap[ i ] == ch )
                    return i + 94;
            }

            return -1;
        }
    }

    public static final byte STYLE_PLAIN        = 0x00;
    public static final byte STYLE_BOLD         = 0x01;
    public static final byte STYLE_ITALIC       = 0x02;
    public static final byte STYLE_UNDERLINE    = 0x04;

    private int[]   m_extraCharMap, m_charWidth, m_buffer, m_preprocessedData;
    private byte[]  m_data;
    
    private int m_nChar, m_width, m_height, m_space, m_charDistance,
                m_bytesPerLine, m_bytesPerChar, m_yUnderline, m_italicStride;
    private int m_style, m_additionalCharWidth, m_color, m_bufferWidth;
}