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

package com.spukmk2me.scene.complex;

import com.spukmk2me.Util;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.ITopLeftOriginSceneNode;
import com.spukmk2me.video.RenderTool;

/**
 *  An advanced node built from ClippingSceneNode, acts like a viewport.
 */
public final class ViewportSceneNode extends ITopLeftOriginSceneNode
{
    /**
     *  Constructor.
     */
    public ViewportSceneNode()
    {
        m_clippingNode = new ClippingSceneNode();
        ISceneNode.AddSceneNode( m_clippingNode, this );
        m_originX = m_originY = 0;
    }

    public void Render( RenderTool renderTool )
    {
        if ( m_autoUpdate )
            CalculateOrigin( renderTool.c_timePassed );

        m_clippingNode.SetClipping(
            Util.FPRound( m_originX ), Util.FPRound( m_originY ),
            m_width, m_height );
    }

    public short GetAABBWidth()
    {
        return m_width;
    }

    public short GetAABBHeight()
    {
        return m_height;
    }

    /**
     *  Setup information for viewport.
     *  @param width The width of viewport.
     *  @param height The height of viewport.
     *  @param viewableX X coordinate of top-left point of viewable area.
     *  @param viewableY Y coordinate of top-left point of viewable area.
     *  @param viewableWidth Total navigable width.
     *  @param viewableHeight Total navigable height.
     *  @param alterX X coordinate of "alter" rectangle's top left point.
     *  @param alterY Y coordinate of "alter" rectangle's top left point.
     *  @param alterWidth Width of "alter" rectangle.
     *  @param alterHeight Height of "alter" rectangle.
     *  @param movingSpeed Moving speed, in 16-16 fixed-point format.
     *  @param movingType See the constants for more information. If movingType
     * is MOVINGTYPE_MANUAL, all the following parameters:
     * viewableX, viewableY, viewableWidth, viewableHeight,
     * alterX, alterY, alterWidth, alterHeight, movingSpeed and cursorNode
     * will be ignored.
     *  @param cursorNode A scene node acts as the cursor of this viewport. The
     * viewport will takes it's information for automatic calculation of
     * viewport information.
     *  @param autoUpdate true if you want the viewport automatically update
     * the coordinate itself (before it's drawn), when the moving type isn't
     * manual. Set this argument to false to manually update viewport via
     * CalculateOrigin(). Please distinct this argument with MOVINGTYPE_MANUAL,
     * since autoUpdate help overcome the viewport synchronization problem. If
     * you don't understand what's written before, just ignore and set it to
     * true.
     */
    public void SetupViewport( short width, short height,
        short viewableX, short viewableY,
        short viewableWidth, short viewableHeight,
        short alterX, short alterY, short alterWidth, short alterHeight,
        int movingSpeed, byte movingType, ISceneNode cursorNode,
        boolean autoUpdate )
    {
        m_width             = width;
        m_height            = height;
        m_viewableX         = viewableX;
        m_viewableY         = viewableY;
        m_viewableWidth     = viewableWidth;
        m_viewableHeight    = viewableHeight;
        m_alterX            = alterX;
        m_alterY            = alterY;
        m_alterWidth        = alterWidth;
        m_alterHeight       = alterHeight;
        m_movingSpeed       = movingSpeed;
        m_movingType        = movingType;
        m_cursorNode        = cursorNode;
        m_autoUpdate        = autoUpdate;
        
        m_clippingNode.SetClipping(
            Util.FPRound( m_originX ), Util.FPRound( m_originY ),
            width, height );
    }

