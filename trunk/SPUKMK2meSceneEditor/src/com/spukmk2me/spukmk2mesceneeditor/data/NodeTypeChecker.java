package com.spukmk2me.spukmk2mesceneeditor.data;

import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.NullSceneNode;
import com.spukmk2me.scene.ImageSceneNode;
import com.spukmk2me.scene.SpriteSceneNode;
import com.spukmk2me.scene.StringSceneNode;
import com.spukmk2me.scene.TiledLayerSceneNode;

public final class NodeTypeChecker
{
    private NodeTypeChecker() {}

    public static byte GetNodeType( ISceneNode node )
    {
        if ( node == null )
            return NT_UNKNOWN;

        if ( node instanceof NullSceneNode )
            return NT_NULL;

        if ( node instanceof ImageSceneNode )
            return NT_IMAGE;

        if ( node instanceof SpriteSceneNode )
            return NT_SPRITE;

        if ( node instanceof StringSceneNode )
            return NT_STRING;

        if ( node instanceof TiledLayerSceneNode )
            return NT_TILED;

        return NT_UNKNOWN;
    }

    public static final byte NT_UNKNOWN     = -1;
    public static final byte NT_NULL        = 0;
    public static final byte NT_IMAGE       = 1;
    public static final byte NT_SPRITE      = 2;
    public static final byte NT_STRING      = 3;
    public static final byte NT_TILED       = 4;
}