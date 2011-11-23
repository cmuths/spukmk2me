package com.spukmk2me.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public interface IFileSystem
{
    /**
     *  Open a stream to read file.
     *  @param filename Filename of file to be read.
     *  @return An InputStream object. Can be null if opening process fails.
     *  @throws IOException If there's any I/O exception occurred.
     */
    public InputStream OpenFile( String filename, byte filetype )
        throws IOException;

    /**
     *  Open a stream to write file, overwrite if file exists.
     *  @param filename Filename of file to be written.
     *  @return An OutputStream object. Can be null if stream creating process
     * fails.
     *  @throws IOException If there's any I/O exception occurred.
     */
    public OutputStream WriteFile( String filename, byte filetype )
        throws IOException;
    
    // Standard file type
    public static final byte FILETYPE_INTERNAL      = 0;
    public static final byte FILETYPE_EXTERNAL      = 1;
    public static final byte FILETYPE_URL           = 2;
}
