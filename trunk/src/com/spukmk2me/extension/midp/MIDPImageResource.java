package com.spukmk2me.extension.midp;

import java.io.IOException;
import javax.microedition.lcdui.Image;

//#ifdef __SPUKMK2ME_DEBUG
//# import com.spukmk2me.debug.Logger;
//#endif
import com.spukmk2me.video.IImageResource;

final class MIDPImageResource
    //#ifdef __SPUKMK2ME_SCENESAVER
//#     extends IImageResource
    //#else
    implements IImageResource
    //#endif
{
    public MIDPImageResource( String filename ) throws IOException
    {
        //#ifdef __SPUKMK2ME_DEBUG
//#         if ( filename.charAt( 0 ) != '/' )
//#             Logger.Trace( "Warning: image filename does not start with '." );
//# 
//#         Logger.Log( "Loading image: " + filename + "..." );
        //#endif

        m_filename  = filename;
        m_image     = Image.createImage( filename );

        //#ifdef __SPUKMK2ME_DEBUG
//#         System.out.println( " Loaded." );
        //#endif
    }

    public String GetSource()
    {
        return m_filename;
    }

    public short GetWidth()
    {
        return (short)m_image.getWidth();
    }

    public short GetHeight()
    {
        return (short)m_image.getHeight();
    }

    public Image GetMIDPImage()
    {
        return m_image;
    }

    private final Image     m_image;
    private final String    m_filename;
}
