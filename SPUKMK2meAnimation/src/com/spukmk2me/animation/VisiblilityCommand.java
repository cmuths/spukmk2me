package com.spukmk2me.animation;

import java.io.DataInputStream;
import java.io.IOException;

/* $if EXPORTABLE$ */
import java.io.DataOutputStream;
/* $endif$ */

public final class VisiblilityCommand implements Command
{
    public int GetCommandCode()
    {
        return Command.CMDCODE_VISIBLE;
    }
    
    public Command CreateClone()
    {
        return new VisiblilityCommand();
    }
    
    public void Read( DataInputStream dis ) throws IOException
    {
        c_name = dis.readUTF();
        c_flags = dis.readByte();
    }

    /* $if EXPORTABLE$ */
    public void Write( DataOutputStream dos ) throws IOException
    {
        dos.writeUTF( c_name );
        dos.writeByte( c_flags );
    }
    
    public String GetParamAsString()
    {
        String param = c_name + ' ';
        
        if ( (c_flags & 0x02) != 0 )
            param += "enable ";
        else
            param += "disable ";
            
        if ( (c_flags & 0x01) != 0 )
            param += "visible";
        else
            param += "invisible";
            
        return param;
    }
    
    public void ReadParamFromString( String param )
    {
        c_name = param.substring( 0, param.indexOf( ' ' ) );
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        c_flags = 0;
        
        if ( param.substring( 0, param.indexOf( ' ' ) ).equals( "enable" ) )
            c_flags |= 0x02;
            
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        if ( param.equals( "visible" ) )
            c_flags |= 0x01;
    }
    
    public String GetCommandLabel()
    {
        return CMDLABEL;
    }
    
    private static final String CMDLABEL = "vis";
    /* $endif$ */
    
    public String c_name;
    // Bit 0: visibale/invisible
    // Bit 1: enable/disable
    public byte c_flags;
}
