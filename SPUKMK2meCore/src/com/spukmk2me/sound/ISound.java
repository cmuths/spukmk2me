/*
 *  SPUKMK2me - SPUKMK2 Engine for J2ME platform
 *  Copyright 2010 - 2011  HNYD Team
 *
 *   SPUKMK2me is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *   SPUKMK2me is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with SPUKMK2me.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spukmk2me.sound;

import com.spukmk2me.debug.Logger;
import com.spukmk2me.resource.IResource;
import com.spukmk2me.resource.IResourceConstructionData;

public abstract class ISound extends IResource 
{
    protected ISound( String proxyname )
    {
        super( proxyname );
    }
    
    public final byte GetResourceType()
    {
        return IResource.RT_SOUND;
    }
    
    /* $if SPUKMK2ME_SCENESAVER$ */
    public final void SetConstructionData( IResourceConstructionData creationData )
    {
        if ( !(creationData instanceof SoundConstructionData) )
        {
            Logger.Log(
                "ERROR: This isn't creation data for ISoundResource." );
        }
        
        m_creationData = creationData;
    }
    
    public final IResourceConstructionData GetConstructionData()
    {
        return m_creationData;
    }
    /* $endif$ */
    
    /**
     *   Set the number of loops.
     *   @param numberOfLoops Number of loops, -1 for infinite loops.
     *  If this parameter gets a negative value other than -1, the operation
     *  will be ignored.
     */
    public abstract void SetNumberOfLoops( int numberOfLoops );
    
    /**
     *   Get the number of loops.
     *   @return Number of loops was set. If the number of loops is infinite,
     *  returns -1. Default is 1.
     */
    public abstract int GetNumberOfLoops();
    
    /**
     *   Set the volume for this sound object.
     *   @param volume 16.16 fixed point, this number is a volume multiplier.
     *  The default number is 0x00010000 (1.0).
     */
    public abstract void SetVolume( int volume );
    
    /**
     *   Get the current volume of this sound object.
     *   @return Current volume of this sound.
     */
    public abstract int GetVolume();
    
    /**
     *   Get tbe current status of sound object.
     *   @return Current state.
     */
    public abstract byte GetState();
    
    /**
     *  Do some actions with the specified slot.
     *  \details Read the constants for more information. This function musn't
     * produce any garbage if the slot isn't associated with any sound.
     *  @param action The sound action.
     *  @return Success code. 0 is success, negative values indicate failure.
     */
    public abstract int Action( byte action );
    
    public static final byte ACTION_NONE    = -1;
    public static final byte ACTION_CACHE   = 0;
    public static final byte ACTION_PLAY    = 1;
    public static final byte ACTION_PAUSE   = 2;
    public static final byte ACTION_RELEASE = 3;
    
    public static final byte STATE_ASSOCIATED           = 1;
    public static final byte STATE_STARTED_CACHING      = 2;
    public static final byte STATE_CACHED               = 3;
    public static final byte STATE_STARTED_PLAYING      = 4;
    public static final byte STATE_PLAYING              = 5;
    public static final byte STATE_STARTED_PAUSING      = 6;
    public static final byte STATE_PAUSED               = 7;
    public static final byte STATE_STARTED_RELEASING    = 8;
    public static final byte STATE_RELEASED             = 9;
    public static final byte STATE_PAUSED_BY_ACCIDENT   = 10;
    public static final byte STATE_UNDEFINED            = -128;
    
    /* $if SPUKMK2ME_SCENESAVER$ */
    private IResourceConstructionData m_creationData;
    /* $endif$ */
}
