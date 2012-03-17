package com.spukmk2me.optional.scene;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

import com.spukmk2me.Util;
import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.io.IFileSystem;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IResource;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.optional.font.BitmapFont;
/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */
/* $if SPUKMK2ME_SCENESAVER$ */
import com.spukmk2me.video.IImageResource.ImageResourceCreationData;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.optional.font.BitmapFont.BitmapFontCreationData;
/* $endif$ */

/**
 *  Standard resource loader.
 *  \details This loader loads IImageResource, ISubImage, BitmapFont (and
 * stores creation data in SceneEditor build).
 */
public final class StandardResourceLoader implements IResourceLoader
{
    /**
     *  Create a standard resource loader.
     *  @param vdriver Used video driver.
     *  @param resourceManager A clean resource manager for extracting
     * IImageResource objects.
     *  @param fsystem Used file system.
     *  @param pathToSceneFile Absolute path to scene file which is being
     * loaded.
     *  @param dstPathSeparator Path separator used in current system.
     */
    public StandardResourceLoader( IVideoDriver vdriver,
        ResourceManager resourceManager, IFileSystem fsystem,
        String pathToSceneFile )
    {
        m_vdriver           = vdriver;
        m_fileSystem        = fsystem;
        m_resourceManager   = resourceManager;
        m_pathToSceneFile   = pathToSceneFile;
        
        m_preloadedResources    = new DoublyLinkedList[ 4 ];
        m_preloadedResNames     = new DoublyLinkedList[ 4 ];
        
        for ( int i = 1; i != 4; ++i )
        {
            m_preloadedResources[ i ]   = new DoublyLinkedList();
            m_preloadedResNames[ i ]    = new DoublyLinkedList();
        }
    }

    public byte[] GetLoadableResourceID()
    {
        return AVAIABLE_ID;
    }

    public IResource LoadResource( InputStream is, byte typeID )
        throws IOException
    {
        DataInputStream dis = new DataInputStream( is );
        IResource resource = null;

        switch ( typeID )
        {
            case 1: // IImageResource
                {
                    String path         = dis.readUTF();
                    String proxyName    = dis.readUTF();
                    
                    resource = CheckPreloadedResources( proxyName, (byte)1 );
                    
                    if ( resource == null )
                    {
                        path = Util.ConvertToAbsolutePath(
                            m_pathToSceneFile, path, '/',
                            m_fileSystem.GetPathSeparator() );
                        resource = m_vdriver.CreateImageResource(
                            m_fileSystem.OpenFile( path,
                                IFileSystem.LOCATION_AUTODETECT ) );
                    }
                    /* $if SPUKMK2ME_SCENESAVER$ */
                    ImageResourceCreationData creationData =
                        ((IImageResource)resource).
                            new ImageResourceCreationData();

                    creationData.c_path         = path;
                    creationData.c_proxyName    = proxyName;
                    resource.SetCreationData( creationData );
                    /* $endif$ */
                }

                break;

            case 2: // ISubImage
                {
                    String  		proxyName;
                    IImageResource	imgRes;
                    int     		rotationDegree, imgResIndex;
                    short   		x, y, w, h;
                    byte    		flippingFlags;

                    rotationDegree  = dis.readInt();
                    x               = dis.readShort();
                    y               = dis.readShort();
                    w               = dis.readShort();
                    h               = dis.readShort();
                    imgResIndex     = dis.readUnsignedShort();
                    flippingFlags   = dis.readByte();
                    proxyName       = dis.readUTF();
                    
                    imgRes 			=
                        (IImageResource)m_resourceManager.GetResource(
                        imgResIndex, ResourceManager.RT_IMAGERESOURCE );
                    
                    resource = CheckPreloadedResources( proxyName, (byte)2 );
                    
                    if ( resource == null )
                    {
                        resource = m_vdriver.CreateSubImage( imgRes,
                            x, y, w, h, rotationDegree, flippingFlags );
                    }
                    /* $if SPUKMK2ME_SCENESAVER$ */
                    ISubImage.SubImageCreationData data =
                        ((ISubImage)resource).new SubImageCreationData();

                    data.c_rotationDegree   = rotationDegree;
                    data.c_x                = x;
                    data.c_y                = y;
                    data.c_width            = w;
                    data.c_height           = h;
                    data.c_resource    		= imgRes;
                    data.c_flippingFlags    = flippingFlags;
                    data.c_proxyName        = proxyName;

                    resource.SetCreationData( data );
                    /* $endif$ */
                }

                break;

            case 3: // BitmapFont
                {
                    String path         = dis.readUTF();
                    String proxyName    = dis.readUTF();
                    
                    resource = CheckPreloadedResources( proxyName, (byte)3 );

                    if ( resource == null )
                    {
                        path = Util.ConvertToAbsolutePath(
                            m_pathToSceneFile, path, '/',
                            m_fileSystem.GetPathSeparator() );

                        {
                            InputStream temp_is = m_fileSystem.OpenFile(
                                path, IFileSystem.LOCATION_AUTODETECT );

                            resource = new BitmapFont( temp_is );
                            temp_is.close();
                        }
                    }
                    /* $if SPUKMK2ME_SCENESAVER$ */
                    BitmapFontCreationData data =
                        ((BitmapFont)resource).new BitmapFontCreationData();

                    data.c_path         = path;
                    data.c_proxyName    = proxyName;

                    resource.SetCreationData( data );
                    /* $endif$ */
                }

                break;
        }

        return resource;
    }

    public void AddPreloadedResources( IResource resource,
        String proxyName, byte resourceType )
    {
        /* $if SPUKMK2ME_DEBUG$ */
        if ( (resourceType < 1) || (resourceType > 3) )
        {
            Logger.Trace( "Cannot accept this type of resources" );
            return;
        }
        /* $endif$ */
        
        m_preloadedResources[ resourceType ].push_back( resource );
        m_preloadedResNames[ resourceType ].push_back( proxyName );
    }
    
    private IResource CheckPreloadedResources( String proxyName,
        byte resourceType )
    {
        DoublyLinkedList.Iterator i =
            m_preloadedResNames[ resourceType ].first();
        DoublyLinkedList.Iterator end =
            m_preloadedResNames[ resourceType ].end();
        DoublyLinkedList.Iterator resI =
            m_preloadedResources[ resourceType ].first();
        
        for ( ; !i.equals( end ); i.fwrd() )
        {
            if ( proxyName.equals( i.data() ) )
                return (IResource)resI.data();
            
            resI.fwrd();
        }
        
        return null;
    }

    private static final byte[] AVAIABLE_ID = { 1, 2, 3 };

    private IVideoDriver        m_vdriver;
    private IFileSystem         m_fileSystem;
    private ResourceManager     m_resourceManager;
    private DoublyLinkedList[]  m_preloadedResources;
    private DoublyLinkedList[]  m_preloadedResNames;
    private String              m_pathToSceneFile;
}