package com.spukmk2me.scene;

import java.io.IOException;
import java.io.DataInputStream;

import com.spukmk2me.resource.ResourceSet;

public interface ISceneNodeProducer
{
    public ISceneNode ConstructSceneNode( DataInputStream dis, byte nodeType )
        throws IOException;
    public void UpdateResourceSet( ResourceSet resourceSet );
}
