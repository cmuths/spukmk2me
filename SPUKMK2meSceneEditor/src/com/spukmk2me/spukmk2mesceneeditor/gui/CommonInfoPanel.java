package com.spukmk2me.spukmk2mesceneeditor.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import com.spukmk2me.video.ICFont;
import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.ImageSceneNode;
import com.spukmk2me.scene.StringSceneNode;

import com.spukmk2me.spukmk2mesceneeditor.data.CentralData;
import com.spukmk2me.spukmk2mesceneeditor.data.NodeTypeChecker;

public final class CommonInfoPanel extends JPanel
    implements SceneManagerEventListener
{
    public CommonInfoPanel( JFrame masterFrame )
    {
        m_masterFrame = masterFrame;
        initComponents();
    }

    public void ImageResourceChanged(
        IImageResource imageResource, byte changingCode ) {}
    public void ImageChanged( ISubImage image, byte changingCode ) {}
    public void FontChanged( ICFont font, byte changingCode ) {}

    public void CurrentNodeChanged()
    {
        ReloadData();
    }

    public void SetCentralData( CentralData data )
    {
        m_centralData = data;
    }

    public void Reset()
    {
        ReloadData();
    }

    private void ReloadData()
    {
        ISceneNode node = m_centralData.GetCurrentNode();
        boolean enable = (node != null) &&
            (m_centralData.GetCurrentNode() != m_centralData.GetRootNode());

        if ( node == null )
            return;

        if ( node != null )
            node.c_visible &= CheckForDisplayability( node );

        m_applyButton.setEnabled( enable );
        m_cancelButton.setEnabled( enable );
        m_enableCheckbox.setEnabled( enable );
        m_exportedCheckbox.setEnabled( enable );
        m_proxyTextField.setEnabled( enable );
        m_visibleCheckbox.setEnabled( enable );
        m_xTextbox.setEnabled( enable );
        m_yTextbox.setEnabled( enable );

        //if ( enable )
        {
            int nodeType = NodeTypeChecker.GetNodeType( node );

            if ( nodeType == NodeTypeChecker.NT_UNKNOWN )
                m_nodeTypeLabel.setText( UNKNOWN_TYPE );
            else
            {
                if ( node == m_centralData.GetRootNode() )
                    m_nodeTypeLabel.setText( ROOT_NODE );
                else
                    m_nodeTypeLabel.setText( NODE_TYPES[ nodeType ] );
            }

            m_xTextbox.setText( String.valueOf( node.c_x ) );
            m_yTextbox.setText( String.valueOf( node.c_y ) );
            m_proxyTextField.setText( node.c_proxyName );
            m_exportedCheckbox.setSelected( node.c_exportFlag );
            m_visibleCheckbox.setSelected( node.c_visible );
            m_enableCheckbox.setSelected( node.c_enable );
        }
        /*else
        {
            if ( m_centralData.GetCurrentNode() ==
                m_centralData.GetRootNode() )
            {
                m_nodeTypeLabel.setText( ROOT_NODE );
                m_visibleCheckbox.setSelected( node.c_visible );
                m_enableCheckbox.setSelected( node.c_enable );
            }
            else
            {
                m_nodeTypeLabel.setText( EMPTY_STRING );
                m_visibleCheckbox.setSelected( false );
                m_enableCheckbox.setSelected( false );
            }

            m_nodeIDLabel.setText( EMPTY_STRING );
            m_xTextbox.setText( EMPTY_STRING );
            m_yTextbox.setText( EMPTY_STRING );
        }*/
    }

    private boolean CheckForDisplayability( ISceneNode node )
    {
        switch ( NodeTypeChecker.GetNodeType( node ) )
        {
            case NodeTypeChecker.NT_IMAGE:
                if ( ((ImageSceneNode)node).GetImage() == null )
                    return false;

                break;

            case NodeTypeChecker.NT_STRING:
                StringSceneNode.StringSceneNodeInfoData info =
                    (StringSceneNode.StringSceneNodeInfoData)node.c_infoData;
                
                if ( info.c_font == null )
                    return false;

                break;
        }

        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        m_nodeIDLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        m_nodeTypeLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        m_proxyTextField = new javax.swing.JTextField();
        m_exportedCheckbox = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        m_xTextbox = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        m_yTextbox = new javax.swing.JTextField();
        m_visibleCheckbox = new javax.swing.JCheckBox();
        m_enableCheckbox = new javax.swing.JCheckBox();
        m_applyButton = new javax.swing.JButton();
        m_cancelButton = new javax.swing.JButton();

        jLabel1.setText("Node ID:");

        m_nodeIDLabel.setText("Unknown");

        jLabel3.setText("Node type: ");

        m_nodeTypeLabel.setText("Unknown");

        jLabel7.setText("Proxy name:");

        m_exportedCheckbox.setText("Exported");

        jLabel5.setText("X:");

        jLabel6.setText("Y:");

        m_visibleCheckbox.setText("Visible");

        m_enableCheckbox.setText("Enable");

        m_applyButton.setText("Apply");
        m_applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_applyButtonActionPerformed(evt);
            }
        });

        m_cancelButton.setText("Cancel");
        m_cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_nodeTypeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .addComponent(m_nodeIDLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(m_proxyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_exportedCheckbox)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_xTextbox, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_yTextbox, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_visibleCheckbox)
                    .addComponent(m_enableCheckbox))
                .addContainerGap(105, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(111, Short.MAX_VALUE)
                .addComponent(m_cancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_applyButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(m_nodeIDLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(m_nodeTypeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(m_proxyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_exportedCheckbox))
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(m_xTextbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_visibleCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(m_yTextbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_enableCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_applyButton)
                    .addComponent(m_cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_cancelButtonActionPerformed
        ReloadData();
    }//GEN-LAST:event_m_cancelButtonActionPerformed

    private void m_applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_applyButtonActionPerformed

        short x = 0, y = 0;
        
        try
        {
            x = Short.parseShort( m_xTextbox.getText() );
            y = Short.parseShort( m_yTextbox.getText() );
        } catch ( NumberFormatException e ) {
            JOptionPane.showConfirmDialog( m_masterFrame,
                "X and Y must be valid signed 16-bit numbers.",
                "Error", JOptionPane.YES_OPTION,
                JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        String  proxyName   = m_proxyTextField.getText();
        boolean export      = m_exportedCheckbox.isSelected();
        
        if ( export )
        {
            // Check for duplication
            //...
            
        }
                
        ISceneNode node = m_centralData.GetCurrentNode();

        node.c_x = x;
        node.c_y = y;
        node.c_visible  = m_visibleCheckbox.isSelected();
        node.c_enable   = m_enableCheckbox.isSelected();
        
        if ( export )
        {
            node.c_exportFlag   = true;
            node.c_proxyName    = proxyName;
        }
        else
        {
            node.c_exportFlag   = false;
            node.c_proxyName    = "";
        }
    }//GEN-LAST:event_m_applyButtonActionPerformed

    private static final String UNKNOWN_TYPE    = "Unknown";
    private static final String ROOT_NODE       = "Root node";
    private static final String EMPTY_STRING    = "";
    private static final String[] NODE_TYPES    = {
        "Null scene node",
        "Image scene node",
        "Sprite scene node",
        "String scene node",
        "Tiled scene node",
        "Clipping scene node",
        "Viewport scene node"
    };

    private JFrame      m_masterFrame;
    private CentralData m_centralData;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JButton m_applyButton;
    private javax.swing.JButton m_cancelButton;
    private javax.swing.JCheckBox m_enableCheckbox;
    private javax.swing.JCheckBox m_exportedCheckbox;
    private javax.swing.JLabel m_nodeIDLabel;
    private javax.swing.JLabel m_nodeTypeLabel;
    private javax.swing.JTextField m_proxyTextField;
    private javax.swing.JCheckBox m_visibleCheckbox;
    private javax.swing.JTextField m_xTextbox;
    private javax.swing.JTextField m_yTextbox;
    // End of variables declaration//GEN-END:variables
}
