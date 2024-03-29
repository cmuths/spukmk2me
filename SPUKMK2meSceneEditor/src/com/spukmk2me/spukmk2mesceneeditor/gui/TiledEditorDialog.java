package com.spukmk2me.spukmk2mesceneeditor.gui;

import com.spukmk2me.scene.TiledLayerSceneNode;

public class TiledEditorDialog extends javax.swing.JDialog
{
    public TiledEditorDialog( java.awt.Frame parent, boolean modal,
        TiledLayerSceneNode editedNode )
    {
        super(parent, modal);
        initComponents();
        m_node = editedNode;
        assembleComponents();
    }

    private void assembleComponents()
    {
        m_mainCanvas    = new TiledEditorMainCanvas();
        m_imageCanvas   = new TiledEditorImageCanvas();
        m_spriteCanvas  = new TiledEditorSpriteCanvas();
        m_inputHandler  = new TiledEditorInputHander();
        TiledLayerSceneNode.TiledLayerSceneNodeInfoData info =
            (TiledLayerSceneNode.TiledLayerSceneNodeInfoData)m_node.c_infoData;

        m_screenScrollPane.setViewportView( m_mainCanvas );
        m_imageScrollPane.setViewportView( m_imageCanvas );
        m_spriteScrollPane.setViewportView( m_spriteCanvas );

        m_mainCanvas.setNode( m_node );
        m_imageCanvas.setNode( m_node );
        m_spriteCanvas.setNode( m_node );

        m_inputHandler.setup( m_mainCanvas, m_imageCanvas, m_spriteCanvas,
            m_mainCanvas.getCursorNode(), m_imageCanvas.getCursorNode(),
            null, m_mainCanvas.getEditedData(), info );

        this.addKeyListener( m_inputHandler );
        m_mainCanvas.setFocusable( true );
        m_mainCanvas.addKeyListener( m_inputHandler );
        //imageCanvas.setFocusable( true );
        m_imageCanvas.addKeyListener( m_inputHandler );
        
        //mainCanvas.addMouseListener( inputHandler );
        //imageCanvas.addMouseListener( inputHandler );
    }
    
    private void disposeDialog()
    {
        this.dispose();
        m_mainCanvas.setRenderingMode( J2SERenderingPanel.RENDERINGMODE_PASSIVE, 0 );
        m_imageCanvas.setRenderingMode( J2SERenderingPanel.RENDERINGMODE_PASSIVE, 0 );
        m_spriteCanvas.setRenderingMode( J2SERenderingPanel.RENDERINGMODE_PASSIVE, 0 );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        m_screenScrollPane = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        m_imageScrollPane = new javax.swing.JScrollPane();
        m_spriteScrollPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        m_okButton = new javax.swing.JButton();
        m_cancelButton = new javax.swing.JButton();
        m_gridCheckbox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Tiled editor");

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);
        jSplitPane1.setLeftComponent(m_screenScrollPane);

        jSplitPane2.setResizeWeight(0.5);
        jSplitPane2.setLeftComponent(m_imageScrollPane);
        jSplitPane2.setRightComponent(m_spriteScrollPane);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 818, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jSplitPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 798, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 524, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jSplitPane1.setRightComponent(jPanel2);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        m_okButton.setText("OK");
        m_okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_okButtonActionPerformed(evt);
            }
        });

        m_cancelButton.setText("Cancel");
        m_cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_cancelButtonActionPerformed(evt);
            }
        });

        m_gridCheckbox.setText("Display grid");
        m_gridCheckbox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                m_gridCheckboxStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_gridCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 601, Short.MAX_VALUE)
                .addComponent(m_cancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_okButton)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_okButton)
                    .addComponent(m_cancelButton)
                    .addComponent(m_gridCheckbox))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void m_cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_cancelButtonActionPerformed
        disposeDialog();
    }//GEN-LAST:event_m_cancelButtonActionPerformed

    private void m_gridCheckboxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_m_gridCheckboxStateChanged
        m_mainCanvas.setGridVisible( m_gridCheckbox.isSelected() );
    }//GEN-LAST:event_m_gridCheckboxStateChanged

    private void m_okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_okButtonActionPerformed
        TiledLayerSceneNode.TiledLayerSceneNodeInfoData info =
            (TiledLayerSceneNode.TiledLayerSceneNodeInfoData)m_node.c_infoData;
        
        info.c_terrainData = m_mainCanvas.getEditedData();
        m_node.SetupTiledLayer( info.c_images, info.c_sprites,
            info.c_spriteSpeed, info.c_terrainData,
            info.c_tableWidth, info.c_tableHeight, info.c_stepX, info.c_stepY );

        disposeDialog();
    }//GEN-LAST:event_m_okButtonActionPerformed

    private TiledLayerSceneNode m_node;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JButton m_cancelButton;
    private javax.swing.JCheckBox m_gridCheckbox;
    private javax.swing.JScrollPane m_imageScrollPane;
    private javax.swing.JButton m_okButton;
    private javax.swing.JScrollPane m_screenScrollPane;
    private javax.swing.JScrollPane m_spriteScrollPane;
    // End of variables declaration//GEN-END:variables

    private TiledEditorMainCanvas   m_mainCanvas;
    private TiledEditorImageCanvas  m_imageCanvas;
    private TiledEditorSpriteCanvas m_spriteCanvas;
    private TiledEditorInputHander  m_inputHandler;
}
