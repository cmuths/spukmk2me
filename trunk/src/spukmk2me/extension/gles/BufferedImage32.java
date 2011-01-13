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

package spukmk2me.extension.gles;

import java.io.IOException;
import java.nio.IntBuffer;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.lcdui.Image;

import spukmk2me.video.RenderTool;
import spukmk2me.video.IImage;

/**
 *  32-bit image type.
 *  \details This image will be used for OpenGL ES driver (may be, of course,
 * we haven't completed GL driver yet).
 */
public final class BufferedImage32 extends IImage
{
    private BufferedImage32() {}

    /**
     *  Regular constructor.
     *  \details Load an image from file
     *  @param filename The image file name.
     *  @throws IOException If there is any I/O problem when loading.
     */
    public BufferedImage32( String filename ) throws IOException
    {
        //System.out.println( filename );
        Image img = Image.createImage( filename );
        //System.out.println( img );
        
        c_width     = (short)img.getWidth();
        c_height    = (short)img.getHeight();
        c_useAlpha  = false;

        c_image     = IntBuffer.wrap( new int[ c_width * c_height ] );
        img.getRGB( c_image.array(), 0, c_width, 0, 0, c_width, c_height );
        c_image.rewind();

        c_internalFormat = IF_8A8R8G8B;
    }

    /**
     *  Not very regular constructor.
     *  \details Create an image with specific width and height.
     *  @param width The width of new image.
     *  @param height The height of new image.
     */
    public BufferedImage32( short width, short height )
    {
        c_width     = width;
        c_height    = height;
        c_useAlpha  = false;
        c_image     = IntBuffer.wrap( new int[ width * height ] );
        c_image.rewind();
        c_internalFormat = IF_NONE;
    }

    public void Render( RenderTool renderTool )
    {
        IntBuffer vertexBuffer = (IntBuffer)renderTool.c_reserved1;

        vertexBuffer.rewind();
        vertexBuffer.put( renderTool.c_rasterX << 16 );
        vertexBuffer.put( renderTool.c_scrHeight - (renderTool.c_rasterY + c_height - 1) << 16 );
        vertexBuffer.put( renderTool.c_rasterX + c_width - 1 << 16 );
        vertexBuffer.put( renderTool.c_scrHeight - (renderTool.c_rasterY + c_height - 1) << 16 );
        vertexBuffer.put( renderTool.c_rasterX << 16 );
        vertexBuffer.put( renderTool.c_scrHeight - renderTool.c_rasterY << 16 );
        vertexBuffer.put( renderTool.c_rasterX + c_width - 1 << 16 );
        vertexBuffer.put( renderTool.c_scrHeight - renderTool.c_rasterY << 16 );
        vertexBuffer.rewind();

        ((GL10)renderTool.c_rAPI).glTexImage2D( GL10.GL_TEXTURE_2D, 0,
            4, c_width, c_height, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
            c_image );
        ((GL10)renderTool.c_rAPI).glDrawArrays( GL10.GL_TRIANGLE_STRIP, 0, 4);
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
    public static BufferedImage32[] LoadImagesFromFile( String filename,
        short width, short height ) throws IOException
    {
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

        BufferedImage32[] images = new BufferedImage32[ nImage ];

        for ( int i = nImageH; i != 0; --i )
        {
            for ( int j = nImageW; j != 0; --j )
            {
                images[ imgIterator ] = new BufferedImage32( width, height );
                img.getRGB( images[ imgIterator ].c_image.array(), 0, width,
                    _x, _y, width, height );

                ++imgIterator;
                _x += width;
            }

            _y += height;
            _x = 0;
        }

        return images;
    }

    public static final byte IF_NONE        = 0;
    public static final byte IF_8A8R8G8B    = 1;

    public IntBuffer c_image;       //!< Buffer for image data.
    public byte c_internalFormat;   //!< As the name implies.
}
