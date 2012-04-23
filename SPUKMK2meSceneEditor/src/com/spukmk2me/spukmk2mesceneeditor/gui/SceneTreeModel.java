package com.spukmk2me.spukmk2mesceneeditor.gui;

import java.util.LinkedList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.spukmk2me.scene.ISceneNode;
import javax.swing.event.TreeModelEvent;

public final class SceneTreeModel implements TreeModel
{
    public SceneTreeModel()
    {
        m_modelListenerList = new LinkedList<TreeModelListener>();
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

        for ( childNode = stopNode.c_next; childNode != stopNode; )
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
        m_modelListenerList.add( listener );
    }

    public void removeTreeModelListener( TreeModelListener listener )
    {
        if ( m_modelListenerList.size() == 0 )
            return;

        for ( int i = 0; i != m_modelListenerList.size(); ++i )
        {
            if ( m_modelListenerList.get( i ) == listener )
            {
                m_modelListenerList.remove( i );
                break;
            }
        }
    }

    public void addNode( ISceneNode node, ISceneNode parent )
    {
        parent.AddChild( node );
        fireInsertEvent( generateEvent( node ) );
        //fireChangedEvent( generateEvent( parent ) );
    }

    public void removeNode( ISceneNode node )
    {
        TreeModelEvent event = generateEvent( node );

        if ( node != m_root )
        {
            node.Drop();
            fireRemoveEvent( event );
        }
    }

    public void moveNode( ISceneNode node, boolean upDown )
    {
        if ( node != m_root )
        {
            ISceneNode before = (upDown)? node.c_prev.c_prev : node.c_next;

            if ( before != node )
            {
                node.c_prev.c_next = node.c_next;
                node.c_next.c_prev = node.c_prev;
                node.c_next = before.c_next;
                node.c_prev = before;
                before.c_next.c_prev = node;
                before.c_next = node;
            }

            fireStructureChangedEvent( generateEvent( node.c_parent ) );
        }
    }

    public void noticeNodePropertyChanged( ISceneNode node )
    {
        fireChangedEvent( generateEvent( node ) );
    }

    private TreeModelEvent generateEvent( ISceneNode node )
    {
        ISceneNode iterator = node;
        int treeDepth = 0;

        while ( iterator != m_root )
        {
            iterator = iterator.c_parent;
            ++treeDepth;
        }

        ISceneNode[]    path;
        int[]           childIndices;
        ISceneNode[]    children;
        
        if ( treeDepth == 0 )
        {
            path            = new ISceneNode[] { m_root };
            childIndices    = null;
            children        = null;
        }
        else
        {
            path = new ISceneNode[ treeDepth ];

            iterator = node;

            for ( int i = treeDepth - 1; i != -1; --i )
            {
                iterator = iterator.c_parent;
                path[ i ] = iterator;
            }
            
            childIndices = new int[] { getIndexOfChild( node.c_parent, node ) };
            children = new ISceneNode[] { node };
        }

        return new TreeModelEvent( this, path, childIndices, children );
    }

    private void fireRemoveEvent( TreeModelEvent event )
    {
        for ( int i = 0; i != m_modelListenerList.size(); ++i )
            m_modelListenerList.get( i ).treeNodesRemoved( event );
    }

    private void fireInsertEvent( TreeModelEvent event )
    {
        for ( int i = 0; i != m_modelListenerList.size(); ++i )
            m_modelListenerList.get( i ).treeNodesInserted( event );
    }

    private void fireChangedEvent( TreeModelEvent event )
    {
        for ( int i = 0; i != m_modelListenerList.size(); ++i )
            m_modelListenerList.get( i ).treeNodesChanged( event );
    }

    private void fireStructureChangedEvent( TreeModelEvent event )
    {
        for ( int i = 0; i != m_modelListenerList.size(); ++i )
            m_modelListenerList.get( i ).treeStructureChanged( event );
    }
    
    private ISceneNode                      m_root;
    private LinkedList<TreeModelListener>   m_modelListenerList;
}
