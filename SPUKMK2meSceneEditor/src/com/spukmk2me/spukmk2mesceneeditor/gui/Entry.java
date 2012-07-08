package com.spukmk2me.spukmk2mesceneeditor.gui;

import com.spukmk2me.debug.Logger;
import com.spukmk2me.debug.DefaultMessageExporter;
import com.spukmk2me.extension.j2se.J2SEFileSystem;
import com.spukmk2me.extension.j2se.J2SEVideoDriver;
import com.spukmk2me.extension.nullmodules.NullSoundMonitor;
import com.spukmk2me.resource.DefaultResourceProducer;
import com.spukmk2me.resource.IResource;
import com.spukmk2me.resource.ResourceSet;
import com.spukmk2me.scene.DefaultSceneNodeProducer;
import com.spukmk2me.scene.SceneTreeLoader;
import com.spukmk2me.spukmk2mesceneeditor.data.Saver;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ImageResourceConstructionData;
import com.spukmk2me.video.SubImageConstructionData;
import java.io.*;

public final class Entry
{
    private Entry() {}

    public static void main( String[] args )
    {
        convert();
        
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
    
    private static void convert()
    {
        try
        {
            final String ROOT = "D:\\tuannq\\SRW\\SRWProject\\SRW\\pkgdata_out";
            BufferedReader reader = new BufferedReader( new FileReader( "D:\\tuannq\\SRW\\SRWProject\\filelistingscript\\ssfoutput.txt" ) );

            String str;

            do
            {
                str = reader.readLine();

                if ( str == null )
                    break;

                System.out.println( "Converting " + str );

                if ( str.length() != 0 )
                    convertFile2( ROOT + str.replace( '/', File.separatorChar ) );
            } while ( true );

            reader.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
    
    private static void convertFile( String filename ) throws IOException
    {
        J2SEVideoDriver vdrv = new J2SEVideoDriver();
        J2SEFileSystem fsys = new J2SEFileSystem();
        NullSoundMonitor smtr = new NullSoundMonitor();
        SceneTreeLoader loader = new SceneTreeLoader();
        DefaultResourceProducer producer = new DefaultResourceProducer( vdrv,
            smtr, fsys, filename.substring( 0, filename.lastIndexOf( File.separatorChar ) ) );
        DefaultSceneNodeProducer nodeProducer = new DefaultSceneNodeProducer();
        
        InputStream is = new FileInputStream( filename );
        //is.skip( 24 );
        loader.Load( is , producer, nodeProducer );
        is.close();
        
        FileOutputStream os = new FileOutputStream( filename );
        Saver.Save( loader.GetResourceSet(), loader.Get( "root" ),
            os, filename.substring( 0, filename.lastIndexOf( File.separatorChar ) ), fsys );
        os.close();
    }
    
    private static void convertFile2( String filename ) throws IOException
    {
        J2SEVideoDriver vdrv = new J2SEVideoDriver();
        J2SEFileSystem fsys = new J2SEFileSystem();
        NullSoundMonitor smtr = new NullSoundMonitor();
        SceneTreeLoader loader = new SceneTreeLoader();
        DefaultResourceProducer producer = new DefaultResourceProducer( vdrv,
            smtr, fsys, filename.substring( 0, filename.lastIndexOf( File.separatorChar ) ) );
        DefaultSceneNodeProducer nodeProducer = new DefaultSceneNodeProducer();
        
        InputStream is = new FileInputStream( filename );
        loader.Load( is , producer, nodeProducer );
        is.close();
        
        ResourceSet newSet = new ResourceSet();
        StrGenerator generator = new StrGenerator();
        IResource resource;
        ImageResourceConstructionData resData;
        int n = loader.GetResourceSet().GetNumberOfResources( IResource.RT_IMAGERESOURCE );
        
        for ( int i = 0; i != n; ++i )
        {
            resource = loader.GetResourceSet().GetResource( i, IResource.RT_IMAGERESOURCE );
            //resource.ChangeProxyName( generator.getNextString() );
            resData = (ImageResourceConstructionData)resource.GetConstructionData();
            resData.c_proxyname = resource.GetProxyName();
            newSet.AddResource( resource );
        }
        
        generator = new StrGenerator();
        n = loader.GetResourceSet().GetNumberOfResources( IResource.RT_IMAGE );
        SubImageConstructionData imgData;
        
        for ( int i = 0; i != n; ++i )
        {
            resource = loader.GetResourceSet().GetResource( i, IResource.RT_IMAGE );
            //resource.ChangeProxyName( generator.getNextString() );
            imgData = (SubImageConstructionData)resource.GetConstructionData();
            imgData.c_proxyname = resource.GetProxyName();
            newSet.AddResource( resource );
        }
        
        FileOutputStream os = new FileOutputStream( filename );
        Saver.Save( newSet, loader.Get( "root" ),
            os, filename.substring( 0, filename.lastIndexOf( File.separatorChar ) ), fsys );
        os.close();
    }
    
    private static class StrGenerator
    {
        private StrGenerator()
        {
            m_curString = null;
        }
        
        private String getNextString()
        {
            if ( m_curString == null )
                m_curString = "a";
            else
            {
                char[] chars = m_curString.toCharArray();
                int index = m_curString.length() - 1;
                char nextChar = getNextChar( chars[ index ] );
                
                chars[ index ] = nextChar;
                
                while ( nextChar == 0 )
                {
                    chars[ index ] = 'a';
                    
                    if ( --index == -1 )
                        break;
                    
                    nextChar = getNextChar( chars[ index ] );
                    chars[ index ] = nextChar;
                }
                
                if ( index == -1 )
                    m_curString = "a" + new String( chars );
                else
                    m_curString = new String( chars );
            }
            
            return m_curString;
        }
        
        private char getNextChar( char ch )
        {
            if (    ((ch >= 'a') && (ch < 'z')) ||
                    (ch >= '0') && (ch < '9') )
                return (char)(ch + 1);
            else if ( ch == 'z' )
                return '0';
            else if ( ch == '9' )
                return '_';
            
            return 0;
        }
        
        private String m_curString;
    }
}
