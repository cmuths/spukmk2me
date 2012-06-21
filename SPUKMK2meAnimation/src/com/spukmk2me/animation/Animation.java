package com.spukmk2me.animation;

import java.io.IOException;
import java.io.InputStream;

import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.NamedList;
import com.spukmk2me.debug.Logger;
import com.spukmk2me.io.IFileSystem;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.sound.ISoundMonitor;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.SceneTreeLoader;
import com.spukmk2me.scene.ISceneNodeProducer;
import com.spukmk2me.scene.DefaultSceneNodeProducer;
import com.spukmk2me.resource.IResourceProducer;
import com.spukmk2me.resource.DefaultResourceProducer;

public final class Animation
{
    public Animation( IVideoDriver vdriver, ISoundMonitor smonitor,
        IFileSystem fsystem, boolean reversed )
    {
        m_vdriver   = vdriver;
        m_smonitor  = smonitor;
        m_fsystem   = fsystem;
        m_commands  = new DoublyLinkedList();
        m_nodes     = new NamedList();
        m_objects   = new NamedList();
        m_revertedNodeOriginalInfos = new NamedList();
    }
    
    /**
     *  Load a material file and add it's node to this animation.
     *  \details Material files won't be added if there's name collision.
     * Name collision occurs if there's at least 2 nodes in all material files
     * that have the same name (except "root").
     *  @param materialFile Material filename (absolute path).
     *  @param originalNames Original names for changing node names.
     * Pass null if don't want to change.
     *  @param newNames New names for changing node names.
     * Pass null if don't want to change.
     *  @throws IOException If IO error occurs.
     */
    public void AddMaterialFile( String materialFile,
        String[] originalNames, String[] newNames, boolean applyRevert )
        throws IOException
    {
        SceneTreeLoader     loader = new SceneTreeLoader();
        IResourceProducer   producer;
        ISceneNodeProducer  nProducer;
        String              path = materialFile.substring(
            0, materialFile.lastIndexOf( m_fsystem.GetPathSeparator() ) );
        
        if ( applyRevert )
        {
            producer = new ReversedResourceProducer( m_vdriver, m_smonitor, m_fsystem, path );
            nProducer = new ReversedSceneNodeProducer();
        }
        else
        {
            producer = new DefaultResourceProducer( m_vdriver, m_smonitor, m_fsystem, path );
            nProducer = new DefaultSceneNodeProducer();
        }
        
        InputStream is = m_fsystem.OpenFile(
            materialFile, IFileSystem.LOCATION_AUTODETECT );
        
        loader.Load( is, producer, nProducer );
        is.close();
        
        if ( originalNames != null )
            loader.changeNodeNames( originalNames, newNames );
        
        if ( applyRevert )
            PrepareReversedTree( loader );        
        
        String[] nameList = loader.GetExportedNames();
        
        for ( int i = 0; i != nameList.length; ++i )
        {
            if ( m_nodes.exist( nameList[ i ] ) != -1 )
                return;
        }
        
        for ( int i = 0; i != nameList.length; ++i )
            m_nodes.add( loader.Get( nameList[ i ] ), nameList[ i ] );
    }
    
    public void AddMaterialFromAnimation( Animation anim )
    {
        String[] names = anim.GetNodeNameList();
        
        for ( int i = 0; i != names.length; ++i )
            this.AddNode( anim.GetNode( names[ i ] ), names[ i ] );
        
        DoublyLinkedList.Iterator iname = anim.GetOriginalPositionList().getNameIterator();
        DoublyLinkedList.Iterator idata = anim.GetOriginalPositionList().getObjectIterator();
        int length = anim.GetOriginalPositionList().length();
        
        for ( ; length != 0; --length )
        {
            m_revertedNodeOriginalInfos.add( idata.data(), (String)iname.data() );
            iname.fwrd();
            idata.fwrd();
        }
    }
    
    public void AddObjectFromAnimation( Animation anim )
    {
        DoublyLinkedList.Iterator iname = anim.m_objects.getNameIterator();
        DoublyLinkedList.Iterator idata = anim.m_objects.getObjectIterator();
        int length = anim.m_objects.length();
        
        for ( ; length != 0; --length )
        {
            m_objects.add( idata.data(), (String)iname.data() );
            iname.fwrd();
            idata.fwrd();
        }
    }
    
    /**
     *  Add a new object to animation.
     *  \details If there's name collision, object won't be added.
     *  @param name Object name.
     *  @return True if and only if the name collision doesn't occur.
     */
    public boolean AddAnimationObject( String name )
    {
        return m_objects.add( new AnimationObject(), name );
    }
    
