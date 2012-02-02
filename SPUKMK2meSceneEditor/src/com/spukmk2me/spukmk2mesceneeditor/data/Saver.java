package com.spukmk2me.spukmk2mesceneeditor.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.File;

import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.video.ISubImage;
import com.spukmk2me.scene.ISceneNode;
import com.spukmk2me.scene.NullSceneNode;
import com.spukmk2me.scene.ImageSceneNode;
import com.spukmk2me.scene.SpriteSceneNode;
import com.spukmk2me.scene.StringSceneNode;
import com.spukmk2me.optional.scene.ResourceManager;
import com.spukmk2me.scene.TiledLayerSceneNode;

/**
 *  Place which holds save functions.
 */
public final class Saver
{
    private Saver()
    {
    }

    public static void Save( ResourceManager resourceManager,
        ISceneNode rootNode, OutputStream os, String savePath )
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream( os );

        char[] header = HEADER_MARK.toCharArray();

        for ( int i = 0; i != header.length; ++i )
            dos.writeByte( header[ i ] );

        resourceManager.Save( os, savePath, File.separatorChar );
        SaveTreeStruct( rootNode, resourceManager, dos );
    }

    private static void SaveTreeStruct( ISceneNode rootNode,
        ResourceManager resourceManager, DataOutputStream dos )
        throws IOException
    {
        int numberOfNodes   = 1;
        int treeHeight      = 1;
        int nExportedNodes  = 0;

        ///////////////////////////////
        // Find out number of nodes,
        // tree height, exported nodes
        {
            DoublyLinkedList mainList   = new DoublyLinkedList();
            DoublyLinkedList subList    = new DoublyLinkedList();
            DoublyLinkedList swapList;
            DoublyLinkedList.Iterator   i, end;
            ISceneNode                  child, childStop;
            
            mainList.push_back( rootNode );

            while ( mainList.length() != 0 )
            {
                end = mainList.end();

                for ( i = mainList.first(); !i.equals( end ); i.fwrd() )
                {
                    if ( ((ISceneNode)i.data()).c_exportFlag )
                        ++nExportedNodes;

                    childStop = ((ISceneNode)i.data()).c_children;

                    if ( childStop != null )
                    {
                        for ( child = childStop.c_next; child != childStop; )
                        {
                            subList.push_back( child );
                            child = child.c_next;
                            ++numberOfNodes;
                        }
                    }
                }

                swapList    = mainList;
                mainList    = subList;
                subList     = swapList;
                subList.clear();
                ++treeHeight;
            }
            
            --nExportedNodes; // Root node isn't counted.
        }

        byte[] traversalData;
        
        {
            int nBits = (numberOfNodes - 1 << 1) + 1;
            int traversalDataLength;

            if ( (nBits & 0x00000007) == 0 )
                traversalDataLength = nBits >> 3;
            else
                traversalDataLength = (nBits >> 3) + 1;

            //traversalDataLength <<= 1;
            //traversalDataLength += 1;
            traversalData = new byte[ traversalDataLength ];
        }

        ///////////////////////////////
        // Create traversal data,
        // exported node indexes and
        // exported proxy name

        DoublyLinkedList    fetchedList = new DoublyLinkedList();
        String[]            exportedNodeNames = new String[ nExportedNodes ];
        int[]               exportedNodeIndexes = new int[ nExportedNodes ];

        if ( numberOfNodes != 1 ) // The tree has other nodes except root node
        {
            ISceneNode[]    stack = new ISceneNode[ treeHeight ];
            ISceneNode      currentNode;
            ISceneNode[]    nextChildren = new ISceneNode[ treeHeight ];

            int     topStack = 0, traversalIndex = 0;
            int     currentExportIndex = 0, currentNodeIndex = 0;
            byte    currentTraversalByte = 0, bitCount = 8;

            stack[ 0 ] = rootNode;
            
            if ( rootNode.c_children != null )
                nextChildren[ 0 ] = rootNode.c_children.c_next;
            else
                nextChildren[ 0 ] = null;

            while ( topStack >= 0 )
            {
                currentNode = stack[ topStack ];

                if ( nextChildren[ topStack ] == currentNode.c_children )
                {
                    nextChildren[ topStack ]    = null;
                    stack[ topStack-- ]         = null;
                    currentTraversalByte      <<= 1;
                }
                else
                {
                    stack[ topStack + 1 ]       = nextChildren[ topStack ];
                    nextChildren[ topStack ]    =
                        nextChildren[ topStack ].c_next;
                    ++topStack;
                    fetchedList.push_back( stack[ topStack ] );
                    
                    if ( stack[ topStack ].c_children != null )
                    {
                        nextChildren[ topStack ] =
                            stack[ topStack ].c_children.c_next;
                    }
                    else
                        nextChildren[ topStack ] = null;

                    currentTraversalByte <<= 1;
                    currentTraversalByte |= 0x01;

                    if ( stack[ topStack ].c_exportFlag )
                    {
                        exportedNodeIndexes[ currentExportIndex ] =
                            currentNodeIndex;
                        exportedNodeNames[ currentExportIndex ] =
                            stack[ topStack ].c_proxyName;
                        ++currentExportIndex;
                    }

                    ++currentNodeIndex;
                }

                if ( --bitCount == 0 )
                {
                    traversalData[ traversalIndex++ ] = currentTraversalByte;
                    currentTraversalByte = 0;
                    bitCount = 8;
                }
            }

            // Flush remaining data to traversalData
            if ( bitCount != 8 )
            {
                traversalData[ traversalIndex++ ] =
                    (byte)(currentTraversalByte << bitCount);
            }
        }

        ///////////////////////////////
        // Write to file
        {
            dos.writeInt( nExportedNodes );

            for ( int i = 0; i != nExportedNodes; ++i )
                dos.writeInt( exportedNodeIndexes[ i ] );

            for ( int i = 0; i != nExportedNodes; ++i )
                dos.writeUTF( exportedNodeNames[ i ] );

            dos.writeInt( numberOfNodes );
            
            if ( numberOfNodes != 1 )
            {
                dos.write( traversalData );
                dos.writeShort( treeHeight );

                DoublyLinkedList.Iterator i         = fetchedList.first();
                DoublyLinkedList.Iterator border    = fetchedList.end();

                for ( ; !i.equals( border ); i.fwrd() )
                {
                    WriteSceneNodeInfo( ((ISceneNode)i.data()),
                        resourceManager, dos );
                }

                /*TreeNode<SceneNodeWrapper>[] stack =
                    new TreeNode[ treeHeight ];
                TreeNode<SceneNodeWrapper> currentNode;
                int[] childrenIndex = new int[ treeHeight ];

                int     topStack = 0;

                stack[ 0 ]          = rootNode;
                childrenIndex[ 0 ]  = 0;

                while ( topStack >= 0 )
                {
                    currentNode = stack[ topStack ];

                    if ( childrenIndex[ topStack ] ==
                        currentNode.getChildren().length() )
                    {
                        stack[ topStack ] = null;
                        --topStack;
                    }
                    else
                    {
                        stack[ topStack + 1 ] =
                            (TreeNode<SceneNodeWrapper>)currentNode.
                            getChildren().get( childrenIndex[ topStack ] );
                        ++childrenIndex[ topStack ];
                        ++topStack;
                        childrenIndex[ topStack ] = 0;
                        WriteSceneNodeInfo( stack[ topStack ].data(),
                            resourceManager, dos );
                    }
                }*/
            }
        }
    }

    private static void WriteSceneNodeInfo( ISceneNode node,
        ResourceManager resourceManager, DataOutputStream dos )
        throws IOException
    {
        byte nodeType = (byte)NodeTypeChecker.GetNodeType( node );
        
        dos.writeByte( nodeType );

        switch ( nodeType )
        {
            case NodeTypeChecker.NT_NULL:
                NullSceneNode nullNode = (NullSceneNode)node;

                dos.writeShort( nullNode.c_x );
                dos.writeShort( nullNode.c_y );
                dos.writeByte( Misc.GetVisibleFlag( nullNode ) );
                break;

            case NodeTypeChecker.NT_IMAGE:
                ImageSceneNode imgNode = (ImageSceneNode)node;

                dos.writeShort( imgNode.c_x );
                dos.writeShort( imgNode.c_y );
                dos.writeShort( resourceManager.GetResourceIndex(
                    imgNode.GetImage(), ResourceManager.RT_IMAGE ) );
                dos.writeByte( Misc.GetVisibleFlag( imgNode ) );
                break;

            case NodeTypeChecker.NT_SPRITE:
                SpriteSceneNode spriteNode = (SpriteSceneNode)node;
                SpriteSceneNode.SpriteSceneNodeInfoData spriteInfo =
                    (SpriteSceneNode.SpriteSceneNodeInfoData)node.c_infoData;

                dos.writeInt( spriteInfo.c_mode );
                dos.writeInt( spriteInfo.c_msPerFrame );
                dos.writeShort( spriteNode.c_x );
                dos.writeShort( spriteNode.c_y );
                dos.writeByte( spriteInfo.c_nImages );

                {
                    ISubImage[] imageList = spriteInfo.c_images;

                    for ( int i = 0; i != spriteInfo.c_nImages; ++i )
                    {
                        dos.writeByte( resourceManager.GetResourceIndex(
                            imageList[ i ], ResourceManager.RT_IMAGE ) );
                    }
                }

                dos.writeByte( spriteInfo.c_firstIndex );
                dos.writeByte( spriteInfo.c_lastIndex );
                dos.writeByte( spriteInfo.c_nFrameToStop );
                dos.writeByte( Misc.GetVisibleFlag( spriteNode ) );

                break;

            case NodeTypeChecker.NT_STRING:
                StringSceneNode stringNode = (StringSceneNode)node;
                StringSceneNode.StringSceneNodeInfoData stringInfo =
                    (StringSceneNode.StringSceneNodeInfoData)node.c_infoData;
                
                int fontIndex;
                
                if ( stringInfo.c_font == null )
                    fontIndex = -1;
                else
                {
                    fontIndex = resourceManager.GetResourceIndex(
                        stringInfo.c_font, ResourceManager.RT_FONT );
                }

                dos.writeInt( stringInfo.c_alignment );
                dos.writeShort( stringNode.c_x );
                dos.writeShort( stringNode.c_y );
                dos.writeShort( stringInfo.c_width );
                dos.writeShort( stringInfo.c_height );
                dos.writeByte( fontIndex );
                dos.writeByte( stringInfo.c_nProperties );
                dos.write( stringInfo.c_properties );

                {
                    byte flags = Misc.GetVisibleFlag( stringNode );

                    if ( stringInfo.c_truncate )
                        flags |= 0x01;

                    dos.writeByte( flags );
                }

                dos.writeUTF( stringInfo.c_string );

                break;

            case NodeTypeChecker.NT_TILED:
                TiledLayerSceneNode tiledNode = (TiledLayerSceneNode)node;
                TiledLayerSceneNode.TiledLayerSceneNodeInfoData infoData =
                    (TiledLayerSceneNode.TiledLayerSceneNodeInfoData)node.c_infoData;
                byte flags = Misc.GetVisibleFlag( tiledNode );
                
                if ( infoData.c_repeatedView )
                    flags |= 0x20;

                dos.writeShort( tiledNode.c_x );
                dos.writeShort( tiledNode.c_y );
                dos.writeShort( infoData.c_tableWidth );
                dos.writeShort( infoData.c_tableHeight );
                dos.writeShort( infoData.c_stepX );
                dos.writeShort( infoData.c_stepY );
                dos.writeShort( infoData.c_viewWidth );
                dos.writeShort( infoData.c_viewHeight );
                dos.writeShort( infoData.c_viewX );
                dos.writeShort( infoData.c_viewY );
                dos.writeByte( flags );

                // Terrain data
                if ( infoData.c_terrainData != null )
                    dos.write( infoData.c_terrainData );

                // Images
                if ( infoData.c_images == null )
                    dos.writeShort( 0 );
                else
                {
                    dos.writeShort( infoData.c_images.length );

                    for ( int i = 0; i != infoData.c_images.length; ++i )
                    {
                        dos.writeShort( resourceManager.GetResourceIndex(
                            infoData.c_images[ i ],
                            ResourceManager.RT_IMAGE ) );
                    }
                }

                // Sprites
                if ( infoData.c_sprites == null )
                    dos.writeShort( 0 );
                else
                {
                    dos.writeShort( infoData.c_sprites.length );

                    for ( int i = 0; i != infoData.c_sprites.length; ++i )
                    {
                        ISubImage[] sprite = infoData.c_sprites[ i ];

                        dos.writeShort( sprite.length );

                        for ( int j = 0; j != sprite.length; ++j )
                        {
                            dos.writeShort( resourceManager.GetResourceIndex(
                                sprite[ j ],
                                ResourceManager.RT_IMAGE) );
                        }

                        dos.writeInt( infoData.c_spriteSpeed[ i ] );
                    }
                }

                break;
        }
    }

    private static String HEADER_MARK = "SPUKMK2me_SCENE-FILE_0.1";
}
