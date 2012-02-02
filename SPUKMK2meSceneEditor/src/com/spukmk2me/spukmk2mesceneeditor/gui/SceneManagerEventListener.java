package com.spukmk2me.spukmk2mesceneeditor.gui;

import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.ICFont;
import com.spukmk2me.spukmk2mesceneeditor.data.CentralData;

public interface SceneManagerEventListener
{
    public void ImageResourceChanged(
        IImageResource imageResource, byte changingCode );

    public void ImageChanged( ISubImage image, byte changingCode );

    public void FontChanged( ICFont font, byte changingCode );

    public void CurrentNodeChanged();

    public void SetCentralData( CentralData data );

    public void Reset();

    public static final byte CHANGED    = 0;
    public static final byte DELETED    = 1;
    public static final byte ADDED      = 2;
}