    /**
     *  Manually call this function to do origin calculation. This shouldn't be
     * called if autoUpdate (in SetupViewport()) is true, and don't call it
     * if you don't really understand about it.
     *
     *  @param timePassed Period of time in seconds since the last updated
     * moment. (16-16 fixed point)
     */
    public void CalculateOrigin( int timePassed )
    {
        if ( m_movingType == MOVINGTYPE_MANUAL )
            return;

        if ( m_cursorNode != null )
        {
            m_cursorX = m_cursorNode.c_x;
            m_cursorY = m_cursorNode.c_y;
        }

        int rX1 = m_cursorX - Util.FPRound( m_originX );
        int rY1 = m_cursorY - Util.FPRound( m_originY );
        int rX2, rY2;
        int deltaX, deltaY;

        if ( m_cursorNode == null )
        {
            rX2 = rX1;
            rY2 = rY1;
        }
        else
        {
            rX2 = rX1 + m_cursorNode.GetAABBWidth();
            rY2 = rY1 + m_cursorNode.GetAABBHeight();
        }

        if ( rX1 < m_alterX )
            deltaX = rX1 - m_alterX;
        else if ( rX2 >= m_alterX + m_alterWidth )
            deltaX = rX2 - m_alterX - m_alterWidth + 1;
        else
            deltaX = 0;

        if ( rY1 < m_alterY )
            deltaY = rY1 - m_alterY;
        else if ( rY2 >= m_alterY + m_alterHeight )
            deltaY = rY2 - m_alterY - m_alterHeight + 1;
        else
            deltaY = 0;

        if ( (deltaX == 0) && (deltaY == 0) )
            return;

        int movingX = 0, movingY = 0;

        deltaX <<= 16;
        deltaY <<= 16;

        if ( m_movingType == MOVINGTYPE_ALWAYS_INSIDE )
        {
            movingX = deltaX;
            movingY = deltaY;
        }
        else
        {
            int baseSpeed = Util.FPDiv( m_movingSpeed,
                Math.abs( deltaX ) + Math.abs( deltaY ) );

            switch ( m_movingType )
            {
                case MOVINGTYPE_CONST_SPEED:
                    movingX = Util.FPMul(
                        Util.FPMul( baseSpeed, deltaX ), timePassed );
                    movingY = Util.FPMul(
                        Util.FPMul( baseSpeed, deltaY ), timePassed );
                    break;

                case MOVINGTYPE_SPP:
                    movingX = Util.FPMul(
                        Util.FPMul(
                            Util.FPMul( baseSpeed, Math.abs( deltaX ) ),
                            timePassed ),
                        deltaX );
                    movingY = Util.FPMul(
                        Util.FPMul(
                            Util.FPMul( baseSpeed, Math.abs( deltaY ) ),
                            timePassed ),
                        deltaY );
                    break;
            }
        }

        m_originX += movingX;
        m_originY += movingY;

        if ( m_originX > m_viewableX + m_viewableWidth - m_width << 16 )
        {
            m_originX = m_viewableX + m_viewableWidth - m_width << 16;
        }

        if ( m_originX < m_viewableX << 16 )
            m_originX = m_viewableX << 16;

        if ( m_originY > m_viewableY + m_viewableHeight - m_height << 16 )
        {
            m_originY = m_viewableY + m_viewableHeight - m_height << 16;
        }

        if ( m_originY < m_viewableY << 16 )
            m_originY = m_viewableY << 16;
    }

    /**
     *  Set the origin for this viewport.
     *  \details If you use this function for an automatic viewport, this
     * function may not work. This function is supposed to use in manual mode
     * only.
     *  @param x X coordinate of top-left point.
     *  @param y Y coordinate of top-left point.
     */
    public void SetOrigin( short x, short y )
    {
        m_originX = x << 16;
        m_originY = y << 16;
        m_clippingNode.SetClipping( x, y, m_width, m_height );
    }

    public short GetOriginX()
    {
        return Util.FPRound( m_originX );
    }

    public short GetOriginY()
    {
        return Util.FPRound( m_originY );
    }

    /**
     *  Set the position for cursor.
     *  \details This function won't work if this viewport use a scene node
     * as the cursor, or the moving type is MOVINGTYPE_MANUAL.
     *  @param x X coordinate.
     *  @param y Y coordinate.
     */
    public void SetCursorPosition( short x, short y )
    {
        if ( m_cursorNode == null )
        {
            m_cursorX = x;
            m_cursorY = y;
        }
    }

    /**
     *  Get entry node for ClippingSceneNode.
     *  \details Do not add nodes directly to ClippingSceneNode if you
     * want to use clipping function.
     *  @return Entry node.
     */
    public ISceneNode GetEntryNode()
    {
        return m_clippingNode.GetEntryNode();
    }

    //! Manually control the viewport.
    public static final byte MOVINGTYPE_MANUAL          = 0;
    //! Viewport will move at constant speed.
    public static final byte MOVINGTYPE_CONST_SPEED     = 1;
    //! Viewport will move with the "speed per pixel" speed.
    public static final byte MOVINGTYPE_SPP             = 2;
    //! Cursor will always lie inside the alter rectangle.
    public static final byte MOVINGTYPE_ALWAYS_INSIDE   = 3;

    private ClippingSceneNode   m_clippingNode;
    private ISceneNode          m_cursorNode;

    private int     m_movingSpeed;
    private int     m_originX, m_originY;   // Fixed-point.
    private short   m_width, m_height,
                    m_viewableX, m_viewableY,
                    m_viewableWidth, m_viewableHeight,
                    m_alterX, m_alterY, m_alterWidth, m_alterHeight,
                    m_cursorX, m_cursorY;
    private boolean m_autoUpdate;
    private byte    m_movingType;
}
