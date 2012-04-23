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

package com.spukmk2me.resource;

/**
 *  Interface for resource.
 */
public abstract class IResource
{
    protected IResource( String proxyname )
    {
        m_proxyname = proxyname;
    }
    
    public final String GetProxyName()
    {
        return m_proxyname;
    }
    
    /**
     *   Get the resource type.
     *   \details Different resource types must return different values.
     *  See constants below for default resource types.
     *   @return A number indicates the type of this resource.
     */
    public abstract byte GetResourceType();
    /* $if SPUKMK2ME_SCENESAVER$ */
    public abstract void SetConstructionData( IResourceConstructionData data );
    public abstract IResourceConstructionData GetConstructionData();
    /* $endif$ */
    
    // Below is default resource types
    public static final byte RT_INVALIDRESOURCE = -1;
    public static final byte RT_IMAGERESOURCE   = 1;
    public static final byte RT_IMAGE           = 2;
    public static final byte RT_BITMAPFONT      = 3;
    public static final byte RT_SOUND           = 4;
    
    protected String m_proxyname;
}
