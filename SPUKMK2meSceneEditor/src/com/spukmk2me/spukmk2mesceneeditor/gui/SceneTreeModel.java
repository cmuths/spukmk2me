package com.spukmk2me.spukmk2mesceneeditor.gui;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.scene.ISceneNode;

public final class SceneTreeModel implements TreeModel
{
    public SceneTreeModel()
    {
        m_modelListenerList = new DoublyLinkedList();
    }

    public void SetRoot( ISceneNode rootNode )
    {
        m_root = rootNode;
    }

    public Object getRoot()
    {
        return m_root;
    }

    public Object getChild( Object parent, int index )
    {
        ISceneNode childNode = ((ISceneNode)parent).c_children;

        if ( childNode == null ) // Hope this won't happen
            return null;

        do
        {
            childNode = childNode.c_next;
        } while ( index-- != 0 );

        return childNode;
    }

    public int getChildCount( Object parent )
    {
        ISceneNode childNode = ((ISceneNode)parent).c_children;

        if ( childNode == null )
            return 0;

        ISceneNode stopNode = childNode;
        int count = 0;

        for ( childNode = stopNode.c_next; childNode != stopNode; )
        {
            childNode = childNode.c_next;
            ++count;
        }

        return count;
    }

    public boolean isLeaf( Object node )
    {
        return ((ISceneNode)node).c_children == null;
    }

    public void valueForPathChanged( TreePath path, Object newValue )
    {
        System.out.println( "Path changed" );
    }

    public int getIndexOfChild( Object parent, Object child )
    {
        if ( (parent == null) || (child == null) )
            return -1;

        ISceneNode childNode = ((ISceneNode)parent).c_children;

        if ( childNode == null )
            return -1;

        ISceneNode stopNode = childNode;
        int index = 0;

        for ( childNode = stopNode.c_next; childNode != stopNode;)
        {
            if ( childNode == child )
                return index;

            childNode = childNode.c_next;
            ++index;
        }

        return -1;
    }

    public void addTreeModelListener( TreeModelListener listener )
    {
        m_modelListenerList.push_back( listener );
    }

    public void removeTreeModelListener( TreeModelListener listener )
    {
        if ( m_modelListenerList.length() == 0 )
            return;

        DoublyLinkedList.Iterator i, end;
        int index = 0;

        end = m_modelListenerList.end();

        for ( i = m_modelListenerList.first(); !i.equals( end ); i.fwrd() )
        {
            if ( i.data() == listener )
            {
                m_modelListenerList.erase( index );
                break;
            }

            ++index;
        }
    }
    
    private ISceneNode          m_root;
    private DoublyLinkedList    m_modelListenerList;
}
