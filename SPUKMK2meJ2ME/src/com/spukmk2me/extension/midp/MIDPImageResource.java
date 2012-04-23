package com.spukmk2me.extension.midp;

import java.io.InputStream;
import java.io.IOException;
import javax.microedition.lcdui.Image;

import com.spukmk2me.video.IImageResource;

final class MIDPImageResource extends IImageResource
{
    public MIDPImageResource( InputStream is, String proxyname ) throws IOException
    {
        super( proxyname );
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
