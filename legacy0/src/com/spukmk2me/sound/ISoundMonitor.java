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

package com.spukmk2me.sound;

import java.io.InputStream;

/**
 *  Wrapper for sound engine
 *  \details This engine uses "slots" to manage sounds, e.g. SLOT_BG0,
 * SLOT_BG1, ..., SLOT_FX9. All constants of slots are indexed from 0 to
 * NUMBER_OF_SLOTS - 1.
 */
public interface ISoundMonitor
{
    /**
     *  Turn on / off the sound output.
     *  @param visibility The state of the sound.
     */
    public void SetSoundVisibility( boolean visibility );

    /**
     *  Assign the sound with a input stream (normally, it should be a file)
     *  \details There are several slot for sound, you should see the constants
     * for mor information.
     *  @param soundStream Stream associated with the slot.
     *  @param soundFormat The format of the sound.
     *  @param soundSlot Slot for the sound.
     *  @param loopState The sound is looped or not.
     */
    public void AssignSound(
        InputStream soundStream, byte soundFormat, byte soundSlot,
        boolean loopState );

    /**
     *  Do some actions with the specified slot.
     *  \details Read the constants for more information. This function musn't
     * produce any garbage if the slot isn't associated with any sound.
     *  @param soundSlot The specified slot.
     *  @param action The sound action.
     */
    public void SoundAction( byte soundSlot, byte action );

    /**
     *  Get the status of the specified slot.
     *  @param soundSlot The slot you want to examine;
     *  @return The current status of the slots.
     */
    public byte GetSoundStatus( byte soundSlot );

    /**
     *  Remove all sounds, free all resources associated with them.
     */
    public void Clear();

    /**
     *  Play a note.
     *  \details May be I'll add this function to the deprecated list.
     *  @param note The note you want to play.
     *  @param duration The duration in milliseconds.
     *  @param volume The volume.
     */
    public void PlayNote( int note, int duration, int volume );

    //#ifdef __SPUKMK2ME_MIDP
//#     public static final byte SOUNDMONITOR_MIDP = 1;
    //#endif

    public static final byte SLOT_NONE   = -1;
    public static final byte SLOT_BG0    = 0;
    public static final byte SLOT_BG1    = 1;
    public static final byte SLOT_FX0    = 2;
    public static final byte SLOT_FX1    = 3;
    public static final byte SLOT_FX2    = 4;
    public static final byte SLOT_FX3    = 5;
    public static final byte SLOT_FX4    = 6;
    public static final byte SLOT_FX5    = 7;
    public static final byte SLOT_FX6    = 8;
    public static final byte SLOT_FX7    = 9;
    
    public static final byte NUMBER_OF_SLOTS    = 10;

    public static final byte FORMAT_MIDI        = 1;
    public static final byte FORMAT_WAV         = 2;
    public static final byte FORMAT_MP3         = 3;
    public static final byte FORMAT_OGG         = 4;
    public static final byte FORMAT_UNDEFINED   = -128;

    public static final byte ACTION_NONE    = -1;
    public static final byte ACTION_CACHE   = 0;
    public static final byte ACTION_PLAY    = 1;
    public static final byte ACTION_PAUSE   = 2;
    public static final byte ACTION_RELEASE = 3;

    public static final byte STATUS_ASSOCIATED          = 1;
    public static final byte STATUS_STARTED_CACHING     = 2;
    public static final byte STATUS_CACHED              = 3;
    public static final byte STATUS_STARTED_PLAYING     = 4;
    public static final byte STATUS_PLAYING             = 5;
    public static final byte STATUS_STARTED_PAUSING     = 6;
    public static final byte STATUS_PAUSED              = 7;
    public static final byte STATUS_STARTED_RELEASING   = 8;
    public static final byte STATUS_RELEASED            = 9;
    public static final byte STATUS_PAUSED_BY_ACCIDENT  = 10;
    public static final byte STATUS_UNDEFINED           = -128;
}
