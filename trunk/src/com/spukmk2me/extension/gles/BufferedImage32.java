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

package com.spukmk2me.extension.gles;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.lcdui.Image;

import com.spukmk2me.video.RenderTool;
import com.spukmk2me.video.IImage;

/**
 *  32-bit image type.
 *  \details This image will be used for OpenGL ES driver (may be, of course,
 * we haven't completed GL driver yet).
 */
public final class BufferedImage32 implements IImage
{
    /**
     *  Regular constructor.
     *  \details Load an image from file
     *  @param filename The image file name.
     *  @throws IOException If there is any I/O problem when loading.
     */
    public BufferedImage32( String filename ) throws IOException
    {
        Image img = Image.createImage( filename );
        
        m_x = m_y = 0;
        m_width         = (short)img.getWidth();
        m_height        = (short)img.getHeight();
        
        int[] buffer    = new int[ m_width * m_height ];

        img.getRGB( buffer, 0, m_width, 0, 0, m_width, m_height );
        m_image =
            ByteBuffer.allocateDirect( m_width * m_height << 2 ).asIntBuffer();
        m_internalFormat = IF_8A8R8G8B;
    }

    private BufferedImage32( BufferedImage32 srcImage, short x, short y,
        short width, short height )
    {
        m_image     = srcImage.m_image;
        m_x         = x;
        m_y         = y;
        m_width     = width;
        m_height    = height;
    }

    public short GetWidth()
    {
        return m_width;
    }

    public short GetHeight()
    {
        return m_height;
    }

    public void Render( RenderTool renderTool )
    {
        /*IntBuffer vertexBuffer = (IntBuffer)renderTool.c_reserved1;

        vertexBuffer.rewind();
        vertexBuffer.put( renderTool.c_rasterX << 16 );
        vertexBuffer.put( renderTool.c_scrHeight -
            (renderTool.c_rasterY + m_height - 1) << 16 );
        vertexBuffer.put( renderTool.c_rasterX + m_width - 1 << 16 );
        vertexBuffer.put( renderTool.c_scrHeight -
            (renderTool.c_rasterY + m_height - 1) << 16 );
        vertexBuffer.put( renderTool.c_rasterX << 16 );
        vertexBuffer.put( renderTool.c_scrHeight -
            renderTool.c_rasterY << 16 );
        vertexBuffer.put( renderTool.c_rasterX + m_width - 1 << 16 );
        vertexBuffer.put( renderTool.c_scrHeight -
            renderTool.c_rasterY << 16 );
        vertexBuffer.rewind();

        ((GL10)renderTool.c_rAPI).glTexImage2D( GL10.GL_TEXTURE_2D, 0,
            4, m_width, m_height, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
            m_image );
        ((GL10)renderTool.c_rAPI).glDrawArrays( GL10.GL_TRIANGLE_STRIP, 0, 4);*/
    }    

    public static BufferedImage32 CreateRegionalImage(
        BufferedImage32 srcImage, short x, short y, short width, short height )
    {
        return new BufferedImage32( srcImage, x, y, width, height );
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
        BufferedImage32 img = new BufferedImage32( filename );
        int imgWidth    = img.GetWidth();
        int imgHeight   = img.GetHeight();
        int nImageW     = imgWidth / width;
        int nImageH     = imgHeight / height;
        int nImage      = nImageW * nImageH;
        int imgIterator;
        short _x, _y;

        imgIterator = 0;
        _x = _y = 0;

        if ( nImage == 0 )
            return null;

        BufferedImage32[] images = new BufferedImage32[ nImage ];

        for ( int i = nImageH; i != 0; --i )
        {
            for ( int j = nImageW; j != 0; --j )
            {
                images[ imgIterator ] = new BufferedImage32( img,
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

    private IntBuffer   m_image;
    private short       m_x, m_y, m_width, m_height;
    private byte        m_internalFormat;
}
