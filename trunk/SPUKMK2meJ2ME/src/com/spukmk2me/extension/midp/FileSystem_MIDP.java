package com.spukmk2me.extension.midp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */
import com.spukmk2me.io.IFileSystem;

/**
 *  J2ME File system.
 *  \details Currently only supports packaged file.
 */
public final class FileSystem_MIDP implements IFileSystem
{
    public boolean Exists( String filename, byte location )
    {
        try
        {
            switch ( location )
            {
                case IFileSystem.LOCATION_INTERNAL:
                case IFileSystem.LOCATION_AUTODETECT:
                    {
                        InputStream is = this.getClass().getResourceAsStream( filename );
                        
                        if ( is != null )
                        {
                            is.close();
                            return true;
                        }
                    }
                    
                    break;
                    
                default:
                    /* $if SPUKMK2ME_DEBUG$ */
                    Logger.Trace( "Unsupported file type." );
                    /* $endif$ */
            }
        } catch ( IOException e ) {
            return false;
        }
        
        return false;
    }
    
    public InputStream OpenFile( String filename, byte location )
        throws IOException
    {
        switch ( location )
        {
            case IFileSystem.LOCATION_INTERNAL:
            case IFileSystem.LOCATION_AUTODETECT:
                return this.getClass().getResourceAsStream( filename );
                
            default:
                /* $if SPUKMK2ME_DEBUG$ */
                Logger.Trace( "Unsupported file type." );
                /* $endif$ */
                return null;
        }
    }

    public OutputStream WriteFile( String filename, byte location )
        throws IOException
    {
        return null;
    }
    
    public char GetPathSeparator()
    {
        return '/';
    }
}
