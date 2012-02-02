package com.spukmk2me.optional.scene;

import com.spukmk2me.Util;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */
import com.spukmk2me.video.IResource;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.optional.font.BitmapFont;

public final class StandardResourceSaver implements IResourceSaver
{
    public StandardResourceSaver( String pathToSceneFile,
        char srcPathSeparator )
    {
        m_pathToSceneFile   = pathToSceneFile;
        m_srcPathSeparator  = srcPathSeparator;
    }

    public byte[] GetSaveableResourceID()
    {
        return SAVEABLE_IDS;
    }

    // Unimplemented
    public short GetResourceSize( IResource resource, byte resourceTypeID )
    {
        switch ( resourceTypeID )
        {
            case 1:
                return 0;

            case 2:
                return 0;

            case 3:
                return 0;
        }

        /* $if SPUKMK2ME_DEBUG$ */
        Logger.Trace( "Received unexpected resource type ID." );
        /* $endif$ */

        return 0;
    }

    public void SaveResource( OutputStream os, IResource resource,
        byte resourceTypeID ) throws IOException
    {
        DataOutputStream dos = new DataOutputStream ( os );

        switch ( resourceTypeID )
        {
            case 1: // IImageResource
                {
                    IImageResource.ImageResourceCreationData creationData =
                        (IImageResource.ImageResourceCreationData)resource.
                        GetCreationData();

                    dos.writeUTF(
                        Util.ConvertToRelativePath(
                            creationData.c_path, m_pathToSceneFile,
                            m_srcPathSeparator, '/' ) );
                    dos.writeUTF( creationData.c_proxyName );
                }

                break;

            case 2: // ISubImage
                {
                    ISubImage.SubImageCreationData creationData =
                        (ISubImage.SubImageCreationData)resource.
                            GetCreationData();

                    dos.writeInt( creationData.c_rotationDegree );
                    dos.writeShort( creationData.c_x );
                    dos.writeShort( creationData.c_y );
                    dos.writeShort( creationData.c_width );
                    dos.writeShort( creationData.c_height );
                    creationData.c_imageResIndex =
                        m_resourceManager.GetResourceIndex(
                            ((ISubImage)resource).GetImageResource(),
                            ResourceManager.RT_IMAGERESOURCE );
                    dos.writeShort( creationData.c_imageResIndex );
                    dos.writeByte( creationData.c_flippingFlags );
                    dos.writeUTF( creationData.c_proxyName );
                }

                break;

            case 3: // ICFont
                {
                    BitmapFont.BitmapFontCreationData creationData =
                        (BitmapFont.BitmapFontCreationData)resource.
                            GetCreationData();

                    dos.writeUTF(
                        Util.ConvertToRelativePath(
                            creationData.c_path, m_pathToSceneFile,
                            m_srcPathSeparator, '/' ) );
                    dos.writeUTF( creationData.c_proxyName );
                }

                break;
        }
    }

    public void SetResourceManager( ResourceManager resourceManager )
    {
        m_resourceManager = resourceManager;
    }

    private static final byte[] SAVEABLE_IDS = { 1, 2, 3 };

    private ResourceManager m_resourceManager;
    private String          m_pathToSceneFile;
    private char            m_srcPathSeparator;
}
