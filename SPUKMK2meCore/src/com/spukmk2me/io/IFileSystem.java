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

package com.spukmk2me.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public interface IFileSystem
{
    /**
     *  Open a stream to read file.
     *  @param filename Filename of file to be read.
     *  @param location Location to read file.
     *  @return An InputStream object. Can be null if opening process fails.
     *  @throws IOException If there's any I/O exception occurred.
     */
    public InputStream OpenFile( String filename, byte location )
        throws IOException;

    /**
     *  Open a stream to write file, overwrite if file exists.
     *  @param filename Filename of file to be written.
     *  @param location Location of file to be read.
     *  @return An OutputStream object. Can be null if stream creating process
     * fails.
     *  @throws IOException If there's any I/O exception occurred.
     */
    public OutputStream WriteFile( String filename, byte location )
        throws IOException;
    
    /**
     *  Get the path separator of current system.
     *  @return Path separator.
     */
    public char GetPathSeparator();
    
    public static final byte LOCATION_AUTODETECT    = -1;
    public static final byte LOCATION_INTERNAL      = 0;
    public static final byte LOCATION_EXTERNAL      = 1;
    public static final byte LOCATION_URL           = 2;
}
