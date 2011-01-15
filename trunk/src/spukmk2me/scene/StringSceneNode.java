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

package spukmk2me.scene;

import spukmk2me.video.IFontRenderer;
import spukmk2me.video.RenderTool;
import spukmk2me.video.BitmapFont;

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
     *  @param s The string you want to associate with.
     *  @param color The text color, in ARGB 8888 format.
     *  @param width The width of the text, used in truncating & aligning.
     *  @param height The height of the text, used in aligning.
     *  @param size The size of the text.
     *  @param style The style.
     *  @param alignment Alignment.
     *  @param truncate Use truncating feature or not.
     */
    public void SetupString( BitmapFont font, char[] s, int color,
        int style, int alignment, short width, short height, boolean truncate )
    {
        SetString( s );
        Initialise( font, color, style, alignment, width, height, truncate );
    }

    /**
     *  An alternative setup function, which use String as input.
     */
    public void SetupString( BitmapFont font, String s, int color,
        int style, int alignment, short width, short height, boolean truncate )
    {
        SetString( s );
        Initialise( font, color, style, alignment, width, height, truncate );
    }
    
    /**
     *  Set the string.
     *  @param s The new string to display.
     */
    private void SetString( char[] s )
    {
        if ( s == null )
        {
            m_str = m_renderedString = null;
            return;
        }

        m_str = s;
        m_renderedString = new char[ s.length ];
    }

    /**
     *  Alternative version of SetString() which takes String as input.
     *  @param s The new string to display.
     */
    private void SetString( String s )
    {
        SetString( (s == null)? (char[])null : s.toCharArray() );
    }

    /**
     *  Get the current string.
     *  @return The current string.
     */
    public char[] GetString()
    {
        return m_str;
    }

    /**
     *  Get the color of the text.
     *  @return Current color in ARGB 8888 format.
     */
    public int GetColor()
    {
        return m_color;
    }

    public void Render( RenderTool renderTool )
    {
        if ( m_str == null )
            return;

        IFontRenderer fr = renderTool.c_fontRenderer;
        fr.PresetSettings( m_font, m_style );

        short y = (short)(renderTool.c_rasterY + m_startY);

        for ( int i = 0; i != m_nLine; ++i )
        {
            fr.RenderString( m_renderedString, m_lineStartIndexes[ i ],
                m_lineStartIndexes[ i + 1 ] - m_lineStartIndexes[ i ], m_color,
                (short)(renderTool.c_rasterX + m_lineStartX[ i ]), y );
            y += m_font.c_height;
        }
    }

    public short GetWidth()
    {
        return m_width;
    }

    // Unimplemented
    public short GetHeight()
    {
        return 0;
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
                    m_str, lastIndex, copyBorder - lastIndex, m_style );

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
                        m_renderedString[ m_renderedLength++ ] = '\n';
                        ++m_nLine;
                        remainedWidth = m_width;
                        hasAmbiguousSeparator = false;
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
                    remainedWidth -= m_font.c_space;
                }

                lastIndex = nextIndex;
            }
        }

        int[]   widthOfLines;
        int     lineIterator = 1;

        widthOfLines        = new int[ m_nLine ];
        m_lineStartIndexes  = new int[ m_nLine + 1 ];
        m_lineStartIndexes[ 0 ] = 0;

        for ( int i = 0; i != m_renderedLength; ++i )
        {
            if ( m_renderedString[ i ] == '\n' )
            {
                m_lineStartIndexes[ lineIterator ] = i + 1;
                widthOfLines[ lineIterator - 1 ] = m_font.GetStringWidth(
                    m_str, m_lineStartIndexes[ lineIterator - 1 ],
                    i - m_lineStartIndexes[ lineIterator - 1 ] - 1, m_style );
                ++lineIterator;
            }
        }

        m_lineStartIndexes[ m_nLine ] = m_renderedLength;
        widthOfLines[ m_nLine - 1 ] = m_font.GetStringWidth(
            m_str, m_lineStartIndexes[ m_nLine - 1 ],
            m_renderedLength - m_lineStartIndexes[ m_nLine - 1 ] - 1,
            m_style );

        
        m_lineStartX = new int[ m_nLine ];
        
        if ( (m_alignment & IFontRenderer.ALIGN_LEFT) != 0 )
        {
            for ( int i = 0; i != m_nLine; ++i )
                m_lineStartX[ i ] = 0;
        }
        else if ( (m_alignment & IFontRenderer.ALIGN_CENTERX) != 0 )
        {
            for ( int i = 0; i != m_nLine; ++i )
                m_lineStartX[ i ] = (m_width - widthOfLines[ i ]) / 2;
        }
        else if ( (m_alignment & IFontRenderer.ALIGN_RIGHT) != 0 )
        {
            for ( int i = 0; i != m_nLine; ++i )
                m_lineStartX[ i ] = m_width - widthOfLines[ i ];
        }

        if ( (m_alignment & IFontRenderer.ALIGN_TOP) != 0 )
            m_startY = 0;
        else if ( (m_alignment & IFontRenderer.ALIGN_CENTERY) != 0 )
            m_startY = (m_height - m_nLine * m_font.c_height) / 2;
        else if ( (m_alignment & IFontRenderer.ALIGN_BOTTOM) != 0 )
            m_startY = m_height - m_nLine * m_font.c_height;
    }

    private void Initialise( BitmapFont font, int color, int style,
        int alignment, short width, short height, boolean truncate )
    {
        m_color         = color;
        m_font          = font;
        m_style         = (byte)style;
        m_alignment     = (byte)alignment;
        m_width         = width;
        m_height        = height;
        m_truncate      = truncate;
        PreprocessString();
    }

    private BitmapFont m_font;
    private char[]  m_str, m_renderedString;
    private int[]   m_lineStartIndexes, m_lineStartX;
    private int     m_color, m_renderedLength, m_nLine, m_startY;
    private short   m_width, m_height;
    private boolean m_truncate;
    private byte    m_style, m_alignment;
}
