package com.spukmk2me.video;

import com.spukmk2me.resource.IResource;
import com.spukmk2me.resource.IResourceConstructionData;

public final class BitmapFontConstructionData
    implements IResourceConstructionData
{
    public byte GetAssociatedResourceType()
    {
        return IResource.RT_BITMAPFONT;
    }

    public String c_path, c_proxyname;
}
