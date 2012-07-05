package com.spukmk2me.animation;

import java.io.IOException;

public final class DelayCommand implements Command
{
    public byte GetCommandCode()
    {
        return Command.CMDCODE_DELAY;
    }
    
    public Command CreateClone()
    {
        return new DelayCommand();
    }
    
    public void Read( StringMappedDataInputStream dis ) throws IOException
    {
        c_time = dis.readInt();
    }

    /* $if EXPORTABLE$ */
    public void Write( StringMappedDataOutputStream dos ) throws IOException
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
    
    public String GetDataStrings()
    {
        return null;
    }
    
    private static final String CMDLABEL = "delay";
    /* $endif$ */

    public int c_time;
}
