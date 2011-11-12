package com.spukmk2me.optional.scene;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
//#ifdef __SPUKMK2ME_SCENESAVER
//# import java.io.OutputStream;
//# import java.io.DataOutputStream;
//# 
//# import com.spukmk2me.DoublyLinkedList;
//# import com.spukmk2me.video.ISubImage;
//# import com.spukmk2me.video.ICFont;
//#endif
//#ifdef __SPUKMK2ME_DEBUG
//# import com.spukmk2me.debug.Logger;
//#endif
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IResource;
import com.spukmk2me.video.IImageResource;

/**
 *  Memory-optimized resource manager, for loading only.
 */
public final class ResourceManager
{
    public ResourceManager( IVideoDriver vdriver )
    {
        m_vdriver               = vdriver;

        //#ifdef __SPUKMK2ME_SCENESAVER
//#         m_resourceLists         = new DoublyLinkedList[ 4 ];
//#         m_resourceLists[ 0 ]    = null;
//# 
//#         for ( int i = 1; i != 4; ++i )
//#             m_resourceLists[ i ] = new DoublyLinkedList();
        //#else
        m_resourceLists         = new IResource[ 4 ][];
        m_proxyNames            = new String[ 4 ][];
        m_resourceLists[ 0 ]    = null;
        m_proxyNames[ 0 ]       = null;
        //#endif
    }
    
    public IResource GetResource( String proxyName, byte type )
    {
        //#ifdef __SPUKMK2ME_DEBUG
//#         if ( (type < 0) || (type > 2) )
//#             Logger.Log( "Unknown resource type: " + type );
        //#endif

        //#ifdef __SPUKMK2ME_SCENESAVER
//#         DoublyLinkedList.Iterator i     = m_resourceLists[ type ].first();
//#         DoublyLinkedList.Iterator end   = m_resourceLists[ type ].end();
//#         for ( ; !i.equals( end ); i.fwrd() )
//#         {
//#             if ( ((IResource)i.data()).GetCreationData().c_proxyName.
//#                 equals( proxyName ) )
//#                 return (IResource)i.data();
//#         }
        //#else
        String[] list = m_proxyNames[ type ];

        for ( int i = 0; i != list.length; ++i )
        {
            if ( proxyName.equals( list[ i ] ) )
                return m_resourceLists[ type ][ i ];
        }
        //#endif

        return null;
    }

    public IResource GetResource( int index, byte type )
    {
        //#ifdef __SPUKMK2ME_SCENESAVER
//#         return (IResource)m_resourceLists[ type ].get( index );
        //#else
        return m_resourceLists[ type ][ index ];
        //#endif
    }

    //#ifdef __SPUKMK2ME_SCENESAVER
//#     public void AddResource( IResource resource, byte type )
//#     {
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
//# 
//#         m_resourceLists[ type ].push_back( resource );
//#     }
//# 
//#     public void RemoveResource( String proxyName, byte type )
//#     {
        //#ifdef __SPUKMK2ME_DEBUG
//#         if ( (type < 0) || (type > 3) )
//#         {
//#             Logger.Log( "Resource type not found." );
//#         }
        //#endif
//# 
//#         DoublyLinkedList.Iterator i, end;
//#         int index = 0;
//# 
//#         end = m_resourceLists[ type ].end();
//#         i   = m_resourceLists[ type ].first();
//# 
//#         for ( ; !i.equals( end ); i.fwrd() )
//#         {
//#             if ( ((IResource)i.data()).GetCreationData().c_proxyName.equals(
//#                 proxyName ) )
//#             {
//#                 m_resourceLists[ type ].erase( index );
//#                 return;
//#             }
//# 
//#             ++index;
//#         }
//#     }
//# 
//#     public void RemoveResource( int index, byte type )
//#     {
        //#ifdef __SPUKMK2ME_DEBUG
//#         if ( (type < 0) || (type > 3) )
//#         {
//#             Logger.Log( "Resource type not found." );
//#         }
        //#endif
//# 
//#         m_resourceLists[ type ].erase( index );
//#     }
//# 
//#     public int GetResourceIndex( IResource resource, byte type )
//#     {
//#         DoublyLinkedList.Iterator i, end;
//#         int index = 0;
//# 
//#         end = m_resourceLists[ type ].end();
//#         i   = m_resourceLists[ type ].first();
//# 
//#         for ( ; !i.equals( end ); i.fwrd() )
//#         {
//#             if ( i.data() == resource )
//#                 return index;
//# 
//#             ++index;
//#         }
//# 
//#         return -1;
//#     }
//# 
//#     public int GetNumberOfResources( byte type )
//#     {
//#         return m_resourceLists[ type ].length();
//#     }
    //#endif

