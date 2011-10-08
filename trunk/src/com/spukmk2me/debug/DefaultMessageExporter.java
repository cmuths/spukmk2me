package com.spukmk2me.debug;

public class DefaultMessageExporter implements MessageExporter
{
    public DefaultMessageExporter() {}

    public byte Initialise()
    {
        return 0;
    }

    public byte Finalise()
    {
        return 0;
    }

    public void ExportMessage( String message )
    {
        new SPUKMK2meException( message ).printStackTrace();
    }
}
