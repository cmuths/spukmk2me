package com.spukmk2me.spukmk2mesceneeditor.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.TiledLayerSceneNode;

public class TiledEditorInputHander implements KeyListener, MouseListener
{
    public TiledEditorInputHander() {}

    public void keyPressed( KeyEvent e )
    {
        int keyCode = e.getKeyCode();

        switch ( keyCode )
        {
            case KeyEvent.VK_W:
                if ( m_mainY != 0 )
                {
                    --m_mainY;
                    repositionCursors( true, false, false );
                }

                break;

            case KeyEvent.VK_S:
                if ( m_mainY < m_info.c_tableHeight - 1 )
                {
                    ++m_mainY;
                    repositionCursors( true, false, false );
                }

                break;

            case KeyEvent.VK_A:
                if ( m_mainX != 0 )
                {
                    --m_mainX;
                    repositionCursors( true, false, false );
                }
                break;

            case KeyEvent.VK_D:
                if ( m_mainX < m_info.c_tableWidth - 1 )
                {
                    ++m_mainX;
                    repositionCursors( true, false, false );
                }

                break;
                
            case KeyEvent.VK_NUMPAD1:
                if ( m_imageX > 0 )
                {
                    --m_imageX;
                    repositionCursors( false, true, false );
                }

                break;
                
            case KeyEvent.VK_NUMPAD2:
                if ( m_imageX < m_info.c_images.length - 1 )
                {
                    ++m_imageX;
                    repositionCursors( false, true, false );
                }

                break;
                
            case KeyEvent.VK_ENTER:
                m_terrain[ m_mainX + m_mainY * m_info.c_tableWidth ] =
                    (byte)m_imageX;
                m_mainPanel.repaint();
                break;
                
            case KeyEvent.VK_DELETE:
                m_terrain[ m_mainX + m_mainY * m_info.c_tableWidth ] = -1;
                m_mainPanel.repaint();
                break;
        }
    }
    
    public void keyTyped(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}

    public void setup( JPanel mainPanel, JPanel imagePanel,
        JPanel spritePanel, ISceneNode mainCursor,
        ISceneNode imageCursor, ISceneNode spriteCursor,
        byte[] terrain, TiledLayerSceneNode.TiledLayerSceneNodeInfoData info )
    {
        m_mainPanel     = mainPanel;
        m_imagePanel    = imagePanel;
        m_spritePanel   = spritePanel;
        m_mainCursor    = mainCursor;
        m_imageCursor   = imageCursor;
        m_spriteCursor  = imageCursor;
        m_mainX     = m_mainY = 0;
        m_imageX    = m_spriteX = 0;
        m_info      = info;
        m_terrain   = terrain;
        
        m_mainW     = info.c_stepX;
        m_mainH     = info.c_stepY;
        m_imageW    = info.c_stepX;
        //m_spriteW   = info.c_sprites.length;
        
        m_currentDataPanel = 0;
    }

    private void repositionCursors( boolean main, boolean image,
        boolean sprite )
    {
        if ( main )
        {
            m_mainCursor.SetPosition(
                (short)(m_mainX * m_mainW), (short)(m_mainY * m_mainH) );
            m_mainPanel.update( m_mainPanel.getGraphics() );
        }

        if ( image )
        {
            int x = m_imageX * m_imageW;
            int y = 0;

            if ( x >= 600 )
            {
                int nPerLine = 600 / m_imageW;
                int nLine = m_info.c_images.length / nPerLine;

                y = nLine * m_info.c_stepY;
                x = m_imageW * (m_imageX % nPerLine);
            }

            m_imageCursor.SetPosition( (short)x, (short)y );
            m_imagePanel.update( m_imagePanel.getGraphics() );
        }

        if ( sprite )
        {
            m_spriteCursor.SetPosition(
                (short)(m_spriteX * m_spriteW), (short)0 );
            m_spritePanel.update( m_spritePanel.getGraphics() );
        }
    }
    
    public void mouseClicked( MouseEvent e )
    {
        int x = e.getX();
        int y = e.getY();
        int indexX, indexY, maxX = 0, maxY = 0;

        if ( e.getSource() == m_mainPanel )
        {
            maxX = m_info.c_tableWidth;
            maxY = m_info.c_tableHeight;
        }
        else if ( e.getSource() == m_imagePanel )
            maxX = m_info.c_images.length;
        else if ( e.getSource() == m_spritePanel )
            maxX = m_info.c_sprites.length;

        indexX = -1;

        if ( m_info.c_stepX != 0 )
            indexX = x / m_info.c_stepX;

        if ( indexX >= maxX )
            indexX = -1;

        indexY = -1;

        if ( m_info.c_stepY != 0 )
            indexY = y / m_info.c_stepY;

        if ( indexY >= maxY )
            indexY = -1;

        if ( e.getSource() == m_mainPanel )
        {
            if ( (indexX != -1) && (indexY != -1) )
            {
                m_mainX = indexX;
                m_mainY = indexY;
                m_terrain[ indexX + indexY * m_info.c_tableWidth ] =
                    (byte)((m_currentDataPanel == 0)? m_imageX : m_spriteX);
                repositionCursors( true, false, false );
                m_mainPanel.repaint();
            }
        }
        else if ( e.getSource() == m_imagePanel )
        {
            if ( indexX != -1 )
            {
                m_imageX = indexX;
                repositionCursors( false, true, false );
                m_imagePanel.repaint();
                m_spritePanel.repaint();
            }
        }
        else if ( e.getSource() == m_spritePanel )
        {
            if ( indexX != -1 )
            {
                m_spriteX = indexX;
                repositionCursors( false, false, true );
            }
        }
    }

    public void mouseEntered( MouseEvent e )
    {
    }

    public void mouseExited( MouseEvent e )
    {
    }

    public void mousePressed( MouseEvent e )
    {
    }

    public void mouseReleased( MouseEvent e ) {}
    
    private TiledLayerSceneNode.TiledLayerSceneNodeInfoData m_info;
    private JPanel      m_mainPanel, m_imagePanel, m_spritePanel;
    private ISceneNode  m_mainCursor, m_imageCursor, m_spriteCursor;
    private byte[]      m_terrain;
    private int         m_mainX, m_mainY, m_imageX, m_spriteX,
                        m_mainW, m_mainH, m_imageW,
                        m_spriteW;
    private byte        m_currentDataPanel; // 0: image, 1: sprite
}
