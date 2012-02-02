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
