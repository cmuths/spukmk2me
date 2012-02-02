/*
 *  SPUKMK2me - SPUKMK2 Engine for J2ME platform
 *  Copyright 2010 - 2011  HNYD Team
 *
 *   SPUKMK2me is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *   SPUKMK2me is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with SPUKMK2me.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spukmk2me.scene;

import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.RenderInfo;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */

/**
 *  An essential part of SPUKMK2me rendering engine.
 *  \details Manage scene nodes and animators (since animators are
 * scene nodes). Implements of scene manager must have a way to ensure that in
 * a rendering sequence, animators must be activated before scene nodes.
 *  @see com.spukmk2me.SPUKMK2Device
 *  @see com.spukmk2me.video.IVideoDriver
 */
public final class SceneManager
{
    /**
     *  A constructor
     *  @param videoDriver Current video driver.
     *  @param soundMonitor Current sound monitor.
     *  @param numberOfLayers Number of layers you want to create.
     */
    public SceneManager( IVideoDriver videoDriver, int numberOfLayers )
    {
        m_nLayer    = numberOfLayers;
        m_vdriver   = videoDriver;

        // Initilaise
        ISceneNode mark = new NullSceneNode();

        m_rootNode      = new NullSceneNode();
        m_rootNode.c_x  = m_rootNode.c_y = 0;

        m_rootNode.c_next   = m_rootNode.c_prev = mark;
        mark.c_next         = mark.c_prev       = m_rootNode;

        m_layerNodes = new ISceneNode[ m_nLayer ];

        for ( int i = 0; i != m_nLayer; ++i )
        {
            m_layerNodes[ i ] = new NullSceneNode();
            //m_layerNodes[ i ].c_x   = m_layerNodes[ i ].c_y = 0;
            m_rootNode.AddChild( m_layerNodes[ i ] );
        }

        // Rendering initialisation
        m_stack     = new ISceneNode[ DEFAULT_STACK_SIZE ];
        m_finish    = new ISceneNode[ DEFAULT_STACK_SIZE ];
        m_stackX    = new short[ DEFAULT_STACK_SIZE ];
        m_stackY    = new short[ DEFAULT_STACK_SIZE ];
    }

    public void ChangeStackSize( int newStackSize )
    {
        m_stack     = new ISceneNode[ newStackSize ];
        m_finish    = new ISceneNode[ newStackSize ];
        m_stackX    = new short[ newStackSize ];
        m_stackY    = new short[ newStackSize ];
    }

    /**
     *  Render all scene nodes managed by this scene manager.
     */
    public void RenderAll()
    {
        RenderSceneNode( m_rootNode, (short)0, (short)0, true );
    }

    public IVideoDriver GetVideoDriver()
    {
        return m_vdriver;
    }    

    /**
     *  Get the root of animators or the root of each layer.
     *  @param index If index = -1, this function returns the root of
     * animators. Otherwise, it'll return the root node of layer associated
     * with index.
     * @return The special node.
     */
    public ISceneNode GetSpecialNode( int index )
    {
        return m_layerNodes[ index ];
    }    

    /**
     *  Remove all scene node. All layers go to ( 0, 0 ).
     */
    public void Clear()
    {
        for ( int i = 0; i != m_nLayer; ++i )
        {
            m_layerNodes[ i ].DropChildren();
            m_layerNodes[ i ].SetPosition( (short)0, (short)0 );
            m_layerNodes[ i ].c_visible = m_layerNodes[ i ].c_enable = true;
        }
    }

    /**
     *  Render a scene node.
     *  @param node The scene node to render.
     *  @param x The absolute X coordinate to render the node.
     *  @param y The absolute Y coordinate to render the node.
     *  @param renderAtOrigin Set the raster point to the origin of the
     * device. Parameters x and y will be ignored if renderAtOrigin is true.
     */
    public void RenderSceneNode( ISceneNode node, short x, short y,
        boolean renderAtOrigin )
    {
        /* $if SPUKMK2ME_DEBUG$ */
        if ( node == null )
        {
            Logger.Trace( "Rendered node is null." );
            return;
        }
        /* $endif$ */

        ISceneNode  iterator;
        RenderInfo  renderInfo = m_vdriver.GetRenderInfo();
        int         topStack = 0;

        m_stack[ 0 ]    = node;
        m_finish[ 0 ]   = node.c_next;

        if ( renderAtOrigin )
        {
            int origin = m_vdriver.GetOrigin();

            renderInfo.c_rasterX = (short)(origin >> 16);
            renderInfo.c_rasterY = (short)(origin & 0x0000FFFF);
        }
        else
        {
            renderInfo.c_rasterX = x;
            renderInfo.c_rasterY = y;
        }

        while ( topStack != -1 )
        {
            if ( m_stack[ topStack ] == m_finish[ topStack ] )
            {
                m_stack[ topStack ]     = null;
                m_finish[ topStack ]    = null;
                renderInfo.c_rasterX -= m_stackX[ topStack ];
                renderInfo.c_rasterY -= m_stackY[ topStack-- ];
            }
            else
            {
                iterator            = m_stack[ topStack ];
                m_stack[ topStack ] = iterator.c_next;

                if ( iterator.c_enable )
                {
                    renderInfo.c_rasterX += iterator.c_x;
                    renderInfo.c_rasterY += iterator.c_y;

                    if ( iterator.c_visible )
                        iterator.Render( m_vdriver );

                    if ( iterator.c_children == null )
                    {
                        renderInfo.c_rasterX -= iterator.c_x;
                        renderInfo.c_rasterY -= iterator.c_y;
                    }
                    else
                    {
                        m_stack[ ++topStack ]   = iterator.c_children.c_next;
                        m_finish[ topStack ]    = iterator.c_children;
                        m_stackX[ topStack ]    = iterator.c_x;
                        m_stackY[ topStack ]    = iterator.c_y;
                    }
                }
            }
        }
    }

    private static final int DEFAULT_STACK_SIZE = 20;

    private IVideoDriver    m_vdriver;
    private ISceneNode      m_rootNode;
    private ISceneNode[]    m_layerNodes;
    private ISceneNode[]    m_stack, m_finish;
    private short[]         m_stackX, m_stackY;
    private int             m_nLayer;
}
