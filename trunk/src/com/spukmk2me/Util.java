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

package com.spukmk2me;

import java.io.DataInputStream;
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
        m_randSeed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
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
        m_randSeed = (m_randSeed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        return (int)(m_randSeed >>> (48 - bits));
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
     *  Read a ANSI-encoded line of text.
     *  \details Can only deal with end-of-line character CR/LF and LF/CR.
     *  Users must provide their own buffer because of allocation/deallocation
     *  on Java is unreliable.
     */
    public static String ReadLine( DataInputStream is, byte[] buffer )
        throws IOException
    {
        int     i = 0;
        byte    c = 0;

        while ( (is.available() > 0) && (i < buffer.length) )
        {
            c = is.readByte();

            if ( (c == 13) || (c == 10) )
                break;

            buffer[ i++ ] = c;
        }

        is.readByte();
        return new String( buffer, 0, i );
    }

    /**
     *  Read the incoming word in the input stream.
     *  \details End-of-line characters, EOF, spaces and TABs are removed
     *  from the word.
     */
    public static String ReadWord( DataInputStream is, byte[] buffer )
        throws IOException
    {
        if ( is.available() == 0 )
            return "";

        int     i = 0;
        byte    c = 0;

        do
        {
            c = is.readByte();
        } while ( ((c == 32) || (c == 8) || (c == 10) || (c == 13)) &&
                (is.available() > 0) );

        buffer[ i++ ] = c;

        while ( (is.available() > 0) && (i < buffer.length) )
        {
            c = is.readByte();

            if ( (c == 13) || (c == 10) || (c == 32) || (c == 8) )
                break;

            buffer[ i++ ] = c;
        }

        return new String( buffer, 0, i );
    }

    /**
     *  Multiply two fixed-point number.
     *  @param fnumber1 The first number.
     *  @param fnumber2 The seconds number.
     *  @return The product.
     */
    public static int FPMul( int fnumber1, int fnumber2 )
    {
        /*boolean positive = ((fnumber1 ^ fnumber2) & 0x80000000) == 0 ;

        fnumber1 = Math.abs( fnumber1 );
        fnumber2 = Math.abs( fnumber2 );

        int product     = fnumber1 * (fnumber2 >> 16);
        int fnumber_dec = fnumber2 & 0x0000FFFF;
        int i = 1;

        while ( i != 17 )
        {
            fnumber_dec <<= 1;

            if ( (fnumber_dec & 0x00010000) != 0 )
                product += fnumber1 >> i;

            ++i;
        }

        return ( positive )? product : -product;*/
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
        /*boolean positive = ((fnumber1 ^ fnumber2) & 0x80000000) == 0;

        fnumber1 = Math.abs( fnumber1 );
        fnumber2 = Math.abs( fnumber2 );

        int quotient   = fnumber1 / fnumber2 << 16;
        int remainder  = fnumber1 % fnumber2;
        int i = 15;

        while ( i != -1 )
        {
            remainder <<= 1;
            quotient   |= remainder / fnumber2 << i--;
            remainder  %= fnumber2;
        }

        return ( positive )? quotient : -quotient;*/
        return (int)(((long)fnumber1 << 32) / (long)fnumber2 >> 16);
    }

    /**
     *  Round a fixed-point real number.
     *  @param fnumber Number to be rounded.
     *  @return Rounded result.
     */
    public static short FPRound( int fnumber )
    {
        boolean positive = (fnumber & 0x80000000) == 0;

        if ( !positive )
            fnumber = -fnumber;

        int result = fnumber >> 16;

        if ( positive )
        {
            if ( (fnumber & 0x00008000) != 0 )
                ++result;
        }
        
        return (short)(( positive )? result : -result);
    }

    /**
     *  Calculate the sine of angle. Unimplemented
     *  @param angle Measured in radius, it's a fixed-point number.
     *  @return Sine of angle.
     */
    public static int FPSin( int angle )
    {
        return 0;
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

    private static long m_randSeed;
}
