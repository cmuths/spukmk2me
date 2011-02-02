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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spukmk2me.scene;

import com.spukmk2me.Util;
import com.spukmk2me.video.RenderTool;

/**
 *  An animator used for translating scene node.
 */
public final class TranslateAnimator extends IAnimator
{
    public TranslateAnimator() {}      

    public final void Render( RenderTool renderTool )
    {
        if ( m_sceneNode == null )
            return;

        m_currentX += Util.FPMul( m_speedX, renderTool.c_timePassed );
        m_currentY += Util.FPMul( m_speedY, renderTool.c_timePassed );

        if ( m_speedX > 0 )
        {
            if ( m_currentX > m_desX )
                m_currentX = m_desX;
        }
        else
        {
            if ( m_currentX < m_desX )
                m_currentX = m_desX;
        }

        if ( m_speedY > 0 )
        {
            if ( m_currentY > m_desY )
                m_currentY = m_desY;
        }
        else
        {
            if ( m_currentY < m_desY )
                m_currentY = m_desY;
        }

        m_sceneNode.c_x = Util.FPRound( m_currentX );
        m_sceneNode.c_y = Util.FPRound( m_currentY );
    }

    public ISceneNode GetSceneNode()
    {
        return m_sceneNode;
    }

    /**
     *  Setup the translate information.
     *  \details Do nothing if the node is null.
     *  @param node The scene node to animate.
     *  @param speed The speed of moving.
     *  @param desX The X-coordinate of destination.
     *  @param desY The Y-coordinate of destination.
     */
    public void SetAnimation( ISceneNode node, int speed,
        short desX, short desY )
    {
        m_sceneNode = node;

        if ( m_sceneNode == null )
            return;

        m_currentX  = node.c_x << 16;
        m_currentY  = node.c_y << 16;
        m_desX      = desX << 16;
        m_desY      = desY << 16;

        int deltaX  = m_desX - m_currentX;
        int deltaY  = m_desY - m_currentY;
        
        if ( (deltaX != 0) || (deltaY != 0) )
        {            
            // Wrong formula, but I think it's faster than square root
            // calculation.
            int rangeSpeed = Util.FPDiv( speed,
                Math.abs( deltaX ) + Math.abs( deltaY ) );

            if ( rangeSpeed < 0 )
                rangeSpeed = -rangeSpeed;

            m_speedX = Util.FPMul( deltaX, rangeSpeed );
            m_speedY = Util.FPMul( deltaY, rangeSpeed );
        }
        else
            m_speedX = m_speedY = 0;

        //System.out.println( "Speed: " + m_speedX + ' ' + m_speedY );
    }

    public final boolean IsAnimating()
    {
        if ( m_sceneNode == null )
            return false;

        return !((m_currentX == m_desX) && (m_currentY == m_desY));
    }

    private ISceneNode  m_sceneNode;
    private int         m_currentX, m_currentY, m_speedX, m_speedY,
                        m_desX, m_desY;
}
