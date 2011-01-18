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

import spukmk2me.scene.ISceneNode;
import spukmk2me.scene.NullSceneNode;

/**
 *  RPG world may be a little big name for this class.
 *  \details Take the scene node from RPGWorld and add to your scene manager.
 * And DO NOT interfere with this node's order of direct children .
 */
public final class RPGWorld
{
    public RPGWorld( int numberOfCollidingGroup, boolean[] collidingTable )
    {       
        m_rpgRootNode           = new NullSceneNode();
        m_fakedFirstNode        = new RPGNullObject();
        m_fakedFirstNode.c_next = m_fakedFirstNode.c_prev = m_fakedFirstNode;
        ISceneNode.AddSceneNode( m_fakedFirstNode.c_sceneNode, m_rpgRootNode);
        m_collisionDetector     = new RPGCollisionDetector(
            numberOfCollidingGroup, collidingTable );
    }

    /**
     *  Add an object to this manager.
     *  \details The object must setup its relative coordinate (relative to
     * its cell).
     *  @param object The object which is going to be added.
     *  @param collisionGroup The object's collision group.
     */
    public void AddObject( RPGObject object, int collidingGroup )
    {
        //#ifdef __SPUKMK2ME_DEBUG
        if ( (object.c_next != null) || (object.c_prev != null) )
        {
            System.out.println(
                "RPGWorld - Warning: Potentially defected object" +
                "added. Object: " + object.toString() );
        }
        //#endif

        object.c_prev = m_fakedFirstNode.c_prev;
        object.c_next = m_fakedFirstNode;
        m_fakedFirstNode.c_prev.c_next = object;
        m_fakedFirstNode.c_prev = object;

        m_collisionDetector.AddObject( object, collidingGroup );
        ISceneNode.AddSceneNode( object.c_sceneNode, m_rpgRootNode );
    }    

    private void RemoveDeadObjects()
    {
        RPGObject buffer;
        
        for ( RPGObject iterator = m_fakedFirstNode.c_next;
            iterator != m_fakedFirstNode; )
        {
            if ( iterator.ReadyForRemoval() )
            {
                buffer      = iterator;
                iterator    = iterator.c_next;
                //#ifdef __SPUKMK2ME_DEBUG
                System.out.print( "Collecting " + buffer.toString() );
                //#endif
                buffer.Drop();
                //#ifdef __SPUKMK2ME_DEBUG
                System.out.println( ". Collected" );
                //#endif
            }
            else
                iterator = iterator.c_next;
        }
    }

    private void ResortObjects()
    {
        RPGObject fIterator, bIterator; // Forward and backward iterator
        int currentY;

        // Smallest value, to support the following sorting algorithm.
        m_fakedFirstNode.c_y = 0x80000000;

        for ( fIterator = m_fakedFirstNode.c_next;
            fIterator != m_fakedFirstNode;
            fIterator = fIterator.c_next )
        {
            currentY    = fIterator.c_y;
            bIterator   = fIterator.c_prev;

            while ( (bIterator.c_y > currentY) )
                bIterator = bIterator.c_prev;

            //if ( bIterator == m_fakedFirstNode )
            //    continue;

            // Inserting sequence
            if ( bIterator.c_next != fIterator )
            {
                fIterator.c_prev.c_next = fIterator.c_next;
                fIterator.c_next.c_prev = fIterator.c_prev;
                fIterator.c_sceneNode.Drop();
                
                RPGObject.InsertAfter( fIterator, bIterator );        
                ISceneNode.InsertAfter(
                    fIterator.c_sceneNode, bIterator.c_sceneNode );
                fIterator = bIterator;
            }
        }
    }

    public void ProcessObjects( int deltaTime )
    {
        m_collisionDetector.CheckForCollision();

        RPGObject iterator;

        for ( iterator = m_fakedFirstNode.c_next; iterator != m_fakedFirstNode;
            iterator = iterator.c_next )
        {
            iterator.Action( deltaTime );
            iterator.SyncPhysicWithRendering();
        }
        
        RemoveDeadObjects();
        ResortObjects();
    }

    public void Clear()
    {
        RPGObject buffer, iterator;
        
        for ( iterator = m_fakedFirstNode; iterator != m_fakedFirstNode; )
        {
            buffer      = iterator;
            iterator    = iterator.c_next;
            buffer.Drop();
        }
    }

    public ISceneNode GetWorldSceneNode()
    {
        return m_rpgRootNode;
    }

    private RPGObject               m_fakedFirstNode;
    private RPGCollisionDetector    m_collisionDetector;
    private ISceneNode              m_rpgRootNode;
}
