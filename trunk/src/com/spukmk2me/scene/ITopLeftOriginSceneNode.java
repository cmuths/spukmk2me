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

/**
 *  Abstract scene node that has AABB top-left coordinate (0, 0).
 *  \details Because ISceneNode now has GetAABBX() and GetAABBY(), sub-classes
 * must implement them, resulting extra code (and byte-code too). Since
 * there are many scene nodes which take (0, 0) as their AABB top-left
 * coordinate, this class was written with the hope reducing the size
 * of SPUKMK2me.
 */
public abstract class ITopLeftOriginSceneNode extends ISceneNode
{
    public final short GetAABBX()
    {
        return 0;
    }

    public final short GetAABBY()
    {
        return 0;
    }
}
