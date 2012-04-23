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

package com.spukmk2me.extension.nullmodules;

import com.spukmk2me.sound.ISound;

public final class NullSound extends ISound 
{
    NullSound( String proxyname )
    {
        super( proxyname );
        m_state     = ISound.STATE_UNDEFINED;
        m_nLoops    = 1;
        m_volume    = 0x00010000;
    }
    
    public void SetNumberOfLoops( int numberOfLoops )
    {
        m_nLoops    = numberOfLoops;
    }
    
    public int GetNumberOfLoops()
    {
        return m_nLoops;
    }
    
    public void SetVolume( int volume )
    {
        m_volume = volume;
    }
    
    public int GetVolume()
    {
        return m_volume;
    }
    
    public byte GetState()
    {
        return m_state;
    }
    
    public int Action( byte action )
    {
        switch ( action )
        {
            case ISound.ACTION_CACHE:
                if ( m_state == ISound.STATE_ASSOCIATED )
                {
                    m_state = ISound.STATE_STARTED_CACHING;
                    // . . .
                    m_state = ISound.STATE_CACHED;
                }
                
                break;
                
            case ISound.ACTION_PLAY:
                if ( (m_state == ISound.STATE_CACHED) ||
                     (m_state == ISound.STATE_PAUSED) )
                {
                    m_state = ISound.STATE_STARTED_PLAYING;
                    // . . .                    
                    m_state = ISound.STATE_PLAYING;
                }
                
                break;
                
            case ISound.ACTION_PAUSE:
                if ( (m_state == ISound.STATE_PLAYING) )
                {
                    m_state = ISound.STATE_STARTED_PAUSING;
                    // . . .
                    m_state = ISound.STATE_PAUSED;
                }
                
                break;
                
            case ISound.ACTION_RELEASE:
                if ( (m_state == ISound.STATE_ASSOCIATED) ||
                     (m_state == ISound.STATE_CACHED) ||
                     (m_state == ISound.STATE_PLAYING) ||
                     (m_state == ISound.STATE_PAUSED) )
                {
                    m_state = ISound.STATE_STARTED_RELEASING;
                    // . . .
                    m_state = ISound.STATE_RELEASED;
                }
                
                break;
        }
        
        return 0;
    }
    
    private int     m_nLoops;
    private int     m_volume;
    private byte    m_state;
}
