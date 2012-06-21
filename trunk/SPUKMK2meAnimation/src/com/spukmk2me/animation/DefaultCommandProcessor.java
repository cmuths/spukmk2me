package com.spukmk2me.animation;

import com.spukmk2me.scene.ISceneNode;
/* $if DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */


public final class DefaultCommandProcessor implements ICommandProcessor
{
    public DefaultCommandProcessor() {}
    
    public void Prepare( Animation animation, boolean revert )
    {
        m_animation     = animation;
        m_animReversed  = revert;
    }
    
    public boolean IsSupported( int commandCode )
    {
        return  ( commandCode == Command.CMDCODE_REPOS ) ||
                ( commandCode == Command.CMDCODE_VISIBLE ) ||
                ( commandCode == Command.CMDCODE_ASSIGN ) ||
                ( commandCode == Command.CMDCODE_DELAY ) ||
                ( commandCode == Command.CMDCODE_DECLARE ) ||
                ( commandCode == Command.CMDCODE_ASSEMBLE ) ||
                ( commandCode == Command.CMDCODE_SHIFTEDRANGE ) |
                ( commandCode == Command.CMDCODE_OBJECTREPOS );
    }

    public int Process( Command command )
    {
        int addedTime = 0;
        
        switch ( command.GetCommandCode() )
        {
            case Command.CMDCODE_REPOS:
                {
                    RepositionCommand reposCmd = (RepositionCommand)command;
                    ISceneNode node = m_animation.GetNode( reposCmd.c_name );
                    NodePositionInfo info = (NodePositionInfo)m_animation.
                        GetOriginalPositionList().get( reposCmd.c_name );
                    
                    if ( info != null ) // Node was reversed
                    {
                        short newX  = (short)(node.c_x + info.c_x - reposCmd.c_x );
                        
                        node.SetPosition( newX, reposCmd.c_y );
                        info.c_x = reposCmd.c_x;
                    }
                    else
                        node.SetPosition( reposCmd.c_x, reposCmd.c_y );
                }
                
                break;

            case Command.CMDCODE_VISIBLE:
                {
                    VisiblilityCommand visCmd = (VisiblilityCommand)command;
                    ISceneNode node = m_animation.GetNode( visCmd.c_name );

                    node.c_visible  = (visCmd.c_flags & 0x01) != 0;
                    node.c_enable   = (visCmd.c_flags & 0x02) != 0;
                }
                
                break;

            case Command.CMDCODE_ASSIGN:
                {
                    AssignCommand assignCmd = (AssignCommand)command;
                    ISceneNode node = m_animation.GetNode( assignCmd.c_name );
                    
                    node.Drop();

                    if ( !assignCmd.c_address.equals( AssignCommand.NULL_ADDRESS ) )
                    {
                        m_animation.GetNode( assignCmd.c_address ).
                            AddChild( node );
                    }
                }
                
                break;

            case Command.CMDCODE_DELAY:
                addedTime = ((DelayCommand)command).c_time;
                
                break;
                
            case Command.CMDCODE_DECLARE:
                m_animation.AddAnimationObject(
                    ((ObjectDeclarationCommand)command).c_name );
                
                break;
                
            case Command.CMDCODE_ASSEMBLE:
                {
                    ObjectAssembleCommand assembleCmd =
                        (ObjectAssembleCommand)command;
                    AnimationObject obj = m_animation.GetAnimationObject(
                        assembleCmd.c_objectName );
                    
                    if ( assembleCmd.c_assembleType == 0 )
                    {
                        ObjectNodeInfo info = new ObjectNodeInfo();
                        boolean materialReversed = m_animation.
                            GetOriginalPositionList().exist(
                                assembleCmd.c_nodeName ) != -1;
                        
                        info.c_node = m_animation.GetNode( assembleCmd.c_nodeName );
                        info.c_dependantFlags = assembleCmd.c_dependantFlags;
                        
                        if ( materialReversed )
                        {
                            long hbb = info.c_node.GetHierarchicalBoundingRect();
                            short nx = (short)((hbb >> 48) & 0x0000FFFF);
                            short nw = (short)((hbb >> 16) & 0x0000FFFF);
                            
                            info.c_shiftX = (short)(0 - nw - (nx << 1));
                        }
                        
                        obj.AttachNode( info, assembleCmd.c_nodeName );
                    }
                    else
                        obj.DetachNode( assembleCmd.c_nodeName );
                }
                
                break;
                
            case Command.CMDCODE_SHIFTEDRANGE:
                {
                    ShiftedRangeCommand shiftCmd =
                        (ShiftedRangeCommand)command;
                    AnimationObject obj = m_animation.GetAnimationObject(
                        shiftCmd.c_objectName );
                    ObjectNodeInfo nodeInfo =
                        obj.GetNodeInfo( shiftCmd.c_nodeName );
                    boolean materialReversed =
                        m_animation.GetOriginalPositionList().exist( shiftCmd.c_nodeName ) != -1;
                    
                    /* $if DEBUG$ */
                    Logger.Log( "Shift: " + shiftCmd.c_objectName + ' ' + shiftCmd.c_nodeName + '\n' );
                    Logger.Log( "Base: " + shiftCmd.c_shiftX + ' ' + shiftCmd.c_shiftY + ' ' + shiftCmd.c_shiftZ + '\n' );
                    /* $endif$ */
                    
                    if ( materialReversed )
                    {
                        /* $if DEBUG$ */
                        Logger.Log( "Reversal detected. \n" );
                        /* $endif$ */
                        
                        nodeInfo.c_shiftX += nodeInfo.c_originalShiftX - shiftCmd.c_shiftX; 
                        nodeInfo.c_originalShiftX = shiftCmd.c_shiftX;
                    }
                    else
                        nodeInfo.c_shiftX = shiftCmd.c_shiftX;

                    nodeInfo.c_shiftY = shiftCmd.c_shiftY;
                    nodeInfo.c_shiftZ = shiftCmd.c_shiftZ;
                    obj.RealignNodes();
                    
                    /* $if DEBUG$ */
                    Logger.Log( "Shifted range: " + nodeInfo.c_shiftX + ' ' +
                        nodeInfo.c_shiftY + ' ' + nodeInfo.c_shiftZ +'\n' );
                    /* $endif$ */
                }
                
                break;
                
            case Command.CMDCODE_OBJECTREPOS:
            {
                ObjectRepositionCommand objReposCmd =
                    (ObjectRepositionCommand)command;
                AnimationObject obj = m_animation.GetAnimationObject(
                    objReposCmd.c_name );
                
                if ( m_animReversed )
                    objReposCmd.c_x = (short)-objReposCmd.c_x;
 
                obj.c_x = objReposCmd.c_x;
                obj.c_y = objReposCmd.c_y;
                obj.c_z = objReposCmd.c_z;
                obj.RealignNodes();
            }
            
            break;
        }
        
        return addedTime;
    }
    
    public void Execute( int deltaTime )
    {
        m_animation.RealignAnimationObjects();
    }
    
    private Animation   m_animation;
    private boolean     m_animReversed;
}
