package com.spukmk2me.spukmk2mesceneeditor.data;

import com.spukmk2me.scene.ISceneNode;

// Miscellaneous functions
public final class Misc
{
    private Misc() {}

    public static byte GetVisibleFlag( ISceneNode node )
    {
        byte ret = 0;

        if ( node.c_visible )
            ret |= 0x80;

        if ( node.c_enable )
            ret |= 0x40;

        return ret;
    }

    public static String ConvertSeparatorCharacter(
        String path, char originalChar, char convertedChar )
    {
        char[] str = path.toCharArray();

        for ( int i = 0; i != str.length; ++i )
        {
            if ( str[ i ] == originalChar )
                str[ i ] = convertedChar;
        }

        return new String( str );
    }

    public static String WORKINGDIR = ".";
}
