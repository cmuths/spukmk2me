package com.spukmk2me;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */

public final class DoublyLinkedList
{
    private final class Element
    {
        public Element() {}

        public void drop()
        {
            /* $if SPUKMK2ME_DEBUG$
            if ( (c_prev == null) || (c_next == null) )
            {
                Logger.Trace( "Dropping unassigned element." );
            }
            $endif$ */

            c_prev.c_next = c_next;
            c_next.c_prev = c_prev;
            c_next = c_prev = null;
        }

        public Object   c_data;
        public Element  c_next, c_prev;
    }

    public final class Iterator
    {
        private Iterator( Element element )
        {
            m_element = element;
        }

        public void fwrd()
        {
            m_element = m_element.c_next;
        }

        public void back()
        {
            m_element = m_element.c_prev;
        }

        public Object data()
        {
            return m_element.c_data;
        }

        private Iterator _clone()
        {
            return new Iterator( m_element );
        }

        public boolean equals( Iterator iterator )
        {
            return m_element.c_data == iterator.data();
        }
        
        private Element m_element;
    }

    public DoublyLinkedList()
    {
        m_ffe = new Element();
        m_ffi = new Iterator( m_ffe );
        clear();
    }

    public void clear()
    {
        m_ffe.c_next = m_ffe.c_prev = m_ffe;
        m_ffe.c_data = null;
        m_nElement  = 0;
    }

    public void push_back( Object data )
    {
        Element newElement = new Element();

        newElement.c_data = data;
        newElement.c_prev = m_ffe.c_prev;
        newElement.c_next = m_ffe;
        m_ffe.c_prev.c_next = newElement;
        m_ffe.c_prev        = newElement;
        ++m_nElement;
    }

    public void push_front( Object data )
    {
        Element newElement = new Element();

        newElement.c_data   = data;
        newElement.c_prev   = m_ffe;
        newElement.c_next   = m_ffe.c_next;
        m_ffe.c_next.c_prev = newElement;
        m_ffe.c_next        = newElement;
        ++m_nElement;
    }

    public Object pop_back()
    {
        if ( m_nElement == 0 )
            return null;

        Element removedElement = m_ffe.c_prev;

        removedElement.drop();
        --m_nElement;

        return removedElement.c_data;
    }

    public Object pop_front()
    {
        if ( m_nElement == 0 )
            return null;

        Element removedElement = m_ffe.c_next;

        removedElement.drop();
        --m_nElement;

        return removedElement.c_data;
    }

    public Object peek_back()
    {
        return m_ffe.c_prev.c_data;
    }

    public Object peek_front()
    {
        return m_ffe.c_next.c_data;
    }

    public Iterator first()
    {
        if ( m_nElement == 0 )
            return m_ffi;

        Iterator ret = m_ffi._clone();

        ret.fwrd();
        return ret;
    }

    public Iterator last()
    {
        if ( m_nElement == 0 )
            return m_ffi;

        Iterator ret = m_ffi._clone();

        ret.back();
        return ret;
    }

    public Iterator end()
    {
        return m_ffi;
    }

    public int length()
    {
        return m_nElement;
    }

    public Object get( int index )
    {
        if ( (index < 0) || (index >= m_nElement) )
            return null;

        Iterator i = first();

        while ( index-- != 0 )
            i.fwrd();

        return i.data();
    }

    public void erase( int index )
    {
        /* $if SPUKMK2ME_DEBUG$
        if ( (index < 0) || (index >= m_nElement) )
            Logger.Trace( "Index is out of bounds." );
        $endif$ */

        Iterator i = first();

        while ( index-- != 0 )
            i.fwrd();

        i.m_element.drop();
        --m_nElement;
    }

    public void erase( Object data )
    {
        Iterator i = first();

        for ( ; !i.equals( m_ffi ); i.fwrd() )
        {
            if ( i.data() == data )
            {
                i.m_element.drop();
                --m_nElement;
				return;
            }
        }
    }

    private final Iterator  m_ffi;
    private Element         m_ffe;
    private int             m_nElement;
}
