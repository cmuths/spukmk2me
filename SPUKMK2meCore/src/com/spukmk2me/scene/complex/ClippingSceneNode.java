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

package com.spukmk2me.scene.complex;

import com.spukmk2me.Util;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.RenderInfo;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.NullSceneNode;

/**
 *  Scene node which provides clipping feature.
 */
public final class ClippingSceneNode extends ComplexSceneNode
{
    private final class SubClippingSceneNode extends ISceneNode
    {
        public SubClippingSceneNode() {}

        public void Render( IVideoDriver driver )
        {
            driver.SetClipping( m_x, m_y, m_width, m_height );
        }
        
        public final short GetAABBX()
        {
            return 0;
        }

        public final short GetAABBY()
        {
            return 0;
        }
        
        public short GetAABBWidth()
        {
            return 0;
        }

        public short GetAABBHeight()
        {
            return 0;
        }
        
        private void SetClipping( int x, int y, int width, int height )
        {
            m_x         = (short)x;
            m_y         = (short)y;
            m_width     = (short)width;
            m_height    = (short)height;
        }

        private short m_x, m_y, m_width, m_height;
    }

    public ClippingSceneNode()
    {
        m_entryNode         = new NullSceneNode();
        m_unclippingNode    = new SubClippingSceneNode();
        AddChild( m_entryNode );
        AddChild( m_unclippingNode );
        c_visible = true;
    }
    
    public void Render( IVideoDriver driver )
    {
        long clippingArea = driver.GetClipping();

        RenderInfo rInfo = driver.GetRenderInfo();
        int clipX, clipY, clipW, clipH;
        int oldClipX, oldClipY, oldClipW, oldClipH;

        oldClipX = (short)(clippingArea >>> 48);
        oldClipY = (short)(clippingArea >> 32 & 0x000000000000FFFFL);
        oldClipW = (short)(clippingArea >> 16 & 0x000000000000FFFFL);
        oldClipH = (short)(clippingArea & 0x000000000000FFFFL);

        if ( Util.RectIntersect(
                oldClipX, oldClipY, oldClipW, oldClipH,
                rInfo.c_rasterX, rInfo.c_rasterY, m_width, m_height ) )
        {
            clipX = Math.max( rInfo.c_rasterX, oldClipX );
            clipY = Math.max( rInfo.c_rasterY, oldClipY );
            clipW = Math.min( rInfo.c_rasterX + m_width, oldClipX + oldClipW ) - clipX;
            clipH = Math.min( rInfo.c_rasterY + m_height, oldClipY + oldClipH ) - clipY;
        }
        else
            clipX = clipY = clipW = clipH = 0;
            
        driver.SetClipping( clipX, clipY, clipW, clipH );
        m_unclippingNode.SetClipping( oldClipX, oldClipY, oldClipW, oldClipH );
    }
    
    public short GetAABBX()
    {
        return 0;
    }
    
    public short GetAABBY()
    {
        return 0;
    }
    
    public short GetAABBWidth()
    {
        return m_width;
    }

    public short GetAABBHeight()
    {
        return m_height;
    }
    
    public long GetHierarchicalBoundingRect()
    {
        return  ((long)(m_width & 0x0000FFFF) << 16) |
                (long)(m_height & 0x0000FFFF);
    }

    /**
     *  Set clipping rectangle.
     *  @param x X coordinate of top-left point.
     *  @param y Y coordinate of top-left point.
     *  @param width Width of clipping rectangle.
     *  @param height Height of clipping rectangle.
     */
    public void SetClipping( int x, int y, int width, int height )
    {
        m_entryNode.c_x = (short)-x;
        m_entryNode.c_y = (short)-y;
        m_width         = (short)width;
        m_height        = (short)height;
    }

    public short GetOriginX()
    {
        return (short)-m_entryNode.c_x;
    }

    public short GetOriginY()
    {
        return (short)-m_entryNode.c_y;
    }

    /**
     *  Get entry node for ClippingSceneNode.
     *  \details Do not add nodes directly to ClippingSceneNode if you
     * want to use clipping function.
     *  @return Entry node.
     */
    public ISceneNode GetEntryNode()
    {
        return m_entryNode;
    }
    
    /* $if SPUKMK2ME_SCENESAVER$ */
    public class ClippingSceneNodeInfoData
    {
        public short c_x, c_y, c_width, c_height;
    }
    /* $endif$ */

    private SubClippingSceneNode    m_unclippingNode;
    private NullSceneNode           m_entryNode;
    private short                   m_width, m_height;
}
