package com.spukmk2me.animation;

import java.io.IOException;
import java.io.InputStream;

/* $if EXPORTABLE$ */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStream;
/* $endif$ */

import com.spukmk2me.DoublyLinkedList;

/* $if DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */

public final class CommandIO
{
    public CommandIO()
    {
        m_prototypes = new DoublyLinkedList();
        m_prototypes.push_back( new RepositionCommand() );
        m_prototypes.push_back( new VisiblilityCommand() );
        m_prototypes.push_back( new AssignCommand() );
        m_prototypes.push_back( new DelayCommand() );
        m_prototypes.push_back( new ObjectDeclarationCommand() );
        m_prototypes.push_back( new ObjectAssembleCommand() );
        m_prototypes.push_back( new ObjectRepositionCommand() );
        m_prototypes.push_back( new ShiftedRangeCommand() );
    }
    
    public void AddPrototype( Command cmd )
    {
        m_prototypes.push_back( cmd );
    }
    
    public DoublyLinkedList ReadCommands( InputStream is )
        throws IOException
    {
        DoublyLinkedList cmds = new DoublyLinkedList();
        StringMappedDataInputStream dis =
            new StringMappedDataInputStream( is );
        Command cmd;
        int nCmd, cmdCode;

        is.skip( MARKER.length() );
        nCmd = dis.readInt();
        
        dis.readStringMappingTable();

        for ( int i = 0; i != nCmd; ++i )
        {
            cmdCode = dis.readInt();
            cmd = CreateCommand( cmdCode );
            cmd.Read( dis );
            cmds.push_back( cmd );
        }

        return cmds;
    }
    
    /* $if EXPORTABLE$ */
    public void WriteCommands(
        DoublyLinkedList commands, OutputStream os ) throws IOException
    {
        DoublyLinkedList.Iterator i = commands.first();
        DoublyLinkedList.Iterator end = commands.end();
        StringMappedDataOutputStream dos =
            new StringMappedDataOutputStream( os );  
        Command cmd;

        dos.write( MARKER.getBytes() );
        dos.writeInt( commands.length() );
        
        for ( ; !i.equals( end ); i.fwrd() )
        {
            dos.addStringsToMappingTable(
                ((Command)i.data()).GetDataStrings() );
        }
        
        dos.writeStringMappingTable();
        
        i = commands.first();
        end = commands.end();

        for ( ; !i.equals( end ); i.fwrd() )
        {
            cmd = (Command)i.data();
            dos.writeInt( cmd.GetCommandCode() );
            cmd.Write( dos );
        }
    }
    
    public DoublyLinkedList LoadTextBasedCommands( BufferedReader reader )
        throws IOException
    {
        DoublyLinkedList commands = new DoublyLinkedList();
        String line = "";
        
        while ( line != null )
        {
            line = GetNextLine( reader );
            
            if ( line != null )
            {
                int firstSpaceIndex = line.indexOf( ' ' );
                String commandLabel = line.substring( 0, firstSpaceIndex );
                Command cmd = CreateCommand( GetCommandCode( commandLabel ) );
                
                cmd.ReadParamFromString( line.substring( firstSpaceIndex + 1 ) );
                commands.push_back( cmd );
            }
        }
        
        return commands;
    }
    
    public static void SaveAsTextBasedCommands( BufferedWriter writer,
        DoublyLinkedList commands ) throws IOException
    {
        DoublyLinkedList.Iterator i = commands.first();
        DoublyLinkedList.Iterator e = commands.end();
        Command cmd;
        String cmdString;
        int frameIndex = 1;
        
        writer.write( "# Frame 0" );
        writer.newLine();
        
        for ( ; !i.equals( e ); i.fwrd() )
        {
            cmd = (Command)i.data();
            cmdString = cmd.GetCommandLabel() + ' ' + cmd.GetParamAsString();
            System.out.println( cmdString );
            writer.write( cmdString );
            writer.newLine();
            
            if ( cmd.GetCommandCode() == Command.CMDCODE_DELAY )
            {
                writer.newLine();
                writer.write( "# Frame " + frameIndex );
                writer.newLine();
                ++frameIndex;
            }
        }
    }
    
    private int GetCommandCode( String commandLabel )
    {
        DoublyLinkedList.Iterator i = m_prototypes.first();
        DoublyLinkedList.Iterator e = m_prototypes.end();
        Command cmd;
        
        for ( ; !i.equals ( e ); i.fwrd() )
        {
            cmd = (Command)i.data();
            
            if ( commandLabel.equals( cmd.GetCommandLabel() ) )
                return cmd.GetCommandCode();
        }
        
        return -1;
    }
    
    private static String GetNextLine( BufferedReader reader ) throws IOException
    {
        String line = null;
        boolean continueReading = true;
        
        while ( continueReading )
        {
            line = reader.readLine();
            
            // End of file
            if ( line == null )
                break;
            
            int firstIndex, lastIndex;
            
            for ( firstIndex = 0; firstIndex != line.length(); ++firstIndex )
            {
                if ( line.charAt( firstIndex ) != ' ' )
                    break;
            }
            
            for ( lastIndex = line.length() - 1; lastIndex != -1; --firstIndex )
            {
                if ( line.charAt( firstIndex ) != ' ' )
                    break;
            }
            
            line = line.substring( firstIndex, lastIndex + 1 );
                
            if ( line.length() != 0 )
                continueReading = line.charAt( 0 ) == '#';
        }
        
        /* $if DEBUG$ */
        Logger.Log( line + '\n' );
        /* $endif$ */
        
        return line;
    }
    /* $endif$ */
    
    private Command CreateCommand( int commandCode )
    {
        DoublyLinkedList.Iterator i = m_prototypes.first();
        DoublyLinkedList.Iterator e = m_prototypes.end();
        Command cmd;
        
        for ( ; !i.equals ( e ); i.fwrd() )
        {
            cmd = (Command)i.data();
            
            if ( cmd.GetCommandCode() == commandCode )
                return cmd.CreateClone();
        }
        
        return null;
    }
    
    private static final String MARKER = "SPUKMK2me Animation 0.1";
    private DoublyLinkedList m_prototypes;
}
