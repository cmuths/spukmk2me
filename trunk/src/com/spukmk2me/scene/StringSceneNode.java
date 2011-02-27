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

package com.spukmk2me.scene;

import com.spukmk2me.video.ICFontRenderer;
import com.spukmk2me.video.RenderTool;
import com.spukmk2me.video.ICFont;

/**
 *  A scene node that display string.
 */
public final class StringSceneNode extends ISceneNode
{
    /**
     *  Constructor.
     */
    public StringSceneNode() {}

    /**
     *  Setup the string.
     *  \details If no truncating and special alignment in one direction is
     * used, StringSceneNode will recalculate the width and the height of this
     * node according to the visible width and visible height of the rendered
     * string.
     *  @param s The string you want to associate with.
     *  @param properties Properties of rendered text. Depends on used font.
     *  @param alignment Alignment.
     *  @param width The width of the text, used in truncating & aligning.
     *  @param height The height of the text, used in aligning.
     *  @param truncate Use truncating feature or not.
     */
    public void SetupString( ICFont font, char[] s, byte[] properties,
        int alignment, short width, short height, boolean truncate )
    {
        boolean duplicate =
            truncate || ((alignment & ADVANCED_ALIGNMENT) != 0);

        SetString( s, duplicate );
        Initialise( font, properties, alignment, width, height,
            truncate, duplicate );
    }

    /**
     *  An alternative setup function, which use String as input.
     */
    public void SetupString( ICFont font, String s, byte[] properties,
        int alignment, short width, short height, boolean truncate )
    {
        boolean duplicate =
            truncate || ((alignment & ADVANCED_ALIGNMENT) != 0);
        
        SetString( s, duplicate );
        Initialise( font, properties, alignment, width, height,
            truncate, duplicate );
    }
    
    /**
     *  Set the string.
     *  @param s The new string to display.
     */
    private void SetString( char[] s, boolean duplicate )
    {
        m_str = s;
        
        if ( duplicate )
            m_renderedString = new char[ s.length ];
        else
            m_renderedString = null;
    }

    /**
     *  Alternative version of SetString() which takes String as input.
     *  @param s The new string to display.
     */
    private void SetString( String s, boolean duplicate )
    {
        SetString( (s == null)? (char[])null : s.toCharArray(), duplicate );
    }

    /**
     *  Get the current string.
     *  @return The current string.
     */
    public char[] GetString()
    {
        return m_str;
    }

    public void Render( RenderTool renderTool )
    {
        if ( m_str == null )
            return;

        ICFontRenderer fr = renderTool.c_fontRenderer;
        m_font.PresetProperties( m_properties );

        if ( m_renderedString == null ) // Unprocessed string
        {
            fr.RenderString( m_str, m_font, 0, m_str.length,
                renderTool.c_rasterX, renderTool.c_rasterY );
        }
        else
        {
            short y = (short)(renderTool.c_rasterY + m_startY);

            for ( int i = 0; i != m_nLine; ++i )
            {
                fr.RenderString( m_renderedString, m_font,
                    m_lineStartIndexes[ i ],
                    m_lineStartIndexes[ i + 1 ] - m_lineStartIndexes[ i ],
                    (short)(renderTool.c_rasterX + m_lineStartX[ i ]),
                    y );
                y += m_font.GetLineHeight();
            }
        }
    }

    public short GetWidth()
    {
        return m_width;
    }

    public short GetHeight()
    {
        return m_height;
    }
    
