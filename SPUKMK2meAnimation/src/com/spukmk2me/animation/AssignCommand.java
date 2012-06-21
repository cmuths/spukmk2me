package com.spukmk2me.animation;

import java.io.DataInputStream;
import java.io.IOException;

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
    
    public void Read( StringMappedDataInputStream dis ) throws IOException
    {
        c_name      = dis.readStringID();
        c_address   = dis.readStringID();
    }

    /* $if EXPORTABLE$ */
    public void Write( StringMappedDataOutputStream dos ) throws IOException
    {
        dos.writeStringID( c_name );
        dos.writeStringID( c_address );
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
    
    public String GetDataStrings()
    {
        return c_name + ' ' + c_address;
    }
    
    private static final String CMDLABEL = "assign";
	/* $endif$ */

    public static final String NULL_ADDRESS = "null";
    
    // See RepositionCommand for special names.
    public String c_name, c_address;
}
