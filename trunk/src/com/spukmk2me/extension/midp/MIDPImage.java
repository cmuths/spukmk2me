/*
 *  SPUKMK2me - SPUKMK2 Engine for J2ME platform
 *  Copyright 2010 - 2011  HNYD Team
 *
 *   SPUKMK2me is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *   SPUKMK2me is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with SPUKMK2me.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spukmk2me.extension.midp;

import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

import com.spukmk2me.video.RenderTool;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.IImage;

/**
 *  A wrapper of MIDP Image.
 *  \details This implement use MIDP javax.microedition.lcdui.Image to store
 * image. You may think that it's ironic to create a IImage type, and then
 * create an implement that wrap MIDP Image. If that's true, consider your
 * opinion again. This engine is created not only for regular MIDP rendering
 * API, we want to to use it with other API like OpenGL ES, so there will be
 * more implement of IImage that MIDP API can't handle.\n
 *  MIDP Image varies among platforms, we can't know it structure, but we know
 * one thing: the creator of mobile devices optimized the image for their
 * platforms, so we won't try to invent a new wheel for this
 * MIDP rendering API.
 */
final class MIDPImage implements IImage
{
    /**
     *  Regular constructor.
     *  \details Load an image from file
     *  @param filename The image file name.
     *  @throws IOException If there is any I/O problem when loading.
     */
    public MIDPImage( String filename ) throws IOException
    {
        //#ifdef __SPUKMK2ME_DEBUG
        System.out.print( "Loading image: " + filename );
        //#endif
        Image img = Image.createImage( filename );
        //#ifdef __SPUKMK2ME_DEBUG
        System.out.println( " Loaded." );
        //#endif
        
        CreateImage( img, (short)0, (short)0,
            (short)img.getWidth(), (short)img.getHeight(), 0 );
    }

    public MIDPImage( MIDPImage image, short x, short y,
        short width, short height, int rotationDegree, byte flippingFlag )
    {
        int     midpTransformationFlag  = 0;
        boolean hasVerticalFlipping     = false;

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

                hasVerticalFlipping = true;
            }
        }

        while ( rotationDegree >= 0x01680000 ) // larger or equal 360
            rotationDegree -= 0x01680000;

        while ( rotationDegree < 0 )
            rotationDegree += 0x01680000;

        // I don't know why they come up with those Sprite constants.
        if ( hasVerticalFlipping )
        {
            switch ( rotationDegree )
            {
                case 0:
                    midpTransformationFlag = 2; // Sprite.TRANS_MIRROR
                    break;

                case 0x005A0000:
                    midpTransformationFlag = 4; // Sprite.TRANS_MIRROR_ROT270
                    break;

                case 0x00B40000:
                    midpTransformationFlag = 1; // Sprite.TRANS_MIRROR_ROT180
                    break;

                case 0x010E0000:
                    midpTransformationFlag = 7; // Sprite.TRANS_MIRROR_ROT90
                    break;
            }
        }
        else
        {
            switch ( rotationDegree )
            {
                case 0:
                    midpTransformationFlag = 0; // Sprite.TRANS_NONE
                    break;

                case 0x005A0000:
                    midpTransformationFlag = 6; // Sprite.TRANS_ROT270
                    break;

                case 0x00B40000:
                    midpTransformationFlag = 3; // Sprite.TRANS_ROT180
                    break;

                case 0x010E0000:
                    midpTransformationFlag = 5; // Sprite.TRANS_ROT90
                    break;
            }
        }

        CreateImage( image.GetImage(),
            (short)(x + image.m_x), (short)(y + image.m_y), width, height,
            midpTransformationFlag );
    }

    private void CreateImage( Image image, short x, short y,
        short width, short height, int midpTransformationFlag )
    {
        m_image                     = image;
        m_x                         = x;
        m_y                         = y;
        m_width                     = width;
        m_height                    = height;
        m_midpTransformationFlag    = midpTransformationFlag;
    }

    public void Render( RenderTool renderTool )
    {
        ((Graphics)renderTool.c_rAPI).drawRegion( m_image,
            m_x, m_y, m_width, m_height, m_midpTransformationFlag,
            renderTool.c_rasterX, renderTool.c_rasterY,
            Graphics.TOP | Graphics.LEFT );
    }

    public short GetWidth()
    {
        return m_width;
    }

    public short GetHeight()
    {
        return m_height;
    }

    public Image GetImage()
    {
        return m_image;
    }

    /**
     *  Load the image and chop it to small pieces.
     *  \details This function will be used when you want to extract small
     * images from a large one.
     *  @param filename The image file name.
     *  @param width The width of small images.
     *  @param height The height of small images.
     *  @return Array of small images.
     *  @throws IOException If there is any I/O problem when loading.
     */
    public static MIDPImage[] LoadImagesFromFile( String filename,
        short width, short height ) throws IOException
    {
        //#ifdef __SPUKMK2ME_DEBUG
        System.out.println( "Loading image batch..." );
        //#endif

        MIDPImage img   = new MIDPImage( filename );
        
        int imgWidth    = img.GetWidth();
        int imgHeight   = img.GetHeight();
        int nImageW     = imgWidth / width;
        int nImageH     = imgHeight / height;
        int nImage      = nImageW * nImageH;
        int _x, _y, imgIterator;

        _x = _y = imgIterator = 0;

        if ( nImage == 0 )
            return null;

        MIDPImage[] images = new MIDPImage[ nImage ];

        for ( int i = nImageH; i != 0; --i )
        {
            for ( int j = nImageW; j != 0; --j )
            {
                images[ imgIterator ] =
                    new MIDPImage( img, (short)_x, (short)_y, width, height,
                        0, (byte)0 );
                ++imgIterator;
                _x += width;
            }

            _y += height;
            _x = 0;
        }

        //#ifdef __SPUKMK2ME_DEBUG
        System.out.println( "Image batch loaded." );
        //#endif

        return images;
    }

    private Image   m_image;
    private int     m_midpTransformationFlag;
    private short   m_x, m_y, m_width, m_height;
}
