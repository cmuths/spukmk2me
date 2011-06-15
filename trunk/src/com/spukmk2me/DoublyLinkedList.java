package com.spukmk2me;

public final class DoublyLinkedList
{
    private final class Element
    {
        public Element() {}

        public void Drop()
        {
            //#ifdef __SPUKMK2ME_DEBUG
            if ( (c_prev == null) || (c_next == null) )
            {

            }
            //#endif

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

        public void Next()
        {
            m_element = m_element.c_next;
        }

        public void Prev()
        {
            m_element = m_element.c_prev;
        }

        public Object Get()
        {
            return m_element.c_data;
        }

        public Iterator Clone()
        {
            return new Iterator( m_element );
        }

        public boolean equals( Iterator iterator )
        {
            return m_element.c_data == iterator.Get();
        }
        
        private Element m_element;
    }

    public DoublyLinkedList()
    {
        m_ffe = new Element();
        m_ffi = new Iterator( m_ffe );
        Clear();
    }

    public void Clear()
    {
        m_ffe.c_next = m_ffe.c_prev = m_ffe;
        m_ffe.c_data = null;
        m_nElement  = 0;
    }

    public void Push_Back( Object data )
    {
        Element newElement = new Element();

        newElement.c_data = data;
        newElement.c_prev = m_ffe.c_prev;
        newElement.c_next = m_ffe;
        m_ffe.c_prev.c_next = newElement;
        m_ffe.c_prev        = newElement;
        ++m_nElement;
    }

    public Iterator Begin()
    {
        Iterator ret = m_ffi.Clone();
        return ret;
    }

    public Iterator End()
    {
        return m_ffi;
    }

    public void Remove( Iterator iterator )
    {
        iterator.m_element.Drop();
    }

    public int Length()
    {
        return m_nElement;
    }

    public Object Get( int index )
    {
        Iterator i = Begin();

        while ( index-- != 0 )
            i.Next();

        return i.Get();
    }

    private final Iterator  m_ffi;
    private Element         m_ffe;
    private int             m_nElement;
}
