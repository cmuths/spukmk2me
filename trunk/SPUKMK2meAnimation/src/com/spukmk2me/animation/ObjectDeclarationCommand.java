package com.spukmk2me.animation;

import java.io.DataInputStream;
import java.io.IOException;

/* $if EXPORTABLE$ */
import java.io.DataOutputStream;
/* $endif$ */

public final class ObjectDeclarationCommand implements Command {

    public int GetCommandCode()
    {
        return Command.CMDCODE_DECLARE;
    }
    
    public Command CreateClone()
    {
        return new ObjectDeclarationCommand();
    }

    public void Read( DataInputStream dis ) throws IOException
    {
        c_name = dis.readUTF();
    }
    
    /* $if EXPORTABLE$ */
    public void Write( DataOutputStream dos ) throws IOException
    {
        dos.writeUTF( c_name );
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
    
    private static final String CMDLABEL = "declare";
    /* $endif$ */
    
    public String   c_name;
}
