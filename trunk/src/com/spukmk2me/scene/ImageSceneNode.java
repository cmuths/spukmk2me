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

import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IImage;

/**
 *  Scene node that display an image.
 */
public final class ImageSceneNode extends ITopLeftOriginSceneNode
{
    /**
     *  A constructor
     *  \details This constructor takes the image parameter as the displayed
     * image.
     *  @param image The image will be displayed by this node.
     */
    public ImageSceneNode( IImage image )
    {        
        m_image = image;
    }

    public void Render( IVideoDriver driver )
    {
        driver.RenderImage( m_image );
    }

    public short GetAABBWidth()
    {
        return m_image.GetWidth();
    }

    public short GetAABBHeight()
    {
        return m_image.GetHeight();
    }

    /**
     *  Get the image which is held by this node.
     *  @return The current image of this node.
     */
    public IImage GetImage()
    {
        return m_image;
    }

    /**
     *  Set new image to this node for displaying
     *  @param image New image.
     */
    public final void SetImage( IImage image )
    {
        m_image = image;
    }    
    
    private IImage m_image;
}
