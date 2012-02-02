package com.spukmk2me.spukmk2mesceneeditor.gui;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.video.ICFont;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.scene.ISceneNode;

import com.spukmk2me.spukmk2mesceneeditor.data.CentralData;

public class DisplayedSceneTree extends JTree
    implements SceneManagerEventListener, TreeSelectionListener
{
    /**
     *  Create a tree that display scene structure
     *  @param selectionMode Selection mode, see
     * javax.swing.tree.TreeSelectionModel.
     */
    public DisplayedSceneTree( int selectionMode )
    {
        m_treeModel = new SceneTreeModel();
        m_treeModel.SetRoot( null );

        this.getSelectionModel().setSelectionMode( selectionMode );
        this.addTreeSelectionListener( this );
    }

    public void ImageResourceChanged( IImageResource imageResource,
        byte changingCode )
    {
    }

    public void ImageChanged( ISubImage image, byte changingCode )
    {
    }

    public void FontChanged( ICFont font, byte changingCode )
    {
    }

    public void CurrentNodeChanged()
    {
    }

    /**
     *  @param data CentralData that hold the tree. If this parameter is null,
     * events noticing won't be generated.
     */
    public void SetCentralData( CentralData data )
    {
        m_data = data;
        Reset();
    }

    public void Reset()
    {
        this.setModel( null );
        m_treeModel.SetRoot( m_data.GetRootNode() );
        this.setModel( m_treeModel );
    }

    public void valueChanged( TreeSelectionEvent e )
    {
        if ( m_data != null )
        {
            m_data.SetCurrentNode( (ISceneNode)this.
                getLastSelectedPathComponent() );

            DoublyLinkedList            list = m_data.GetListenerList();
            DoublyLinkedList.Iterator   i, end;

            end = list.end();

            for ( i = list.first(); !i.equals( end ); i.fwrd() )
                ((SceneManagerEventListener)i.data()).CurrentNodeChanged();
        }
    }

    public CentralData      m_data;
    public SceneTreeModel   m_treeModel;
}
