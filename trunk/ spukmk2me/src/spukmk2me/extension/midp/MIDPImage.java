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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package spukmk2me.extension.midp;

import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

import spukmk2me.video.RenderTool;
import spukmk2me.video.IImage;

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
final class MIDPImage extends IImage
{
    private MIDPImage() {}

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
        c_image = Image.createImage( filename );
        //#ifdef __SPUKMK2ME_DEBUG
        System.out.println( " Loaded." );
        //#endif
        
        c_width     = (short)c_image.getWidth();
        c_height    = (short)c_image.getHeight();
        c_useAlpha  = false;        
    }

    /**
     *  Not very regular constructor.
     *  \details Create an image with specific width and height.
     *  @param width The width of new image.
     *  @param height The height of new image.
     */
    public MIDPImage( short width, short height )
    {
        c_width     = width;
        c_height    = height;
        c_useAlpha  = false;
        c_image     = Image.createImage( width, height );
    }

    public void Render( RenderTool renderTool )
    {
        ((Graphics)renderTool.c_rAPI).drawImage( c_image,
            renderTool.c_rasterX, renderTool.c_rasterY,
            Graphics.TOP | Graphics.LEFT );
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
        System.out.print( "Loading images from file: " + filename );
        //#endif

        Image img       = Image.createImage( filename );
        
        int imgWidth    = img.getWidth();
        int imgHeight   = img.getHeight();
        int nImageW     = imgWidth / width;
        int nImageH     = imgHeight / height;
        int nImage      = nImageW * nImageH;
        int _x, _y, imgIterator;

        _x = _y = imgIterator = 0;

        if ( nImage == 0 )
            return null;

        MIDPImage[] images = new MIDPImage[ nImage ];
        int[] rgbData = new int[ width * height ];

        for ( int i = nImageH; i != 0; --i )
        {
            for ( int j = nImageW; j != 0; --j )
            {
                images[ imgIterator ] = new MIDPImage( width, height );
                img.getRGB( rgbData, 0, width, _x, _y, width, height );
                images[ imgIterator ].c_image =
                    Image.createRGBImage( rgbData, width, height, true );
                
                ++imgIterator;
                _x += width;
            }

            _y += height;
            _x = 0;
        }

        //#ifdef __SPUKMK2ME_DEBUG
        System.out.println( " Loaded." );
        //#endif

        return images;
    }

    public Image c_image; //!< Like I said, this class wrap the MIDP Image.
}
