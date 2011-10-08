package com.spukmk2me.scene;

import com.spukmk2me.video.ISubImage;

public class TiledLayerSceneNodeInfoData
{
    public ISubImage[][]    c_sprites;
    public ISubImage[]      c_images;
    public int[]            c_spriteSpeed;
    public byte[]           c_terrainData;
    public short            c_width, c_height, c_startX, c_startY,
                            c_step1X, c_step1Y, c_step2X, c_step2Y;
}
