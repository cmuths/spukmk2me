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

import spukmk2me.DoublyLinkedElement;

/**
 *  Manager for one-time-used animator nodes.
 *  \details Still don't understand? Just image about a situation when you must
 * pop up explosion sprite each time your bullets hit enemies and you'll
 * understand. After the animation is stopped, the animators and the controlled
 * scene node will be dropped.
 */
public final class OneTimeUsedAnimatorManager
{
    /**
     *  Default constructor.
     */
    public OneTimeUsedAnimatorManager()
    {
        m_fakedFirstNode = new DoublyLinkedElement();

        m_fakedFirstNode.c_data = null;
        m_fakedFirstNode.c_next = m_fakedFirstNode.c_prev = m_fakedFirstNode;
    }

    /**
     *  Add an animator to manage.
     *  @param animator The new animator.
     */
    public void AddAnimator( IAnimator animator )
    {
        DoublyLinkedElement node = new DoublyLinkedElement();

        node.c_data = animator;
        node.c_prev = m_fakedFirstNode.c_prev;
        node.c_next = m_fakedFirstNode;
        m_fakedFirstNode.c_prev.c_next = node;
        m_fakedFirstNode.c_prev = node;
    }

    /**
     *  Check the animators for removal.
     */
    public void CheckAnimatorsForRemoval()
    {
        DoublyLinkedElement iterator, buffer;

        for ( iterator = m_fakedFirstNode.c_next;
            iterator != m_fakedFirstNode; )
        {
            if ( !((IAnimator)iterator.c_data).IsAnimating() )
            {
                buffer      = iterator;
                iterator    = iterator.c_next;
                ((IAnimator)buffer.c_data).GetSceneNode().Drop();
                ((IAnimator)buffer.c_data).Drop();
                buffer.Drop();
            }
            else
                iterator = iterator.c_next;
        }

    }

    private DoublyLinkedElement m_fakedFirstNode; //!< For circular list.
}
