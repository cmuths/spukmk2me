package com.spukmk2me.spukmk2mesceneeditor.gui;

import com.spukmk2me.scene.ISceneNode;
import javax.swing.tree.TreeSelectionModel;

import com.spukmk2me.spukmk2mesceneeditor.data.CentralData;
import javax.swing.JOptionPane;

public class MoveSceneNodeDialog extends javax.swing.JDialog
{
    public MoveSceneNodeDialog( java.awt.Frame parent, boolean modal,
        CentralData centralData )
    {
        super( parent, modal );
        initComponents();
        loadTreeData( centralData );
        m_movingOccurred = false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        m_srcTreeHolder = new javax.swing.JScrollPane();
        m_dstTreeHolder = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        m_backButton = new javax.swing.JButton();
        m_moveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Move nodes");

        jSplitPane1.setLeftComponent(m_srcTreeHolder);
        jSplitPane1.setRightComponent(m_dstTreeHolder);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 376, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 261, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        m_backButton.setText("Back");
        m_backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_backButtonActionPerformed(evt);
            }
        });

        m_moveButton.setText("Move");
        m_moveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_moveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(242, Short.MAX_VALUE)
                .addComponent(m_moveButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_backButton)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_backButton)
                    .addComponent(m_moveButton)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public boolean nodeMoved()
    {
        return m_movingOccurred;
    }
    
    private void m_backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_backButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_m_backButtonActionPerformed

    private void m_moveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_moveButtonActionPerformed
        int nNodes = m_srcTree.getSelectionCount();
        
        if ( nNodes == 0 )
        {
            JOptionPane.showMessageDialog( this, "There's no source node.",
                "Error", JOptionPane.INFORMATION_MESSAGE );
            return;
        }
        
        if ( m_dstTree.getSelectionCount() == 0 )
        {
            JOptionPane.showMessageDialog( this, "There's no destination node.",
                "Error", JOptionPane.INFORMATION_MESSAGE );
            return;
        }
        
        Object[] path = m_dstTree.getSelectionPaths()[ 0 ].getPath();
        ISceneNode dstNode = (ISceneNode)path[ path.length - 1 ];
        ISceneNode[] movedNodes = new ISceneNode[ nNodes ];
        
        for ( int i = 0; i != nNodes; ++i )
        {
            path = m_srcTree.getSelectionPaths()[ i ].getPath();
            movedNodes[ i ] = (ISceneNode)path[ path.length - 1 ];
        }
        
        ISceneNode node;
        boolean qualified = true;
        
        // Check for moving qualification
        for ( int i = 0; i != nNodes; ++i )
        {
            node = dstNode;
            
            while ( node != null )
            {
                node = node.c_parent;
                
                if ( node == movedNodes[ i ] )
                {
                    qualified = false;
                    break;
                }
            }
        }
        
        if ( !qualified )
        {
            JOptionPane.showMessageDialog( this,
                "Cannot move node to its decendant.",
                "Error", JOptionPane.INFORMATION_MESSAGE );
            return;
        }
        
        // Move
        for ( int i = 0; i != nNodes; ++i )
        {
            movedNodes[ i ].Drop();
            dstNode.AddChild( movedNodes[ i ] );
        }
        
        m_srcTree.Reset();
        m_dstTree.Reset();
        m_movingOccurred = true;
    }//GEN-LAST:event_m_moveButtonActionPerformed

    private void loadTreeData( CentralData data )
    {
        m_srcTree = new DisplayedSceneTree(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
        m_srcTree.SetCentralData( data );
        m_dstTree = new DisplayedSceneTree(
            TreeSelectionModel.SINGLE_TREE_SELECTION );
        m_dstTree.SetCentralData( data );
        
        m_srcTreeHolder.setViewportView( m_srcTree );
        m_dstTreeHolder.setViewportView( m_dstTree );
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton m_backButton;
    private javax.swing.JScrollPane m_dstTreeHolder;
    private javax.swing.JButton m_moveButton;
    private javax.swing.JScrollPane m_srcTreeHolder;
    // End of variables declaration//GEN-END:variables

    private DisplayedSceneTree m_srcTree, m_dstTree;
    private boolean m_movingOccurred;
}
