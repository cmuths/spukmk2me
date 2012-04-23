package com.spukmk2me.resource;

import java.io.DataOutputStream;
import java.io.IOException;

import com.spukmk2me.Util;
import com.spukmk2me.io.IFileSystem;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ImageResourceConstructionData;
import com.spukmk2me.video.SubImageConstructionData;
import com.spukmk2me.video.BitmapFontConstructionData;
import com.spukmk2me.sound.SoundConstructionData;;

public final class DefaultResourceExporter extends IResourceExporter
{
    public DefaultResourceExporter( String dirPathToSceneFile,
        IFileSystem fsystem, ResourceSet resourceSet )
    {
        m_dirPathToSceneFile = dirPathToSceneFile;
        m_fsystem = fsystem;
        m_resourceSet = resourceSet;
    }
    
    public boolean IsSupported( byte resourceType )
    {
        return  (resourceType == IResource.RT_IMAGERESOURCE) ||
                (resourceType == IResource.RT_IMAGE) ||
                (resourceType == IResource.RT_BITMAPFONT) ||
                (resourceType == IResource.RT_SOUND);
    }
    
    // Unimplemented
    protected int GetWrittenDataSize( IResourceConstructionData data )
    {
        return 0;
    }

    protected void SaveTypeBasedConstructionData( DataOutputStream os,
        IResourceConstructionData data ) throws IOException
    {
        switch ( data.GetAssociatedResourceType() )
        {
            case IResource.RT_IMAGERESOURCE:
                {
                    ImageResourceConstructionData imgResData =
                        (ImageResourceConstructionData)data;
                    
                    os.writeUTF(
                        Util.ConvertToRelativePath(
                            imgResData.c_path, m_dirPathToSceneFile,
                            m_fsystem.GetPathSeparator(), '/' ) );
                    os.writeUTF( imgResData.c_proxyname );
                }
                
                break;
                
            case IResource.RT_IMAGE:
                {
                    SubImageConstructionData imgData =
                        (SubImageConstructionData)data;
                    
                    os.writeInt( imgData.c_rotationDegree );
                    os.writeShort( imgData.c_x );
                    os.writeShort( imgData.c_y );
                    os.writeShort( imgData.c_width );
                    os.writeShort( imgData.c_height );
                    
                    int resourceIndex = m_resourceSet.GetResourceIndex(
                        imgData.c_resource );
                        
                    os.writeShort( resourceIndex );
                    os.writeByte( imgData.c_flippingFlags );
                    os.writeUTF( imgData.c_proxyname );
                }
                
                break;
            
            case IResource.RT_BITMAPFONT:
                {
                    BitmapFontConstructionData fontData =
                        (BitmapFontConstructionData)data;
                    
                    os.writeUTF(
                        Util.ConvertToRelativePath(
                            fontData.c_path, m_dirPathToSceneFile,
                            m_fsystem.GetPathSeparator(), '/' ) );
                    os.writeUTF( fontData.c_proxyname );
                }
                
                break;
            
            case IResource.RT_SOUND:
                {
                    SoundConstructionData soundData =
                        (SoundConstructionData)data;
                        
                    os.writeUTF(
                        Util.ConvertToRelativePath(
                                soundData.c_path, m_dirPathToSceneFile,
                            m_fsystem.GetPathSeparator(), '/' ) );
                    os.writeUTF( soundData.c_proxyname );
                }
                
                break;
        }
    }
    
    private String m_dirPathToSceneFile;
    private IFileSystem m_fsystem;
    private ResourceSet m_resourceSet;
}
