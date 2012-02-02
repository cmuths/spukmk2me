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

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.ToneControl;
import javax.microedition.media.control.VolumeControl;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */
import com.spukmk2me.sound.ISoundMonitor;

/**
 *  An implements if ISoundMonitor, which uses MIDP API.
 *  \details Ogg Vorbis isn't supported.
 */
public final class SoundMonitor_MIDP implements ISoundMonitor, PlayerListener
{    
    public SoundMonitor_MIDP()
    {
        m_players           = new Player[ NUMBER_OF_SLOTS ];
        m_volumeControls    = new VolumeControl[ NUMBER_OF_SLOTS ];
        m_toneControls      = new ToneControl[ NUMBER_OF_SLOTS ];
        m_status    = new byte[ NUMBER_OF_SLOTS ];

        for ( int i = 0; i != NUMBER_OF_SLOTS; ++i )
            m_status[ i ] = STATUS_UNDEFINED;

        m_visibility = true;
    }

    public void SetSoundVisibility( boolean visibility )
    {
        m_visibility = visibility;

        for ( byte i = 0; i != NUMBER_OF_SLOTS; ++i )
            if ( m_volumeControls[ i ] != null )
                m_volumeControls[ i ].setMute( !visibility );
    }
    
    public void AssignSound( InputStream soundStream, byte soundFormat,
        byte soundSlot, boolean loopState )
    {
        try
        {
            String formatString;

            switch ( soundFormat )
            {
                case FORMAT_MIDI:
                    formatString = "audio/midi";
                    break;

                case FORMAT_WAV:
                    formatString = "audio/x-wav";
                    break;

                case FORMAT_MP3:
                    formatString = "audio/mpeg";
                    break;

                case FORMAT_OGG:
                default:
                    formatString = null;
            }

            if ( formatString != null )
            {
                m_players[ soundSlot ] =
                    Manager.createPlayer( soundStream, formatString );
                m_players[ soundSlot ].realize();
                m_players[ soundSlot ].setLoopCount( ( loopState )? -1 : 1 );
                m_volumeControls[ soundSlot ] =
                    (VolumeControl)m_players[ soundSlot ].getControl(
                        "javax.microedition.media.control.VolumeControl" );
                m_toneControls[ soundSlot ] =
                    (ToneControl)m_players[ soundSlot ].getControl(
                        "javax.microedition.media.control.ToneControl" );
                m_players[ soundSlot ].addPlayerListener( this );
            }
        } catch ( IOException e ) {
        	/* $if SPUKMK2ME_DEBUG$ */
            Logger.Trace( "IOException" );
            /* $endif$ */
        }
        catch ( MediaException e ) {
            /* $if SPUKMK2ME_DEBUG$ */
         	Logger.Trace( "MediaException" );
            /* $endif$ */
        }
    }

    public void SoundAction( byte soundSlot, byte action )
    {
        if ( m_players[ soundSlot ] == null )
            return;

        try
        {
            switch ( action )
            {
                case ACTION_CACHE:
                    m_players[ soundSlot ].prefetch();
                    m_status[ soundSlot ] = STATUS_STARTED_CACHING;
                    return;

                case ACTION_PLAY:
                    m_volumeControls[ soundSlot ].setMute( !m_visibility );
                    m_players[ soundSlot ].start();
                    m_status[ soundSlot ] = STATUS_STARTED_PLAYING;
                    return;

                case ACTION_PAUSE:
                    m_players[ soundSlot ].stop();
                    m_status[ soundSlot ] = STATUS_STARTED_PAUSING;
                    return;

                case ACTION_RELEASE:
                    m_players[ soundSlot ].close();
                    m_volumeControls[ soundSlot ]   = null;
                    m_toneControls[ soundSlot ]     = null;
                    m_status[ soundSlot ]           = STATUS_STARTED_RELEASING;
                    return;
            }
        } catch ( MediaException e ) {
        	/* $if SPUKMK2ME_DEBUG$ */
        	Logger.Trace( "MediaException" );
            /* $endif$ */
        }
    }

    public byte GetSoundStatus( byte soundSlot )
    {
        return m_status[ soundSlot ];
    }

    public void Clear()
    {        
        for ( byte i = 0; i != NUMBER_OF_SLOTS; ++i )
            SoundAction( i, ACTION_RELEASE );
    }

    public void PlayNote( int note, int duration, int volume )
    {
        if ( m_visibility )
        {
            try
            {
                Manager.playTone( note, duration, volume );
            } catch ( MediaException e ) {
            	/* $if SPUKMK2ME_DEBUG$ */
            	Logger.Trace( "MediaException" );
                /* $endif$ */
            }
        }
    }

    public void playerUpdate( Player player, String event, Object eventData )
    {
        int index = FindPlayerIndex( player );
        
        if ( event.equals( PlayerListener.STARTED ) )
        {
            m_status[ index ] = STATUS_PLAYING;
        }
        else if ( event.equals( "bufferingStarted" ) )
        {
            m_status[ index ] = STATUS_STARTED_CACHING;
        }
        else if ( event.equals( "bufferingStopped" ) )
        {
            m_status[ index ] = STATUS_CACHED;
        }
        else if ( event.equals( PlayerListener.STOPPED ) )
        {
            m_status[ index ] = STATUS_PAUSED;
        }
        else if ( event.equals( PlayerListener.CLOSED ) )
        {
            m_status[ index ]   = STATUS_RELEASED;
            //m_players[ index ].removePlayerListener( this );
            m_players[ index ]  = null;
            m_status[ index ]   = STATUS_UNDEFINED;
        }
        else if ( event.equals( PlayerListener.DEVICE_UNAVAILABLE ) )
        {
            m_status[ index ] = STATUS_PAUSED_BY_ACCIDENT;
        }
        else if ( event.equals( PlayerListener.DEVICE_AVAILABLE ) )
        {
            if ( m_status[ index ] == STATUS_PAUSED_BY_ACCIDENT )
            {
                SoundAction( (byte)index, ACTION_PLAY );
            }
        }
        else if ( event.equals( PlayerListener.ERROR ) )
        {
            SoundAction( (byte)index, ACTION_RELEASE );
        }
    }

    private int FindPlayerIndex( Player player )
    {
        if ( player != null )
        {
            for ( int i = 0; i != NUMBER_OF_SLOTS; ++i )
                if ( m_players[ i ] == player )
                    return i;
        }

        return -1;
    }
    
    private Player[]        m_players;
    private VolumeControl[] m_volumeControls;
    private ToneControl[]   m_toneControls;
    private byte[]          m_status;
    private boolean         m_visibility;
}
