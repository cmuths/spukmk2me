package com.spukmk2me.spukmk2mesceneeditor.gui;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.awt.FileDialog;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.spukmk2me.scene.ISceneNode;

import com.spukmk2me.spukmk2mesceneeditor.data.CentralData;
import com.spukmk2me.spukmk2mesceneeditor.data.Saver;
import com.spukmk2me.spukmk2mesceneeditor.data.Loader;
import javax.swing.tree.TreeSelectionModel;

public class MainGUI extends javax.swing.JFrame
{
    public MainGUI()
    {
        initComponents();
        AssemblePanels();
    }

    private void AssemblePanels()
    {
        m_centralData = new CentralData();

        m_sceneTree         = new DisplayedSceneTree(
            TreeSelectionModel.SINGLE_TREE_SELECTION );
        m_commonInfoPanel   = new CommonInfoPanel( this );
        m_renderingPanel    = new MainRenderingPanel();
        m_privateInfoPanel  = new PrivateInfoPanel( this );
        
        // Rendering panel must be added last
        m_centralData.AddSceneManagerEventListener( m_sceneTree );
        m_centralData.AddSceneManagerEventListener( m_commonInfoPanel );
        m_centralData.AddSceneManagerEventListener( m_privateInfoPanel );
        m_centralData.AddSceneManagerEventListener( m_renderingPanel );

        m_sceneTree.SetCentralData( m_centralData );
        m_renderingPanel.SetCentralData( m_centralData );
        m_commonInfoPanel.SetCentralData( m_centralData );
        m_privateInfoPanel.SetCentralData( m_centralData );
        
        m_sceneTreeHolder.setViewportView( m_sceneTree );
        m_renderingHolder.setViewportView( m_renderingPanel );
        m_commonInfoHolder.setViewportView( m_commonInfoPanel );
        m_privateInfoHolder.setViewportView( m_privateInfoPanel );
        m_centralData.GetDevice().GetVideoDriver().PrepareRenderingContext();
        
        Reset();

        m_renderingPanel.setRenderingMode(
            MainRenderingPanel.RENDERINGMODE_PASSIVE, 33 );
    }

