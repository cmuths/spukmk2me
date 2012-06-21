package com.spukmk2me.spukmk2mesceneeditor.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.resource.ResourceSet;
import com.spukmk2me.resource.DefaultResourceProducer;
import com.spukmk2me.scene.DefaultSceneNodeProducer;
import com.spukmk2me.scene.SceneTreeLoader;

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

        SceneTreeLoader sceneLoader = new SceneTreeLoader();
        DefaultResourceProducer producer =
            new DefaultResourceProducer( m_data.GetDevice().GetVideoDriver(),
                m_data.GetDevice().GetSoundMonitor(),
                m_data.GetDevice().GetFileSystem(),
                savePath );
        
        boolean result = sceneLoader.Load( dis, producer,
            new DefaultSceneNodeProducer() );
        ISceneNode rootNode = sceneLoader.Get( "root" );
        
        m_data.SetResourceSet( sceneLoader.GetResourceSet() );
        m_data.ChangeRootNode( rootNode );
        m_data.DispatchReload();

        return result;
    }

    private CentralData m_data;
}
