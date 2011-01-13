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

import spukmk2me.video.RenderTool;
import spukmk2me.video.IImage;

/**
 *  Scene node that display an image.
 */
public final class ImageSceneNode extends ISceneNode
{
    /**
     *  A constructor
     *  \details This constructor takes the image parameter as the displayed
     * image.
     *  @param image The image will be displayed by this node.
     */
    public ImageSceneNode( IImage image )
    {        
        m_image         = image;
    }

    /**
     *  You can say it's a copy constructor.
     *  \details The displayed image will be "referenced" from the source node,
     * not duplicated to save the memory.
     *  @param node The source node.
     */
    public ImageSceneNode( ImageSceneNode node )
    {
        if ( node != null )
            m_image = node.GetImage();
        else
            m_image = null;
    }

    public final void Render( RenderTool renderTool )
    {
        // I'm searching for a way to elminate this type of callings
        m_image.Render( renderTool );
    }

    public final short GetWidth()
    {
        return m_image.c_width;
    }

    public final short GetHeight()
    {
        return m_image.c_height;
    }

    /**
     *  Get the image which is held by this node.
     *  @return The current image of this node.
     */
    public final IImage GetImage()
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