    private void PreprocessString()
    {
        if ( !m_truncate )
        {
            m_nLine = 1;

            for ( int i = 0; i != m_str.length; ++i )
            {
                if ( m_str[ i ] == '\n' )
                    ++m_nLine;
            }

            m_renderedLength = m_str.length;
            System.arraycopy( m_str, 0, m_renderedString, 0, m_str.length );
        }
        else
        {
            int     lastIndex       = 0, nextIndex = 0;
            int     remainedWidth   = m_width, nextWidth;
            int     i, copyBorder;
            // Ambiguous separator is a separator which was temporary space,
            // but it can become endline character if the next word doesn't fit
            // the width.
            boolean hasAmbiguousSeparator = false;
            char    c;

            m_renderedLength    = 0;
            m_nLine             = 1;
            m_font.PresetProperties( m_properties );

            while ( lastIndex != m_str.length )
            {
                //++lastIndex;
                //nextIndex = m_str.indexOf( ' ', lastIndex ) + 1;
                while ( nextIndex != m_str.length )
                {
                    c = m_str[ nextIndex++ ];

                    if ( (c == ' ') || (c == '\n') )
                        break;
                }

                copyBorder = ( nextIndex == m_str.length )? nextIndex :
                    nextIndex - 1;

                //if ( nextIndex == m_str.length ) // Didn't find anything
                //    nextIndex = strLength;
                
                nextWidth = m_font.GetStringWidth(
                    m_str, lastIndex, copyBorder - lastIndex );

                if ( remainedWidth < nextWidth )
                {
                    //m_renderedString += "\n" +
                    //    m_str.substring( lastIndex, nextIndex );
                    if ( hasAmbiguousSeparator )
                        --m_renderedLength;

                    if ( remainedWidth < m_width )
                    {
                        m_renderedString[ m_renderedLength++ ] = '\n';
                        ++m_nLine;
                    }

                    for ( i = lastIndex; i != copyBorder; ++i )
                        m_renderedString[ m_renderedLength++ ] = m_str[ i ];

                    if ( m_width < nextWidth )
                    {
                        //m_renderedString += "\n";
                        if ( nextIndex != m_str.length )
                        {
                            m_renderedString[ m_renderedLength++ ] = '\n';
                            ++m_nLine;
                            remainedWidth = m_width;
                            hasAmbiguousSeparator = false;
                        }
                    }
                    else
                    {
                        remainedWidth = m_width - nextWidth;
                        hasAmbiguousSeparator = (copyBorder != m_str.length);
                    }
                }
                else
                {
                    //m_renderedString +=
                    //    m_str.substring( lastIndex, nextIndex );
                    for ( i = lastIndex; i != copyBorder; ++i )
                        m_renderedString[ m_renderedLength++ ] = m_str[ i ];
                    
                    remainedWidth -= nextWidth;
                    hasAmbiguousSeparator = (copyBorder != m_str.length);
                }

                if ( hasAmbiguousSeparator )
                {
                    m_renderedString[ m_renderedLength++ ] = ' ';
                    remainedWidth -= m_font.GetCharWidth( ' ' );
                }

                lastIndex = nextIndex;
            }
        }

        int[]   widthOfLines;
        int     lineIterator = 1;

        widthOfLines        = new int[ m_nLine ];
        m_lineStartIndexes  = new int[ m_nLine + 1 ];
        m_lineStartIndexes[ 0 ] = 0;

        m_font.PresetProperties( m_properties );

        for ( int i = 0; i != m_renderedLength; ++i )
        {
            if ( m_renderedString[ i ] == '\n' )
            {
                m_lineStartIndexes[ lineIterator ] = i + 1;
                widthOfLines[ lineIterator - 1 ] = m_font.GetStringWidth(
                    m_renderedString, m_lineStartIndexes[ lineIterator - 1 ],
                    i - m_lineStartIndexes[ lineIterator - 1 ] );
                ++lineIterator;
            }
        }

        m_lineStartIndexes[ m_nLine ] = m_renderedLength;
        widthOfLines[ m_nLine - 1 ] = m_font.GetStringWidth(
            m_renderedString, m_lineStartIndexes[ m_nLine - 1 ],
            m_renderedLength - m_lineStartIndexes[ m_nLine - 1 ] );
        
        m_lineStartX = new int[ m_nLine ];
        
        if ( (m_alignment & ALIGN_CENTERX) != 0 )
        {
            for ( int i = 0; i != m_nLine; ++i )
                m_lineStartX[ i ] = (m_width - widthOfLines[ i ]) / 2;
        }
        else if ( (m_alignment & ALIGN_RIGHT) != 0 )
        {
            for ( int i = 0; i != m_nLine; ++i )
                m_lineStartX[ i ] = m_width - widthOfLines[ i ];
        }
        else
        {
            m_width = 0;
            
            for ( int i = 0; i != m_nLine; ++i )
            {
                m_lineStartX[ i ] = 0;
                
                if ( widthOfLines[ i ] > m_width )
                    m_width = (short)widthOfLines[ i ];
            }
        }

        if ( (m_alignment & ALIGN_CENTERY) != 0 )
            m_startY = (m_height - m_nLine * m_font.GetLineHeight()) / 2;
        else if ( (m_alignment & ALIGN_BOTTOM) != 0 )
            m_startY = m_height - m_nLine * m_font.GetLineHeight();
        else
        {
            m_startY = 0;
            m_height = (short)(m_nLine * m_font.GetLineHeight());
        }
    }

    private void Initialise( ICFont font, byte[] properties,
        int alignment, short width, short height, boolean truncate,
        boolean duplicate )
    {
        m_font          = font;
        m_properties    = properties;
        m_alignment     = (byte)alignment;
        m_width         = width;
        m_height        = height;
        m_truncate      = truncate;

        if ( duplicate )
            PreprocessString();
        else
        {
            m_width = (short)m_font.GetStringWidth( m_str, 0, m_str.length );

            int nLine = 1;

            for ( int i = 0; i != m_str.length; ++i )
            {
                if ( m_str[ i ] == '\n' )
                    ++nLine;
            }

            m_height = (short)(nLine * m_font.GetLineHeight());
        }
    }

    public static final byte ALIGN_NONE     = 0x00;
    public static final byte ALIGN_LEFT     = 0x01;
    public static final byte ALIGN_RIGHT    = 0x02;
    public static final byte ALIGN_TOP      = 0x04;
    public static final byte ALIGN_BOTTOM   = 0x08;
    public static final byte ALIGN_CENTERX  = 0x10;
    public static final byte ALIGN_CENTERY  = 0x20;

    private static final byte ADVANCED_ALIGNMENT =
        (byte)ALIGN_BOTTOM | ALIGN_CENTERY | ALIGN_RIGHT | ALIGN_CENTERX;

    private ICFont m_font;

    private char[]  m_str, m_renderedString;
    private int[]   m_lineStartIndexes, m_lineStartX;
    private byte[]  m_properties;
    private int     m_renderedLength, m_nLine, m_startY;
    private short   m_width, m_height;
    private boolean m_truncate;
    private byte    m_alignment;
}
