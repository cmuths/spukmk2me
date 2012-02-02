package com.spukmk2me.spukmk2mesceneeditor.gui;

import java.awt.Graphics;
import java.awt.Color;

import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.RenderInfo;
import com.spukmk2me.extension.j2se.J2SEVideoDriver;

public final class J2SELineSceneNode extends ISceneNode
{
    public J2SELineSceneNode()
    {
        c_visible = c_enable = true;
    }

    public void Render( IVideoDriver vdriver )
    {
        Graphics    g   = (Graphics)vdriver.GetProperty( J2SEVideoDriver.PROPERTY_GRAPHICS );
        RenderInfo  ri  = vdriver.GetRenderInfo();

        g.setColor( new Color( m_color ) );
        g.drawLine( ri.c_rasterX, ri.c_rasterY,
            ri.c_rasterX + m_dx - 1, ri.c_rasterY + m_dy - 1 );
    }

    public short GetAABBX()
    {
        return ( m_dx < 0 )? m_dx : (short)0;
    }

    public short GetAABBY()
    {
        return ( m_dy < 0 )? m_dy : (short)0;
    }

    public short GetAABBWidth()
    {
        return (short)Math.abs( m_dx );
    }

    public short GetAABBHeight() {
        return (short)Math.abs( m_dy );
    }

    public void SetupLine( short dx, short dy, int color )
    {
        m_dx    = dx;
        m_dy    = dy;
        m_color = color;
    }

    private int     m_color;
    private short   m_dx, m_dy;
}
