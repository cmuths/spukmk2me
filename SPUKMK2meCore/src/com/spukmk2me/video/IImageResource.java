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

package com.spukmk2me.video;

/* $if SPUKMK2ME_DEBUG && SPUKMK2ME_SCENESAVER$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */

/**
 *  Presents image resource. Simply put, an instance of this class presents
 * an image that is loaded from somewhere.
 *  \details This class is implemented in video driver side.
 */
public abstract class IImageResource implements IResource
{
    public abstract short GetWidth();
    public abstract short GetHeight();

    /* $if SPUKMK2ME_SCENESAVER$ */
    public final void SetCreationData( IResourceCreationData creationData )
    {
        if ( !(creationData instanceof ImageResourceCreationData) )
        {
            Logger.Log(
                "ERROR: This isn't creation data for IImageResource." );
        }
        
        m_creationData = creationData;
    }
    
    public final IResourceCreationData GetCreationData()
    {
        return m_creationData;
    }
    
    private IResourceCreationData m_creationData;
    
    public final class ImageResourceCreationData
        extends IResourceCreationData
    {
        public ImageResourceCreationData() {}

        public String c_path;
    }
	/* $endif$ */
}
