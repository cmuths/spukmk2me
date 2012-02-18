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
 *  Hold data for sub image, which will be visible to ImageSceneNode.
 *  \details This class is implemented in video driver side.
 */
public abstract class ISubImage implements IResource
{
    public abstract void Render( IVideoDriver driver );
    public abstract short GetWidth();
    public abstract short GetHeight();

    /* $if SPUKMK2ME_SCENESAVER$ */
    public final void SetCreationData(
        IResourceCreationData creationData )
    {
    	/* $if SPUKMK2ME_DEBUG$ */
        if ( !(creationData instanceof SubImageCreationData) )
        {
            Logger.Log( "This isn't creation data for ISubImage" );
        }
        /* $endif$ */

        m_creationData = creationData;
    }

    public IResourceCreationData GetCreationData()
    {
        return m_creationData;
    }

    private IResourceCreationData m_creationData;

    // There is something improper here.
    // Because ISubImage is a resource which require IImageResource to be
    // initialised, so IImageResource must be loaded before.
    public final class SubImageCreationData
        extends IResourceCreationData
    {
        public SubImageCreationData() {}

        public IImageResource c_resource;
        public int      c_rotationDegree;
        public short    c_x, c_y, c_width, c_height;
        public byte     c_flippingFlags;
    }
	/* $endif$ */
}