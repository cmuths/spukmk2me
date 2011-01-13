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

/**
 *  Animators is things that control scene nodes.
 *  \details You active the animators by adding them to scene nodes (that's
 * why IAnimator inherit ISceneNode). To be precise, add animators to the
 * animator root node in your scene manager. Animation controls are implemented
 * in Render() method.
 */
public abstract class IAnimator extends ISceneNode
{
    public final short GetWidth()
    {
        return 0;
    }

    public final short GetHeight()
    {
        return 0;
    }

    /**
     *  Tell whether the animator is animating.
     *  @return true if it's animating, otherwise return false.
     */
    public abstract boolean IsAnimating();

    /**
     *  Get the current controlled scene node.
     *  @return The controlled scene node.
     */
    public abstract ISceneNode GetSceneNode();
}
