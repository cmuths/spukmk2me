package com.spukmk2me.optional.scene;

import com.spukmk2me.video.IImageResource;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IResource;
import com.spukmk2me.optional.font.BitmapFont;
//#ifdef __SPUKMK2ME_SCENESAVER
//# import com.spukmk2me.video.IImageResource.ImageResourceCreationData;
//# import com.spukmk2me.video.ISubImage;
//# import com.spukmk2me.optional.font.BitmapFont.BitmapFontCreationData;
//#endif

/**
 *  Standard resource loader.
 *  \details This loader loads IImageResource, ISubImage, BitmapFont (and
 * stores creation data in SceneEditor build).
 */
public final class StandardResourceLoader implements IResourceLoader
{
    public StandardResourceLoader( IVideoDriver vdriver,
        String pathToSceneFile, char dstPathSeparator )
    {
        m_vdriver           = vdriver;
        m_pathToSceneFile   = pathToSceneFile;
        m_dstPathSeparator  = dstPathSeparator;
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

                    path = ResourceProducer.convertToAbsolutePath(
                        m_pathToSceneFile, path, '/', m_dstPathSeparator );

                    resource = m_vdriver.CreateImageResource( path );
                    //#ifdef __SPUKMK2ME_SCENESAVER
//#                     ImageResourceCreationData creationData =
//#                         ((IImageResource)resource).
//#                             new ImageResourceCreationData();
//# 
//#                     creationData.c_path         = path;
//#                     creationData.c_proxyName    = proxyName;
//#                     resource.SetCreationData( creationData );
                    //#endif
                }

                break;

            case 2: // ISubImage
                {
                    String  proxyName;
                    int     rotationDegree, imgResIndex;
                    short   x, y, w, h;
                    byte    flippingFlags;

                    rotationDegree  = dis.readInt();
                    x               = dis.readShort();
                    y               = dis.readShort();
                    w               = dis.readShort();
                    h               = dis.readShort();
                    imgResIndex     = dis.readUnsignedShort();
                    flippingFlags   = dis.readByte();
                    proxyName       = dis.readUTF();

                    resource = m_vdriver.CreateSubImage(
                        m_imageResources[ imgResIndex ],
                        x, y, w, h, rotationDegree, flippingFlags );
                    //#ifdef __SPUKMK2ME_SCENESAVER
//#                     ISubImage.SubImageCreationData data =
//#                         ((ISubImage)resource).new SubImageCreationData();
//# 
//#                     data.c_rotationDegree   = rotationDegree;
//#                     data.c_x                = x;
//#                     data.c_y                = y;
//#                     data.c_width            = w;
//#                     data.c_height           = h;
//#                     data.c_imageResIndex    = imgResIndex;
//#                     data.c_flippingFlags    = flippingFlags;
//#                     data.c_proxyName        = proxyName;
//# 
//#                     resource.SetCreationData( data );
                    //#endif
                }

                break;

            case 3: // BitmapFont
                {
                    String path         = dis.readUTF();
                    String proxyName    = dis.readUTF();

                    path = ResourceProducer.convertToAbsolutePath(
                        m_pathToSceneFile, path, '/', m_dstPathSeparator );

                    resource = new BitmapFont(
                        this.getClass().getResourceAsStream( path ) );
                    //#ifdef __SPUKMK2ME_SCENESAVER
//#                     BitmapFontCreationData data =
//#                         ((BitmapFont)resource).new BitmapFontCreationData();
//# 
//#                     data.c_path         = path;
//#                     data.c_proxyName    = proxyName;
//# 
//#                     resource.SetCreationData( data );
                    //#endif
                }

                break;
        }

        return resource;
    }

    public void SetImageResources( IImageResource[] imageResources )
    {
        m_imageResources = imageResources;
    }

    private static final byte[] AVAIABLE_ID = { 1, 2, 3 };

    private IVideoDriver        m_vdriver;
    private IImageResource[]    m_imageResources;
    private String              m_pathToSceneFile;
    private char                m_dstPathSeparator;
}
