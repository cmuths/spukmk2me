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
     *  Constructor
     *  @param s The string you want to associate with.
     *  @param color The text color, in ARGB 8888 format.
     *  @param width The width of the text, used in truncating & aligning.
     *  @param height The height of the text, used in aligning.
     *  @param size The size of the text.
     *  @param style The style.
     *  @param align Alignment.
     *  @param truncate Use truncating feature or not.
     *  @param detectMode The mode for truncating detection.
     */
    public StringSceneNode( BitmapFont font, char[] s, int color,
        short width, short height, boolean truncate, byte style,
        byte alignment, byte detectMode )
    {
        SetString( s );
        Initialise( font, color, width, height, truncate, style, alignment,
            detectMode );
    }

    /**
     *  An alternative constructor, which use String as input.
     */
    public StringSceneNode( BitmapFont font, String s, int color,
        short width, short height, boolean truncate, byte style,
        byte alignment, byte detectMode )
    {
        SetString( s );
        Initialise( font, color, width, height, truncate, style, alignment,
            detectMode );
    }
    
    /**
     *  Set the string.
     *  @param s The new string to display.
     */
    public void SetString( char[] s )
    {
        if ( s == null )
        {
            m_str = m_renderedString = null;
            return;
        }

        int bufferLength = s.length;
        
        m_str = s;
        
        for ( int i = 0; i != m_str.length; ++i )
            if ( m_str[ i ] == ' ' )
                ++bufferLength;
        
        m_renderedString = new char[ bufferLength ];
        
        if ( m_detectMode == DETECTMODE_MANUAL )
        {
            System.arraycopy( m_str, 0, m_renderedString, 0, m_str.length );
            m_renderedLength = m_str.length;
        }
    }

    /**
     *  Alternative version of SetString() which takes String as input.
     *  @param s The new string to display.
     */
    public void SetString( String s )
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

    /**
     *  Set the color of displayed text.
     *  @param color The new color in ARGB 8888 format.
     */
    public void SetColor( int color )
    {
        m_color = color;
    }

    public void Render( RenderTool renderTool )
    {
        IFontRenderer fr = renderTool.c_fontRenderer;
        fr.PresetSettings( m_font, m_style );

        if ( m_truncate )
        {
            if ( m_detectMode == DETECTMODE_AUTO )
                DetectLength();
            
            fr.RenderString( m_renderedString, 0, m_renderedLength, m_color,
                m_alignment, renderTool.c_rasterX, renderTool.c_rasterY,
                m_width, m_height );
        }
        else
        {
            fr.RenderString( m_str, 0, 0x7FFFFFFF, m_color, m_alignment,
                renderTool.c_rasterX, renderTool.c_rasterY,
                m_width, m_height );
        }
    }

    // Unimplemented
    public short GetWidth()
    {
        return 0;
    }

    // Unimplemented
    public short GetHeight()
    {
        return 0;
    }
    
    /**
     *  Activate truncating feature.
     *  \details This may be misnomer, since this function detect and wrap the
     * text.
     */
    public void DetectLength()
    {
        int lastIndex       = 0, nextIndex = 0;
        int remainedWidth   = m_width, nextWidth;
        int strLength       = m_str.length;
        int i;

        m_renderedLength = 0;

        while ( lastIndex != strLength )
        {
            //++lastIndex;
            //nextIndex = m_str.indexOf( ' ', lastIndex ) + 1;
            while ( nextIndex != m_str.length )
            {
                if ( m_str[ nextIndex++ ] == ' ' )
                    break;
            }

            //if ( nextIndex == m_str.length ) // Didn't find anything
            //    nextIndex = strLength;
                
            nextWidth = m_font.GetStringWidth(
                m_str, lastIndex, nextIndex - lastIndex, m_style );

            if ( remainedWidth < nextWidth )
            {
                //m_renderedString += "\n" +
                //    m_str.substring( lastIndex, nextIndex );
                m_renderedString[ m_renderedLength++ ] = '\n';

                for ( i = lastIndex; i != nextIndex; ++i )
                    m_renderedString[ m_renderedLength++ ] = m_str[ i ];

                if ( m_width + remainedWidth < nextWidth )
                {
                    //m_renderedString += "\n";
                    m_renderedString[ m_renderedLength++ ] = '\n';
                    remainedWidth = m_width;
                }
                else
                    remainedWidth = m_width - nextWidth;
            }
            else
            {
                //m_renderedString +=
                //    m_str.substring( lastIndex, nextIndex );
                for ( i = lastIndex; i != nextIndex; ++i )
                    m_renderedString[ m_renderedLength++ ] = m_str[ i ];

                remainedWidth -= nextWidth;
            }            

            lastIndex = nextIndex;
        }
    }

    private void Initialise( BitmapFont font, int color, short width,
        short height, boolean truncate, byte style, byte alignment,
        byte detectMode )
    {
        m_color         = color;
        m_font          = font;
        m_style         = style;
        m_alignment     = alignment;
        m_width         = width;
        m_height        = height;
        m_truncate      = truncate;
        m_detectMode    = detectMode;
    }

    //!< Automatically detect and wrap the text each time it's rendered.
    public static final byte DETECTMODE_AUTO       = 1;

    //!< The user manually call DetectLength() to do wrapping.
    public static final byte DETECTMODE_MANUAL     = 2;
    
    private BitmapFont m_font;
    private char[]  m_str, m_renderedString;
    private int     m_color, m_renderedLength;
    private short   m_width, m_height;
    private boolean m_truncate;
    private byte    m_style, m_alignment, m_detectMode;
}
