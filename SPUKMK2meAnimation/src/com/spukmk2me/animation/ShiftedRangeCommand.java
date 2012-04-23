package com.spukmk2me.animation;

import java.io.DataInputStream;
import java.io.IOException;

/* $if EXPORTABLE$ */
import java.io.DataOutputStream;
/* $endif$ */

public final class ShiftedRangeCommand implements Command
{
    public int GetCommandCode()
    {
        return Command.CMDCODE_SHIFTEDRANGE;
    }
    
    public Command CreateClone()
    {
        return new ShiftedRangeCommand();
    }
    
    public void Read( DataInputStream dis ) throws IOException
    {
        c_nodeName      = dis.readUTF();
        c_objectName    = dis.readUTF();
        c_shiftX        = dis.readShort();
        c_shiftY        = dis.readShort();
        c_shiftZ        = dis.readShort();
    }

    /* $if EXPORTABLE$ */
    public void Write( DataOutputStream dos ) throws IOException
    {
        dos.writeUTF( c_nodeName );
        dos.writeUTF( c_objectName );
        dos.writeShort( c_shiftX );
        dos.writeShort( c_shiftY );
        dos.writeShort( c_shiftZ );
    }
    
    public String GetParamAsString()
    {
        return c_nodeName + ' ' + c_objectName + ' ' +
            c_shiftX + ' ' + c_shiftY + ' ' + c_shiftZ;
    }

    public void ReadParamFromString( String param )
    {
        c_nodeName = param.substring( 0, param.indexOf( ' ' ) );
        param = param.substring( param.indexOf( ' ' ) + 1 ); 
        
        c_objectName = param.substring( 0, param.indexOf( ' ' ) );
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        c_shiftX = Short.parseShort( param.substring( 0, param.indexOf( ' ' ) ) );
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        c_shiftY = Short.parseShort( param.substring( 0, param.indexOf( ' ' ) ) );
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        c_shiftZ = Short.parseShort( param );
    }
    
    public String GetCommandLabel()
    {
        return CMDLABEL;
    }
    
    private static final String CMDLABEL = "shift";
    /* $endif$ */
    
    public String c_nodeName, c_objectName;
    public short c_shiftX, c_shiftY, c_shiftZ;
}
