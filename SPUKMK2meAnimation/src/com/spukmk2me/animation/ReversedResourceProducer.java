package com.spukmk2me.animation;

import java.io.DataInputStream;
import java.io.IOException;

import com.spukmk2me.io.IFileSystem;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.SubImageConstructionData;
import com.spukmk2me.sound.ISoundMonitor;
import com.spukmk2me.resource.IResource;
import com.spukmk2me.resource.IResourceConstructionData;
import com.spukmk2me.resource.IResourceProducer;
import com.spukmk2me.resource.DefaultResourceProducer;

public final class ReversedResourceProducer extends IResourceProducer
{
    public ReversedResourceProducer( IVideoDriver vdriver, ISoundMonitor smonitor,
        IFileSystem fsystem, String dirPathToSceneFile )
    {
        m_defaultProducer = new DefaultResourceProducer(
            vdriver, smonitor, fsystem, dirPathToSceneFile );
    }

    public IResource CreateResource( IResourceConstructionData data )
        throws IOException
    {
        return m_defaultProducer.CreateResource( data );
    }

    public boolean IsSupported( byte resourceType )
    {
        return m_defaultProducer.IsSupported( resourceType );
    }

    public IResourceConstructionData LoadTypeBasedConstructionData(
        DataInputStream is, int resourceType ) throws IOException
    {
        IResourceConstructionData data =
            m_defaultProducer.LoadTypeBasedConstructionData( is, resourceType );
        
        if ( resourceType == IResource.RT_IMAGE )
        {
            SubImageConstructionData imgData =
                (SubImageConstructionData)data;
            
            if ( (imgData.c_flippingFlags & IVideoDriver.FLIP_HORIZONTAL) != 0 )
                imgData.c_flippingFlags &= ~IVideoDriver.FLIP_HORIZONTAL;
            else
                imgData.c_flippingFlags |= IVideoDriver.FLIP_HORIZONTAL;
        }
        
        return data;
    }
    
    private DefaultResourceProducer m_defaultProducer;
}
