package com.spukmk2me.extension.j2se;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.spukmk2me.io.IFileSystem;

public final class J2SEFileSystem implements IFileSystem
{
    public boolean Exists( String filename, byte location )
    {
        try
        {
            switch ( location )
            {
                case IFileSystem.LOCATION_EXTERNAL:
                case IFileSystem.LOCATION_AUTODETECT:
                    {
                        InputStream is = new FileInputStream( filename );
                        
                        if ( is != null )
                        {
                            is.close();
                            return true;
                        }
                    }
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
            case IFileSystem.LOCATION_EXTERNAL:
            case IFileSystem.LOCATION_AUTODETECT:
                return new FileInputStream( filename );
            
            default:
                return null;
        }
    }

    public OutputStream WriteFile( String filename, byte location )
        throws IOException
    {
        switch ( location )
        {
            case IFileSystem.LOCATION_EXTERNAL:
            case IFileSystem.LOCATION_AUTODETECT:
                return new FileOutputStream( filename );
            
            default:
                return null;
        }
    }
    
    public char GetPathSeparator()
    {
        return File.separatorChar;
    }
}
