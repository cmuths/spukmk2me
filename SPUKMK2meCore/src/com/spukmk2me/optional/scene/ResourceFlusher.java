package com.spukmk2me.optional.scene;

import java.io.OutputStream;
import java.io.IOException;
import java.util.Hashtable;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */

import com.spukmk2me.video.IResource;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ICFont;

public final class ResourceFlusher
{
    public ResourceFlusher( IResourceSaver defaultSaver )
    {
        m_saverTable = new Hashtable();
        AddResourceSaver( defaultSaver );
    }

    public void AddResourceSaver( IResourceSaver saver )
    {
        /* $if SPUKMK2ME_DEBUG$ */
        if ( saver == null )
        {
            Logger.Trace( "ERROR: Null saver has been passed." );
        }
        else if ( saver.GetSaveableResourceID() == null )
        {
            Logger.Trace( "ERROR: ID list of this saver is null." );
        }
        else if ( saver.GetSaveableResourceID().length == 0 )
        {
            Logger.Trace( "WARNING: ID list of this saver is empty." );
        }
        /* $endif$ */

        Byte    keyObject;
        byte[]  idList = saver.GetSaveableResourceID();
        int     i;

        for ( i = 0; i != idList.length; ++i )
        {
            keyObject = new Byte( idList[ i ] );

            /* $if SPUKMK2ME_DEBUG$ */
            if ( m_saverTable.containsKey( keyObject ) )
            {
                Logger.Trace(
                    "There has been an existed id in previous saver" );
            }
        	/* $endif$ */
            
            m_saverTable.put( keyObject, saver );
        }
    }

    public void FlushResource( IResource resource, OutputStream os,
        String sceneFileDirectory, char platformPathSeparator )
        throws IOException
    {
        byte typeID = GetResourceTypeID( resource );

        os.write( typeID );

        IResourceSaver saver =
            (IResourceSaver)m_saverTable.get( new Byte( typeID ) );

        short size = saver.GetResourceSize( resource, typeID );

        os.write( size >>> 8 );
        os.write( size );
        saver.SaveResource( os, resource, typeID );
    }

    private byte GetResourceTypeID( IResource resource )
    {
        if ( resource instanceof IImageResource )
            return ResourceManager.RT_IMAGERESOURCE;
        else if ( resource instanceof ISubImage )
            return ResourceManager.RT_IMAGE;
        else if ( resource instanceof ICFont )
            return ResourceManager.RT_FONT;
        else
            return -1;
    }

    private Hashtable m_saverTable;
}
