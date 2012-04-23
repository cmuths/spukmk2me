package com.spukmk2me.animation;

import java.io.DataInputStream;
import java.io.IOException;

/* $if EXPORTABLE$ */
import java.io.DataOutputStream;
/* $endif$ */

public final class AssignCommand implements Command
{
    public int GetCommandCode()
    {
        return Command.CMDCODE_ASSIGN;
    }
    
    public Command CreateClone()
    {
        return new AssignCommand();
    }
    
    public void Read( DataInputStream dis ) throws IOException
    {
        c_name      = dis.readUTF();
        c_address   = dis.readUTF();
    }

    /* $if EXPORTABLE$ */
    public void Write( DataOutputStream dos ) throws IOException
    {
        dos.writeUTF( c_name );
        dos.writeUTF( c_address );
    }
    
    public String GetParamAsString()
	{
		return c_name + ' ' + c_address;
	}
	
    public void ReadParamFromString( String param )
	{
		c_name = param.substring( 0, param.indexOf( ' ' ) );
		param = param.substring( param.indexOf( ' ' ) + 1 );
		
		c_address = param;
	}
    
    public String GetCommandLabel()
    {
        return CMDLABEL;
    }
    
    private static final String CMDLABEL = "assign";
	/* $endif$ */

    public static final String NULL_ADDRESS = "null";
    
    // See RepositionCommand for special names.
    public String c_name, c_address;
}
