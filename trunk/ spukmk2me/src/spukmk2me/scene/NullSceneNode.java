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

/**
 *  Used as a dummy or a faked-first node in the list. It's multi-purpose node.
 */
public final class NullSceneNode extends ISceneNode
{
    /**
     *  Default constructor.
     */
    public NullSceneNode()
    {        
        c_visible = false;
    }

    public final void Render( RenderTool renderTool ) {}

    public final short GetWidth()
    {
        return c_width;
    }

    public final short GetHeight()
    {
        return c_height;
    }

    public short    c_width,    //!< The width of this node.
                    c_height;   //!< The height of this node.
}
