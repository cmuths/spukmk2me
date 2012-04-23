package com.spukmk2me.video;

import com.spukmk2me.resource.IResource;
import com.spukmk2me.resource.IResourceConstructionData;

public final class ImageResourceConstructionData
    implements IResourceConstructionData
{
    public byte GetAssociatedResourceType()
    {
        return IResource.RT_IMAGERESOURCE;
    }
    
    public String c_path, c_proxyname;    
}
