package com.spukmk2me.animation;

import java.io.DataInputStream;
import java.io.IOException;

import com.spukmk2me.resource.ResourceSet;
import com.spukmk2me.scene.DefaultSceneNodeProducer;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.ISceneNodeProducer;
import com.spukmk2me.scene.TiledLayerSceneNode;
import com.spukmk2me.scene.LineSceneNode;

final class ReversedSceneNodeProducer implements ISceneNodeProducer
{
    public ReversedSceneNodeProducer()
    {
        m_producer = new DefaultSceneNodeProducer();
    }

    public ISceneNode ConstructSceneNode( DataInputStream dis, byte type )
        throws IOException
    {
        ISceneNode node = m_producer.ConstructSceneNode( dis, type );
        
        switch ( type )
        {
            case 4:
                {
                    TiledLayerSceneNode tiledNode = (TiledLayerSceneNode)node;
                    byte[] data = tiledNode.c_terrainData;
                    int baseIndex = 0;
                    byte swap;
                    
                    for ( int y = tiledNode.c_tableHeight; y != 0; --y )
                    {
                        for ( int i = 0; i != tiledNode.c_tableWidth / 2; ++i )
                        {
                            swap = data[ baseIndex + i ];
                            data[ baseIndex + i ] = data[ baseIndex + tiledNode.c_tableWidth - 1 - i ];
                            data[ baseIndex + tiledNode.c_tableWidth - 1 - i ] = swap;
                        }
                        
                        baseIndex += tiledNode.c_tableWidth;
                    }
                }
                
                break;
                
            case 7:
                {
                    LineSceneNode lineNode = (LineSceneNode)node;
                    
                    lineNode.c_y += lineNode.c_deltaY;
                    lineNode.c_deltaY = (short)(0 - lineNode.c_deltaY);
                }
        }
    
        return node;
    }
    
    public void UpdateResourceSet( ResourceSet resourceSet )
    {
        m_producer.UpdateResourceSet( resourceSet );
        m_resourceSet = resourceSet;
    }
    
    private DefaultSceneNodeProducer m_producer;
    private ResourceSet m_resourceSet;
}