    private void Reset()
    {
        m_centralData.DispatchReset();
        m_sceneTree.setSelectionPath(
            new TreePath( m_centralData.GetRootNode() ) );
        m_commonInfoPanel.CurrentNodeChanged();
        
        m_filename = null;
        this.setTitle( "SPUKMK2ME Scene Editor - %new%" );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        m_addNodeButton = new javax.swing.JButton();
        m_removeNodeButton = new javax.swing.JButton();
        m_moveButton = new javax.swing.JButton();
        m_upButton = new javax.swing.JButton();
        m_downButton = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        m_sceneTreeHolder = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        m_renderingHolder = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        m_commonInfoHolder = new javax.swing.JScrollPane();
        m_privateInfoHolder = new javax.swing.JScrollPane();
        m_menuBar = new javax.swing.JMenuBar();
        m_fileMenu = new javax.swing.JMenu();
        m_newSceneMenuItem = new javax.swing.JMenuItem();
        m_openSceneMenuItem = new javax.swing.JMenuItem();
        m_saveSceneMenuItem = new javax.swing.JMenuItem();
        m_saveasSceneMenuItem = new javax.swing.JMenuItem();
        m_menuFile1stSeparator = new javax.swing.JPopupMenu.Separator();
        m_exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        m_imageResouceMenuItem = new javax.swing.JMenuItem();
        m_imageMenuItem = new javax.swing.JMenuItem();
        m_fontMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        m_screenSizeMenuItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SPUKMK2me Scene Editor");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        m_addNodeButton.setText("Add");
        m_addNodeButton.setFocusable(false);
        m_addNodeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_addNodeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        m_addNodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_addNodeButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(m_addNodeButton);

        m_removeNodeButton.setText("Remove");
        m_removeNodeButton.setFocusable(false);
        m_removeNodeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_removeNodeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        m_removeNodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_removeNodeButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(m_removeNodeButton);

        m_moveButton.setText("Move");
        m_moveButton.setFocusable(false);
        m_moveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_moveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        m_moveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_moveButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(m_moveButton);

        m_upButton.setText("Up");
        m_upButton.setFocusable(false);
        m_upButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_upButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        m_upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_upButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(m_upButton);

        m_downButton.setText("Down");
        m_downButton.setFocusable(false);
        m_downButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        m_downButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        m_downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_downButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(m_downButton);

        jSplitPane1.setResizeWeight(0.3);
        jSplitPane1.setLeftComponent(m_sceneTreeHolder);

        jSplitPane2.setResizeWeight(0.7);
        jSplitPane2.setLeftComponent(m_renderingHolder);

        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setResizeWeight(0.4);
        jSplitPane3.setTopComponent(m_commonInfoHolder);
        jSplitPane3.setRightComponent(m_privateInfoHolder);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(jPanel2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel1);

        m_fileMenu.setText("File");

        m_newSceneMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        m_newSceneMenuItem.setText("New scene");
        m_newSceneMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_newSceneMenuItemActionPerformed(evt);
            }
        });
        m_fileMenu.add(m_newSceneMenuItem);

        m_openSceneMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        m_openSceneMenuItem.setText("Open scene");
        m_openSceneMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_openSceneMenuItemActionPerformed(evt);
            }
        });
        m_fileMenu.add(m_openSceneMenuItem);

        m_saveSceneMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        m_saveSceneMenuItem.setText("Save scene");
        m_saveSceneMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_saveSceneMenuItemActionPerformed(evt);
            }
        });
        m_fileMenu.add(m_saveSceneMenuItem);

        m_saveasSceneMenuItem.setText("Save scene as...");
        m_saveasSceneMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_saveasSceneMenuItemActionPerformed(evt);
            }
        });
        m_fileMenu.add(m_saveasSceneMenuItem);
        m_fileMenu.add(m_menuFile1stSeparator);

        m_exitMenuItem.setText("Exit");
        m_fileMenu.add(m_exitMenuItem);

        m_menuBar.add(m_fileMenu);

        jMenu1.setText("Resources");

        m_imageResouceMenuItem.setText("Image resource");
        m_imageResouceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_imageResouceMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(m_imageResouceMenuItem);

        m_imageMenuItem.setText("Image");
        m_imageMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_imageMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(m_imageMenuItem);

        m_fontMenuItem.setText("Font");
        m_fontMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_fontMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(m_fontMenuItem);

        m_menuBar.add(jMenu1);

        jMenu2.setText("Misc");

        m_screenSizeMenuItem.setText("Screen size");
        m_screenSizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_screenSizeMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(m_screenSizeMenuItem);

        m_menuBar.add(jMenu2);

        jMenu3.setText("Help");
        m_menuBar.add(jMenu3);

        setJMenuBar(m_menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void m_imageResouceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_imageResouceMenuItemActionPerformed
        
        ImageResourceDialog dlg = new ImageResourceDialog(
            m_centralData.GetResourceManager(),
            m_centralData.GetDevice().GetVideoDriver(),
            this, true );

        int width   = dlg.getWidth();
        int height  = dlg.getHeight();

        dlg.setBounds(
            (this.getWidth() - width >> 1) + this.getX(),
            (this.getHeight() - height >> 1) + this.getY(), width, height );

        dlg.setVisible( true );
    }//GEN-LAST:event_m_imageResouceMenuItemActionPerformed

    private void m_addNodeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_addNodeButtonActionPerformed

        if ( m_centralData.GetCurrentNode() == null )
            return;

        ISceneNode node = m_centralData.GetCurrentNode();

        if ( node != null )
        {
            AddSceneNodeDialog dialog = new AddSceneNodeDialog(
                m_centralData, this, true );

            dialog.setVisible( true );
            m_centralData.DispatchReload();
        }
    }//GEN-LAST:event_m_addNodeButtonActionPerformed

    private void m_imageMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_imageMenuItemActionPerformed

        ImageDialog dlg = new ImageDialog( m_centralData.GetResourceManager(),
            m_centralData.GetDevice().GetVideoDriver(), this, true );

        dlg.setVisible( true );
    }//GEN-LAST:event_m_imageMenuItemActionPerformed

    private void m_fontMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_fontMenuItemActionPerformed

        FontDialog dlg = new FontDialog( m_centralData.GetResourceManager(),
            this, true );

        dlg.setVisible( true );
    }//GEN-LAST:event_m_fontMenuItemActionPerformed

    private void m_newSceneMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_newSceneMenuItemActionPerformed
        Reset();
    }//GEN-LAST:event_m_newSceneMenuItemActionPerformed

    private void m_screenSizeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_screenSizeMenuItemActionPerformed
        new ScreenSizeDialog( this, true , m_renderingPanel ).
            setVisible( true );
        m_sceneTreeHolder.getViewport().reshape( 0, 0,
            m_renderingPanel.getDisplayedWidth(),
            m_renderingPanel.getDisplayedHeight() );
    }//GEN-LAST:event_m_screenSizeMenuItemActionPerformed

    private void m_saveSceneMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_saveSceneMenuItemActionPerformed
        if ( m_filename == null )
            m_saveasSceneMenuItemActionPerformed( null );
        else
        {
            FileOutputStream os = null;
            
            try
            {
                os = new FileOutputStream( m_directory + m_filename );

                Saver.Save( m_centralData.GetResourceManager(),
                    m_centralData.GetRootNode(), os,
                    m_directory.substring( 0, m_directory.length() - 1 ) );
            } catch ( IOException e ) {
                JOptionPane.showMessageDialog( this,
                    "Error when saving file", "Error",
                    JOptionPane.ERROR_MESSAGE );
            } finally {
                try
                {
                    if ( os != null )
                        os.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
        
    }//GEN-LAST:event_m_saveSceneMenuItemActionPerformed

    private void m_openSceneMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_openSceneMenuItemActionPerformed
        FileDialog dlg = new FileDialog( this, "Load", FileDialog.LOAD );

        dlg.setVisible( true );
        
        if ( dlg.getFile() == null )
            return;

        Loader          loader = new Loader( m_centralData );
        FileInputStream is = null;

        try
        {
            is = new FileInputStream( dlg.getDirectory() + dlg.getFile() );

            String sceneFilePath = dlg.getDirectory();

            if ( sceneFilePath != null )
            {
                // Remove last character (if it's a path separator)
                if ( sceneFilePath.charAt( sceneFilePath.length() - 1 ) ==
                    File.separatorChar )
                {
                    sceneFilePath = sceneFilePath.substring(
                        0, sceneFilePath.length() - 1 );
                }

                if ( !loader.Load( is, sceneFilePath ) )
                {
                    JOptionPane.showMessageDialog( this,
                        "File header does not match", "Error",
                        JOptionPane.ERROR_MESSAGE );
                    m_centralData.DispatchReset();
                }
                else
                {
                    m_filename  = dlg.getFile();
                    m_directory = dlg.getDirectory();
                    this.setTitle( "SPUKMK2ME Scene Editor - " + m_filename );
                }
            }
            else
            {
                JOptionPane.showMessageDialog( this,
                    "Invalid file path", "Error",
                    JOptionPane.ERROR_MESSAGE );
            }
        } catch ( IOException e ) {
            JOptionPane.showMessageDialog( this,
                "Error when loading", "Error", JOptionPane.ERROR_MESSAGE );
            e.printStackTrace();
        } finally {
            try
            {
                if ( is != null )
                    is.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_m_openSceneMenuItemActionPerformed

    private void m_upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_upButtonActionPerformed
        moveNode( m_centralData.GetCurrentNode(), true );
    }//GEN-LAST:event_m_upButtonActionPerformed

    private void m_downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_downButtonActionPerformed
        moveNode( m_centralData.GetCurrentNode(), false );
    }//GEN-LAST:event_m_downButtonActionPerformed

    private void m_removeNodeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_removeNodeButtonActionPerformed
        ISceneNode node = m_centralData.GetCurrentNode();
        
        if ( node != m_centralData.GetRootNode() )
        {
            node.Drop();
            m_centralData.DispatchReload();
        }
    }//GEN-LAST:event_m_removeNodeButtonActionPerformed

    private void m_saveasSceneMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_saveasSceneMenuItemActionPerformed
        FileDialog dlg = new FileDialog( this, "Save scene", FileDialog.SAVE );
        FileOutputStream os = null;

        dlg.setVisible( true );

        if ( dlg.getFile() != null )
        {
            try
            {
                String dir = dlg.getDirectory();

                os = new FileOutputStream(
                    dlg.getDirectory() + dlg.getFile() );

                Saver.Save( m_centralData.GetResourceManager(),
                    m_centralData.GetRootNode(), os,
                    dir.substring( 0, dir.length() - 1 ) );
                
                m_filename  = dlg.getFile();
                m_directory = dlg.getDirectory();
                this.setTitle( "SPUKMK2ME Scene Editor - " + m_filename );
            } catch ( IOException e ) {
                JOptionPane.showMessageDialog( this,
                    "Error when saving file", "Error",
                    JOptionPane.ERROR_MESSAGE );
            } finally {
                try
                {
                    if ( os != null )
                        os.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_m_saveasSceneMenuItemActionPerformed

    private void m_moveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_moveButtonActionPerformed
        MoveSceneNodeDialog dlg = new MoveSceneNodeDialog(
            this, true, m_centralData );
        
        dlg.setVisible( true );
        
        if ( dlg.nodeMoved() )
        {
            m_centralData.DispatchReload();
        }
    }//GEN-LAST:event_m_moveButtonActionPerformed

    private void moveNode( ISceneNode node, boolean upDown )
    {
        if ( node != null );
        {
            if ( node != m_centralData.GetRootNode() )
            {
                ISceneNode before = (upDown)? node.c_prev.c_prev : node.c_next;
                
                if ( before != node )
                {
                    node.c_prev.c_next = node.c_next;
                    node.c_next.c_prev = node.c_prev;
                    node.c_next = before.c_next;
                    node.c_prev = before;
                    before.c_next.c_prev = node;
                    before.c_next = node;
                    
                    m_centralData.DispatchReload();
                }
            }
        }
    }
    
    private CentralData             m_centralData;
    private DisplayedSceneTree      m_sceneTree;
    private CommonInfoPanel         m_commonInfoPanel;
    private PrivateInfoPanel        m_privateInfoPanel;
    private MainRenderingPanel      m_renderingPanel;
    
    private String                  m_filename, m_directory;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton m_addNodeButton;
    private javax.swing.JScrollPane m_commonInfoHolder;
    private javax.swing.JButton m_downButton;
    private javax.swing.JMenuItem m_exitMenuItem;
    private javax.swing.JMenu m_fileMenu;
    private javax.swing.JMenuItem m_fontMenuItem;
    private javax.swing.JMenuItem m_imageMenuItem;
    private javax.swing.JMenuItem m_imageResouceMenuItem;
    private javax.swing.JMenuBar m_menuBar;
    private javax.swing.JPopupMenu.Separator m_menuFile1stSeparator;
    private javax.swing.JButton m_moveButton;
    private javax.swing.JMenuItem m_newSceneMenuItem;
    private javax.swing.JMenuItem m_openSceneMenuItem;
    private javax.swing.JScrollPane m_privateInfoHolder;
    private javax.swing.JButton m_removeNodeButton;
    private javax.swing.JScrollPane m_renderingHolder;
    private javax.swing.JMenuItem m_saveSceneMenuItem;
    private javax.swing.JMenuItem m_saveasSceneMenuItem;
    private javax.swing.JScrollPane m_sceneTreeHolder;
    private javax.swing.JMenuItem m_screenSizeMenuItem;
    private javax.swing.JButton m_upButton;
    // End of variables declaration//GEN-END:variables
}
