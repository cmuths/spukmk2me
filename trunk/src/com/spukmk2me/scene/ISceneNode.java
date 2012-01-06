/*
 *  SPUKMK2me - SPUKMK2 Engine for J2ME platform
 *  Copyright 2010 - 2011  HNYD Team
 *
 *   SPUKMK2me is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *   SPUKMK2me is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with SPUKMK2me.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spukmk2me.scene;

import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.video.IVideoDriver;

//#ifdef __SPUKMK2ME_DEBUG
//# import com.spukmk2me.debug.Logger;
//#endif

/**
 *  Basic element of SPUKMK2me rendering engine.
 *  \details SPUKMK2me doesn't render image, line, point, etc... directly.
 * It manages scene nodes, which represents every renderable elements. To
 * render something, first you must write a class which inherits ISceneNode,
 * and write the rendering sequences in the proprietary video driver.\n
 *  Scene nodes are manager by SceneManager, so you must add scene nodes to the
 * scene manager to make them visible. Simply put, adding a scene node to scene
 * manager is adding the node to a "tree", where the root is maintained by
 * scene manager. So just one call of IVideoDriver.RenderSceneNode() to the
 * root can make all the tree visible to the screen.\n\n
 *  Below are some specifications of scene nodes that you should notice:\n
 *  Position of the node: c_x and c_y should be the most top-left point of your
 * scene node. E.g. if you create a node called CircleSceneNode, c_x and c_y
 * must be the most top-left point, not the center of the circle.
 *  If a node is enabled, renderer will go deeper from this node to render
 * its children. If not, this node and its children won't be rendered.\n
 *  Visibility of a node only affect the node only and don't affect visibility
 * of its children.\n
 *  If a node has child or children, its c_root must point to a NullSceneNode,
 * which supposed to be a faked-first node of its child/children. If you take a
 * look to the attributes of ISceneNode, you can see c_prev and c_next, so you
 * can suggest that the children are hold in a doubly-linked list. This list
 * must be circular, and its first node is that NullSceneNode I've declared
 * above.\n
 *  Remember this, faked-first nodes are required in all level of the tree,
 * except for the root level, where there is only exactly one node. You can add
 * faked first node to that level, through, it doesn't matter.\n
 *  The last thing is: rendering sequences of a node is processed by
 * SceneManager.RenderSceneNode(), so please take a look if you feel strange
 * about rendering sequence, and may be you can fix some bugs.
 *  @see com.spukmk2me.scene.SceneManager
 *  @see com.spukmk2me.video.IVideoDriver
 */
public abstract class ISceneNode
{
    protected ISceneNode()
    {
        c_visible = c_enable    = true;
        //c_prev    = c_next      = null;
        //c_root    = null;
    }

    /**
     *  Specify how to render this node.
     *  \details The implements must use renderTool to render theirs contents
     * to the screen. The current raster point is described by
     * renderTool.c_rasterX and renderTool.c_rasterY.
     *  @param renderTool A set of data for rendering.
     */
    public abstract void Render( IVideoDriver driver );

    /**
     *  Get the top-left X coordinate of AABB.
     *  \details The coordinate of AABB is relative to node's position.
     *  @return The X coordinate of top-left point of the node.
     */
    public abstract short GetAABBX();
    
    /**
     *  Get the top-left Y coordinate of AABB.
     *  \details The coordinate of AABB is relative to node's position.
     *  @return The Y coordinate of top-left point of the node.
     */
    public abstract short GetAABBY();

    /**
     *  Get the width of AABB.
     *  @return The width of the node.
     */
    public abstract short GetAABBWidth();

    /**
     *  Get the height of AABB.
     *  @return The width of the node.
     */
    public abstract short GetAABBHeight();

    /**
     *  Add the scene node to this scene manager.
     *  \details The node which is going to be added must be unmanaged by any
     * scene manager (there should be only one scene manager at
     * the same time). If not, don't email me about your program crashing.
     *  @param node The node which is going to be added.
     *  @param parent The parent of this node, CANNOT be null.
     */
    public final void AddChild( ISceneNode node )
    {
        //#ifdef __SPUKMK2ME_DEBUG
//#         if ( (node.c_next != null) || (node.c_prev != null) ||
//#             (node.c_parent != null) )
//#             Logger.Trace( "Adding potentially defected node." );
        //#endif

        ISceneNode bindingNode;

        if ( c_children == null )
        {
            c_children              = bindingNode = new NullSceneNode();
            bindingNode.c_parent    = this;
            bindingNode.c_prev      = bindingNode.c_next = bindingNode;
        }
        else
            bindingNode = c_children;

        node.c_prev = bindingNode.c_prev;
        node.c_next = bindingNode;
        bindingNode.c_prev.c_next   = node;
        bindingNode.c_prev          = node;
        node.c_parent               = this;
    }

    /**
     *  Insert a node after another.
     *  \details The added node shouldn't be in any list, and the predecessor
     * node must be in a list.
     *  @param node The node that is going to be added.
     *  @param predecessor The node that is supposed to lie right before the
     * added node.
     */
    public final void InsertAfter( ISceneNode predecessor )
    {
        c_prev = predecessor;
        c_next = predecessor.c_next;
        predecessor.c_next.c_prev   = this;
        predecessor.c_next          = this;
        c_parent = predecessor.c_parent;
    }

