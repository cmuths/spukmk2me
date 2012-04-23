package com.spukmk2me.video;

import com.spukmk2me.resource.IResource;
import com.spukmk2me.resource.IResourceConstructionData;

public final class SubImageConstructionData
    implements IResourceConstructionData
{
    public byte GetAssociatedResourceType()
    {
        return IResource.RT_IMAGE;
    }
    
    public IImageResource   c_resource;
    public String           c_proxyname;
    public int      c_rotationDegree;
    public short    c_x, c_y, c_width, c_height;
    public byte     c_flippingFlags;
}
