package com.spukmk2me.debug;

public final class Logger
{
    public static void Log( String message )
    {
        new SPUKMK2meException( message ).printStackTrace();
    }
}
