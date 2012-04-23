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

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class IResourceExporter
{
    public final void SaveResourceConstructionData(
        DataOutputStream os, IResourceConstructionData data )
        throws IOException
    {
        byte type = data.GetAssociatedResourceType();
        
        os.write( type );
        
        if ( !IsSupported( type ) )
            os.writeShort( 0 );
        else
        {
            os.writeShort( GetWrittenDataSize( data ) );
            SaveTypeBasedConstructionData( os, data );
        }
    }
    
    public abstract boolean IsSupported( byte resourceType );
    
    protected abstract int GetWrittenDataSize(
        IResourceConstructionData data );
    
    /**
     *   Write the construction data.
     *   \details This function won't do anything if the type is not supported.
     *   @param os Output stream. DataOutputStream is used for convenient.
     *   @param data Data to be written.
     *   @throws IOException If IO error occurs.
     */
    protected abstract void SaveTypeBasedConstructionData(
        DataOutputStream os, IResourceConstructionData data )
        throws IOException;
}
