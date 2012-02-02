package com.spukmk2me.spukmk2mesceneeditor.data;

import com.spukmk2me.Device;
import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.scene.SceneManager;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.NullSceneNode;
import com.spukmk2me.optional.scene.ResourceManager;

import com.spukmk2me.extension.j2se.J2SEVideoDriver;
import com.spukmk2me.extension.j2se.J2SEFileSystem;
import com.spukmk2me.spukmk2mesceneeditor.gui.SceneManagerEventListener;

public final class CentralData
{
    public CentralData()
    {
        m_listenerList          = new DoublyLinkedList();
        IVideoDriver vdriver    = new J2SEVideoDriver();
        SceneManager scene      = new SceneManager( vdriver, 0 );

        scene.ChangeStackSize( 1000 );
        m_device = Device.CreateSPUKMK2meDevice(
            vdriver, null, null, new J2SEFileSystem(), scene );
        m_resourceManager = new ResourceManager();
        DispatchReset();
    }

    public ResourceManager GetResourceManager()
    {
        return m_resourceManager;
    }

    public void ChangeRootNode( ISceneNode node )
    {
        m_rootNode = node;
    }

    public ISceneNode GetRootNode()
    {
        return m_rootNode;
    }
    
    public void SetCurrentNode( ISceneNode node )
    {
        m_currentNode = node;
    }

    public ISceneNode GetCurrentNode()
    {
        return m_currentNode;
    }

    public void DispatchReload()
    {
        DoublyLinkedList.Iterator i, end;

        end = m_listenerList.end();

        for ( i = m_listenerList.first(); !i.equals( end ); i.fwrd() )
            ((SceneManagerEventListener)i.data()).SetCentralData( this );
    }

    public void DispatchReset()
    {
        m_resourceManager.Clear();
        m_device.GetSceneManager().Clear();
        m_rootNode = new NullSceneNode();
        m_rootNode.c_exportFlag  = true;
        m_rootNode.c_proxyName   = "root";
        m_currentNode   = null;

        DoublyLinkedList.Iterator i, end;

        end = m_listenerList.end();

        for ( i = m_listenerList.first(); !i.equals( end ); i.fwrd() )
            ((SceneManagerEventListener)i.data()).Reset();
    }

    public void AddSceneManagerEventListener(
        SceneManagerEventListener listener )
    {
        m_listenerList.push_back( listener );
    }

    public Device GetDevice()
    {
        return m_device;
    }

    public DoublyLinkedList GetListenerList()
    {
        return m_listenerList;
    }

    private Device              m_device;
    private DoublyLinkedList    m_listenerList;
    private ResourceManager     m_resourceManager;
    private ISceneNode          m_rootNode, m_currentNode, m_topViewNode;
}