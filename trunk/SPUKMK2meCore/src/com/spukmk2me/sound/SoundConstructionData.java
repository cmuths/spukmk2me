package com.spukmk2me.sound;

import com.spukmk2me.resource.IResource;
import com.spukmk2me.resource.IResourceConstructionData;

public final class SoundConstructionData implements IResourceConstructionData
{
    public byte GetAssociatedResourceType()
    {
        return IResource.RT_SOUND;
    }
    
    public String   c_path, c_proxyname;
    public byte     c_format;
}
