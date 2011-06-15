package com.spukmk2me.video;

/**
 *  Presents image resource. Simply put, an instance of this class presents
 * an image that is loaded from somewhere.
 *  \details This class is implemented in video driver side.
 */
public interface IImageResource
{
    public short GetWidth();
    public short GetHeight();
}
