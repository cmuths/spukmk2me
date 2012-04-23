package com.spukmk2me.animation;

import java.io.DataInputStream;
import java.io.IOException;

/* $if EXPORTABLE$ */
import java.io.DataOutputStream;
/* $endif$ */

public final class ObjectRepositionCommand implements Command
{
    public int GetCommandCode()
    {
        return Command.CMDCODE_OBJECTREPOS;
    }

    public Command CreateClone()
    {
        return new ObjectRepositionCommand();
    }

    public void Read( DataInputStream dis ) throws IOException
    {
        c_name  = dis.readUTF();
        c_x     = dis.readShort();
        c_y     = dis.readShort();
        c_z     = dis.readShort();
    }

    /* $if EXPORTABLE$ */
    public void Write( DataOutputStream dos ) throws IOException
    {
        dos.writeUTF( c_name );
        dos.writeShort( c_x );
        dos.writeShort( c_y );
        dos.writeShort( c_z );
    }

    public String GetParamAsString()
    {
        return c_name + ' ' + c_x + ' ' + c_y + ' ' + c_z;
    }

    public void ReadParamFromString( String param )
    {
        c_name = param.substring( 0, param.indexOf( ' ' ) );
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        c_x = Short.parseShort( param.substring( 0, param.indexOf( ' ' ) ) );
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        c_y = Short.parseShort( param.substring( 0, param.indexOf( ' ' ) ) );
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        c_z = Short.parseShort( param );
    }
    
    public String GetCommandLabel()
    {
        return CMDLABEL;
    }
    
    private static final String CMDLABEL = "objrepos";
    /* $endif$ */
    
    public String c_name;
    public short c_x, c_y, c_z;
}
