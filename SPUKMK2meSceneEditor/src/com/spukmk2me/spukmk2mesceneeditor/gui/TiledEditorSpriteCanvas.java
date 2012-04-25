package com.spukmk2me.spukmk2mesceneeditor.gui;

import com.spukmk2me.Device;
import com.spukmk2me.Util;
import com.spukmk2me.scene.SceneManager;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.NullSceneNode;
import com.spukmk2me.scene.TiledLayerSceneNode;
import com.spukmk2me.extension.j2se.J2SEFileSystem;
import com.spukmk2me.extension.j2se.J2SEVideoDriver;
import com.spukmk2me.scene.SpriteSceneNode;

public final class TiledEditorSpriteCanvas extends J2SERenderingPanel
{
    public TiledEditorSpriteCanvas()
    {
        J2SEVideoDriver vdrv    = new J2SEVideoDriver();
        SceneManager    smng    = new SceneManager( vdrv, 1 );
        Device device           =
            Device.CreateSPUKMK2meDevice( vdrv, null, null,
            new J2SEFileSystem(), smng );

        setDevice( device );

        // setup cursor node
        m_superNode     = new NullSceneNode();
        m_viewLayer     = new NullSceneNode();
        m_superNode.AddChild( m_viewLayer );
        m_cursorNode    = new NullSceneNode();
        m_superNode.AddChild( m_cursorNode );
        vdrv.PrepareRenderingContext();
        
        this.setDisplayedNode( m_superNode );
    }
    
    public ISceneNode getCursorNode()
    {
        return m_cursorNode;
    }

    public void setNode( TiledLayerSceneNode node )
    {
        TiledLayerSceneNode.TiledLayerSceneNodeInfoData info =
            (TiledLayerSceneNode.TiledLayerSceneNodeInfoData)node.c_infoData;
        SpriteSceneNode spriteNode;
        short x = 0;

        if ( info.c_sprites != null )
        {
            for ( int i = 0; i != info.c_sprites.length; ++i )
            {
                spriteNode = new SpriteSceneNode( info.c_sprites[ i ] );
                spriteNode.SetAnimating( SpriteSceneNode.MODE_ANIMATING,
                    0, info.c_sprites[ i ].length,
                    Util.FPDiv( 1000 << 1000, info.c_spriteSpeed[ i ] ), 0 );
                spriteNode.SetPosition( x, (short)0 );
                m_viewLayer.AddChild( spriteNode );
                x += info.c_stepX;
            }
        }
            

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
        
        byte renderingMode;
        
        if ( info.c_sprites == null )
            renderingMode = RENDERINGMODE_PASSIVE;
        else if ( info.c_sprites.length == 0 )
            renderingMode = RENDERINGMODE_PASSIVE;
        else
            renderingMode = RENDERINGMODE_ACTIVE;
        
        this.setRenderingMode( renderingMode, 33 );
        this.setDisplayedNode( m_superNode );
    }

    private ISceneNode  m_cursorNode, m_superNode, m_viewLayer;
}
