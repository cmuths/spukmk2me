package com.spukmk2me.extension.j2se;

import java.awt.Panel;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.geom.AffineTransform;

import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IImageResource;

final class J2SESubImage extends ISubImage
{
    public J2SESubImage( J2SEImageResource imageResource,
        short x, short y, short width, short height,
        int rotationDegree, byte flippingFlag )
    {
        CropImageFilter filter = new CropImageFilter( x, y, width, height );
        
        m_image = new Panel().createImage( new FilteredImageSource(
            imageResource.GetJ2SEImage().getSource(), filter ) );
		
		boolean hasHorizontalFlipping = false;
		
		if ( flippingFlag != 0 )
        {
            // Vertical & horizontal flipping
            if ( (flippingFlag & (IVideoDriver.FLIP_HORIZONTAL |
                IVideoDriver.FLIP_VERTICAL)) ==
                (IVideoDriver.FLIP_HORIZONTAL | IVideoDriver.FLIP_VERTICAL) )
            {
                flippingFlag = 0;
                rotationDegree += 0x00B40000;
            }
            
            else
            {
                // Vertical flipping only
                // Vertical flipping = Horizontal flipping + Rotate 180 degree
                if ( (flippingFlag & IVideoDriver.FLIP_VERTICAL) != 0 )
                    rotationDegree += 0x00B40000;

                hasHorizontalFlipping = true;
            }
        }

        while ( rotationDegree >= 0x01680000 ) // larger or equal 360
            rotationDegree -= 0x01680000;

        while ( rotationDegree < 0 )
            rotationDegree += 0x01680000;
            
        double rotationRad = rotationDegree * Math.PI / 0x00B40000;
            
        m_transform = new AffineTransform();
        
        if ( hasHorizontalFlipping )
        {
            m_transform.translate( (double)width / 2, (double)height / 2 );
            m_transform.scale( -1, 1 );
            m_transform.translate( -(double)width / 2, -(double)height / 2 );
        }
            
        m_transform.rotate( rotationRad, (double)width / 2, (double)height / 2 );
        
        double angle = Math.atan( (double)height/width ) - rotationRad;
        double d = Math.pow( Math.pow( width, 2 ) + Math.pow( height, 2 ), 0.5 );
        
        m_width     = (short)Math.abs( Math.round( d * Math.cos( angle ) ) );
        m_height    = (short)Math.abs( Math.round( d * Math.sin( angle ) ) );
    }

    public void Render( IVideoDriver driver )
    {
		Graphics2D g = (Graphics2D)driver.GetProperty( J2SEVideoDriver.PROPERTY_GRAPHICS );
        short x = driver.GetRenderInfo().c_rasterX;
        short y = driver.GetRenderInfo().c_rasterY;
        
        m_transform.translate( x, y );
        g.drawImage( m_image, m_transform, null );
        m_transform.translate( -x, -y );
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

    private Image           m_image;
    private AffineTransform m_transform;
    private short           m_width, m_height;
}
