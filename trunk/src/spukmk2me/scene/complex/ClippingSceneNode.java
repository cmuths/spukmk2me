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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package spukmk2me.scene.complex;

import spukmk2me.video.RenderTool;
import spukmk2me.scene.ISceneNode;
import spukmk2me.scene.NullSceneNode;

public final class ClippingSceneNode extends ISceneNode
{
    private final class SubClippingSceneNode extends ISceneNode
    {
        public SubClippingSceneNode() {}

        public void Render( RenderTool renderTool )
        {
            renderTool.SetClipping( m_x, m_y, m_width, m_height );
        }

        public short GetWidth()
        {
            return m_width;
        }

        public short GetHeight()
        {
            return m_height;
        }

        public void SetClipping( short x, short y, short width, short height )
        {
            m_x         = x;
            m_y         = y;
            m_width     = width;
            m_height    = height;
        }

        private short m_x, m_y, m_width, m_height;
    }

    public ClippingSceneNode()
    {
        m_entryNode         = new NullSceneNode();
        m_unclippingNode    = new SubClippingSceneNode();
        ISceneNode.AddSceneNode( m_entryNode, this );
        ISceneNode.AddSceneNode( m_unclippingNode, this );
    }
    
    public void Render( RenderTool renderTool )
    {
        long clippingArea = renderTool.GetClipping();
        
        renderTool.SetClipping(
            (short)(renderTool.c_rasterX),
            (short)(renderTool.c_rasterY),
            m_width, m_height );
        m_unclippingNode.SetClipping(
            (short)(clippingArea >> 48),
            (short)(clippingArea >> 32 & 0x000000000000FFFFL),
            (short)(clippingArea >> 16 & 0x000000000000FFFFL),
            (short)(clippingArea & 0x000000000000FFFFL) );
    }
    
    public short GetWidth()
    {
        return m_width;
    }

    public short GetHeight()
    {
        return m_height;
    }

    public void SetClipping( short x, short y, short width, short height )
    {
        m_entryNode.c_x = (short)-x;
        m_entryNode.c_y = (short)-y;
        m_width         = width;
        m_height        = height;
    }

    public ISceneNode GetEntryNode()
    {
        return m_entryNode;
    }

    private SubClippingSceneNode    m_unclippingNode;
    private NullSceneNode           m_entryNode;
    private short m_width, m_height;
}
