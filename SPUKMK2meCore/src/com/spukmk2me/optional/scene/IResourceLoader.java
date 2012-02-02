package com.spukmk2me.optional.scene;

import java.io.InputStream;
import java.io.IOException;

import com.spukmk2me.video.IResource;

public interface IResourceLoader
{
    /**
     *  Get the resource type IDs can be loaded by this resource.
     *  \details Resource IDs must be unique. There are several standard
     * resource type IDs (which cannot be used by external loader):
     *      Negative value  : Reserved.
     *      0               : Reserved.
     *      1               : Image (the standard IImageResource).
     *      2               : Partial image (the standard ISubImage).
     *      3               : Font (the standard ICFont).
     *  @return Resource IDs.
     */
    public byte[] GetLoadableResourceID();

    /**
     *  Load the resource.
     *  \details This function must store resource creation data if creation
     * data is available (IResourceCreationData class exists).
     *  @param is Input stream where resource creation info can be read.
     *  @param resourceTypeID Type ID of resource,
     *  @return Loaded resource.
     *  @throws IOException
     */
    public IResource LoadResource( InputStream is, byte resourceTypeID )
        throws IOException;
}
