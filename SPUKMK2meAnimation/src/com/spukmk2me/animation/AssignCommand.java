package com.spukmk2me.animation;

import java.io.IOException;

public final class AssignCommand implements Command
{
    public byte GetCommandCode()
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
        String addr = ( c_address == null )? "null" : c_address;

		return c_name + ' ' + addr;
	}
	
    public void ReadParamFromString( String param )
	{
        final String NULLSTRING = "null";
        
        c_name = param.substring( 0, param.indexOf( ' ' ) );
		param = param.substring( param.indexOf( ' ' ) + 1 );

        c_address = NULLSTRING.equals( param )? null : param;
	}
    
    public String GetCommandLabel()
    {
        return CMDLABEL;
    }
    
    public String GetDataStrings()
    {
        if ( c_address == null )
            return c_name;

        return c_name + ' ' + c_address;
    }
    
    private static final String CMDLABEL = "assign";
	/* $endif$ */

    // See RepositionCommand for special names.
    public String c_name, c_address;
}
