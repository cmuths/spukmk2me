package com.spukmk2me.animation;

import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.NamedList;
import com.spukmk2me.scene.ISceneNode;

public final class AnimationObject
{
    public AnimationObject()
    {
        c_attachedNodeInfos = new NamedList();
    }
    
    public void AttachNode( ObjectNodeInfo info, String name )
    {
        c_attachedNodeInfos.add( info, name );
    }
    
    public void DetachNode( String name )
    {
        c_attachedNodeInfos.remove( name );
    }
    
    public ObjectNodeInfo GetNodeInfo( String name )
    {
        return (ObjectNodeInfo)c_attachedNodeInfos.get( name );
    }
    
    void RealignNodes()
    {
        DoublyLinkedList.Iterator itr = c_attachedNodeInfos.getObjectIterator();
        ObjectNodeInfo nodeInfo;
        ISceneNode node;
        int length = c_attachedNodeInfos.length();
        int x, y, z;
        
        for ( ; length != 0; --length )
        {
            nodeInfo = (ObjectNodeInfo)itr.data();
            node = nodeInfo.c_node;
            
            if ( (nodeInfo.c_dependantFlags & ObjectNodeInfo.DEPEND_X) != 0 )
                x = c_x + nodeInfo.c_shiftX;
            else
                x = 0;
            
            if ( (nodeInfo.c_dependantFlags & ObjectNodeInfo.DEPEND_Y) != 0 )
                y = c_y + nodeInfo.c_shiftY;
            else
                y = 0;
            
            if ( (nodeInfo.c_dependantFlags & ObjectNodeInfo.DEPEND_Z) != 0 )
                z = c_z + nodeInfo.c_shiftZ;
            else
                z = 0;
            
            node.SetPosition( (short)x, (short)(y + z) );
            itr.fwrd();
        }
    }
    
    public short c_x, c_y, c_z;
    private NamedList c_attachedNodeInfos; 
}
