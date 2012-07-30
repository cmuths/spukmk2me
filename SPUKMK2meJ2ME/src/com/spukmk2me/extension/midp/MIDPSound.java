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

package com.spukmk2me.extension.midp;

import javax.microedition.media.Player;
import javax.microedition.media.control.ToneControl;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.MediaException;


/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */
import com.spukmk2me.Util;
import com.spukmk2me.sound.ISound;

public class MIDPSound extends ISound// implements PlayerListener
{
    MIDPSound( Player player, String proxyname )
    {
        super( proxyname );
        m_player    = player;
        m_nLoops    = 1;
        m_volume    = 0x00010000;
        m_state     = ISound.STATE_UNDEFINED;
        
        if ( player != null )
        {
            try
            {
                player.realize();
                m_state = STATE_ASSOCIATED;
            } catch ( MediaException e ) {
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Trace( "MediaException" );
                /* $endif$ */
            }
            
            m_volumeControl = (VolumeControl)player.getControl(
                "javax.microedition.media.control.VolumeControl" );
            m_toneControl = (ToneControl)player.getControl(
                "javax.microedition.media.control.ToneControl" );
            //player.addPlayerListener( this );
        }
    }
    
    public int GetNumberOfLoops()
    {
        return m_nLoops;
    }

    public byte GetState()
    {
        return m_state;
    }

    public void SetNumberOfLoops( int numberOfLoops )
    {
        m_nLoops = numberOfLoops;
    }
    
    public int GetVolume()
    {
        return m_volume;
    }

    public void SetVolume( int volume )
    {
        if ( volume > 0x00010000 )
            volume = 0x00010000;
        else if ( volume < 0 )
            volume = 0;

        m_volume = volume;
        
        if ( m_volumeControl != null )
            m_volumeControl.setLevel( Util.FPRound( m_volume * 100 ) );
    }
    
    public int Action( byte action )
    {
        try
        {
            switch ( action )
            {
                case ISound.ACTION_CACHE:
                    if ( m_state == ISound.STATE_ASSOCIATED )
                    {
                        m_player.prefetch();
                        m_state = ISound.STATE_CACHED;
                        //m_state = ISound.STATE_STARTED_CACHING;
                    }
                    
                    break;
                    
                case ISound.ACTION_PLAY:
                    if ( (m_state == ISound.STATE_CACHED) ||
                         (m_state == ISound.STATE_PAUSED) )
                    {
                        m_player.setLoopCount( m_nLoops );
                        m_player.start();
                        //m_state = ISound.STATE_STARTED_PLAYING;
                        m_state = ISound.STATE_PLAYING;
                    }
                    
                    break;
                    
                case ISound.ACTION_PAUSE:
                    if ( (m_state == ISound.STATE_PLAYING) )
                    {
                        m_player.stop();
                        //m_state = ISound.STATE_STARTED_PAUSING;
                        m_state = ISound.STATE_PAUSED;
                    }
                    
                    break;
                    
                case ISound.ACTION_RELEASE:
                    if ( (m_state == ISound.STATE_ASSOCIATED) ||
                         (m_state == ISound.STATE_CACHED) ||
                         (m_state == ISound.STATE_PLAYING) ||
                         (m_state == ISound.STATE_PAUSED) )
                    {
                        m_player.close();
                        //m_state = ISound.STATE_STARTED_RELEASING;
                        // . . .
                        m_state = ISound.STATE_RELEASED;
                    }
                    
                    break;
            }
        } catch ( MediaException e ) {
            /* $if SPUKMK2ME_DEBUG$ */
            Logger.Trace( "MediaException ");
            return -1;
            /* $endif$ */
        }
        
        return 0;
    }
    
    /*public void playerUpdate( Player player, String event, Object eventData )
    {
        System.out.println( event );
            //m_state = ISound.STATE_ASSOCIATED;
        if ( event.equals( PlayerListener.STARTED ) )
            m_state = ISound.STATE_PLAYING;
        else if ( event.equals( "bufferingStarted" ) )
            m_state = ISound.STATE_STARTED_CACHING;
        else if ( event.equals( "bufferingStopped" ) )
            m_state = ISound.STATE_CACHED;
        else if ( event.equals( PlayerListener.STOPPED ) )
            m_state = ISound.STATE_PAUSED;
        else if ( event.equals( PlayerListener.CLOSED ) )
            m_state = ISound.STATE_RELEASED;
        else if ( event.equals( PlayerListener.DEVICE_UNAVAILABLE ) )
            m_state = STATE_PAUSED_BY_ACCIDENT;
        else if ( event.equals( PlayerListener.DEVICE_AVAILABLE ) )
        {
            if ( m_state == ISound.STATE_PAUSED_BY_ACCIDENT )
                Action( ISound.ACTION_PLAY );
        }
        else if ( event.equals( PlayerListener.ERROR ) )
            Action( ISound.ACTION_RELEASE );
    }*/

    
    private Player          m_player;
    private VolumeControl   m_volumeControl;
    private ToneControl     m_toneControl;
    private int             m_nLoops;
    // volumeScale = initial volumeControl.getLevel() / 1.0
    private int             m_volume, m_volumeScale; // 16.16 fixed point
    private byte            m_state;
}