    /**
     *  Remove this node from scene manager.
     *  \details All of its children are retained.
     */
    public void Drop()
    {
        if ( (c_prev == null) || (c_next == null) || (c_parent == null) )
        {
            //#ifdef __SPUKMK2ME_DEBUG
//#             Logger.Trace( "WARNING: Dropping unattached node." );
            //#endif
            return;
        }
        
        c_prev.c_next = c_next;
        c_next.c_prev = c_prev;

        if ( c_prev == c_next ) // faked-first node found
        {
            ISceneNode faked_first = c_next;
            
            if ( c_parent != null )
                c_parent.c_children = null;

            faked_first.c_next = faked_first.c_prev = faked_first.c_parent =
                null;
        }

        c_next = c_prev = c_parent = null;
    }

    /**
     *  Drop all children.
     */
    public final void DropChildren()
    {
        if ( c_children == null )
            return;

        // ISceneNode.Drop() can alter its parent's c_child, so I must copy
        // it for comparision.
        ISceneNode finishNode   = c_children;
        ISceneNode iterator     = finishNode.c_next;
        
        while ( iterator != finishNode )
        {
            iterator = iterator.c_next;
            iterator.c_prev.Drop();
        }
    }

    /**
     *  Set the position of this node.
     *  \details But c_x and c_y are public, so you can change them manually.
     */
    public final void SetPosition( short x, short y )
    {
        c_x = x;
        c_y = y;
    }

    /**
     *  Get the position of this node, which is relative to stopNode.
     *  \details The X coordinate will be stored in the 16 MSBs, 16 LSBs is the
     * value of Y coordinate.
     *  @param stopNode One of the ancestors of current node. Can be null.
     *  @return The relative position of this node to stopNode.
     */
    public final int GetRelativePosition( ISceneNode stopNode )
    {
        ISceneNode node = this;
        short x = 0;
        short y = 0;

        while ( node != stopNode )
        {
            x += node.c_x;
            y += node.c_y;
            node = node.c_parent;
        }

        // Is (y & 0x0000FFFF) a little redundant? Hell no! Ask Java creators
        // for this! The compiler automatically and "smartly" convert
        // everything to int without my permission. Imagine what'll happen if
        // y is a negative value and you'll see.
        return ( x << 16 ) | (y & 0x0000FFFF);
    }

    /**
     *  Get the hierarchical bounding rectangle of this node and its child.
     *  \details
     *  @return A long value 0xaaaabbbbccccdddd that indicates the bounding
     * rectangle, as follow:\n
     *  - 0xaaaa is the X coordinate for the top-left corner.
     *  - 0xbbbb is the Y coordinate for the top-left corner.
     *  - 0xcccc is the width of rectangle.
     *  - 0xdddd is the heights of rectangle.
     */
    public final long GetHierarchicalBoundingRect()
    {
        DoublyLinkedList stack = new DoublyLinkedList();
        DoublyLinkedList finish = new DoublyLinkedList();
        ISceneNode node;
        short x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        short tx1, ty1, tx2, ty2;
        short curX = (short)-c_x, curY = (short)-c_y;

        stack.push_back( this );
        finish.push_back( c_next );

        while ( stack.length() != 0 )
        {
            node = (ISceneNode)stack.pop_back();

            // Reached all the nodes
            if ( node == finish.peek_back() )
            {
                finish.pop_back();

                if ( stack.length() != 0 )
                {
                    node = node.c_parent;
                    curX -= node.c_x;
                    curY -= node.c_y;
                }
            }
            else
            {
                tx1 = (short)(curX + node.c_x + node.GetAABBX());
                ty1 = (short)(curY + node.c_y + node.GetAABBY());
                tx2 = (short)(tx1 + node.GetAABBWidth() - 1);
                ty2 = (short)(ty1 + node.GetAABBHeight() - 1);

                if ( x1 > tx1 )
                    x1 = tx1;

                if ( y1 > ty1 )
                    y1 = ty1;

                if ( x2 < tx2 )
                    x2 = tx2;

                if ( y2 < ty2 )
                    y2 = ty2;

                stack.push_back( node.c_next );
                
                if ( node.c_children != null )
                {
                    curX += node.c_x;
                    curY += node.c_y;
                    stack.push_back( node.c_children.c_next );
                    finish.push_back( node.c_children );
                }
            }
        }

        return ((long)x1 << 48) | ((long)y1 << 32) |
                ((long)(x2 - x1 + 1) << 16) | (long)(y2 - y1 + 1);
    }

    // These variables are managed by the engine, DO NOT change it manually
    // if you don't understand them fully.
    public ISceneNode   c_prev;     //!< Previous node.
    public ISceneNode   c_next;     //!< Next node.
    public ISceneNode   c_children; //!< The fake node of direct children.
    public ISceneNode   c_parent;   //!< The direct father of this node.
    //#ifdef __SPUKMK2ME_SCENESAVER
//#     public Object       c_infoData;
//#     public String       c_proxyName;
//#     public boolean      c_exportFlag;
    //#endif

    // You can (of course) change the variables below.
    public short        c_x;        //!< X coordinate of this node.
    public short        c_y;        //!< Y coordinate of this node.
    public boolean      c_visible,  //!< Visibility of this node.
                        c_enable;   //!< This node is enabled or not.
}
