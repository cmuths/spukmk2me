package com.spukmk2me.spukmk2mesceneeditor.gui;

import com.spukmk2me.debug.Logger;
import com.spukmk2me.debug.DefaultMessageExporter;

public final class Entry
{
    private Entry() {}

    public static void main( String[] args )
    {
        java.awt.EventQueue.invokeLater(
            new Runnable()
            {
                public void run()
                {
                    Logger.InitialiseLoggingSystem();
                    Logger.AssignExporter( new DefaultMessageExporter() );
                    new MainGUI().setVisible( true );
                }
            }
        );
    }
}
