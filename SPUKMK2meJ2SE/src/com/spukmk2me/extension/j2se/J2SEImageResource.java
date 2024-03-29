package com.spukmk2me.extension.j2se;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import com.spukmk2me.video.IImageResource;

final class J2SEImageResource extends IImageResource
{
    public J2SEImageResource( InputStream is, String proxyname ) throws IOException
    {
        super( proxyname );
        m_image = ImageIO.read( is );
    }

    public short GetWidth()
    {
        return (short)m_image.getWidth();
    }

    public short GetHeight()
    {
        return (short)m_image.getHeight();
    }

    public BufferedImage GetJ2SEImage()
    {
        return m_image;
    }

    private final BufferedImage m_image;
}
