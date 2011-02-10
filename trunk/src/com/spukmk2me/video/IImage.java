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
 *  A abstract way to describe the image type used by SPUKMK2ME.
 */
public interface IImage
{
    /**
     *  Return the implemented image type, used by video drivers.
     *  @see com.spukmk2me.video.IVideoDriver
     */
    public void Render( RenderTool renderTool );

    /**
     *  Get the width of image.
     *  @return The width of image.
     */
    public short GetWidth();

    /**
     *  Get the height of image.
     *  @return The height of image.
     */
    public short GetHeight();
}
