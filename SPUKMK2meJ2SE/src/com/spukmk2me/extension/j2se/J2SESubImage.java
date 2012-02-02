package com.spukmk2me.extension.j2se;

import java.awt.Graphics;

import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IImageResource;

final class J2SESubImage extends ISubImage
{
    public J2SESubImage( J2SEImageResource imageResource,
        short x, short y, short width, short height,
        int rotationDegree, byte flippingFlag )
    {
        m_imageResource = imageResource;
        m_x             = x;
        m_y             = y;
        m_width         = width;
        m_height        = height;
    }

    public IImageResource GetImageResource()
    {
        return m_imageResource;
    }

    public void Render( IVideoDriver driver )
    {
        short x = driver.GetRenderInfo().c_rasterX;
        short y = driver.GetRenderInfo().c_rasterY;

        ((Graphics)driver.GetProperty( J2SEVideoDriver.PROPERTY_GRAPHICS )).
            drawImage(
                m_imageResource.GetJ2SEImage(),
                x, y, x + m_width, y + m_height,
                m_x, m_y, m_x + m_width, m_y + m_height,
                null );
    }

    public short GetWidth()
    {
        return m_width;
    }

    public short GetHeight()
    {
        return m_height;
    }

    public static J2SESubImage[] CreateSubImagesFromResource(
        J2SEImageResource imageResource, short width, short height )
    {
        int nWidth  = imageResource.GetWidth() / width;
        int nHeight = imageResource.GetHeight() / height;

        if ( (nWidth == 0) || (nHeight == 0) )
            return null;

        J2SESubImage[] images = new J2SESubImage[ nWidth * nHeight ];
        short x, y = 0;
        int i, j, imgIterator = 0;

        for ( i = height; i != 0; --i )
        {
            x = 0;

            for ( j = width; j != 0; --j )
            {
                images[ imgIterator++ ] = new J2SESubImage( imageResource,
                   x, y, width, height, 0, (byte)0 );
                x += width;
            }

            y += height;
        }

        return images;
    }

    private J2SEImageResource   m_imageResource;
    private short               m_x, m_y, m_width, m_height;
}
