package spukmk2me.scene.complex;

import spukmk2me.Util;
import spukmk2me.scene.ISceneNode;
import spukmk2me.video.RenderTool;

public final class ViewportSceneNode extends ISceneNode
{
    public ViewportSceneNode()
    {
        m_clippingNode = new ClippingSceneNode();
        ISceneNode.AddSceneNode( m_clippingNode, this );
    }

    public void Render( RenderTool renderTool )
    {
        if ( m_movingType == MOVINGTYPE_MANUAL )
            return;

        if ( m_cursorNode != null )
        {
            m_cursorX = m_cursorNode.c_x;
            m_cursorY = m_cursorNode.c_y;
        }

        int relativeX = m_cursorX - Util.FPRound( m_originX );
        int relativeY = m_cursorY - Util.FPRound( m_originY );
        int deltaX, deltaY;

        if ( relativeX < m_alterX )
            deltaX = relativeX - m_alterX;
        else if ( relativeX >= m_alterX + m_alterWidth )
            deltaX = relativeX - m_alterX - m_alterWidth + 1;
        else
            deltaX = 0;

        if ( relativeY < m_alterY )
            deltaY = relativeY - m_alterY;
        else if ( relativeY >= m_alterY + m_alterHeight )
            deltaY = relativeY - m_alterY - m_alterHeight + 1;
        else
            deltaY = 0;

        if ( (deltaX == 0) && (deltaY == 0) )
            return;

        deltaX <<= 16;
        deltaY <<= 16;

        int baseSpeed = Util.FPDiv( m_movingSpeed,
            Math.abs( deltaX ) + Math.abs( deltaY ) );
        int movingX = 0, movingY = 0;

        switch ( m_movingType )
        {
            case MOVINGTYPE_CONST_SPEED:
                movingX = Util.FPMul(
                    Util.FPMul( baseSpeed, deltaX ),
                    renderTool.c_timePassed );
                movingY = Util.FPMul(
                    Util.FPMul( baseSpeed, deltaY ),
                    renderTool.c_timePassed );
                break;

            case MOVINGTYPE_SPP:
                movingX = Util.FPMul(
                    Util.FPMul(
                        Util.FPMul( baseSpeed, Math.abs( deltaX ) ),
                        renderTool.c_timePassed ),
                    deltaX );
                movingY = Util.FPMul(
                    Util.FPMul(
                        Util.FPMul( baseSpeed, Math.abs( deltaY ) ),
                        renderTool.c_timePassed ),
                    deltaY );
                break;
        }

        m_originX += movingX;
        m_originY += movingY;

        if ( m_originX > m_viewableWidth - m_width << 16 )
            m_originX = m_viewableWidth - m_width << 16;

        if ( m_originX < 0 )
            m_originX = 0;

        if ( m_originY > m_viewableHeight - m_height << 16 )
            m_originY = m_viewableHeight - m_height << 16;

        if ( m_originY < 0 )
            m_originY = 0;

        m_clippingNode.SetClipping(
            Util.FPRound( m_originX ), Util.FPRound( m_originY ),
            m_width, m_height );
    }

    public short GetWidth()
    {
        return m_width;
    }

    public short GetHeight()
    {
        return m_height;
    }

    public void SetupViewport( short width, short height,
        short viewableWidth, short viewableHeight,
        short alterX, short alterY, short alterWidth, short alterHeight,
        int movingSpeed, byte movingType, ISceneNode cursorNode )
    {
        m_width             = width;
        m_height            = height;
        m_viewableWidth     = viewableWidth;
        m_viewableHeight    = viewableHeight;
        m_alterX            = alterX;
        m_alterY            = alterY;
        m_alterWidth        = alterWidth;
        m_alterHeight       = alterHeight;
        m_movingSpeed       = movingSpeed;
        m_movingType        = movingType;
        m_cursorNode        = cursorNode;
        m_originX           = m_originY = 0;
    }

    public void SetOrigin( short x, short y )
    {
        m_originX = x << 16;
        m_originY = y << 16;
        m_clippingNode.SetClipping( x, y, m_width, m_height );
    }

    public void SetCursorPosition( short x, short y )
    {
        if ( m_cursorNode == null )
        {
            m_cursorX = x;
            m_cursorY = y;
        }
    }

    public ISceneNode GetEntryNode()
    {
        return m_clippingNode.GetEntryNode();
    }

    public static final byte MOVINGTYPE_MANUAL          = 0;
    public static final byte MOVINGTYPE_CONST_SPEED     = 1;
    public static final byte MOVINGTYPE_SPP             = 2;

    private ClippingSceneNode   m_clippingNode;
    private ISceneNode          m_cursorNode;

    private int     m_movingSpeed;
    private int     m_originX, m_originY;   // Fixed-point.
    private short   m_width, m_height, m_viewableWidth, m_viewableHeight,
                    m_alterX, m_alterY, m_alterWidth, m_alterHeight,
                    m_cursorX, m_cursorY;
    private byte    m_movingType;
}
