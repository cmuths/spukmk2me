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

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */
import com.spukmk2me.sound.ISoundMonitor;
import com.spukmk2me.sound.ISound;

/**
 *  An implements if ISoundMonitor, which uses MIDP API.
 *  \details Ogg Vorbis isn't supported.
 */
public final class SoundMonitor_MIDP implements ISoundMonitor
{    
    public SoundMonitor_MIDP() {}

    public ISound CreateSound( InputStream soundStream, byte soundFormat,
        String proxyname )
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
                    formatString = "audio/X-wav";
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
                return new MIDPSound(
                    Manager.createPlayer( soundStream, formatString ),
                    proxyname );
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
        
        return null;
    }

    public void Clear()
    {
    }    
}
