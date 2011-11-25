package com.spukmk2me.optional.scene;

import java.io.InputStream;
import java.io.IOException;
import java.util.Hashtable;

import com.spukmk2me.video.IResource;

//#ifdef __SPUKMK2ME_DEBUG
//# import com.spukmk2me.debug.Logger;
//#endif

public final class ResourceProducer
{
    public ResourceProducer( IResourceLoader defaultLoader )
    {
        m_loaderTable = new Hashtable();

        AddResourceLoader( defaultLoader );
    }

    /**
     *  Add a new resource loader to this producer.
     *  \details If there is a problem, the will be displayed to standard
     * output stream in debug build.
     *  @param loader New loader.
     */
    public void AddResourceLoader( IResourceLoader loader )
    {
        //#ifdef __SPUKMK2ME_DEBUG
//#         if ( loader == null )
//#             Logger.Trace( "ERROR: Null parameter is passed." );
//# 
//#         if ( loader.GetLoadableResourceID() == null )
//#             Logger.Trace( "ERROR: No id is specified in this loader." );
//#         else if ( loader.GetLoadableResourceID().length == 0 )
//#             Logger.Trace( "ERROR: No id is specified in this loader." );
        //#endif

        Byte    keyObject;
        byte[]  ids = loader.GetLoadableResourceID();

        for ( int i = 0; i != ids.length; ++i )
        {
            // What a nice hash table, taking object as key.
            keyObject = new Byte( ids[ i ] );

            //#ifdef __SPUKMK2ME_DEBUG
//#             if ( m_loaderTable.containsKey( keyObject ) )
//#                 Logger.Trace( "ERROR: Duplicated resource ID: " + ids[ i ] );
            //#endif

            m_loaderTable.put( keyObject, loader );
        }
    }

    public IResource LoadCreationDataAndConstruct( InputStream is )
        throws IOException
    {
        byte    typeID = (byte)is.read();
        int     resourceCreationDataSize = 0;

        // resourceCreationDataSize = is.readUnsignedShort();
        resourceCreationDataSize |= is.read() << 8;
        resourceCreationDataSize |= (is.read() & 0x000000FF);

        IResourceLoader loader =
            (IResourceLoader)m_loaderTable.get( new Byte( typeID ) );

        if ( loader == null )
        {
            is.skip( resourceCreationDataSize );
            return null;
        }

        return loader.LoadResource( is, typeID );
    }

    private Hashtable m_loaderTable;
}
