package com.spukmk2me.animation;

import com.spukmk2me.scene.ISceneNode;

public final class ObjectNodeInfo
{
    public ObjectNodeInfo()
    {
        c_dependantFlags = DEPEND_X | DEPEND_Y | DEPEND_Z;
    }
    
    public static final byte DEPEND_X   = 0x01;
    public static final byte DEPEND_Y   = 0x02;
    public static final byte DEPEND_Z   = 0x04;
    
    public ISceneNode   c_node;
    public short        c_originalShiftX;
    public short        c_shiftX, c_shiftY, c_shiftZ;
    public byte         c_dependantFlags;
}
