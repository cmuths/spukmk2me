package com.spukmk2me.animation;

import java.io.DataInputStream;
import java.io.IOException;

/* $if EXPORTABLE$ */
import java.io.DataOutputStream;
/* $endif$ */

public final class DelayCommand implements Command
{
    public int GetCommandCode()
    {
        return Command.CMDCODE_DELAY;
    }
    
    public Command CreateClone()
    {
        return new DelayCommand();
    }
    
    public void Read( DataInputStream dis ) throws IOException
    {
        c_time = dis.readInt();
    }

    /* $if EXPORTABLE$ */
    public void Write( DataOutputStream dos ) throws IOException
    {
        dos.writeInt( c_time );
    }
    
	public String GetParamAsString()
	{
		return String.valueOf( (double)c_time / 0x00010000 );
	}
	
    public void ReadParamFromString( String param )
	{
		c_time = (int)(Double.parseDouble( param ) * 0x00010000);
	}
    
    public String GetCommandLabel()
    {
        return CMDLABEL;
    }
    
    private static final String CMDLABEL = "delay";
    /* $endif$ */

    public int c_time;
}
