package com.spukmk2me.optional.scene;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.spukmk2me.DoublyLinkedList;
//#ifdef __SPUKMK2ME_DEBUG
import com.spukmk2me.debug.Logger;
//#endif
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.ICFont;
import com.spukmk2me.optional.font.BitmapFont;

/**
 *  Memory-optimized resource manager, for loading only.
 */
public final class MinimizedResourceLoader
{
    public MinimizedResourceLoader( IVideoDriver vdriver )
    {
        m_vdriver           = vdriver;
        m_resourceLists     = new DoublyLinkedList[ 3 ];

        for ( int i = 0; i != 3; ++i )
            m_resourceLists[ i ]        = new DoublyLinkedList();
    }
    
    public void AddResource( Object resource, String proxyName, byte type )
    {
        //#ifdef __SPUKMK2ME_DEBUG
        if ( (type < 0) || (type > 2) )
            Logger.Log( "Unknown resource type: " + type );
        
        switch ( type )
        {
            case RT_IMAGERESOURCE:
                if ( !(resource instanceof IImageResource) )
                    Logger.Log( "Resource must be image resource." );

                break;

            case RT_IMAGE:
                if ( !(resource instanceof ISubImage) )
                    Logger.Log( "Resource must be image." );

                break;

            case RT_FONT:
                if ( !(resource instanceof ICFont) )
                    Logger.Log( "Resource must be font." );

                break;
        }
        //#endif

        ResourceWrapper wrapper =
            new ResourceWrapper( resource, proxyName );
        m_resourceLists[ type ].Push_Back( wrapper );
    }

    public Object GetResource( String proxyName, byte type )
    {
        //#ifdef __SPUKMK2ME_DEBUG
        if ( (type < 0) || (type > 2) )
            Logger.Log( "Unknown resource type: " + type );
        //#endif

        DoublyLinkedList.Iterator i     = m_resourceLists[ type ].Begin();
        DoublyLinkedList.Iterator end   = m_resourceLists[ type ].End();

        for ( ; !i.equals( end ); i.Next() )
        {
            if ( ((ResourceWrapper)i.Get()).c_proxyName.equals( proxyName ) )
                return ((ResourceWrapper)i.Get()).c_data;
        }

        return null;
    }

    public Object GetResource( int index, byte type )
    {
        return m_resourceLists[ type ].Get( index );
    }

    /**
     *  Clear the resource lists and load new resources from input stream.
     *  This is the optimized version which will be expected to run on low-spec
     * mobile devices.
     */
    public void LoadResources( InputStream is ) throws IOException
    {
        Clear();

        DataInputStream     dis = new DataInputStream( is );
        DoublyLinkedList    list;

        int i;
        String filename, proxyName;

        // Image resource
        list = m_resourceLists[ 0 ];

        for ( i = dis.readInt(); i != 0; --i )
        {
            filename    = dis.readUTF();
            proxyName   = dis.readUTF();
            list.Push_Back( new ResourceWrapper(
                m_vdriver.CreateImageResource( filename ), proxyName ) );
        }

        // Image
        list        = m_resourceLists[ 1 ];

        int     rotationDegree, imgResIndex;
        short   x, y, w, h;
        byte    flippingFlags;

        for ( i = dis.readInt(); i != 0; --i )
        {
            rotationDegree  = dis.readInt();
            x               = dis.readShort();
            y               = dis.readShort();
            w               = dis.readShort();
            h               = dis.readShort();
            imgResIndex     = dis.readUnsignedShort();
            flippingFlags   = dis.readByte();
            proxyName       = dis.readUTF();

            DoublyLinkedList.Iterator itr = m_resourceLists[ 0 ].Begin();

            while ( imgResIndex-- != 0 )
                itr.Next();

            list.Push_Back( new ResourceWrapper(
                m_vdriver.CreateSubImage(
                    (IImageResource)itr.Get(),
                    x, y, w, h, rotationDegree, flippingFlags ), proxyName ) );
        }

        // Font
        list = m_resourceLists[ 2 ];

        for ( i = dis.readInt(); i != 0; --i )
        {
            filename    = dis.readUTF();
            proxyName   = dis.readUTF();
            list.Push_Back( new ResourceWrapper(
                new BitmapFont(
                    this.getClass().getResourceAsStream( filename ) ),
                proxyName ) );
        }
    }

    private void Clear()
    {
        for ( int i = 0; i != 3; ++i )
            m_resourceLists[ i ].Clear();
    }

    public static final byte RT_IMAGERESOURCE   = 0;
    public static final byte RT_IMAGE           = 1;
    public static final byte RT_FONT            = 2;

    private IVideoDriver        m_vdriver;
    private DoublyLinkedList[]  m_resourceLists;
}
