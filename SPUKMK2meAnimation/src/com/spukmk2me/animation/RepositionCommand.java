package com.spukmk2me.animation;

import java.io.DataInputStream;
import java.io.IOException;

/* $if EXPORTABLE$ */
import java.io.DataOutputStream;
/* $endif$ */

public final class RepositionCommand implements Command
{
    public int GetCommandCode()
    {
        return Command.CMDCODE_REPOS;
    }
    
    public Command CreateClone()
    {
        return new RepositionCommand();
    }
    
    public void Read( DataInputStream dis ) throws IOException
    {
        c_name = dis.readUTF();
        c_x = dis.readShort();
        c_y = dis.readShort();
    }

    /* $if EXPORTABLE$ */
    public void Write( DataOutputStream dos ) throws IOException
    {
        dos.writeUTF( c_name );
        dos.writeShort( c_x );
        dos.writeShort( c_y );
    }
    
    public String GetParamAsString()
    {
        return c_name + ' ' + c_x + ' ' + c_y;
    }

    public void ReadParamFromString( String param )
    {
        c_name = param.substring( 0, param.indexOf( ' ' ) );
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        c_x = Short.parseShort( param.substring( 0, param.indexOf( ' ' ) ) );
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        c_y = Short.parseShort( param );
    }
    
    public String GetCommandLabel()
    {
        return CMDLABEL;
    }
    
    private static final String CMDLABEL = "repos";
    /* $endif$ */

    public String   c_name;
    public short    c_x, c_y;
}
