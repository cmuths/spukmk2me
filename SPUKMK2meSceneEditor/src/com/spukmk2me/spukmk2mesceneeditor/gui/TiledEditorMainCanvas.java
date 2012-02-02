package com.spukmk2me.spukmk2mesceneeditor.gui;

import com.spukmk2me.Device;
import com.spukmk2me.scene.SceneManager;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.NullSceneNode;
import com.spukmk2me.scene.TiledLayerSceneNode;
import com.spukmk2me.extension.j2se.J2SEFileSystem;
import com.spukmk2me.extension.j2se.J2SEVideoDriver;

public final class TiledEditorMainCanvas extends J2SERenderingPanel
{
    public TiledEditorMainCanvas()
    {
        J2SEVideoDriver vdrv    = new J2SEVideoDriver();
        SceneManager    smng    = new SceneManager( vdrv, 1 );
        Device device           =
            Device.CreateSPUKMK2meDevice( vdrv, null, null,
            new J2SEFileSystem(), smng );

        setDevice( device );
        m_device = device;

        // setup cursor node
        m_superNode  = new NullSceneNode();
        m_viewLayer  = new NullSceneNode();
        m_superNode.AddChild( m_viewLayer );
        m_cursorNode = new NullSceneNode();
        m_superNode.AddChild( m_cursorNode );
        this.setDisplayedNode( m_superNode );

        // setup grid layer
        m_gridLayer = new NullSceneNode();
        m_gridLayer.c_enable = false;
        m_superNode.AddChild( m_gridLayer );

        vdrv.PrepareRenderingContext();
    }

    public void setNode( TiledLayerSceneNode node )
    {
        TiledLayerSceneNode.TiledLayerSceneNodeInfoData info =
            (TiledLayerSceneNode.TiledLayerSceneNodeInfoData)node.c_infoData;

        m_temporaryData = new byte[ info.c_terrainData.length ];
        System.arraycopy( info.c_terrainData, 0, m_temporaryData, 0,
            m_temporaryData.length );

        m_temporaryNode = new TiledLayerSceneNode();
        m_temporaryNode.SetupTiledLayer(
            info.c_images, info.c_sprites, info.c_spriteSpeed,
            m_temporaryData, info.c_tableWidth, info.c_tableHeight,
            info.c_stepX, info.c_stepY );

        this.setDisplayedSize( info.c_tableWidth * info.c_stepX,
            info.c_tableHeight * info.c_stepY );

        // node setup
        m_viewLayer.AddChild( m_temporaryNode );

        J2SELineSceneNode lineNode;

        lineNode = new J2SELineSceneNode();
        lineNode.SetupLine( info.c_stepX, (short)1, 0x00000000 );
        lineNode.SetPosition( (short)0, (short)0 );
        m_cursorNode.AddChild( lineNode );
        
        lineNode = new J2SELineSceneNode();
        lineNode.SetupLine( (short)1, info.c_stepY, 0x00000000 );
        lineNode.SetPosition( (short)0, (short)0 );
        m_cursorNode.AddChild( lineNode );

        lineNode = new J2SELineSceneNode();
        lineNode.SetupLine( (short)1, info.c_stepY, 0x00000000 );
        lineNode.SetPosition( info.c_stepX, (short)0 );
        m_cursorNode.AddChild( lineNode );

        lineNode = new J2SELineSceneNode();
        lineNode.SetupLine( info.c_stepX, (short)1, 0x00000000 );
        lineNode.SetPosition( (short)0, info.c_stepY );
        m_cursorNode.AddChild( lineNode );

        // build grid layer
        {
            short coord     = (short)(info.c_stepX - 1);
            short length    = (short)(info.c_tableHeight * info.c_stepY);

            m_gridLayer.DropChildren();

            for ( int i = 0; i != info.c_tableWidth; ++i )
            {
                lineNode = new J2SELineSceneNode();
                lineNode.SetupLine( (short)1, length, 0x00000000 );
                lineNode.SetPosition( coord, (short)0 );
                m_gridLayer.AddChild( lineNode );
                coord += info.c_stepX;
            }

            coord   = (short)(info.c_stepY - 1);
            length  = (short)(info.c_tableWidth * info.c_stepX);

            for ( int i = 0; i != info.c_tableHeight; ++i )
            {
                lineNode = new J2SELineSceneNode();
                lineNode.SetupLine( length, (short)1, 0x00000000 );
                lineNode.SetPosition( (short)0, coord );
                m_gridLayer.AddChild( lineNode );
                coord += info.c_stepY;
            }
        }
    }

    public byte[] getEditedData()
    {
        return m_temporaryData;
    }

    public ISceneNode getCursorNode()
    {
        return m_cursorNode;
    }

    public void setGridVisible( boolean visibility )
    {
        m_gridLayer.c_enable = visibility;
        this.repaint();
    }


    private Device              m_device;
    private TiledLayerSceneNode m_temporaryNode;
    private ISceneNode          m_cursorNode, m_viewLayer,
                                m_superNode, m_gridLayer;
    private byte[]              m_temporaryData;
}
