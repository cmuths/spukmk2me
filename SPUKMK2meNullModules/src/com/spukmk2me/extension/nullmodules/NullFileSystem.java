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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public final class NullFileSystem
{
    public InputStream OpenFile( String filename, byte location )
        throws IOException
    {
        return null;
    }

    public OutputStream WriteFile( String filename, byte location )
        throws IOException
    {
        return null;
    }
    
    public char GetPathSeparator()
    {
        return 0;
    }
}