    /**
     *  Clear the resource lists and load new resources from input stream.
     *  This is the optimized version which will be expected to run on low-spec
     * mobile devices.
     */
    public void LoadResources( InputStream is, String pathToSceneFile,
        char dstPathSeparator )
        throws IOException
    {
        //#ifdef __SPUKMK2ME_SCENESAVER
//#         Clear();
        //#endif

        DataInputStream dis     = new DataInputStream( is );
        StandardResourceLoader  defaultLoader =
            new StandardResourceLoader( m_vdriver, pathToSceneFile,
                dstPathSeparator );
        ResourceProducer        producer =
            new ResourceProducer( defaultLoader );
        
        // IImageResource
        int n = dis.readInt();

        if ( n != 0 )
        {
            IImageResource[] imgResources = new IImageResource[ n ];

            //#ifndef __SPUKMK2ME_SCENESAVER
            m_resourceLists[ RT_IMAGERESOURCE ] = new IResource[ n ];
            m_proxyNames[ RT_IMAGERESOURCE ]    = new String[ n ];
            //#endif

            for ( int i = 0; i != n; ++i )
            {
                imgResources[ i ] = (IImageResource)producer.
                    LoadCreationDataAndConstruct( is );
                //#ifdef __SPUKMK2ME_SCENESAVER
//#                 m_resourceLists[ RT_IMAGERESOURCE ].
//#                     push_back( imgResources[ i ] );
                //#else
                m_resourceLists[ RT_IMAGERESOURCE ][ i ] = imgResources[ i ];
                //#endif
            }

            defaultLoader.SetImageResources( imgResources );
        }

        // ISubImages
        n = dis.readInt();

        //#ifndef __SPUKMK2ME_SCENESAVER
        if ( n != 0 )
        {
            m_resourceLists[ RT_IMAGE ] = new IResource[ n ];
            m_proxyNames[ RT_IMAGE ] = new String[ n ];
        }
        //#endif

        for ( int i = 0; i != n; ++i )
        {
            //#ifdef __SPUKMK2ME_SCENESAVER
//#             m_resourceLists[ RT_IMAGE ].push_back(
//#                 producer.LoadCreationDataAndConstruct( is ) );
            //#else
            m_resourceLists[ RT_IMAGE ][ i ] =
                producer.LoadCreationDataAndConstruct( is );
            //#endif
        }
     
        // BitmapFont
        n = dis.readInt();

        //#ifndef __SPUKMK2ME_SCENESAVER
        if ( n != 0 )
        {
            m_resourceLists[ RT_FONT ] = new IResource[ n ];
            m_proxyNames[ RT_FONT ] = new String[ n ];
        }
        //#endif

        for ( int i = n; i != 0; --i )
        {
            //#ifdef __SPUKMK2ME_SCENESAVER
//#             m_resourceLists[ RT_FONT ].push_back(
//#                 producer.LoadCreationDataAndConstruct( is ) );
            //#else
            m_resourceLists[ RT_FONT ][ i ] =
                producer.LoadCreationDataAndConstruct( is );
            //#endif
        }
    }

    //#ifdef __SPUKMK2ME_SCENESAVER
//#     public void Save( OutputStream os, String sceneFileDirectory,
//#         char srcPathSeparator ) throws IOException
//#     {
//#         DataOutputStream dos = new DataOutputStream( os );
//#         DoublyLinkedList.Iterator i;
//#         StandardResourceSaver resourceSaver = new StandardResourceSaver(
//#             sceneFileDirectory, srcPathSeparator );
//# 
//#         resourceSaver.SetResourceManager( this );
//# 
//#         ResourceFlusher flusher = new ResourceFlusher( resourceSaver );
//#         int length;
//# 
//#         // IImage resource
//#         length  = m_resourceLists[ ResourceManager.RT_IMAGERESOURCE ].length();
//#         i       = m_resourceLists[ ResourceManager.RT_IMAGERESOURCE ].first();
//#         dos.writeInt( length );
//# 
//#         while ( length-- != 0 )
//#         {
//#             flusher.FlushResource( (IResource)i.data(), os,
//#                 sceneFileDirectory, srcPathSeparator );
//#             i.fwrd();
//#         }
//# 
//#         // ISubImage
//#         length  = m_resourceLists[ ResourceManager.RT_IMAGE ].length();
//#         i       = m_resourceLists[ ResourceManager.RT_IMAGE ].first();
//#         dos.writeInt( length );
//# 
//#         while ( length-- != 0 )
//#         {
//#             flusher.FlushResource( (IResource)i.data(), os,
//#                 sceneFileDirectory, srcPathSeparator );
//#             i.fwrd();
//#         }
//# 
//#         // ICFont
//#         length  = m_resourceLists[ ResourceManager.RT_FONT ].length();
//#         i       = m_resourceLists[ ResourceManager.RT_FONT ].first();
//#         dos.writeInt( length );
//# 
//#         while ( length-- != 0 )
//#         {
//#             flusher.FlushResource( (IResource)i.data(), os,
//#                 sceneFileDirectory, srcPathSeparator );
//#             i.fwrd();
//#         }
//#     }
//# 
//#     public void Clear()
//#     {
//#         for ( int i = 1; i != 4; ++i )
//#             m_resourceLists[ i ].clear();
//#     }
    //#endif

    public static final byte RT_INVALIDRESOURCE = -1;
    public static final byte RT_IMAGERESOURCE   = 1;
    public static final byte RT_IMAGE           = 2;
    public static final byte RT_FONT            = 3;

    private IVideoDriver        m_vdriver;

    //#ifdef __SPUKMK2ME_SCENESAVER
//#     private DoublyLinkedList[]  m_resourceLists;
    //#else
    private IResource[][]       m_resourceLists;
    private String[][]          m_proxyNames;
    //#endif
}
