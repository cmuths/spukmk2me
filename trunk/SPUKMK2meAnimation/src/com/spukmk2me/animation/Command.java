package com.spukmk2me.animation;

import java.io.DataInputStream;
import java.io.IOException;
/* $if EXPORTABLE$ */
import java.io.DataOutputStream;
/* $endif$ */

public interface Command
{
    public int GetCommandCode();
    public Command CreateClone();
    public void Read( DataInputStream dis ) throws IOException;
    
    /* $if EXPORTABLE$ */
    public void Write( DataOutputStream dos ) throws IOException;
    public String GetCommandLabel();
    public String GetParamAsString();
    public void ReadParamFromString( String param );
    /* $endif$ */

    // Default command codes
    public static final int CMDCODE_REPOS           = 0;
    public static final int CMDCODE_VISIBLE         = 1;
    public static final int CMDCODE_ASSIGN          = 2;
    public static final int CMDCODE_DELAY           = 3;
    public static final int CMDCODE_DECLARE         = 4;
    public static final int CMDCODE_ASSEMBLE        = 5;
    public static final int CMDCODE_SHIFTEDRANGE    = 6;
    public static final int CMDCODE_OBJECTREPOS     = 7;
}
