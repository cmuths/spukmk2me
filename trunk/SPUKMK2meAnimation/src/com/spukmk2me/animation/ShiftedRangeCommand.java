package com.spukmk2me.animation;

import java.io.IOException;

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
    
    public void Read( StringMappedDataInputStream dis ) throws IOException
    {
        c_nodeName      = dis.readStringID();
        c_objectName    = dis.readStringID();
        c_shiftX        = dis.readShort();
        c_shiftY        = dis.readShort();
        c_shiftZ        = dis.readShort();
    }

    /* $if EXPORTABLE$ */
    public void Write( StringMappedDataOutputStream dos ) throws IOException
    {
        dos.writeStringID( c_nodeName );
        dos.writeStringID( c_objectName );
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
    
    public String GetDataStrings()
    {
        return c_nodeName + ' ' + c_objectName;
    }
    
    private static final String CMDLABEL = "shift";
    /* $endif$ */
    
    public String c_nodeName, c_objectName;
    public short c_shiftX, c_shiftY, c_shiftZ;
}
