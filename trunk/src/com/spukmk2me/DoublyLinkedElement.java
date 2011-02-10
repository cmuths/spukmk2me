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

package com.spukmk2me;

/**
 *  An simple implement of doubly-linked element.
 *  \details This class is written to implement doubly-linked lists which
 * are circular and have faked-first node.
 */
public final class DoublyLinkedElement
{
    /**
     *  Default constructor.
     */
    public DoublyLinkedElement() {}

    /**
     *  Drop the node from the list that hold it.
     */
    public void Drop()
    {
        //#ifdef __SPUKMK2ME_DEBUG
        if ( (c_prev == null) || (c_next == null) )
        {
            System.out.println( "DoublyLinkedElement - Error: Dropping an " +
                "unattatched node. Object: " + c_data.toString() );
        }
        //#endif

        c_prev.c_next = c_next;
        c_next.c_prev = c_prev;
        c_next = c_prev = null;
    }

    public Object c_data; //!< Object that hold the data.
    public DoublyLinkedElement  c_next, //!< The next node.
                                c_prev; //!< The previous node.
}
