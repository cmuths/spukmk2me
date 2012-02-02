package com.spukmk2me.spukmk2mesceneeditor.gui;

import com.spukmk2me.video.ICFont;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.spukmk2mesceneeditor.data.CentralData;

public final class MainRenderingPanel extends J2SERenderingPanel
    implements SceneManagerEventListener
{
    public MainRenderingPanel()
    {
    }
    
    ///////////////////////////////////
    //
    public void SetCentralData( CentralData data )
    {
        m_data = data;
        setDevice( data.GetDevice() );
    }

    public void ImageResourceChanged(
        IImageResource imageResource, byte changingCode )
    {
        paint( this.getGraphics() );
    }

    public void ImageChanged( ISubImage image, byte changingCode )
    {
        paint( this.getGraphics() );
    }

    public void FontChanged( ICFont font, byte changingCode )
    {
        paint( this.getGraphics() );
    }

    public void CurrentNodeChanged()
    {
        setDisplayedNode( m_data.GetCurrentNode() );

        if ( m_renderingMode == RENDERINGMODE_PASSIVE )
            RenderScene( 0 );
    }

    public void Reset()
    {
    }

    private CentralData m_data;
}
