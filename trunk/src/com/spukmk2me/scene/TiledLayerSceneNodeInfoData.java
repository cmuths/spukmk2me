package com.spukmk2me.scene;

import com.spukmk2me.video.ISubImage;

public class TiledLayerSceneNodeInfoData
{
    public ISubImage[][]    c_sprites;
    public ISubImage[]      c_images;
    public int[]            c_spriteSpeed;
    public byte[]           c_terrainData;
    public short            c_tableWidth, c_tableHeight,
                            c_stepX, c_stepY,
                            c_viewX, c_viewY, c_viewWidth, c_viewHeight;
    public boolean          c_repeatedView;
}
