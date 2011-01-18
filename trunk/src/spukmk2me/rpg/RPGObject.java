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

import spukmk2me.Util;
import spukmk2me.scene.*;

/**
 *  What's RPG object?
 *  \details Many people played RPGs, and if you're new to it, RPG stands for
 * role-playing game, where you take the role of one or several peoples and try
 * to explore the world. Since RPGs have a wide range of interaction, objects
 * in RPGs are vary too. What I'm trying to describe isn't really a "real" RPG
 * object, it can only be considered as a "entity" which is usually added to
 * OBJ layer in 2D RPGs. And it only solve the problems of display order and
 * collision detection on OBJ layer.\n
 *  x, y, z, width, length and height ? They are data for displaying and
 * collision checking. Your object stands on the ground (Oxy), so let's try to
 * draw a rectangular border around it, you'll understand what x, y, width and
 * length mean. And about z and height, it's just the z coordinate of your
 * object and the height of it. Note that z are calculated as the difference in
 * altitude between the lowest part of your object and the "ground". y, length,
 * z will be used to calculate the Y coordinate for rendering. The rendering
 * order of objects will be decided by y, the higher y, the later being
 * rendered.\n
 *  Like ISceneNode, RPGObjects are maintained by doubly-linked list.\n
 *  Each RPGObject should have a representative scene node. If the object
 * really doesn't have to display any thing, just create a NullSceneNode for
 * it.
 */
public abstract class RPGObject
{
    protected RPGObject() {}

    public abstract void Affect( RPGObject object );
    public abstract void Action( int deltaTime );
    public abstract boolean AffectableBy( RPGObject object );
    public abstract boolean ReadyForRemoval();

    public final void SyncPhysicWithRendering()
    {
        c_sceneNode.c_x = Util.FPRound( c_x );
        c_sceneNode.c_y =
            (short)(Util.FPRound( c_y - c_z + c_length ) -
            c_sceneNode.GetHeight() );
    }

    /**
     *  Insert one object after another.
     *  \details The added object shouldn't be in any list, and the predecessor
     * object must be in a list. This function doesn't affect collision list
     * order.
     *  @param object Object that is going to be added.
     *  @param predecessor The object that supposed to lie right before the
     * added object.
     */

    public static void InsertAfter( RPGObject object, RPGObject predecessor )
    {
        object.c_prev = predecessor;
        object.c_next = predecessor.c_next;
        predecessor.c_next.c_prev   = object;
        predecessor.c_next          = object;
    }

    public final void Drop()
    {
        //#ifdef __SPUKMK2ME_DEBUG
        if ( (c_prev == null) || (c_next == null) )
        {
            System.out.println( "RPGObject dropping - Error: Dropping an " +
                "unattached object." );
        }

        if ( (c_colPrev == null) || (c_colNext == null) )
        {
            System.out.println( "RPGObject collision dropping - Error: " +
                "Dropping an unattached object." );
        }
        //#endif

        c_prev.c_next   = c_next;
        c_next.c_prev   = c_prev;
        c_prev = c_next = null;

        c_colPrev.c_colNext = c_colNext;
        c_colNext.c_colPrev = c_colPrev;
        c_colNext = c_colPrev = null;

        c_sceneNode.Drop();
    }

    public RPGObject    c_next, c_prev, c_colPrev, c_colNext;
    public ISceneNode   c_sceneNode;

    // All dimensional data are fixed-point.
    public int      c_x, c_y, c_z, c_width, c_length, c_height;
}
