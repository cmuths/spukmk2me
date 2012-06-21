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

public final class NamedList
{
    public NamedList()
    {
        m_objects       = new DoublyLinkedList();
        m_proxyNames    = new DoublyLinkedList();
    }
    
    /**
     *   Add a new object to the list.
     *   \details The object won't be added if there's
     *  another object with the same name in the list.
     *   @param object Object which is going to be added. null is not accepted.
     *   @param name Proxy name for this object.
     *   @return True if there's no other object with the same name.
     */
    public boolean add( Object object, String name )
    {
        if ( exist( name ) != -1 )
            return false;
        
        m_objects.push_back( object );
        m_proxyNames.push_back( name );
        return true;
    }
    
    /**
     *   Remove the object with the name in the list.
     *   \details This function does nothing if there isn't any object
     *  assigned with name. 
     *   @param name Proxy name of the object which is going to be removed.
     *   @return True if there's a object with this proxy name in the list.
     */
    public boolean remove( String name )
    {
        int index = exist( name );
        
        if ( index != -1 )
        {
            m_objects.erase( index );
            m_proxyNames.erase( index );
            return true;
        }
        
        return false;
    }
    
    /**
     *   Check if the list has already have an object with this proxy name.
     *   @param name Proxy name you want to check.
     *   @return Index of the object with this proxy name. -1 if not found.
     */
    public int exist( String name )
    {
        DoublyLinkedList.Iterator i = m_proxyNames.first();
        DoublyLinkedList.Iterator e = m_proxyNames.end();
        int index = 0;
        
        for ( ; !i.equals( e ); i.fwrd() )
        {
            if ( name.equals( i.data() ) )
                return index;
            
            ++index;
        }
        
        return -1;
    }
    
    /**
     *   Get the object assigned with this proxy name.
     *   @param name Proxy name you want to check.
     *   @return Object assigned with this name, null if not exist.
     */
    public Object get( String name )
    {
        DoublyLinkedList.Iterator i = m_proxyNames.first();
        DoublyLinkedList.Iterator e = m_proxyNames.end();
        DoublyLinkedList.Iterator objIterator = m_objects.first();
        
        for ( ; !i.equals( e ); i.fwrd() )
        {
            if ( name.equals( i.data() ) )
                return objIterator.data();
            
            objIterator.fwrd();
        }
        
        return null;
    }
    
    /**
     *   Clear the list. 
     */
    public void clear()
    {
        m_objects.clear();
        m_proxyNames.clear();
    }
    
    /**
     *  Get the length of this list.
     *  @return The length of this list.
     */
    public int length()
    {
        return m_objects.length();
    }
    
    public DoublyLinkedList.Iterator getObjectIterator()
    {
        return m_objects.first();
    }
    
    public DoublyLinkedList.Iterator getNameIterator()
    {
        return m_proxyNames.first();
    }
    
    private DoublyLinkedList m_objects, m_proxyNames;
}
