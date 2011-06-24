package com.spukmk2me.optional.scene;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.spukmk2me.DoublyLinkedList;
//#ifdef __SPUKMK2ME_DEBUG
//# import com.spukmk2me.debug.Logger;
//#endif
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IResource;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.ICFont;

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
            m_resourceLists[ i ] = new DoublyLinkedList();
    }
    
    public void AddResource( IResource resource, byte type )
    {
        //#ifdef __SPUKMK2ME_DEBUG
//#         if ( (type < 0) || (type > 2) )
//#             Logger.Log( "Unknown resource type: " + type );
//#         
//#         switch ( type )
//#         {
//#             case RT_IMAGERESOURCE:
//#                 if ( !(resource instanceof IImageResource) )
//#                     Logger.Log( "Resource must be image resource." );
//# 
//#                 break;
//# 
//#             case RT_IMAGE:
//#                 if ( !(resource instanceof ISubImage) )
//#                     Logger.Log( "Resource must be image." );
//# 
//#                 break;
//# 
//#             case RT_FONT:
//#                 if ( !(resource instanceof ICFont) )
//#                     Logger.Log( "Resource must be font." );
//# 
//#                 break;
//#         }
        //#endif

        m_resourceLists[ type ].push_back( resource );
    }

    public IResource GetResource( String proxyName, byte type )
    {
        //#ifdef __SPUKMK2ME_DEBUG
//#         if ( (type < 0) || (type > 2) )
//#             Logger.Log( "Unknown resource type: " + type );
        //#endif

        DoublyLinkedList.Iterator i     = m_resourceLists[ type ].first();
        DoublyLinkedList.Iterator end   = m_resourceLists[ type ].end();

        for ( ; !i.equals( end ); i.fwrd() )
        {
            if ( ((IResource)i.data()).GetCreationData().c_proxyName.
                equals( proxyName ) )
                return (IResource)i.data();
        }

        return null;
    }

    public Object GetResource( int index, byte type )
    {
        return m_resourceLists[ type ].get( index );
    }

    /**
     *  Clear the resource lists and load new resources from input stream.
     *  This is the optimized version which will be expected to run on low-spec
     * mobile devices.
     */
    public void LoadResources( InputStream is ) throws IOException
    {
        Clear();

        DataInputStream dis     = new DataInputStream( is );
        StandardResourceLoader  defaultLoader =
            new StandardResourceLoader( m_vdriver );
        ResourceProducer        producer =
            new ResourceProducer( defaultLoader );
        
        // IImageResource
        int n = dis.readInt();
        IImageResource[] imgResources = new IImageResource[ n ];

        for ( int i = 0; i != n; ++i )
        {
            imgResources[ i ] =
                (IImageResource)producer.LoadCreationDataAndConstruct( is );
            m_resourceLists[ 0 ].push_back( imgResources[ i ] );
        }

        defaultLoader.SetImageResources( imgResources );

        // ISubImages
        for ( int i = dis.readInt(); i != 0; --i )
        {
            m_resourceLists[ 1 ].push_back(
                producer.LoadCreationDataAndConstruct( is ) );
        }

        // BitmapFont
        for ( int i = dis.readInt(); i != 0; --i )
        {
            m_resourceLists[ 2 ].push_back(
                producer.LoadCreationDataAndConstruct( is ) );
        }
    }

    private void Clear()
    {
        for ( int i = 0; i != 3; ++i )
            m_resourceLists[ i ].clear();
    }

    public static final byte RT_IMAGERESOURCE   = 0;
    public static final byte RT_IMAGE           = 1;
    public static final byte RT_FONT            = 2;

    private IVideoDriver        m_vdriver;
    private DoublyLinkedList[]  m_resourceLists;
}
