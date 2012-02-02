package com.spukmk2me.spukmk2mesceneeditor.gui;

import com.spukmk2me.Device;
import com.spukmk2me.scene.ISceneNode;

public interface SceneDisplaySurface
{
    public void setDevice( Device device );
    public void setDisplayedNode( ISceneNode node );
    public void setDisplayedSize( int width, int height );
    public int getDisplayedWidth();
    public int getDisplayedHeight();
    public void setOrigin( int x, int y );
    public int getOriginX();
    public int getOriginY();
    public void setRenderingMode( byte mode, long mspf );

    public static final byte RENDERINGMODE_PASSIVE  = 0;
    public static final byte RENDERINGMODE_ACTIVE   = 1;
}
