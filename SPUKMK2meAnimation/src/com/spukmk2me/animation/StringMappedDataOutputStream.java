package com.spukmk2me.animation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

import com.spukmk2me.DoublyLinkedList;

/**
 *  Map strings into 8/16/32 bit numbers, based on the number of strings.
 *  If the number of strings is 255 or lower, strings will be mapped into 8-
 * bit numbers.
 *  If the number of strings is 65535 or lower, strings will be mapped into 16-
 * bit numbers.
 *  Otherwise, strings will be mapped into 32-bit numbers.
 */
public final class StringMappedDataOutputStream extends DataOutputStream
{
    StringMappedDataOutputStream( OutputStream os )
    {
        super( os );
        m_stringList = new DoublyLinkedList();
        m_nBytePerString = 1;
    }
    
    /**
     *  Add the strings to the mapping table
     *  @param strings Strings, separated by a space.
     */
    public void addStringsToMappingTable( String strings )
    {
        if ( strings == null )
            return;
        
        int nextSpaceIndex;
        boolean continueConverting = true;
        
        while ( continueConverting )
        {
            strings = removeBeforeAndAfterSpaces( strings );
            nextSpaceIndex = strings.indexOf( ' ' );
            
            if ( nextSpaceIndex == -1 )
            {
                nextSpaceIndex = strings.length();
                continueConverting = false;
            }
            
            addString( strings.substring( 0, nextSpaceIndex ) );
            
            if ( continueConverting )
                strings = strings.substring( nextSpaceIndex + 1 );
        }
    }
    
    public void writeStringMappingTable() throws IOException
    {
        this.writeByte( m_nBytePerString );
        this.writeInt( m_stringList.length() );
        
        DoublyLinkedList.Iterator i = m_stringList.first();
        DoublyLinkedList.Iterator e = m_stringList.end();
        
        for ( ; !i.equals( e ); i.fwrd() )
            this.writeUTF( (String)i.data() );
    }
    
    public void writeStringID( String str ) throws IOException
    {
        int index = getIndex(str);
        
        if ( m_nBytePerString == 1 )
            this.writeByte( (byte)index );
        else if ( m_nBytePerString == 2 )
            this.writeShort( (short)index );
        else
            this.writeInt( index );
    }
    
    private String removeBeforeAndAfterSpaces( String str )
    {
        int firstIndex = 0;
        int lastIndex = str.length() - 1;
        
        while ( str.charAt( firstIndex ) == ' ' )
            ++firstIndex;
        
        while ( str.charAt( lastIndex ) == ' ' )
            --lastIndex;
        
        if ( firstIndex > lastIndex )
            return null;
        
        return str.substring( firstIndex, lastIndex + 1 );
    }
    
    private void addString( String str )
    {
        DoublyLinkedList.Iterator i = m_stringList.first();
        DoublyLinkedList.Iterator e = m_stringList.end();
        
        for ( ; !i.equals( e ); i.fwrd() )
        {
            if ( str.equals( i.data() ) )
                return;
        }
        
        m_stringList.push_back( str );
        
        if ( m_stringList.length() == 256 )
            m_nBytePerString = 2;
        
        if ( m_stringList.length() == 65536 )
            m_nBytePerString = 4;
    }
    
    private int getIndex( String str )
    {
        if ( str == null )
            return -1;

        DoublyLinkedList.Iterator i = m_stringList.first();
        DoublyLinkedList.Iterator e = m_stringList.end();
        int index = 0;
        
        for ( ; !i.equals( e ); i.fwrd() )
        {
            if ( str.equals( i.data() ) )
                return index;
            
            ++index;
        }
        
        return -1;
    }
    
    private DoublyLinkedList    m_stringList;
    private int                 m_nBytePerString;
}
