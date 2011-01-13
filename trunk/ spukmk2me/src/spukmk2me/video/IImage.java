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
 *  A abstract way to describe the image type used by SPUKMK2ME.
 */
public abstract class IImage
{
    /**
     *  Return the implemented image type, used by video drivers.
     *  @see spukmk2me.video.IVideoDriver
     */
    public abstract void Render( RenderTool renderTool );

    public short    c_width;    //!< The width of image.
    public short    c_height;   //!< The height of image.
    public boolean  c_useAlpha; //!< This image uses alpha channel or not.
}
