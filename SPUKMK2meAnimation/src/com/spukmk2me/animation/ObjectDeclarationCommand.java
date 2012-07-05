package com.spukmk2me.animation;

import java.io.IOException;

public final class ObjectDeclarationCommand implements Command
{
    public byte GetCommandCode()
    {
        return Command.CMDCODE_DECLARE;
    }
    
    public Command CreateClone()
    {
        return new ObjectDeclarationCommand();
    }

    public void Read( StringMappedDataInputStream dis ) throws IOException
    {
        c_name = dis.readStringID();
    }
    
    /* $if EXPORTABLE$ */
    public void Write( StringMappedDataOutputStream dos ) throws IOException
    {
        dos.writeStringID( c_name );
    }
    
    public String GetParamAsString()
    {
        return c_name;
    }

    public void ReadParamFromString( String param )
    {
        c_name = param;
    }
    
    public String GetCommandLabel()
    {
        return CMDLABEL;
    }
    
    public String GetDataStrings()
    {
        return c_name;
    }
    
    private static final String CMDLABEL = "declare";
    /* $endif$ */
    
    public String   c_name;
}
