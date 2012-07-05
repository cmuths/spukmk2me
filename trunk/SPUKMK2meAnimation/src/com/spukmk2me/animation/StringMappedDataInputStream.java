package com.spukmk2me.animation;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

import com.spukmk2me.DoublyLinkedList;

public final class StringMappedDataInputStream extends DataInputStream
{
    public StringMappedDataInputStream( InputStream is )
    {
        super( is );
        m_stringList = new DoublyLinkedList();
    }
    
    public void readStringMappingTable() throws IOException
    {
        m_nBytePerString = this.readUnsignedByte();
        
        int n = this.readInt();
        
        for ( ; n != 0; --n )
            m_stringList.push_back( this.readUTF() );
    }
    
    public String readStringID() throws IOException
    {
        int id;
        
        if ( m_nBytePerString == 1 )
            id = this.readUnsignedByte();
        else if ( m_nBytePerString == 2 )
            id = this.readUnsignedShort();
        else
            id = this.readInt();
        
        if ( id == -1 )
            return null;
        
        return (String)m_stringList.get( id );
    }
    
    private DoublyLinkedList    m_stringList;
    private int                 m_nBytePerString;
}
