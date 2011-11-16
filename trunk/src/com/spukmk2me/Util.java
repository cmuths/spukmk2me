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

package com.spukmk2me;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 *  Provide some utility functions that are frequently used by game developer.
 *  \details Included stuff:\n
 *  - Random numbers (wrapped from standard library).\n
 *  - ReadWord/ReadLine functions used in text reading.\n
 *  - Some function for handling 16-16 fixed-point real number.
 */
public final class Util
{
    /**
     *  Initialize the random system.
     *  \details Mimics CLDC 1.1 java.util.Random.setSeed( long ).
     */
    public static void InitialiseRandomSeed( long seed )
    {
        RANDSEED = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
    }

    /**
     *  \details This is a linear congruential pseudorandom number generator,
     * as defined by D. H. Lehmer and described by Donald E. Knuth in
     * The Art of Computer Programming, Volume 2: Seminumerical Algorithms,
     * section 3.2.1.
     *  @param bits Random bits
     *  @return The next pseudorandom value from this random number generator's
     * sequence
     */
    private static int GetRandNext( int bits )
    {
        RANDSEED = (RANDSEED * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        return (int)(RANDSEED >>> (48 - bits));
    }

    /**
     *  Generate the next random number.
     *  \details Mimics CLDC 1.1 java.util.Random.nextInt( int ).
     *  @param bound The bound of generated integer.
     *  @return A pseudo-randomized number which is smaller than bound and
     * greater or equal 0.
     */
    public static int Rand( int bound )
    {        
        if ( bound <= 0 )
            return 0;

        if ( (bound & -bound) == bound ) // i.e., n is a power of 2
            return (int)((bound * (long)GetRandNext( 31 )) >> 31);

        int bits, val;
        
        do
        {
            bits    = GetRandNext( 31 );
            val     = bits % bound;
        } while (bits - val + (bound - 1) < 0);
        
        return val;
    }

    /**
     *   Read a ANSI-encoded line of text.
     *   \details Can only deal with end-of-line character CR/LF and LF/CR.
     *  Users must provide their own buffer because allocation/deallocation
     *  on Java is unreliable.\n
     *   \details User must remove any extra bytes, such as BOM in the input
     * stream before passing the stream to this function.
     *   @note Current implement requires buffer size is at least
     *  max_bytes_per_line + 2.
     *   @param is Input stream.
     *   @param buffer User-provided buffer, for avoiding mass object
     * instantiation.
     */
    public static String ReadUTF8Line( InputStream is, byte[] buffer )
        throws IOException
    {
        int     i = 2;
        byte    c = 0;
        DataInputStream dis = new DataInputStream( is );

        while ( (dis.available() > 0) && (i < buffer.length) )
        {
            c = dis.readByte();

            if ( (c == 13) || (c == 10) )
                break;

            buffer[ i++ ] = c;
        }

        // Read LF (or CR)
        if ( dis.available() != 0 )
            dis.readByte();

        buffer[ 0 ] = (byte)(i - 2 >> 8);
        buffer[ 1 ] = (byte)((i - 2) & 0x000000FF);
        return new DataInputStream( new ByteArrayInputStream( buffer, 0, i ) ).
            readUTF();
    }

    /**
     *  Read the incoming word in the input stream.
     *  \details End-of-line characters, EOF, spaces and TABs are removed
     *  from the word.
     *   \details User must remove any extra bytes, such as BOM in the input
     * stream before passing the stream to this function.
     *   @note Current implement requires buffer size is at least
     *  max_bytes_per_line + 2.
     *   @param is Input stream.
     *   @param buffer User-provided buffer, for avoiding mass object
     * instantiation.
     */
    public static String ReadUTF8Word( InputStream is, byte[] buffer )
        throws IOException
    {
        if ( is.available() == 0 )
            return "";

        int     i = 2;
        byte    c = 0;
        DataInputStream dis = new DataInputStream( is );

        do
        {
            c = dis.readByte();
        } while ( ((c == 32) || (c == 8) || (c == 10) || (c == 13)) &&
                (dis.available() > 0) );

        buffer[ i++ ] = c;

        while ( (dis.available() > 0) && (i < buffer.length) )
        {
            c = dis.readByte();

            if ( (c == 13) || (c == 10) || (c == 32) || (c == 8) )
                break;

            buffer[ i++ ] = c;
        }

        buffer[ 0 ] = (byte)(i - 2 >> 8);
        buffer[ 1 ] = (byte)((i - 2) & 0x000000FF);
        return new DataInputStream( new ByteArrayInputStream( buffer, 0, i ) ).
            readUTF();
    }

    /**
     *  Test if two rectangles intersect.
     *  @param x1 X coordinate of top-left point of the first rectangle.
     *  @param y1 Y coordinate of top-left point of the first rectangle.
     *  @param w1 Width of the first rectangle.
     *  @param h1 Height of the first rectangle.
     *  @param x2 X coordinate of top-left point of the second rectangle.
     *  @param y2 X coordinate of top-left point of the second rectangle.
     *  @param w2 Width of the second rectangle.
     *  @param h2 Height of the second rectangle.
     *  @return true if two rectangle intersect
     */
    public static boolean RectIntersect(
        short x1, short y1, short w1, short h1,
        short x2, short y2, short w2, short h2 )
    {
        return  (x1 < x2 + w2) && (y1 < y2 + h2) &&
                (x2 < x1 + w1) && (y2 < y1 + h1);
    }

    /**
     *  Multiply two fixed-point number.
     *  @param fnumber1 The first number.
     *  @param fnumber2 The seconds number.
     *  @return The product.
     */
    public static int FPMul( int fnumber1, int fnumber2 )
    {
        return (int)(((long)fnumber1 * (long)fnumber2) >> 16);
    }

    /**
     *  Divide two fixed-point number.
     *  @param fnumber1 The dividend.
     *  @param fnumber2 The divisor.
     *  @return The quotient.
     */
    public static int FPDiv( int fnumber1, int fnumber2 )
    {
        return (int)(((long)fnumber1 << 32) / (long)fnumber2 >> 16);
    }

    /**
     *  Round a fixed-point real number.
     *  @param fnumber Number to be rounded.
     *  @return Rounded result.
     */
    public static short FPRound( int fnumber )
    {
        if ( (fnumber & 0x00008000) != 0 )
            return (short)((fnumber >> 16) + 1);

        return (short)(fnumber >> 16);
    }

    /**
     *  Calculate the sine of angle x. Unimplemented
     *  @param x Measured in radiant, it's a fixed-point number.
     *  @return sin(x).
     */
    public static int FPSin( int x )
    {
        int sinx = 0, epsilon, factorial, k;
        int squaredX = FPMul( x, x );
        boolean sign = true;

        epsilon = factorial = k = 1;

        while ( epsilon != 0 )
        {
            epsilon = x / factorial;

            if ( sign )
                sinx += epsilon;
            else
                sinx -= epsilon;

            factorial *= ++k;
            factorial *= ++k;
            x = FPMul( x, squaredX );
        }

        return sinx;
    }

    /**
     *  Calculate the cosine of angle. Unimplemented
     *  @param angle Measured in radius, it's a fixed-point number.
     *  @return Cosine of angle.
     */
    public static int FPCos( int angle )
    {
        return 0;
    }

    /**
     *  Calculate the tangent of angle. Unimplemented
     *  @param angle Measured in radius, it's a fixed-point number.
     *  @return Tangent of angle.
     */
    public static int FPTan( int angle )
    {
        return 0;
    }

    /**
     *  Calculate the arcsine of angle. Unimplemented
     *  @param angle Measured in radius, it's a fixed-point number.
     *  @return Arcsine of angle.
     */
    public static int FPArcsin( int angle )
    {
        return 0;
    }

    /**
     *  Calculate the arccosine of angle. Unimplemented
     *  @param angle Measured in radius, it's a fixed-point number.
     *  @return Arccosine of angle.
     */
    public static int FPArccos( int angle )
    {
        return 0;
    }

    /**
     *  Calculate the arctan of angle. Unimplemented
     *  @param angle Measured in radius, it's a fixed-point number.
     *  @return Arctan of angle.
     */
    public static int FPArctan( int angle )
    {
        return 0;
    }

    /**
     *  Calculate the natural logarithm of x. Unimplemented.
     *  @param x A fixed-point number.
     *  @return Natural logarithm of x.
     */
    public static int FPLog( int x )
    {
        return 0;
    }

    /**
     *  Calculate n power of x. Unimplemented
     *  @param x Base number.
     *  @param n The power.
     *  @return n power of x.
     */
    public static int FPPow( int x, int n )
    {
        return 0;
    }

    public static final int PI = 0;

    private static long RANDSEED;
}
