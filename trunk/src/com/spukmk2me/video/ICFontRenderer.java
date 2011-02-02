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

package com.spukmk2me.video;

/**
 *  Interface for character font renderer.
 */
public interface ICFontRenderer
{
    public void SetRenderTool( RenderTool renderTool );

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
     *  @param x X coordinate.
     *  @param y Y coordinate.
     */
    public void RenderString( char[] s, ICFont font, int offset, int length,
        int x, int y );
    
    /**
     *  Alternative version of RenderString(), which takes String as input.
     */
    public void RenderString( String s, ICFont font, int offset, int length,
        int x, int y );
}
