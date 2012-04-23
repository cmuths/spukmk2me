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
 */
public interface ISoundMonitor
{
    /**
     *  Create a sound object.
     *  @param soundStream Stream to get sound data.
     *  @param soundFormat See constants below.
     *  @param proxyname Leave null if you don't add it to a ResourceSet.
     *  @param loop True to loop the sound.
     */
    public ISound CreateSound( InputStream soundStream, byte soundFormat,
        String proxyname );
    
    /**
     *  Remove all created sounds, free all resources associated with them.
     */
    public void Clear();

    public static final byte FORMAT_AUTODETECT  = 0;
    public static final byte FORMAT_MIDI        = 1;
    public static final byte FORMAT_WAV         = 2;
    public static final byte FORMAT_MP3         = 3;
    public static final byte FORMAT_OGG         = 4;
    public static final byte FORMAT_UNDEFINED   = -128;
}
