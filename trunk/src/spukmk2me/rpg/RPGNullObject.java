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

package spukmk2me.rpg;

import spukmk2me.scene.NullSceneNode;

public final class RPGNullObject extends RPGObject
{
    public RPGNullObject()
    {
        c_sceneNode = new NullSceneNode();
    }

    public void Affect( RPGObject object ) {}
    public void Action( int deltaTime ) {}

    public boolean AffectableBy( RPGObject object )
    {
        return false;
    }

    public boolean ReadyForRemoval()
    {
        return false;
    }

    public void LastWish() {}
}
