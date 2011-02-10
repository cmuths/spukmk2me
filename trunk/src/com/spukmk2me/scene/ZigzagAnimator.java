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

import com.spukmk2me.video.RenderTool;

/**
 *  A animator that makes scene nodes move in a zigzag line.
 *  \details It's based on TranslateAnimator.
 */
public final class ZigzagAnimator extends IAnimator
{
    public ZigzagAnimator()
    {
        m_translateAnimator = new TranslateAnimator();
    }

    public void Render( RenderTool renderTool )
    {
        if ( m_sceneNode == null )
            return;

        if ( m_remainingTime == 0 )
            return;

        if ( !m_translateAnimator.IsAnimating() )
        {
            AdvanceToNextPoint();
            m_translateAnimator.SetAnimation( m_sceneNode, m_speed,
                m_coordinates[ m_currentIndex << 1 ],
                m_coordinates[ (m_currentIndex << 1) + 1 ] );

            if ( m_remainingTime == TIME_BASED_ON_MOVES )
            {
                if ( --m_remainingMoves < 1 )
                    StopAnimation();
            }
        }

        if ( m_remainingTime > 0 )
        {
            m_remainingTime -= renderTool.c_timePassed;
            
            if ( m_remainingTime < 0 )
                StopAnimation();
        }

        if ( m_remainingTime != 0 )
            m_translateAnimator.Render( renderTool );
    }

    public ISceneNode GetSceneNode()
    {
        return m_sceneNode;
    }

    /**
     *  Setup the animation.
     *  @param node The node to control.
     *  @param zigzagCoordinates The coordinates of zigzag line. Each two
     * sequent elements, start with the divisible-by-2 index, describes the
     * coordinate of a point in zigzag line (in pixels). E.g.
     * zigzagCoordinate[ 0 ] is x0, zigzagCoordinate[ 1 ] is y0 and so on.
     * The zigzag line starts from (x0, y0), and move to (x1, y1),
     * then (x2, y2), ..., (xk, yk), where k is the last point of moving.\n
     * Let's say n = zigzagCoordinates.length / 2, if fixedStopPosition is
     * false, the "last point of moving" k = n - 1, and if it's true then
     * n = k - 2, which is the second last point in the array.\n
     * In the case fixedStropPoint is true, as I mentioned above, the point
     * with the index k - 1 will be the position of controlled scene node after
     * moving time is out.
     *  @param speed Speed of moving animation.
     *  @param movingTime Time of moving sequence, measured in seconds. Must
     * be fixed-point number. The the constants for special values.
     *  @param zigzagMode The "moving style" of the node. See the constants for
     * the list of mode available.
     *  @param numberOfMoves A "move" will be counted after the scene node
     * finished moving on a line segment. If movingTime is TIME_BASED_ON_MOVES,
     * the animation will be stopped after "numberOfMoves" moves. Otherwise
     * this argument is ignored.
     *  @param fixedStopPosition Set this to true to force the scene node to
     * stop at a fixed point after animation. The point is specified by the
     * last two elements in zigzagCoordinates. So if fixedStopPosition is true,
     * the last point won't take part in the zigzag orbit.
     */
    public void SetAnimation( ISceneNode node, short[] zigzagCoordinates,
        int speed, int movingTime, int zigzagMode, int numberOfMoves,
        boolean fixedStopPosition )
    {
        m_sceneNode         = node;
        m_coordinates       = zigzagCoordinates;
        m_speed             = speed;
        m_remainingTime     = movingTime;
        m_mode              = zigzagMode;
        // Because of the implement, one additional move must be added.
        m_remainingMoves    = numberOfMoves + 1;
        m_fixedStop         = fixedStopPosition;

        if ( fixedStopPosition )
            m_lastIndex = (zigzagCoordinates.length >> 1) - 2;
        else
            m_lastIndex = (zigzagCoordinates.length >> 1) - 1;

        if ( (m_mode | DIRECTION_BACKWARD) != 0 )
        {
            m_currentIndex  = m_lastIndex;
            m_forward       = false;
        }
        else // Forward
        {
            m_currentIndex  = 0;
            m_forward       = true;
        }

        node.SetPosition( zigzagCoordinates[ m_currentIndex << 1 ],
            zigzagCoordinates[ (m_currentIndex << 1) + 1 ] );
        m_translateAnimator.SetAnimation( null, 0, (short)0, (short)0 );
    }

    public boolean IsAnimating()
    {
        if ( m_sceneNode == null )
            return false;

        return m_remainingTime != 0;
    }

    private void AdvanceToNextPoint()
    {
        if ( m_forward )
            ++m_currentIndex;
        else
            --m_currentIndex;

        if ( (m_mode | MODE_BOUNCE) != 0 )
        {
            if ( m_currentIndex < 0 )
            {
                m_currentIndex  = 1;
                m_forward       = true;
            }
            else if ( m_currentIndex > m_lastIndex )
            {
                m_currentIndex  = m_lastIndex - 1;
                m_forward       = false;
            }
        }
        else if ( (m_mode | MODE_LOOP) != 0 )
        {
            if ( m_currentIndex < 0 )
                m_currentIndex = m_lastIndex;
            else if ( m_currentIndex > m_lastIndex )
                m_currentIndex = 0;
        }
    }

    private void StopAnimation()
    {
        m_remainingTime = 0;

        if ( m_fixedStop )
        {
            m_sceneNode.SetPosition(
                m_coordinates[ m_lastIndex << 1 ],
                m_coordinates[ (m_lastIndex << 1) + 1 ] );
        }
    }

    public static final int MODE_LOOP   = 0x00000001;
    public static final int MODE_BOUNCE = 0x00000002;

    /**
     *  DIRECTION_BACKWARD's a mode too.
     *  \details If the corresponding bit is 0, the direction will be forward.
     * Backward animation will be started from the last point.
     */
    public static final int DIRECTION_BACKWARD  = 0x00010000;

    /**
     *  Pass this value to SetAnimation() to get a infinite animation.
     */
    public static final int TIME_INFINITE       = -1;
    
    /**
     *  Pass this value to SetAnimation() to stop animation after several
     * moves between points.
     */
    public static final int TIME_BASED_ON_MOVES = -2;
    
    private ISceneNode          m_sceneNode;
    private TranslateAnimator   m_translateAnimator;
    private short[]             m_coordinates;
    private int                 m_remainingTime, m_mode, m_currentIndex,
                                m_lastIndex, m_speed, m_remainingMoves;
    private boolean             m_forward, m_fixedStop;
}
