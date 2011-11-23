package com.spukmk2me.extension.midp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//#ifdef __SPUKMK2ME_DEBUG
//# import com.spukmk2me.io.IFileSystem;
//# import com.spukmk2me.debug.Logger;
//#endif

/**
 *  J2ME File system.
 *  \details Currently only supports packaged file.
 */
public final class FileSystem_MIDP implements IFileSystem
{
    public FileSystem_MIDP() {}

    public InputStream OpenFile( String filename, byte filetype )
        throws IOException
    {
        if ( filetype == IFileSystem.FILETYPE_INTERNAL )
            return this.getClass().getResourceAsStream( filename );
        //#ifdef __SPUKMK2ME_DEBUG
//#         else
//#         {
//#             Logger.Trace( "Unsupported file type." );
//#             return null;
//#         }
        //#endif
    }

    public OutputStream WriteFile( String filename, byte filetype )
        throws IOException
    {
        return null;
    }
}
