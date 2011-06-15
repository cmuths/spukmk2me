package com.spukmk2me.video;

/**
 *  Hold data for sub image, which will be visible to ImageSceneNode.
 *  \details This class is implemented in video driver side.
 */
public interface ISubImage
{
    IImageResource GetImageResource();
    public void Render( IVideoDriver driver );
    public short GetWidth();
    public short GetHeight();
}
