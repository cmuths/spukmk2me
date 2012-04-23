package com.spukmk2me.resource;

import java.io.DataInputStream;
import java.io.IOException;

import com.spukmk2me.Util;
import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ImageResourceConstructionData;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.SubImageConstructionData;
import com.spukmk2me.video.BitmapFont;
import com.spukmk2me.video.BitmapFontConstructionData;
import com.spukmk2me.sound.ISoundMonitor;
import com.spukmk2me.sound.ISound;
import com.spukmk2me.sound.SoundConstructionData;
import com.spukmk2me.io.IFileSystem;
/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */


public final class DefaultResourceProducer extends IResourceProducer
{
    public DefaultResourceProducer( IVideoDriver vdriver,
        ISoundMonitor smonitor, IFileSystem fsystem, String dirPathToSceneFile )
    {
        m_vdriver               = vdriver;
        m_smonitor              = smonitor;
        m_fsystem               = fsystem;
        m_dirPathToSceneFile    = dirPathToSceneFile;
        m_imageResources        = new DoublyLinkedList();
        m_loadedResources       = new ResourceSet();
    }
    
    public boolean IsSupported( byte resourceType )
    {
        return  (resourceType == IResource.RT_IMAGERESOURCE) ||
                (resourceType == IResource.RT_IMAGE) ||
                (resourceType == IResource.RT_BITMAPFONT) ||
                (resourceType == IResource.RT_SOUND);
    }

    public IResource CreateResource( IResourceConstructionData data )
        throws IOException
    {
        switch ( data.GetAssociatedResourceType() )
        {
            case IResource.RT_IMAGERESOURCE:
                {
                    ImageResourceConstructionData imgResData =
                        (ImageResourceConstructionData)data;
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Log( "Creating img resources: " + imgResData.c_proxyname + "... " );
                    /* $endif$ */
                    
                    IResource res = m_loadedResources.GetResource( imgResData.c_proxyname,
                        IResource.RT_IMAGERESOURCE );
                    
                    if ( res == null )
                    {
                        res = m_vdriver.CreateImageResource(
                            imgResData.c_path, imgResData.c_proxyname );
                    }    
                    
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Log( "OK\n" );
                    /* $endif$ */
                    
                    m_imageResources.push_back( res );
                    return res;
                }
                
            case IResource.RT_IMAGE:
                {
                    SubImageConstructionData subImgData =
                        (SubImageConstructionData)data;
                    
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Log( "Creating sub image: " + subImgData.c_proxyname + "... " );
                    /* $endif$ */
                    
                    IResource img = m_loadedResources.GetResource( subImgData.c_proxyname,
                        IResource.RT_IMAGE );
                    
                    if ( img == null )
                    {
                        img = m_vdriver.CreateSubImage( subImgData.c_resource,
                            subImgData.c_x, subImgData.c_y,
                            subImgData.c_width, subImgData.c_height,
                            subImgData.c_rotationDegree,
                            subImgData.c_flippingFlags,
                            subImgData.c_proxyname );
                    }
                    
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Log( "OK\n" );
                    /* $endif$ */
                    
                    return img;
                }
                
            case IResource.RT_BITMAPFONT:
                {
                    BitmapFontConstructionData fontData =
                        (BitmapFontConstructionData)data;
                    
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Log( "Creating sub image: " + fontData.c_proxyname + "... " );
                    /* $endif$ */
                    IResource font = m_loadedResources.GetResource( fontData.c_proxyname,
                        IResource.RT_BITMAPFONT );
                    
                    if ( font == null )
                    {
                        font = new BitmapFont(
                            m_fsystem.OpenFile( fontData.c_path,
                                    IFileSystem.LOCATION_AUTODETECT ),
                            fontData.c_proxyname );
                    }
                    
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Log( "OK\n" );
                    /* $endif$ */
                    
                    return font;
                }
                
            case IResource.RT_SOUND:
                {
                    SoundConstructionData soundData =
                        (SoundConstructionData)data;
                    
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Log( "Creating sub image: " + soundData.c_proxyname + "... " );
                    /* $endif$ */
                    IResource sound = m_loadedResources.GetResource( soundData.c_proxyname,
                        IResource.RT_SOUND );
                    
                    if ( sound == null )
                    {
                        sound = m_smonitor.CreateSound(
                            m_fsystem.OpenFile( soundData.c_path,
                                IFileSystem.LOCATION_AUTODETECT ),
                            soundData.c_format, soundData.c_proxyname );
                    }
                    
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Log( "OK\n" );
                    /* $endif$ */
                    
                    return sound;
                }
        }

        return null;
    }

