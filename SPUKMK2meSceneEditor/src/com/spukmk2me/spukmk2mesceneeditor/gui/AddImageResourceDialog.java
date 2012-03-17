package com.spukmk2me.spukmk2mesceneeditor.gui;

import java.io.IOException;
import java.io.File;
import java.awt.Dialog;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.spukmk2me.video.IVideoDriver;
import com.spukmk2me.optional.scene.ResourceManager;
import com.spukmk2me.spukmk2mesceneeditor.data.Misc;
import com.spukmk2me.video.IImageResource;

public class AddImageResourceDialog extends JDialog
{
    public AddImageResourceDialog( ResourceManager resourceManager,
        IVideoDriver vdriver, Dialog parent, boolean modal )
    {
        super( parent, modal );
        m_resourceManager   = resourceManager;
        m_vdriver           = vdriver;
        initComponents();
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
        m_filenameTextField = new javax.swing.JTextField();
        m_browseButton = new javax.swing.JButton();
        m_addButton = new javax.swing.JButton();
        m_cancelButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        m_proxyTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add image resource");
        setModal(true);

        jLabel1.setText("Filename:");

        m_browseButton.setText("Browse");
        m_browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_browseButtonActionPerformed(evt);
            }
        });

        m_addButton.setText("Add");
        m_addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_addButtonActionPerformed(evt);
            }
        });

        m_cancelButton.setText("Cancel");
        m_cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_cancelButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Proxy name:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(m_cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_addButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(m_proxyTextField)
                            .addComponent(m_filenameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(m_browseButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(m_filenameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(m_proxyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_addButton)
                    .addComponent(m_cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void m_browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_browseButtonActionPerformed

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle( "Open image resource" );
        fileChooser.setCurrentDirectory( new File( Misc.WORKINGDIR ) );

        if ( fileChooser.showOpenDialog( this ) ==
            JFileChooser.APPROVE_OPTION )
        {
            File file = fileChooser.getSelectedFile();

            m_filenameTextField.setText( file.getAbsolutePath() );
            m_proxyTextField.setText( file.getName() );
            Misc.WORKINGDIR = file.getParent();
        }
    }//GEN-LAST:event_m_browseButtonActionPerformed

    private void m_cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_cancelButtonActionPerformed

        this.setVisible( false );
        this.dispose();
    }//GEN-LAST:event_m_cancelButtonActionPerformed

    private void m_addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_addButtonActionPerformed

        String proxyName = m_proxyTextField.getText();

        if ( proxyName == null )
        {
            JOptionPane.showMessageDialog( this,
                "Proxy name is null?" );
            return;
        }
        else if ( proxyName.length() == 0 )
        {
            JOptionPane.showMessageDialog( this,
                "Proxy name is empty" );
            return;
        }
        else if ( m_resourceManager.GetResource( proxyName,
            ResourceManager.RT_IMAGERESOURCE ) != null )
        {
            JOptionPane.showMessageDialog( this,
                "Duplicated proxy name" );
            return;
        }

        if ( m_resourceManager.GetResource(
            proxyName, ResourceManager.RT_IMAGERESOURCE ) == null )
        {
            try
            {
                IImageResource resource = m_vdriver.CreateImageResource(
                    m_filenameTextField.getText() );
                IImageResource.ImageResourceCreationData creationData =
                    resource.new ImageResourceCreationData();
                
                creationData.c_path = m_filenameTextField.getText();
                creationData.c_proxyName = m_proxyTextField.getText();
                resource.SetCreationData( creationData );

                m_resourceManager.AddResource( resource,
                    ResourceManager.RT_IMAGERESOURCE );
                this.setVisible( false );
                this.dispose();
            } catch ( IOException e ) {
                JOptionPane.showConfirmDialog( this,
                    "IO error was occured.", "Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE );
            }
        }
        else
        {
            JOptionPane.showConfirmDialog( this,
                "Duplicated proxy name", "Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE );
        }
    }//GEN-LAST:event_m_addButtonActionPerformed

    private ResourceManager m_resourceManager;
    private IVideoDriver    m_vdriver;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton m_addButton;
    private javax.swing.JButton m_browseButton;
    private javax.swing.JButton m_cancelButton;
    private javax.swing.JTextField m_filenameTextField;
    private javax.swing.JTextField m_proxyTextField;
    // End of variables declaration//GEN-END:variables
}