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

package spukmk2me.scene;

import spukmk2me.Util;
import spukmk2me.video.RenderTool;
import spukmk2me.video.IImage;

public final class SpriteSceneNode extends ISceneNode
{
    /**
     *  A constructor
     *  \details The sprite will be duplicated by this node.
     *  @param sprite The sprite that will be used by this node.
     */
    public SpriteSceneNode( IImage[] images )
    {
        m_images        = images;
    }    

    /**
     *  Copy constructor.
     *  \details The sprite from source node will be duplicated.
     *  @param node The source node.
     */
    public SpriteSceneNode( SpriteSceneNode node )
    {
        m_images        = node.GetImages();
    }

    public final void Render( RenderTool renderTool )
    {
        if ( m_autoDrop && !m_animating )
        {
            this.Drop();
            return;
        }
        
        if ( m_images == null )
            return;

        if ( m_animating )
        {
            int deltaFrame =
                Util.FPDiv( renderTool.c_timePassed, m_secPerFrame );

            if ( m_direction )
                m_currentFrame += deltaFrame;
            else
                m_currentFrame -= deltaFrame;

            if ( m_frameStop )
            {
                m_remainingFrames -= deltaFrame;

                if ( m_remainingFrames <= 0 )
                    m_animating = false;
            }
        }

        int length_fp = m_lastIndex - m_firstIndex + 1 << 16;

        while ( Util.FPRound( m_currentFrame ) < m_firstIndex )
            m_currentFrame += length_fp;

        while ( Util.FPRound( m_currentFrame ) > m_lastIndex )
            m_currentFrame -= length_fp;

        m_images[ Util.FPRound( m_currentFrame ) ].Render( renderTool );
    }

    public final short GetWidth()
    {
        return ( m_images == null )? 0 : m_images[ 0 ].c_width;
    }

    public final short GetHeight()
    {
        return ( m_images == null )? 0 : m_images[ 0 ].c_height;
    }
    
    /**
     *  Set the frame index of this sprite.
     *  @param frameIndex The index of the frame.
     */
    public void SetFrameIndex( int frameIndex )
    {
        //#ifdef __SPUKMK2ME_DEBUG
        if ( m_images != null )
        {
            if ( frameIndex >= m_images.length )
                System.out.println( "Set sprite index out of range." );
        }
        //#endif
        
        m_currentFrame = frameIndex << 16;
    }

    public IImage[] GetImages()
    {
        return m_images;
    }

    /**
     *  Setup the animation.
     *  @param mode The animating mode. Can be combined from MODE_BACKWARD,
     * MODE_FRAMESTOP, MODE_TIMESTOP, MODE_ANIMATING, MODE_AUTODROP.
     *  @param firstIndex The first frame index of animated frame sequence.
     *  @param lastFrame The last frame index of animated frame sequence.
     *  @param msPerFrame Amount of time for a frame, measured in milliseconds.
     * Pass -1 value to this parameter if you want to keep the old msPerFrame.
     *  @param nFrameToStop The animation will stop after nFrameToStop frames.
     * Ignored if the mode doesn't contain MODE_FRAMESTOP.
     */
    public void SetAnimating( int mode, int firstIndex, int lastIndex,
        int msPerFrame, int nFrameToStop )
    {
        m_animating = (mode & MODE_ANIMATING) != 0;
        m_direction = (mode & MODE_BACKWARD) == 0;
        m_frameStop = (mode & MODE_FRAMESTOP) != 0;
        m_autoDrop  = (mode & MODE_AUTODROP) != 0;

        if ( msPerFrame != -1 )
            m_secPerFrame = (msPerFrame << 16) / 1000;

        m_firstIndex    = firstIndex;
        m_lastIndex     = lastIndex;

        if ( m_frameStop )
        {
            m_remainingFrames   = nFrameToStop << 16;
            
            if ( m_direction )
            {
                m_finishFrame = (Util.FPRound( m_currentFrame ) - firstIndex +
                    nFrameToStop) % (lastIndex - firstIndex + 1) + firstIndex;
            }
            else
            {
                m_finishFrame = lastIndex - ((lastIndex -
                    Util.FPRound( m_currentFrame ) + nFrameToStop) %
                    (lastIndex - firstIndex + 1));
            }
        }
    }

    //! If the enabled bit of MODE_BACKWARD is zero, the sprite won't animate.
    public static final byte    MODE_ANIMATING      = 0x01;
    //! If the enabled bit of MODE_BACKWARD is zero, forward animation is used.
    public static final byte    MODE_BACKWARD       = 0x02;
    //! Stop after n frames.
    public static final byte    MODE_FRAMESTOP      = 0x04;
    //! Automatically drop this node if the animation is stopped.
    public static final byte    MODE_AUTODROP       = 0x10;

    private IImage[]    m_images;
    private int         m_currentFrame, m_remainingFrames, m_secPerFrame,
                        m_firstIndex, m_lastIndex, m_finishFrame;
    private boolean     m_direction, m_animating, m_frameStop, m_autoDrop;
}