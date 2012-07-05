package com.spukmk2me.extension.j2se;

import java.awt.Panel;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.geom.AffineTransform;

import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.IVideoDriver;

final class J2SESubImage extends ISubImage
{
    public J2SESubImage( J2SEImageResource imageResource,
        int x, int y, int width, int height,
        int rotationDegree, int flippingFlag, String proxyname )
    {
        super( proxyname );
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
        double angle = Math.atan( (double)height/width ) + rotationRad;
        double d = Math.pow( Math.pow( width, 2 ) + Math.pow( height, 2 ), 0.5 );
        
        m_width     = (short)Math.abs( Math.round( d * Math.cos( angle ) ) );
        m_height    = (short)Math.abs( Math.round( d * Math.sin( angle ) ) );
        
        m_transform = new AffineTransform();
        
        m_transform.translate(
            Math.abs( d * Math.cos( angle ) ) / 2.0d,
            Math.abs( d * Math.sin( angle ) ) / 2.0d );
        m_transform.rotate( -rotationRad ); // Y-axis in J2SE points toward the center of the Earth.
        m_transform.translate( -(double)width / 2.0d, -(double)height / 2.0d );
        
        if ( hasHorizontalFlipping )
        {
            m_transform.translate( (double)width / 2.0d, (double)height / 2.0d );
            m_transform.scale( -1, 1 );
            m_transform.translate( -(double)width / 2.0d, -(double)height / 2.0d );
        }
    }

    public void Render( IVideoDriver driver )
    {
		Graphics2D g = (Graphics2D)driver.GetProperty( J2SEVideoDriver.PROPERTY_GRAPHICS );
		int x = driver.GetRenderInfo().c_rasterX;
        int y = driver.GetRenderInfo().c_rasterY;
        AffineTransform transform = new AffineTransform(
            1.0f, 0.0f, 0.0f, 1.0f, x, y );
        
        transform.concatenate( m_transform );
        g.drawImage( m_image, transform, null );
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
        J2SEImageResource imageResource, int width, int height,
        String[] proxynames )
    {
        int nWidth  = imageResource.GetWidth() / width;
        int nHeight = imageResource.GetHeight() / height;

        if ( (nWidth == 0) || (nHeight == 0) )
            return null;

        J2SESubImage[] images = new J2SESubImage[ nWidth * nHeight ];
        String proxyname;
        short x, y = 0;
        int i, j, imgIterator = 0;

        for ( i = nHeight; i != 0; --i )
        {
            x = 0;

            for ( j = nWidth; j != 0; --j )
            {
                if ( proxynames == null )
                    proxyname = null;
                else
                    proxyname = proxynames[ imgIterator ];
                
                images[ imgIterator++ ] = new J2SESubImage( imageResource,
                   x, y, width, height, 0, (byte)0, proxyname );
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
