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
import com.spukmk2me.video.RenderTool;

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

        m_animatorRootNode = new NullSceneNode();
        ISceneNode.AddSceneNode( m_animatorRootNode, m_rootNode );

        m_layerNodes = new ISceneNode[ m_nLayer ];

        for ( int i = 0; i != m_nLayer; ++i )
        {
            m_layerNodes[ i ] = new NullSceneNode();
            //m_layerNodes[ i ].c_x   = m_layerNodes[ i ].c_y = 0;
            ISceneNode.AddSceneNode( m_layerNodes[ i ], m_rootNode );
        }

        // Rendering initialisation
        m_stack     = new ISceneNode[ STACK_SIZE ];
        m_finish    = new ISceneNode[ STACK_SIZE ];
        m_stackX    = new short[ STACK_SIZE ];
        m_stackY    = new short[ STACK_SIZE ];
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
        return ( index == -1 )? m_animatorRootNode : m_layerNodes[ index ];
    }    

    /**
     *  Remove all scene node. All layers go to ( 0, 0 ).
     */
    public void Clear()
    {
        ISceneNode iterator, finish;

        for ( int i = 0; i != m_nLayer; ++i )
        {
            m_layerNodes[ i ].DropChildren();

            /*if ( m_layerNodes[ i ].c_children != null )
            {
                finish      = m_layerNodes[ i ].c_children;
                iterator    = finish.c_next;

                while ( iterator != finish )
                {
                    iterator = iterator.c_next;
                    iterator.c_prev.Drop();
                }
            }*/

            m_layerNodes[ i ].SetPosition( (short)0, (short)0 );
            m_layerNodes[ i ].c_visible = m_layerNodes[ i ].c_enable = true;
        }

        m_animatorRootNode.DropChildren();

        /*if ( m_animatorRootNode.c_children != null )
        {
            finish      = m_animatorRootNode.c_children;
            iterator    = finish.c_next;

            while ( iterator != finish )
            {
                iterator = iterator.c_next;
                iterator.c_prev.Drop();
            }
        }*/

        m_animatorRootNode.SetPosition( (short)0, (short)0 );
        m_animatorRootNode.c_visible = m_animatorRootNode.c_enable = true;
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
        ISceneNode  iterator;
        RenderTool  renderTool = m_vdriver.GetRenderTool();
        int         topStack = 0;

        //#ifdef __SPUKMK2ME_DEBUG
        if ( node == null )
        {
            new IllegalArgumentException( "Rendered node is null." ).
                printStackTrace();
            return;
        }
        //#endif

        m_stack[ 0 ]    = node;
        m_finish[ 0 ]   = node.c_next;

        if ( renderAtOrigin )
        {
            int origin = m_vdriver.GetOrigin();

            renderTool.c_rasterX = (short)(origin >> 16);
            renderTool.c_rasterY = (short)(origin & 0x0000FFFF);
        }
        else
        {
            renderTool.c_rasterX = x;
            renderTool.c_rasterY = y;
        }

        while ( topStack != -1 )
        {
            if ( m_stack[ topStack ] == m_finish[ topStack ] )
            {
                m_stack[ topStack ]     = null;
                m_finish[ topStack ]    = null;
                renderTool.c_rasterX -= m_stackX[ topStack ];
                renderTool.c_rasterY -= m_stackY[ topStack-- ];
            }
            else
            {
                iterator            = m_stack[ topStack ];
                m_stack[ topStack ] = iterator.c_next;

                if ( iterator.c_enable )
                {
                    renderTool.c_rasterX += iterator.c_x;
                    renderTool.c_rasterY += iterator.c_y;

                    if ( iterator.c_visible )
                        iterator.Render( renderTool );

                    if ( iterator.c_children == null )
                    {
                        renderTool.c_rasterX -= iterator.c_x;
                        renderTool.c_rasterY -= iterator.c_y;
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

    private static final int STACK_SIZE = 20;

    private IVideoDriver    m_vdriver;
    private ISceneNode      m_rootNode, m_animatorRootNode;
    private ISceneNode[]    m_layerNodes;
    private ISceneNode[]    m_stack, m_finish;
    private short[]         m_stackX, m_stackY;
    private int             m_nLayer;
}
