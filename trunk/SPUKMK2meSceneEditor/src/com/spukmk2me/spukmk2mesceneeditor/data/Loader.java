package com.spukmk2me.spukmk2mesceneeditor.data;

import com.spukmk2me.optional.scene.ResourceProducer;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.File;

import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.optional.scene.SceneTreeLoader;
import com.spukmk2me.optional.scene.StandardResourceLoader;

public final class Loader
{
    public Loader( CentralData data )
    {
        m_data = data;
    }

    public boolean Load( InputStream is, String savePath )
        throws IOException
    {
        m_data.DispatchReset();

        DataInputStream dis = new DataInputStream( is );

        SceneTreeLoader sceneLoader = new SceneTreeLoader(
           m_data.GetResourceManager() );
        StandardResourceLoader resourceLoader =
            new StandardResourceLoader( m_data.GetDevice().GetVideoDriver(),
                m_data.GetResourceManager(),
                m_data.GetDevice().GetFileSystem(),
                savePath );
        ResourceProducer producer = new ResourceProducer( resourceLoader );
        
        boolean result = sceneLoader.Load( dis, producer );
        ISceneNode rootNode = sceneLoader.Get( "root" );
        
        m_data.ChangeRootNode( rootNode );
        m_data.DispatchReload();

        return result;
    }

    private CentralData m_data;
}
