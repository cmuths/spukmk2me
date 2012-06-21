package com.spukmk2me.scene;

import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.video.RenderInfo;

public final class LineSceneNode extends ISceneNode
{
    public void Render( IVideoDriver vdrv )
    {
        RenderInfo info = vdrv.GetRenderInfo();
        
        vdrv.DrawLine( info.c_rasterX, info.c_rasterY,
            info.c_rasterX + c_deltaX, info.c_rasterY + c_deltaY, c_color );
    }

    public short GetAABBX()
    {
        return (c_deltaX < 0)? c_deltaX : 0;
    }

    public short GetAABBY()
    {
        return (c_deltaY < 0)? c_deltaY : 0;
    }

    public short GetAABBWidth()
    {
        return (short)Math.abs( c_deltaX );
    }

    public short GetAABBHeight()
    {
        return (short)Math.abs( c_deltaY );
    }
    
    public void SetData( short deltaX, short deltaY, int color )
    {
        c_deltaX    = deltaX;
        c_deltaY    = deltaY;
        c_color     = color;
    }
    
    public long GetData()
    {
        return  ((long)c_deltaX << 48) |
                (((long)c_deltaY & 0x0000FFFF) << 32) |
                ((long)c_color & 0x00000000FFFFFFFFL);
    }
    
    public int      c_color;
    public short    c_deltaX, c_deltaY;
}