    /**
     *  Rename an animation object.
     *  @param oldName Old name.
     *  @param newName New name.
     *  @return Object can be renamed or not.
     */
    public boolean RenameAnimationObject( String oldName, String newName )
    {
        Object obj = m_objects.get( oldName ); 
        
        if ( obj == null )
        {
            /* $if DEBUG$ */
            Logger.Trace( "Object doesn't exist: " + oldName + '\n' );
            /* $endif$ */
            return false;
        }
        
        if ( m_objects.get( newName ) != null )
        {
            /* $if DEBUG$ */
            Logger.Trace( "New name was used in the list: " + newName + '\n' );
            /* $endif$ */
            return false;
        }
        
        m_objects.remove( oldName );
        m_objects.add( obj, newName );
        return true;
    }
    
    public AnimationObject GetAnimationObject( String name )
    {
        return (AnimationObject)m_objects.get( name );
    }
    
    public void SetCommands( DoublyLinkedList commands )
    {
        m_commands = commands;
    }
    
    public DoublyLinkedList GetCommands()
    {
        return m_commands;
    }
    
    public void AddNode( ISceneNode node, String name )
    {
        m_nodes.add( node, name );
    }
    
    public String[] GetNodeNameList()
    {
        String[] list = new String[ m_nodes.length() ];
        DoublyLinkedList.Iterator itr = m_nodes.getNameIterator();
        int length = m_nodes.length();
        
        for ( int i = 0; i != length; ++i )
        {
            list[ i ] = (String)itr.data();
            itr.fwrd();
        }
        
        return list;
    }
    
    public ISceneNode GetNode( String name )
    {
        return (ISceneNode)m_nodes.get( name );
    }
    
    /**
     *  Clear every things.
     */
    public void Clear()
    {
        m_commands.clear();
        m_nodes.clear();
        m_objects.clear();
    }
    
    NamedList GetOriginalPositionList()
    {
        return m_revertedNodeOriginalInfos;
    }
    
    void RealignAnimationObjects()
    {
        DoublyLinkedList.Iterator itr = m_objects.getObjectIterator();
        
        for ( int i = m_objects.length(); i != 0; --i )
        {
            ((AnimationObject)itr.data()).RealignNodes();
            itr.fwrd();
        }
    }
    
    private void PrepareReversedTree( SceneTreeLoader loader )
    {
        {
            String[] nodeNameList   = loader.GetExportedNames();
            NodePositionInfo info;
            ISceneNode node;
            
            for ( int i = 0; i != nodeNameList.length; ++i )
            {
                info        = new NodePositionInfo();
                node        = loader.Get( nodeNameList[ i ] );
                info.c_x    = node.c_x;
                info.c_y    = node.c_y;
                m_revertedNodeOriginalInfos.add( info, nodeNameList[ i ] );
            }
        }
        
        DoublyLinkedList stack = new DoublyLinkedList();
        DoublyLinkedList finish = new DoublyLinkedList();
        ISceneNode childNode, finishNode;
        long hbb;
        int px = 0, pw = 0, nx, nw;
        ISceneNode root = loader.Get( "root" );;
        ISceneNode node;

        if ( root.c_children != null )
        {
            stack.push_back( root );
            finish.push_back( root.c_next );
        }   

        while ( stack.length() != 0 )
        {
            node = (ISceneNode)stack.pop_back();

            // Reached all the nodes
            if ( node == finish.peek_back() )
                finish.pop_back();
            else
            {
                stack.push_back( node.c_next );
                
                if ( node.c_children != null )
                {
                    finishNode = node.c_children;
                    childNode = finishNode.c_next;
                    stack.push_back( childNode );
                    finish.push_back( finishNode );
                    
                    if ( node != root )
                    {
                        hbb = node.GetHierarchicalBoundingRect();
                        px = (int)((hbb >> 48) & 0x0000FFFF);
                        pw = (int)((hbb >> 16) & 0x0000FFFF);
                    }
                    
                    while ( childNode != finishNode )
                    {
                        hbb = childNode.GetHierarchicalBoundingRect();
                        nx = (int)((hbb >> 48) & 0x0000FFFF);
                        nw = (int)((hbb >> 16) & 0x0000FFFF);
                        childNode.c_x = (short)(-childNode.c_x + pw - nw + ((px - nx) << 1));
                        childNode = childNode.c_next;
                    }
                }
            }
        }
    }
    
    private DoublyLinkedList    m_commands;
    private IVideoDriver        m_vdriver;
    private ISoundMonitor       m_smonitor;
    private IFileSystem         m_fsystem;
    private NamedList           m_nodes;
    private NamedList           m_objects;
    private NamedList           m_revertedNodeOriginalInfos;
}
