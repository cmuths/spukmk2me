package com.spukmk2me.animation;

import java.io.IOException;

public final class ObjectAssembleCommand implements Command
{
    public ObjectAssembleCommand()
    {
        c_dependantFlags =
            ObjectNodeInfo.DEPEND_X | ObjectNodeInfo.DEPEND_Y |
            ObjectNodeInfo.DEPEND_Z; 
    }
    
    public byte GetCommandCode()
    {
        return Command.CMDCODE_ASSEMBLE;
    }
    
    public Command CreateClone()
    {
        return new ObjectAssembleCommand();
    }
    
    public void Read( StringMappedDataInputStream dis ) throws IOException
    {
        c_nodeName          = dis.readStringID();
        c_objectName        = dis.readStringID();
        c_assembleType      = dis.readByte();
        c_dependantFlags    = dis.readByte();
    }

    /* $if EXPORTABLE$ */
    public void Write( StringMappedDataOutputStream dos ) throws IOException
    {
        dos.writeStringID( c_nodeName );
        dos.writeStringID( c_objectName );
        dos.writeByte( c_assembleType );
        dos.writeByte( c_dependantFlags );
    }
    
    public String GetParamAsString()
    {   
        String param = c_nodeName + ' ' + c_objectName + ' ';
        
        if ( c_assembleType == 0 )
            param += "attach ";
        else
            param += "detach ";
        
        if ( c_dependantFlags == 0 )
            param += "none";
        else
        {
            if ( (c_dependantFlags & ObjectNodeInfo.DEPEND_X) != 0 )
                param += 'x';
            
            if ( (c_dependantFlags & ObjectNodeInfo.DEPEND_Y) != 0 )
                param += 'y';
            
            if ( (c_dependantFlags & ObjectNodeInfo.DEPEND_Z) != 0 )
                param += 'z';
        }
        
        return param;
    }

    public void ReadParamFromString( String param )
    {
        c_nodeName = param.substring( 0, param.indexOf( ' ' ) );
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        c_objectName = param.substring( 0, param.indexOf( ' ' ) );
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        if ( param.substring( 0, param.indexOf( ' ' ) ).equals( "attach" ) )
            c_assembleType = 0;
        else
            c_assembleType = 1;
        
        param = param.substring( param.indexOf( ' ' ) + 1 );
        
        c_dependantFlags = 0;
        
        if ( param.indexOf( 'x' ) != -1 )
            c_dependantFlags |= ObjectNodeInfo.DEPEND_X;
        
        if ( param.indexOf( 'y' ) != -1 )
            c_dependantFlags |= ObjectNodeInfo.DEPEND_Y;
        
        if ( param.indexOf( 'z' ) != -1 )
            c_dependantFlags |= ObjectNodeInfo.DEPEND_Z;
    }
    
    public String GetCommandLabel()
    {
        return CMDLABEL;
    }
    
    public String GetDataStrings()
    {
        return c_nodeName + ' ' + c_objectName;
    }
    
    private static final String CMDLABEL = "assemble";
    /* $endif$ */
    
    public String c_nodeName, c_objectName;
    public byte c_assembleType; // 0: attach, 1: detach
    public byte c_dependantFlags;
}
