package com.spukmk2me.extension.midp;

import java.io.InputStream;
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
    public MIDPImageResource( InputStream is ) throws IOException
    {
        m_image = Image.createImage( is );
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

    private final Image m_image;
}
