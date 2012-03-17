package com.spukmk2me.spukmk2mesceneeditor.gui;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.spukmk2mesceneeditor.data.NodeTypeChecker;
import java.awt.Color;

// Renderer for the scene tree
public final class SceneTreeCellRenderer implements TreeCellRenderer
{
    public SceneTreeCellRenderer()
    {
        m_icons = new ImageIcon[ NodeTypeChecker.NUMBERS_OF_TYPES ];
        
        for ( int i =0; i != NodeTypeChecker.NUMBERS_OF_TYPES; ++i )
            m_icons[ i ] = new ImageIcon( this.getClass().getResource(ICON_FILES[ i ]) );
    }
    
    public Component getTreeCellRendererComponent( JTree tree, Object value,
        boolean selected, boolean expanded, boolean leaf,
        int row, boolean hasFocus )
    {
        String label;
        ISceneNode node = (ISceneNode)value;
        int nodeType = NodeTypeChecker.GetNodeType( node );
        
        if ( node.c_proxyName == null )
            label = "-NONAME-";
        else
            label = node.c_proxyName;
        
        JLabel component = new JLabel( label );
        
        component.setIcon( m_icons[ nodeType ] );
        component.setForeground( ( selected )? Color.BLACK : Color.GRAY );
        return component;
    }
    
    private static String[] ICON_FILES = {
        "/nullnodeicon.png",
        "/imgnodeicon.png",
        "/imgnodeicon.png",
        "/stringnodeicon.png",
        "/tilednodeicon.png",
        "/clipnodeicon.png",
        "/imgnodeicon.png",
    };
    
    private ImageIcon m_icons[];
}