    public IResourceConstructionData LoadTypeBasedConstructionData(
        DataInputStream is, int resourceType ) throws IOException
    {
        switch ( resourceType )
        {
            case IResource.RT_IMAGERESOURCE:
                {
                    ImageResourceConstructionData data =
                        new ImageResourceConstructionData();
                    
                    data.c_path         = Util.ConvertToAbsolutePath(
                        m_dirPathToSceneFile, is.readUTF(), '/',
                        m_fsystem.GetPathSeparator() );
                    data.c_proxyname    = is.readUTF();
                    
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Log( "Loaded image resource data: " +
                        data.c_proxyname + " - " + data.c_path + '\n' );
                    /* $endif$ */
                    
                    return data;
                }
                
            case IResource.RT_IMAGE:
                {
                    SubImageConstructionData data =
                        new SubImageConstructionData();
                    int imgResIndex;
                    
                    data.c_rotationDegree   = is.readInt();
                    data.c_x                = is.readShort();
                    data.c_y                = is.readShort();
                    data.c_width            = is.readShort();
                    data.c_height           = is.readShort();
                    imgResIndex             = is.readUnsignedShort();
                    data.c_flippingFlags    = is.readByte();
                    data.c_proxyname        = is.readUTF();
                    
                    /* $if SPUKMK2ME_DEBUG$ */
                    String logString = "Loaded sub image data: " +
                        data.c_proxyname + " - " +
                        data.c_x + ' ' + data.c_y + ' ' +
                        data.c_width + ' ' + data.c_height + ' ' +
                        imgResIndex + ' ';
                    
                    if ( (data.c_flippingFlags & IVideoDriver.FLIP_HORIZONTAL) != 0 )
                        logString += "horizontal ";
                    
                    if ( (data.c_flippingFlags & IVideoDriver.FLIP_VERTICAL) != 0 )
                        logString += "vertical ";
                    
                    logString += Integer.toHexString( data.c_rotationDegree ) + '\n';
                    Logger.Log( logString );
                    /* $endif$ */
                    
                    data.c_resource =
                        (IImageResource)m_imageResources.get( imgResIndex );
                    
                    return data;
                }
                
            case IResource.RT_BITMAPFONT:
                {
                    BitmapFontConstructionData data =
                        new BitmapFontConstructionData();
                    
                    data.c_path         = Util.ConvertToAbsolutePath(
                        m_dirPathToSceneFile, is.readUTF(), '/',
                        m_fsystem.GetPathSeparator() );
                    data.c_proxyname    = is.readUTF();
                    
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Log( "Loaded bitmap font data: " +
                        data.c_proxyname + " - " + data.c_path + '\n' );
                    /* $endif$ */
                    
                    return data;
                }
                
            case IResource.RT_SOUND:
                {
                    SoundConstructionData data =
                        new SoundConstructionData();
                    
                    data.c_path         = Util.ConvertToAbsolutePath(
                        m_dirPathToSceneFile, is.readUTF(), '/',
                        m_fsystem.GetPathSeparator() );
                    data.c_proxyname    = is.readUTF();
                    
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Log( "Loaded sound data: " +
                        data.c_proxyname + " - " + data.c_path + '\n' );
                    /* $endif$ */
                    
                    return data;
                }
        }

        return null;
    }
    
    public void AddLoadedResources( IResource resource )
    {
        m_loadedResources.AddResource( resource );
    }
    
    private IVideoDriver        m_vdriver;
    private ISoundMonitor       m_smonitor;
    private IFileSystem         m_fsystem;
    private String              m_dirPathToSceneFile;
    private DoublyLinkedList    m_imageResources;
    private ResourceSet         m_loadedResources;
}
