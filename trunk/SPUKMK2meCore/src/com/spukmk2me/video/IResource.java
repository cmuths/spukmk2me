package com.spukmk2me.video;

/**
 *  Interface for resource.
 */
public interface IResource
{
    /* $if SPUKMK2ME_SCENESAVER$ */
    public void SetCreationData( IResourceCreationData creationData );
    public IResourceCreationData GetCreationData();
    /* $endif$ */
}
