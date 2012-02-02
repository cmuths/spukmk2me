package com.spukmk2me.optional.scene;

import java.io.OutputStream;
import java.io.IOException;

import com.spukmk2me.video.IResource;

public interface IResourceSaver
{
    /**
     *  Get the resource type IDs can be saved by this resource.
     *  \details Resource IDs must be unique. There are several standard
     * resource type IDs (which cannot be used by external loader):
     *      Negative value  : Reserved.
     *      0               : Reserved.
     *      1               : Image (the standard IImageResource).
     *      2               : Partial image (the standard ISubImage).
     *      3               : Font (the standard ICFont).
     *  @return Resource IDs.
     */
    public byte[] GetSaveableResourceID();

    public short GetResourceSize( IResource resource, byte resourceTypeID );

    /**
     *  Save the resource.
     *  \details This function must store resource creation data if creation
     * data is available (IResourceCreationData class exists).
     *  @param is Input stream where resource creation info can be read.
     *  @param absoluteSavePath Absolute scene file path that will be saved.
     *  @param platformPathSeparator Path separator of current platform.
     *  @param resourceTypeID Type ID of resource.
     *  @return Loaded resource.
     *  @throws IOException
     */
    public void SaveResource( OutputStream os, IResource resource,
        byte resourceTypeID ) throws IOException;
}
