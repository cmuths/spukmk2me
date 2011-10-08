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
//#             Logger.Log( "ERROR: Null parameter is passed." );
//# 
//#         if ( loader.GetLoadableResourceID() == null )
//#             Logger.Log( "ERROR: No id is specified in this loader." );
//#         else if ( loader.GetLoadableResourceID().length == 0 )
//#             Logger.Log( "ERROR: No id is specified in this loader." );
        //#endif

        Byte    keyObject;
        byte[]  ids = loader.GetLoadableResourceID();

        for ( int i = 0; i != ids.length; ++i )
        {
            //#ifdef __SPUKMK2ME_DEBUG
//#             if ( ids[ i ] < 4 ) // Standard ID used
//#             {
//#                 Logger.Log(
//#                     "ERROR: This loader uses standard ID: " + ids[ i ] );
//#             }
            //#endif

            // What a nice hash table, taking object as key.
            keyObject = new Byte( ids[ i ] );

            //#ifdef __SPUKMK2ME_DEBUG
//#             if ( m_loaderTable.containsKey( keyObject ) )
//#                 Logger.Log( "ERROR: Duplicated resource ID: " + ids[ i ] );
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

    /**
     *  Save path format: /aaa/bbb/ccc/
     *  File path format: /xxx/yyy/zzz
     *  Output path format: /../../../xxx/yyy/zzz
     */
    public static String convertToRelativePath(
        String absoluteFilePath, String absoluteSavePath,
        char sourcePlatformPathSeparator, char destPlatformPathSeparator )
    {
        int fileLength  = absoluteFilePath.length();
        int saveLength  = absoluteSavePath.length();

        if ( (fileLength == 0) || (saveLength == 0) )
            return null;

        char[] _absoluteFilePath = absoluteFilePath.toCharArray();
        char[] _absoluteSavePath = absoluteSavePath.toCharArray();

        for( int i = 0; i != saveLength; ++i )
        {
            if ( _absoluteSavePath[ i ] == sourcePlatformPathSeparator )
                _absoluteSavePath[ i ] = destPlatformPathSeparator;
        }

        for( int i = 0; i != fileLength; ++i )
        {
            if ( _absoluteFilePath[ i ] == sourcePlatformPathSeparator )
                _absoluteFilePath[ i ] = destPlatformPathSeparator;
        }

        absoluteFilePath = new String( _absoluteFilePath );
        absoluteSavePath = new String( _absoluteSavePath );

        // Search for the first separator
        int i = 0;

        while ( _absoluteFilePath[ i ] != destPlatformPathSeparator )
        {
            ++i;

            if ( (i == fileLength) || (i == saveLength) )
                return null;
        }
        // Done searching for first separator

        if ( !absoluteFilePath.regionMatches( false,
            0, absoluteSavePath, 0, i + 1 ) )
            return null;

        int lastMatchedSeparatorIndex = i;
        
        ++i;

        while ( (i != fileLength) && (i != saveLength) )
        {
            if ( _absoluteFilePath[ i ] != _absoluteSavePath[ i ] )
                break;

            if ( _absoluteFilePath[ i ] == destPlatformPathSeparator )
                lastMatchedSeparatorIndex = i;

            ++i;
        }

        int nUpperDir = 0;

        for ( i = lastMatchedSeparatorIndex; i != saveLength; ++i )
        {
            if ( _absoluteSavePath[ i ] == destPlatformPathSeparator )
                ++nUpperDir;
        }

        String resultPath = "";
        String parentPath = ".." + destPlatformPathSeparator;

        for ( i = nUpperDir; i != 0; --i )
            resultPath += parentPath;

        resultPath += absoluteFilePath.substring(
            lastMatchedSeparatorIndex + 1 );
        return resultPath;
    }

    public static String convertToAbsolutePath( String sceneFilePath,
        String resFilePath, char srcPathSeparator, char destPathSeparator )
    {
        char[] _sceneFilePath   = sceneFilePath.toCharArray();
        char[] _resFilePath     = resFilePath.toCharArray();

        for ( int i = 0; i != _sceneFilePath.length; ++i )
        {
            if ( _sceneFilePath[ i ] == srcPathSeparator )
                _sceneFilePath[ i ] = destPathSeparator;
        }

        for ( int i = 0; i != _resFilePath.length; ++i )
        {
            if ( _resFilePath[ i ] == srcPathSeparator )
                _resFilePath[ i ] = destPathSeparator;
        }

        resFilePath     = new String( _resFilePath );
        sceneFilePath   = new String( _sceneFilePath );

        String parentPath = ".." + destPathSeparator;
        int lastParentIndex = -1, nParent = 0, temp;

        while ( (temp = resFilePath.indexOf( parentPath, lastParentIndex + 1 ))
            != -1 )
        {
            lastParentIndex = temp + 2;
            ++nParent;
        }

        int sceneTruncatedIndex = sceneFilePath.length() - 1;

        for ( int i = nParent; i != 0; --i )
        {
            sceneTruncatedIndex = sceneFilePath.lastIndexOf(
                destPathSeparator, sceneTruncatedIndex ) - 1;
        }

        return sceneFilePath.substring( 0, sceneTruncatedIndex + 1 ) +
            destPathSeparator + resFilePath.substring( lastParentIndex + 1 );
    }

    private Hashtable m_loaderTable;
}
