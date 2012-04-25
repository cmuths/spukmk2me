package com.spukmk2me.spukmk2mesceneeditor.gui;

import java.awt.Frame;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import com.spukmk2me.video.IImageResource;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.video.ICFont;
import com.spukmk2me.video.BitmapFont;
import com.spukmk2me.scene.ImageSceneNode;
import com.spukmk2me.scene.StringSceneNode;
import com.spukmk2me.scene.TiledLayerSceneNode;
import com.spukmk2me.scene.complex.ClippingSceneNode;
import com.spukmk2me.resource.IResource;
import com.spukmk2me.resource.ResourceSet;
import com.spukmk2me.spukmk2mesceneeditor.data.CentralData;
import com.spukmk2me.spukmk2mesceneeditor.data.NodeTypeChecker;

public class PrivateInfoPanel extends JPanel
    implements SceneManagerEventListener
{
    public PrivateInfoPanel( Frame owner )
    {
        m_owner = owner;
        initComponents();
        m_panels = new JPanel[ 7 ];

        m_panels[ 0 ] = m_unknownPanel;
        m_panels[ 1 ] = m_nullPanel;
        m_panels[ 2 ] = m_imagePanel;
        m_panels[ 3 ] = m_spritePanel;
        m_panels[ 4 ] = m_stringPanel;
        m_panels[ 5 ] = m_tiledPanel;
        m_panels[ 6 ] = m_clippingPanel;
    }

    public void ImageResourceChanged(
        IImageResource imageResource, byte changingCode )
    {
    }

    public void ImageChanged( ISubImage image, byte changingCode )
    {
    }

    public void FontChanged( ICFont font, byte changingCode )
    {
    }

    public void CurrentNodeChanged()
    {
        ReloadData();
    }

    public void SetCentralData( CentralData data )
    {
        m_data = data;
    }

    public void Reset()
    {
    }

    private void ReloadData()
    {
        if ( m_data.GetCurrentNode() == null )
        {
            EnablePanel( 0 );
            return;
        }

        int nodeType = NodeTypeChecker.GetNodeType( m_data.GetCurrentNode() );

        EnablePanel( nodeType + 1 );

        switch ( nodeType )
        {
            case NodeTypeChecker.NT_IMAGE:
                LoadDataForImageSceneNode();
                break;

            case NodeTypeChecker.NT_STRING:
                LoadDataForStringSceneNode();
                break;

            case NodeTypeChecker.NT_TILED:
                LoadDataForTiledSceneNode();
                break;
                
            case NodeTypeChecker.NT_CLIPPING:
                LoadDataForClippingSceneNode();
                break;

            default:
                EnablePanel( 0 );
        }
    }

    private void EnablePanel( int panelIndex )
    {
        for ( int i = 0; i != m_panels.length; ++i )
            m_panels[ i ].setVisible( false );

        m_panels[ panelIndex ].setVisible( true );
    }

    private void LoadDataForImageSceneNode()
    {
        ResourceSet manager = m_data.GetResourceSet();

        DefaultListModel model =
            (DefaultListModel)m_imageList.getModel();
        ImageSceneNode node = (ImageSceneNode)m_data.GetCurrentNode();

        model.clear();
        model.addElement( "---Null---" );

        int n = manager.GetNumberOfResources( IResource.RT_IMAGE );
        int selectedIndex = 0;

        for ( int i = 0; i != n; ++i )
        {
            model.addElement( manager.GetResource( i,
                IResource.RT_IMAGE ).GetProxyName() );

            if ( manager.GetResource( i, IResource.RT_IMAGE ) ==
                node.GetImage() )
                selectedIndex = i + 1;
        }

        m_imageList.setModel( model );
        m_imageList.setSelectedIndex( selectedIndex );
    }

    private void LoadDataForStringSceneNode()
    {
        ResourceSet manager = m_data.GetResourceSet();

        DefaultComboBoxModel boxModel =
            (DefaultComboBoxModel)m_strFontCombobox.getModel();
        StringSceneNode node = (StringSceneNode)m_data.GetCurrentNode();
        StringSceneNode.StringSceneNodeInfoData info =
            (StringSceneNode.StringSceneNodeInfoData)node.c_infoData;

        boxModel.removeAllElements();
        boxModel.addElement( "---Null---" );

        int n = manager.GetNumberOfResources( IResource.RT_BITMAPFONT );

        for ( int i = 0; i != n; ++i )
        {
            boxModel.addElement( manager.GetResource(
                i, IResource.RT_BITMAPFONT ).GetProxyName() );

            if ( info.c_font == manager.GetResource( i,
                IResource.RT_BITMAPFONT ) )
                boxModel.setSelectedItem( boxModel.getElementAt( i + 1 ) );
        }

        if ( info.c_font == null )
            m_strFontCombobox.setSelectedIndex( 0 );
        
        byte align = (byte)info.c_alignment;

        m_strLeftCheckbox.setSelected(
            (align & StringSceneNode.ALIGN_LEFT) != 0 );
        m_strMidXCheckbox.setSelected(
            (align & StringSceneNode.ALIGN_CENTERX) != 0 );
        m_strRightCheckbox.setSelected(
            (align & StringSceneNode.ALIGN_RIGHT) != 0 );
        m_strTopCheckbox.setSelected(
            (align & StringSceneNode.ALIGN_TOP) != 0 );
        m_strMidYCheckbox.setSelected(
            (align & StringSceneNode.ALIGN_CENTERY) != 0 );
        m_strBottomCheckbox.setSelected(
            (align & StringSceneNode.ALIGN_BOTTOM) != 0 );
        m_strTruncateCheckbox.setSelected( info.c_truncate );

        if ( info.c_string != null )
        {
            m_strContentTextField.setText(
                String.valueOf( info.c_string ) );
        }
        else
            m_strContentTextField.setText( "" );
        
        if ( info.c_properties != null )
        {
            byte[]  properties  = info.c_properties;
            int     nColor      = (properties.length - 1) / 4;
            byte    style       = properties[ properties.length - 1 ];

            m_strBoldCheckbox.setSelected(
                (style & BitmapFont.STYLE_BOLD) != 0 );
            m_strItalicCheckbox.setSelected(
                (style & BitmapFont.STYLE_ITALIC) != 0 );
            m_strUnderlineCheckbox.setSelected(
                (style & BitmapFont.STYLE_UNDERLINE) != 0 );
            
            DefaultListModel listModel =
                (DefaultListModel)m_strColorList.getModel();
            int color, pIndex = 0;
            
            listModel.clear();
            
            for ( int i = 0; i != nColor; ++i )
            {
                color   = properties[ pIndex++ ] << 24;
                color  |= properties[ pIndex++ ] << 16 & 0x00FF0000;
                color  |= properties[ pIndex++ ] << 8 & 0x0000FF00;
                color  |= properties[ pIndex++ ] & 0x000000FF;
                listModel.addElement( Integer.toHexString( color ) );
            }
        }
        else
        {
            DefaultListModel listModel =
                (DefaultListModel)m_strColorList.getModel();
            
            listModel.clear();
            m_strBoldCheckbox.setSelected( false );
            m_strItalicCheckbox.setSelected( false );
            m_strUnderlineCheckbox.setSelected( false );
        }
        
        m_strWidthTextField.setText( String.valueOf( info.c_width ) );
        m_strHeightTextField.setText( String.valueOf( info.c_height ) );
    }

    private void LoadDataForTiledSceneNode()
    {
        TiledLayerSceneNode.TiledLayerSceneNodeInfoData info =
            (TiledLayerSceneNode.TiledLayerSceneNodeInfoData)m_data.
                GetCurrentNode().c_infoData;

        {
            DefaultListModel model = new DefaultListModel();

            m_tiledCreatedImgList.setModel( model );

            int n = m_data.GetResourceSet().GetNumberOfResources(
                IResource.RT_IMAGE );

            for ( int i = 0; i != n; ++i )
            {
                model.addElement( m_data.GetResourceSet().GetResource(
                    i, IResource.RT_IMAGE ).GetProxyName() );
            }
            
            model = new DefaultListModel();
            m_tiledImageList.setModel( model );

            if ( info.c_images == null )
            {
                n = 0;
                m_tiledNoImageCheckbox.setSelected( true );
                EnableTiledImagePanelContent( false );
            }
            else
            {
                n = info.c_images.length;
                m_tiledNoImageCheckbox.setSelected( false );
                EnableTiledImagePanelContent( true );
            }

            for ( int i = 0; i != n; ++i )
            {
                model.addElement( info.c_images[ i ].GetProxyName() );
            }
        }

        m_tiledStepXTextField.setText( String.valueOf( info.c_stepX ) );
        m_tiledStepYTextField.setText( String.valueOf( info.c_stepY ) );
        m_tiledWidthTextField.setText( String.valueOf( info.c_tableWidth ) );
        m_tiledHeightTextField.setText( String.valueOf( info.c_tableHeight ) );
        m_tiledViewSpdXTextField.setText( String.valueOf( info.c_viewSpdX * 1.0f / 0x00010000 ) );
        m_tiledViewSpdYTextField.setText( String.valueOf( info.c_viewSpdY * 1.0f / 0x00010000 ) );
        
        m_tiledRepeatedViewCheckBox.setSelected( info.c_repeatedView );
        m_tiledViewWidthTextField.setText( String.valueOf( info.c_viewWidth ) );
        m_tiledViewHeightTextField.setText( String.valueOf( info.c_viewHeight ) );
        m_tiledViewXTextField.setText( String.valueOf( info.c_viewX ) );
        m_tiledViewYTextField.setText( String.valueOf( info.c_viewY ) );
    }
    
    private void LoadDataForClippingSceneNode()
    {
        ClippingSceneNode.ClippingSceneNodeInfoData info =
            (ClippingSceneNode.ClippingSceneNodeInfoData)m_data.
                GetCurrentNode().c_infoData;
        
        m_clippingXTextField.setText( String.valueOf( info.c_x ) );
        m_clippingYTextField.setText( String.valueOf( info.c_y ) );
        m_clippingWidthTextField.setText( String.valueOf( info.c_width ) );
        m_clippingHeightTextField.setText( String.valueOf( info.c_height ) );
    }

    private void EnableTiledImagePanelContent( boolean enable )
    {
        m_tiledImageList.setEnabled( enable );
        m_tiledCreatedImgList.setEnabled( enable );
        m_tiledAddImageButton.setEnabled( enable );
        m_tiledDelImageButton.setEnabled( enable );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_layeredPane = new javax.swing.JLayeredPane();
        m_unknownPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        m_nullPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        m_imagePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_imageList = new javax.swing.JList();
        m_changeButton = new javax.swing.JButton();
        m_spritePanel = new javax.swing.JPanel();
        m_stringPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        m_strContentTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_strFontCombobox = new javax.swing.JComboBox();
        m_strChangeButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        m_strTruncateCheckbox = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        m_strBoldCheckbox = new javax.swing.JCheckBox();
        m_strItalicCheckbox = new javax.swing.JCheckBox();
        m_strUnderlineCheckbox = new javax.swing.JCheckBox();
        m_strLeftCheckbox = new javax.swing.JCheckBox();
        m_strTopCheckbox = new javax.swing.JCheckBox();
        m_strMidXCheckbox = new javax.swing.JCheckBox();
        m_strMidYCheckbox = new javax.swing.JCheckBox();
        m_strRightCheckbox = new javax.swing.JCheckBox();
        m_strBottomCheckbox = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        m_strWidthTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        m_strHeightTextField = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        m_strColorList = new javax.swing.JList( new DefaultListModel() );
        m_strAddColorButton = new javax.swing.JButton();
        m_strRemoveColorButton = new javax.swing.JButton();
        m_strEditColorButton = new javax.swing.JButton();
        m_strColorTextField = new javax.swing.JTextField();
        m_tiledPanel = new javax.swing.JPanel();
        m_tiledTabbedPane = new javax.swing.JTabbedPane();
        m_tiledImagePanel = new javax.swing.JPanel();
        m_tiledNoImageCheckbox = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        m_tiledImageList = new javax.swing.JList();
        m_tiledAddImageButton = new javax.swing.JButton();
        m_tiledDelImageButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        m_tiledCreatedImgList = new javax.swing.JList();
        m_tiledSpritePanel = new javax.swing.JPanel();
        m_tiledNoSpriteCheckbox = new javax.swing.JCheckBox();
        m_tiledInfoPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        m_tiledWidthTextField = new javax.swing.JTextField();
        m_tiledHeightTextField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        m_tiledStepXTextField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        m_tiledStepYTextField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        m_tiledRepeatedViewCheckBox = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        m_tiledViewWidthTextField = new javax.swing.JTextField();
        m_tiledViewHeightTextField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        m_tiledViewXTextField = new javax.swing.JTextField();
        m_tiledViewYTextField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        m_tiledViewSpdYTextField = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        m_tiledViewSpdXTextField = new javax.swing.JTextField();
        m_tiledApplyButton = new javax.swing.JButton();
        m_tiledEditButton = new javax.swing.JButton();
        m_clippingPanel = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        m_clippingXTextField = new javax.swing.JTextField();
        m_clippingYTextField = new javax.swing.JTextField();
        m_clippingWidthTextField = new javax.swing.JTextField();
        m_clippingHeightTextField = new javax.swing.JTextField();
        m_clippingApplyButton = new javax.swing.JButton();

        jLabel9.setText("Unknown type.");

        javax.swing.GroupLayout m_unknownPanelLayout = new javax.swing.GroupLayout(m_unknownPanel);
        m_unknownPanel.setLayout(m_unknownPanelLayout);
        m_unknownPanelLayout.setHorizontalGroup(
            m_unknownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_unknownPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(187, Short.MAX_VALUE))
        );
        m_unknownPanelLayout.setVerticalGroup(
            m_unknownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_unknownPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(275, Short.MAX_VALUE))
        );

        m_unknownPanel.setBounds(0, 0, 270, 300);
        m_layeredPane.add(m_unknownPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel8.setText("There is nothing to do.");

        javax.swing.GroupLayout m_nullPanelLayout = new javax.swing.GroupLayout(m_nullPanel);
        m_nullPanel.setLayout(m_nullPanelLayout);
        m_nullPanelLayout.setHorizontalGroup(
            m_nullPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_nullPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(151, Short.MAX_VALUE))
        );
        m_nullPanelLayout.setVerticalGroup(
            m_nullPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_nullPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(275, Short.MAX_VALUE))
        );

        m_nullPanel.setBounds(0, 0, 270, 300);
        m_layeredPane.add(m_nullPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel1.setText("Image resource:");

        m_imageList.setModel( new DefaultListModel() );
        jScrollPane1.setViewportView(m_imageList);

        m_changeButton.setText("Change");
        m_changeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_changeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout m_imagePanelLayout = new javax.swing.GroupLayout(m_imagePanel);
        m_imagePanel.setLayout(m_imagePanelLayout);
        m_imagePanelLayout.setHorizontalGroup(
            m_imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_imagePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(m_changeButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        m_imagePanelLayout.setVerticalGroup(
            m_imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_imagePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addComponent(m_changeButton)
                .addContainerGap())
        );

        m_imagePanel.setBounds(0, 0, 270, 300);
        m_layeredPane.add(m_imagePanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout m_spritePanelLayout = new javax.swing.GroupLayout(m_spritePanel);
        m_spritePanel.setLayout(m_spritePanelLayout);
        m_spritePanelLayout.setHorizontalGroup(
            m_spritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );
        m_spritePanelLayout.setVerticalGroup(
            m_spritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        m_spritePanel.setBounds(0, 0, 270, 300);
        m_layeredPane.add(m_spritePanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel2.setText("String:");

        jLabel3.setText("Font:");

        m_strFontCombobox.setModel( new DefaultComboBoxModel() );

        m_strChangeButton.setText("Change");
        m_strChangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_strChangeButtonActionPerformed(evt);
            }
        });

        jLabel4.setText("Alignment:");

        m_strTruncateCheckbox.setText("Truncate");

        jLabel5.setText("Color:");

        jLabel6.setText("Style:");

        m_strBoldCheckbox.setText("Bold");

        m_strItalicCheckbox.setText("Italic");

        m_strUnderlineCheckbox.setText("Underline");

        m_strLeftCheckbox.setText("Left");

        m_strTopCheckbox.setText("Top");

        m_strMidXCheckbox.setText("Mid-X");

        m_strMidYCheckbox.setText("Mid-Y");

        m_strRightCheckbox.setText("Right");

        m_strBottomCheckbox.setText("Bottom");

        jLabel7.setText("Width:");

        jLabel10.setText("Height:");

        m_strColorList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane4.setViewportView(m_strColorList);

        m_strAddColorButton.setText("Add");
        m_strAddColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_strAddColorButtonActionPerformed(evt);
            }
        });

        m_strRemoveColorButton.setText("Remove");
        m_strRemoveColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_strRemoveColorButtonActionPerformed(evt);
            }
        });

        m_strEditColorButton.setText("Edit");
        m_strEditColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_strEditColorButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout m_stringPanelLayout = new javax.swing.GroupLayout(m_stringPanel);
        m_stringPanel.setLayout(m_stringPanelLayout);
        m_stringPanelLayout.setHorizontalGroup(
            m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_stringPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(m_stringPanelLayout.createSequentialGroup()
                        .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(m_strTopCheckbox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(m_strLeftCheckbox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(m_strMidYCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(m_strMidXCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(m_strRightCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(m_strBottomCheckbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_strTruncateCheckbox))
                    .addGroup(m_stringPanelLayout.createSequentialGroup()
                        .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_strFontCombobox, 0, 214, Short.MAX_VALUE)
                            .addComponent(m_strContentTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)))
                    .addGroup(m_stringPanelLayout.createSequentialGroup()
                        .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_stringPanelLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(m_strBoldCheckbox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_strItalicCheckbox))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_stringPanelLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_strColorTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                                    .addComponent(jScrollPane4, 0, 0, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_strUnderlineCheckbox)
                            .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(m_strRemoveColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(m_strAddColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(m_strEditColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(27, 27, 27))
                    .addGroup(m_stringPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_strWidthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_strHeightTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                    .addComponent(m_strChangeButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        m_stringPanelLayout.setVerticalGroup(
            m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_stringPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(m_strContentTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(m_strFontCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_strLeftCheckbox)
                    .addComponent(m_strMidXCheckbox)
                    .addComponent(m_strRightCheckbox)
                    .addComponent(m_strTruncateCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_strTopCheckbox)
                    .addComponent(m_strMidYCheckbox)
                    .addComponent(m_strBottomCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(m_stringPanelLayout.createSequentialGroup()
                        .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_stringPanelLayout.createSequentialGroup()
                                .addComponent(m_strAddColorButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_strRemoveColorButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(m_strEditColorButton)
                            .addComponent(m_strColorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(m_strBoldCheckbox)
                    .addComponent(m_strItalicCheckbox)
                    .addComponent(m_strUnderlineCheckbox))
                .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(m_stringPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(m_stringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(m_strWidthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)))
                    .addGroup(m_stringPanelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(m_strHeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_strChangeButton)
                .addGap(10, 10, 10))
        );

        m_stringPanel.setBounds(0, 0, 270, 300);
        m_layeredPane.add(m_stringPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        m_tiledNoImageCheckbox.setText("No image");
        m_tiledNoImageCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_tiledNoImageCheckboxActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(m_tiledImageList);

        m_tiledAddImageButton.setText("Add");
        m_tiledAddImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_tiledAddImageButtonActionPerformed(evt);
            }
        });

        m_tiledDelImageButton.setText("Delete");

        jScrollPane3.setViewportView(m_tiledCreatedImgList);

        javax.swing.GroupLayout m_tiledImagePanelLayout = new javax.swing.GroupLayout(m_tiledImagePanel);
        m_tiledImagePanel.setLayout(m_tiledImagePanelLayout);
        m_tiledImagePanelLayout.setHorizontalGroup(
            m_tiledImagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_tiledImagePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_tiledImagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_tiledNoImageCheckbox)
                    .addGroup(m_tiledImagePanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(m_tiledImagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_tiledDelImageButton, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                            .addComponent(m_tiledAddImageButton, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)))
                .addContainerGap())
        );
        m_tiledImagePanelLayout.setVerticalGroup(
            m_tiledImagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_tiledImagePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_tiledNoImageCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_tiledImagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, m_tiledImagePanelLayout.createSequentialGroup()
                        .addComponent(m_tiledAddImageButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_tiledDelImageButton))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        m_tiledTabbedPane.addTab("Images", m_tiledImagePanel);

        m_tiledNoSpriteCheckbox.setSelected(true);
        m_tiledNoSpriteCheckbox.setText("No spirte");
        m_tiledNoSpriteCheckbox.setEnabled(false);

        javax.swing.GroupLayout m_tiledSpritePanelLayout = new javax.swing.GroupLayout(m_tiledSpritePanel);
        m_tiledSpritePanel.setLayout(m_tiledSpritePanelLayout);
        m_tiledSpritePanelLayout.setHorizontalGroup(
            m_tiledSpritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_tiledSpritePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_tiledNoSpriteCheckbox)
                .addContainerGap(198, Short.MAX_VALUE))
        );
        m_tiledSpritePanelLayout.setVerticalGroup(
            m_tiledSpritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_tiledSpritePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_tiledNoSpriteCheckbox)
                .addContainerGap(202, Short.MAX_VALUE))
        );

        m_tiledTabbedPane.addTab("Sprites", m_tiledSpritePanel);

        jLabel11.setText("Width:");

        jLabel12.setText("Height:");

        jLabel15.setText("StepX:");

        jLabel17.setText("StepY:");

        m_tiledRepeatedViewCheckBox.setText("Repeated view");

        jLabel13.setText("View width:");

        jLabel14.setText("View height:");

        jLabel16.setText("View X:");

        jLabel18.setText("View Y:");

        jLabel23.setText("Spd X:");

        jLabel24.setText("Spd Y:");

        javax.swing.GroupLayout m_tiledInfoPanelLayout = new javax.swing.GroupLayout(m_tiledInfoPanel);
        m_tiledInfoPanel.setLayout(m_tiledInfoPanelLayout);
        m_tiledInfoPanelLayout.setHorizontalGroup(
            m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_tiledInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(7, 7, 7)
                .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(m_tiledHeightTextField)
                    .addComponent(m_tiledWidthTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(m_tiledInfoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_tiledStepXTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE))
                    .addGroup(m_tiledInfoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_tiledStepYTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
            .addGroup(m_tiledInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_tiledRepeatedViewCheckBox)
                    .addGroup(m_tiledInfoPanelLayout.createSequentialGroup()
                        .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(m_tiledViewSpdXTextField)
                            .addComponent(m_tiledViewHeightTextField)
                            .addComponent(m_tiledViewWidthTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(m_tiledInfoPanelLayout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_tiledViewXTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE))
                            .addGroup(m_tiledInfoPanelLayout.createSequentialGroup()
                                .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_tiledViewSpdYTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                                    .addComponent(m_tiledViewYTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        m_tiledInfoPanelLayout.setVerticalGroup(
            m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_tiledInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(m_tiledWidthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(m_tiledStepXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(m_tiledHeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(m_tiledStepYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_tiledRepeatedViewCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(m_tiledViewWidthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(m_tiledViewXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(m_tiledViewHeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(m_tiledViewYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_tiledInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(m_tiledViewSpdYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(m_tiledViewSpdXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        m_tiledTabbedPane.addTab("Info", m_tiledInfoPanel);

        m_tiledApplyButton.setText("Apply");
        m_tiledApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_tiledApplyButtonActionPerformed(evt);
            }
        });

        m_tiledEditButton.setText("Editor");
        m_tiledEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_tiledEditButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout m_tiledPanelLayout = new javax.swing.GroupLayout(m_tiledPanel);
        m_tiledPanel.setLayout(m_tiledPanelLayout);
        m_tiledPanelLayout.setHorizontalGroup(
            m_tiledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, m_tiledPanelLayout.createSequentialGroup()
                .addContainerGap(142, Short.MAX_VALUE)
                .addComponent(m_tiledEditButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_tiledApplyButton)
                .addContainerGap())
            .addComponent(m_tiledTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
        );
        m_tiledPanelLayout.setVerticalGroup(
            m_tiledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, m_tiledPanelLayout.createSequentialGroup()
                .addComponent(m_tiledTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_tiledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_tiledApplyButton)
                    .addComponent(m_tiledEditButton))
                .addContainerGap())
        );

        m_tiledPanel.setBounds(0, 0, 270, 300);
        m_layeredPane.add(m_tiledPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel19.setText("Clip X:");

        jLabel20.setText("Clip Y:");

        jLabel21.setText("Clip width:");

        jLabel22.setText("Clip height:");

        m_clippingApplyButton.setText("Apply");
        m_clippingApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_clippingApplyButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout m_clippingPanelLayout = new javax.swing.GroupLayout(m_clippingPanel);
        m_clippingPanel.setLayout(m_clippingPanelLayout);
        m_clippingPanelLayout.setHorizontalGroup(
            m_clippingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_clippingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_clippingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(m_clippingPanelLayout.createSequentialGroup()
                        .addGroup(m_clippingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(m_clippingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m_clippingYTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .addComponent(m_clippingXTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .addComponent(m_clippingWidthTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .addComponent(m_clippingHeightTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)))
                    .addComponent(m_clippingApplyButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        m_clippingPanelLayout.setVerticalGroup(
            m_clippingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(m_clippingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(m_clippingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(m_clippingXTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_clippingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(m_clippingYTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_clippingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(m_clippingWidthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(m_clippingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(m_clippingHeightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                .addComponent(m_clippingApplyButton)
                .addContainerGap())
        );

        m_clippingPanel.setBounds(0, 0, 270, 300);
        m_layeredPane.add(m_clippingPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(m_layeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(m_layeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_changeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_changeButtonActionPerformed

        int index = m_imageList.getSelectedIndex();
        
        if ( index != -1 )
        {
            ImageSceneNode node = (ImageSceneNode)m_data.GetCurrentNode();

            if ( index == 0 )
                node.SetImage( null );
            else
            {
                node.SetImage( (ISubImage)m_data.GetResourceSet().
                    GetResource( index - 1, IResource.RT_IMAGE ) );
            }
        }
    }//GEN-LAST:event_m_changeButtonActionPerformed

    private void m_strChangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_strChangeButtonActionPerformed

        ResourceSet manager = m_data.GetResourceSet();
        StringSceneNode node    = (StringSceneNode)m_data.GetCurrentNode();

        try
        {
            DefaultListModel model =
                (DefaultListModel)m_strColorList.getModel();
            int nColor = m_strColorList.getModel().getSize();
            int pIndex = 0, color;
            byte[] properties  = new byte[ nColor * 4 + 1 ];
            
            for ( int i = 0; i != nColor; ++i )
            {
                color = (int)Long.parseLong(
                    (String)model.get( i ), 16 );
                properties[ pIndex++ ] = (byte)(color >> 24);
                properties[ pIndex++ ] = (byte)(color >> 16);
                properties[ pIndex++ ] = (byte)(color >> 8);
                properties[ pIndex++ ] = (byte)color;
            }
            
            byte style = 0;
            
            if ( m_strBoldCheckbox.isSelected() )
                style |= BitmapFont.STYLE_BOLD;

            if ( m_strItalicCheckbox.isSelected() )
                style |= BitmapFont.STYLE_ITALIC;

            if ( m_strUnderlineCheckbox.isSelected() )
                style |= BitmapFont.STYLE_UNDERLINE;
            
            properties[ pIndex ] = style;

            byte align = 0;

            if ( m_strLeftCheckbox.isSelected() )
                align |= StringSceneNode.ALIGN_LEFT;

            if ( m_strMidXCheckbox.isSelected() )
                align |= StringSceneNode.ALIGN_CENTERX;

            if ( m_strRightCheckbox.isSelected() )
                align |= StringSceneNode.ALIGN_RIGHT;

            if ( m_strTopCheckbox.isSelected() )
                align |= StringSceneNode.ALIGN_TOP;

            if ( m_strMidYCheckbox.isSelected() )
                align |= StringSceneNode.ALIGN_CENTERY;

            if ( m_strBottomCheckbox.isSelected() )
                align |= StringSceneNode.ALIGN_BOTTOM;

            int fontIndex = m_strFontCombobox.getSelectedIndex();

            ICFont font;
            
            if ( fontIndex == 0 )
                font = null;
            else
            {
                font = (ICFont)manager.GetResource(
                    fontIndex - 1, IResource.RT_BITMAPFONT );
            }
            
            String content  = m_strContentTextField.getText();
            short width     =
                Short.parseShort( m_strWidthTextField.getText() );
            short height    =
                Short.parseShort( m_strHeightTextField.getText() );
            boolean truncate = m_strTruncateCheckbox.isSelected();

            node.SetupString( font, content,
                properties, align, width, height, truncate );
            
            StringSceneNode.StringSceneNodeInfoData info =
                (StringSceneNode.StringSceneNodeInfoData)node.c_infoData;
            
            info.c_string       = content;
            info.c_font         = font;
            info.c_properties   = properties;
            info.c_nProperties  = properties.length;
            info.c_width        = width;
            info.c_height       = height;
            info.c_alignment    = align;
            info.c_truncate     = truncate;
            
        } catch ( NumberFormatException e ) {
            JOptionPane.showMessageDialog( this, "Check your numbers." );
        }
    }//GEN-LAST:event_m_strChangeButtonActionPerformed

    private void m_tiledAddImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_tiledAddImageButtonActionPerformed
        Object[] proxyNames = m_tiledCreatedImgList.getSelectedValues();

        if ( proxyNames == null )
            return;

        if ( proxyNames.length == 0 )
            return;

        DefaultListModel model = (DefaultListModel)m_tiledImageList.getModel();
        boolean contained;

        for ( int i = 0; i != proxyNames.length; ++i )
        {
            contained = false;

            for ( int j = 0; j != model.getSize(); ++j )
            {
                if ( ((String)model.elementAt( j )).equals( proxyNames[ i ] ) )
                {
                    contained = true;
                    break;
                }
            }

            if ( !contained )
                model.addElement( proxyNames[ i ] );
        }
    }//GEN-LAST:event_m_tiledAddImageButtonActionPerformed

    private void m_tiledNoImageCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_tiledNoImageCheckboxActionPerformed
        EnableTiledImagePanelContent( !m_tiledNoImageCheckbox.isSelected() );
    }//GEN-LAST:event_m_tiledNoImageCheckboxActionPerformed

    private void m_tiledApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_tiledApplyButtonActionPerformed

        short   newWidth, newHeight, newStepX, newStepY,
                newViewWidth, newViewHeight, newViewX, newViewY;
        int     newSpdX, newSpdY;

        newWidth = newHeight = newStepX = newStepY =
            newViewWidth = newViewHeight = newViewX = newViewY = 0;

        try
        {
            newWidth    = Short.parseShort( m_tiledWidthTextField.getText() );
            newHeight   = Short.parseShort( m_tiledHeightTextField.getText() );
            newStepX = Short.parseShort( m_tiledStepXTextField.getText() );
            newStepY = Short.parseShort( m_tiledStepYTextField.getText() );
            
            newViewWidth    = Short.parseShort( m_tiledViewWidthTextField.getText() );
            newViewHeight   = Short.parseShort( m_tiledViewHeightTextField.getText() );
            newViewX        = Short.parseShort( m_tiledViewXTextField.getText() );
            newViewY        = Short.parseShort( m_tiledViewYTextField.getText() );
            newSpdX         = (int)(Double.parseDouble( m_tiledViewSpdXTextField.getText() ) * 0x00010000);
            newSpdY         = (int)(Double.parseDouble( m_tiledViewSpdYTextField.getText() ) * 0x00010000);
        } catch ( NumberFormatException e ) {
            JOptionPane.showMessageDialog( this,
                "Invalid input number", "ERROR", JOptionPane.ERROR_MESSAGE );
            return;
        }

        TiledLayerSceneNode.TiledLayerSceneNodeInfoData info =
            (TiledLayerSceneNode.TiledLayerSceneNodeInfoData)m_data.
                GetCurrentNode().c_infoData;
        DefaultListModel model = (DefaultListModel)m_tiledImageList.getModel();

        if ( m_tiledNoImageCheckbox.isSelected() )
            info.c_images = null;
        else
        {
            info.c_images = new ISubImage[ model.getSize() ];

            for ( int i = 0; i != info.c_images.length; ++i )
            {
                info.c_images[ i ] = (ISubImage)m_data.GetResourceSet().
                    GetResource( (String)model.getElementAt( i ),
                    IResource.RT_IMAGE );
            }
        }

        info.c_stepX = newStepX;
        info.c_stepY = newStepY;

        // Copy terrain
        
        byte[] newTerrain;
        
        if ( (newWidth <= 0) || (newHeight <= 0) )
        {
            newWidth = newHeight = 0;
            newTerrain = null;
        }
        else
        {
            newTerrain = new byte[ newWidth * newHeight ];

            for ( int i = 0; i != newTerrain.length; ++i )
                newTerrain[ i ] = -1;
        }

        short copyW = (short)Math.min( info.c_tableWidth, newWidth );
        short copyH = (short)Math.min( info.c_tableHeight, newHeight );

        if ( (copyW >= 0) && (copyH >= 0) )
        {
            int srcIndex = 0, dstIndex = 0;

            for ( int i = 0; i != copyH; ++i )
            {
                for ( int j = 0; j != copyW; ++j )
                {
                    newTerrain[ dstIndex++ ] =
                        info.c_terrainData[ srcIndex++ ];
                }

                srcIndex += info.c_tableWidth - copyW;
                dstIndex += newWidth - copyW;
            }
        }

        info.c_tableWidth   = newWidth;
        info.c_tableHeight  = newHeight;
        info.c_terrainData  = newTerrain;
        
        info.c_viewWidth    = newViewWidth;
        info.c_viewHeight   = newViewHeight;
        info.c_viewX        = newViewX;
        info.c_viewY        = newViewY;
        info.c_viewSpdX     = newSpdX;
        info.c_viewSpdY     = newSpdY;
        info.c_repeatedView = m_tiledRepeatedViewCheckBox.isSelected();

        TiledLayerSceneNode node =
            (TiledLayerSceneNode)m_data.GetCurrentNode();

        node.SetupTiledLayer( info.c_images, info.c_sprites,
            info.c_spriteSpeed, newTerrain,
            newWidth, newHeight, newStepX, newStepY );
        node.SetupRepeatedView( newViewX, newViewY,
            newViewWidth, newViewHeight, newSpdX, newSpdY, info.c_repeatedView );
    }//GEN-LAST:event_m_tiledApplyButtonActionPerformed

    private void m_tiledEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_tiledEditButtonActionPerformed
        TiledEditorDialog dlg = new TiledEditorDialog( m_owner, true,
            (TiledLayerSceneNode)m_data.GetCurrentNode() );

        dlg.setVisible( true );
    }//GEN-LAST:event_m_tiledEditButtonActionPerformed

    private void m_strAddColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_strAddColorButtonActionPerformed
        try
        {
            int color = (int)Long.parseLong(
                m_strColorTextField.getText(), 16 );
            
            DefaultListModel model =
                (DefaultListModel)m_strColorList.getModel();
            
            model.addElement( Integer.toHexString( color ) );
        } catch ( NumberFormatException e ) {
            JOptionPane.showMessageDialog( this, "Wrong number",
                "Error", JOptionPane.ERROR_MESSAGE );
        }
    }//GEN-LAST:event_m_strAddColorButtonActionPerformed

    private void m_strRemoveColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_strRemoveColorButtonActionPerformed
        int index = m_strColorList.getSelectedIndex();
        
        if ( index != -1 )
        {
            DefaultListModel model =
                (DefaultListModel)m_strColorList.getModel();
            
            model.remove( index );
        }
    }//GEN-LAST:event_m_strRemoveColorButtonActionPerformed

    private void m_strEditColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_strEditColorButtonActionPerformed
        int index = m_strColorList.getSelectedIndex();
        
        if ( index != -1 )
        {
            try
            {
                int color = (int)Long.parseLong(
                    m_strColorTextField.getText(), 16 );

                DefaultListModel model =
                    (DefaultListModel)m_strColorList.getModel();

                model.set( index, Integer.toHexString( color ) );
            } catch ( NumberFormatException e ) {
                JOptionPane.showMessageDialog( this, "Wrong number",
                    "Error", JOptionPane.ERROR_MESSAGE );
            }
        }
    }//GEN-LAST:event_m_strEditColorButtonActionPerformed

    private void m_clippingApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_clippingApplyButtonActionPerformed
        try
        {
            short x         = Short.parseShort( m_clippingXTextField.getText() );
            short y         = Short.parseShort( m_clippingYTextField.getText() );
            short width     = Short.parseShort( m_clippingWidthTextField.getText() );
            short height    = Short.parseShort( m_clippingHeightTextField.getText() );
            ClippingSceneNode node = (ClippingSceneNode)m_data.GetCurrentNode();
            ClippingSceneNode.ClippingSceneNodeInfoData info =
                (ClippingSceneNode.ClippingSceneNodeInfoData)node.c_infoData;
            
            node.SetClipping( x, y, width, height );
            info.c_x        = x;
            info.c_y        = y;
            info.c_width    = width;
            info.c_height   = height;
        } catch ( NumberFormatException e ) {
            JOptionPane.showMessageDialog( this, "Wrong number",
                "Error", JOptionPane.ERROR_MESSAGE );
        }
    }//GEN-LAST:event_m_clippingApplyButtonActionPerformed

    private CentralData m_data;
    private JPanel[]    m_panels;
    private Frame       m_owner;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton m_changeButton;
    private javax.swing.JButton m_clippingApplyButton;
    private javax.swing.JTextField m_clippingHeightTextField;
    private javax.swing.JPanel m_clippingPanel;
    private javax.swing.JTextField m_clippingWidthTextField;
    private javax.swing.JTextField m_clippingXTextField;
    private javax.swing.JTextField m_clippingYTextField;
    private javax.swing.JList m_imageList;
    private javax.swing.JPanel m_imagePanel;
    private javax.swing.JLayeredPane m_layeredPane;
    private javax.swing.JPanel m_nullPanel;
    private javax.swing.JPanel m_spritePanel;
    private javax.swing.JButton m_strAddColorButton;
    private javax.swing.JCheckBox m_strBoldCheckbox;
    private javax.swing.JCheckBox m_strBottomCheckbox;
    private javax.swing.JButton m_strChangeButton;
    private javax.swing.JList m_strColorList;
    private javax.swing.JTextField m_strColorTextField;
    private javax.swing.JTextField m_strContentTextField;
    private javax.swing.JButton m_strEditColorButton;
    private javax.swing.JComboBox m_strFontCombobox;
    private javax.swing.JTextField m_strHeightTextField;
    private javax.swing.JCheckBox m_strItalicCheckbox;
    private javax.swing.JCheckBox m_strLeftCheckbox;
    private javax.swing.JCheckBox m_strMidXCheckbox;
    private javax.swing.JCheckBox m_strMidYCheckbox;
    private javax.swing.JButton m_strRemoveColorButton;
    private javax.swing.JCheckBox m_strRightCheckbox;
    private javax.swing.JCheckBox m_strTopCheckbox;
    private javax.swing.JCheckBox m_strTruncateCheckbox;
    private javax.swing.JCheckBox m_strUnderlineCheckbox;
    private javax.swing.JTextField m_strWidthTextField;
    private javax.swing.JPanel m_stringPanel;
    private javax.swing.JButton m_tiledAddImageButton;
    private javax.swing.JButton m_tiledApplyButton;
    private javax.swing.JList m_tiledCreatedImgList;
    private javax.swing.JButton m_tiledDelImageButton;
    private javax.swing.JButton m_tiledEditButton;
    private javax.swing.JTextField m_tiledHeightTextField;
    private javax.swing.JList m_tiledImageList;
    private javax.swing.JPanel m_tiledImagePanel;
    private javax.swing.JPanel m_tiledInfoPanel;
    private javax.swing.JCheckBox m_tiledNoImageCheckbox;
    private javax.swing.JCheckBox m_tiledNoSpriteCheckbox;
    private javax.swing.JPanel m_tiledPanel;
    private javax.swing.JCheckBox m_tiledRepeatedViewCheckBox;
    private javax.swing.JPanel m_tiledSpritePanel;
    private javax.swing.JTextField m_tiledStepXTextField;
    private javax.swing.JTextField m_tiledStepYTextField;
    private javax.swing.JTabbedPane m_tiledTabbedPane;
    private javax.swing.JTextField m_tiledViewHeightTextField;
    private javax.swing.JTextField m_tiledViewSpdXTextField;
    private javax.swing.JTextField m_tiledViewSpdYTextField;
    private javax.swing.JTextField m_tiledViewWidthTextField;
    private javax.swing.JTextField m_tiledViewXTextField;
    private javax.swing.JTextField m_tiledViewYTextField;
    private javax.swing.JTextField m_tiledWidthTextField;
    private javax.swing.JPanel m_unknownPanel;
    // End of variables declaration//GEN-END:variables
}
