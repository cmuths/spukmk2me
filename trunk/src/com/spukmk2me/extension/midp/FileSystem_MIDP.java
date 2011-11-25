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

    public InputStream OpenFile( String filename, byte location )
        throws IOException
    {
        switch ( location )
        {
            case IFileSystem.LOCATION_INTERNAL:
            case IFileSystem.LOCATION_DEFAULT:
                return this.getClass().getResourceAsStream( filename );
                
            default:
                //#ifdef __SPUKMK2ME_DEBUG
//#                 Logger.Trace( "Unsupported file type." );
                //#endif
                return null;
        }
    }

    public OutputStream WriteFile( String filename, byte location )
        throws IOException
    {
        return null;
    }
}
