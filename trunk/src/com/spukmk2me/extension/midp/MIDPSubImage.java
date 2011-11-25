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

import com.spukmk2me.debug.Logger;
import javax.microedition.lcdui.Graphics;

import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.IImageResource;

/**
 *  An implement of ISubImage for MIDP video driver.
 */
final class MIDPSubImage
    //#ifdef __SPUKMK2ME_SCENESAVER
//#     extends ISubImage
    //#else
    implements ISubImage
    //#endif
{
    public MIDPSubImage( MIDPImageResource imageResource,
        short x, short y, short width, short height,
        int rotationDegree, byte flippingFlag )
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

        // Assign to final values
        m_imageResource             = imageResource;
        m_x                         = x;
        m_y                         = y;
        m_width                     = width;
        m_height                    = height;
        m_midpTransformationFlag    = midpTransformationFlag;
    }

    public IImageResource GetImageResource()
    {
        return m_imageResource;
    }

    public void Render( IVideoDriver driver )
    {
        ((VideoDriver_MIDP)driver).GetMIDPGraphics().drawRegion(
            m_imageResource.GetMIDPImage(),
            m_x, m_y, m_width, m_height,
            m_midpTransformationFlag,
            driver.GetRenderInfo().c_rasterX, driver.GetRenderInfo().c_rasterY,
            MIDP_ANCHOR );
    }

    public short GetWidth()
    {
        return m_width;
    }

    public short GetHeight()
    {
        return m_height;
    }

    public static MIDPSubImage[] CreateSubImagesFromResource(
        MIDPImageResource imageResource, short width, short height )
    {
        //#ifdef __SPUKMK2ME_DEBUG
//#         Logger.Log(
//#             "Creating image batch from " + imageResource.toString() +
//#             ", w = " + width + ", h = " + height + "..." );
        //#endif

        int imgWidth    = imageResource.GetWidth();
        int imgHeight   = imageResource.GetHeight();
        int nImageW     = imgWidth / width;
        int nImageH     = imgHeight / height;
        int nImage      = nImageW * nImageH;
        int _x, _y, imgIterator;

        _x = _y = imgIterator = 0;

        if ( nImage == 0 )
            return null;

        MIDPSubImage[] images = new MIDPSubImage[ nImage ];

        for ( int i = nImageH; i != 0; --i )
        {
            for ( int j = nImageW; j != 0; --j )
            {
                images[ imgIterator ] =
                    new MIDPSubImage( imageResource, (short)_x, (short)_y,
                        width, height, 0, (byte)0 );
                ++imgIterator;
                _x += width;
            }

            _y += height;
            _x = 0;
        }

        //#ifdef __SPUKMK2ME_DEBUG
//#         Logger.Log( "Created.\n" );
        //#endif

        return images;
    }

    private static final int    MIDP_ANCHOR = Graphics.TOP | Graphics.LEFT;

    private final MIDPImageResource m_imageResource;
    private final int               m_midpTransformationFlag;
    private final short             m_x, m_y, m_width, m_height;
}
