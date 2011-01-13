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
 *  Regular 32-bit 8A8R8G8B image type.
 *  \details Used for customized effects. Due to the limitation of J2ME
 * platform, we won't provide effects to keep the package as small as possible.
 * You should create them on your own.\n
 *  This type of IImage is intended to use only with MIDP API, so don't expect
 * too much from this class.
 */
public final class RawImage32 extends IImage
{
    private RawImage32() {}

    /**
     *  Regular constructor.
     *  \details Load an image from file
     *  @param filename The image file name.
     *  @throws IOException If there is any I/O problem when loading.
     */
    public RawImage32( String filename ) throws IOException
    {
        //System.out.println( filename );
        Image img = Image.createImage( filename );
        //System.out.println( img );

        c_width     = (short)img.getWidth();
        c_height    = (short)img.getHeight();
        c_useAlpha  = false;
        c_image     = new int[ c_width * c_height ];
        img.getRGB( c_image, 0, c_width, 0, 0, c_width, c_height );
    }

    /**
     *  Not very regular constructor.
     *  \details Create an image with specific width and height.
     *  @param width The width of new image.
     *  @param height The height of new image.
     */
    public RawImage32( short width, short height )
    {
        c_width     = width;
        c_height    = height;
        c_useAlpha  = false;
        c_image     = new int[ width * height ];
    }

    public void Render( RenderTool renderTool )
    {
        ((Graphics)renderTool.c_rAPI).drawRGB( c_image, 0, c_width,
            renderTool.c_rasterX, renderTool.c_rasterY,
            c_width, c_height, c_useAlpha );
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
    public static RawImage32[] LoadImagesFromFile( String filename,
        short width, short height ) throws IOException
    {
        MIDPImage[] srcImages = MIDPImage.LoadImagesFromFile(
            filename, width, height );

        if ( srcImages == null )
            return null;

        RawImage32[] images = new RawImage32[ srcImages.length ];

        for ( int i = 0; i != images.length; ++i )
        {
            images[ i ] = new RawImage32 ( width, height );
            srcImages[ i ].c_image.getRGB( images[ i ].c_image, 0, width, 0, 0,
                width, height );
        }

        return images;
    }

    public int[] c_image; //!< Buffer for image data.
}
