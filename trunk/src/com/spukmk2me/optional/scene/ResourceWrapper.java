package com.spukmk2me.optional.scene;

public class ResourceWrapper
{
    public ResourceWrapper( Object data, String proxyName )
    {
        c_data      = data;
        c_proxyName = proxyName;
    }

    public String   c_proxyName;
    public Object   c_data;
}
