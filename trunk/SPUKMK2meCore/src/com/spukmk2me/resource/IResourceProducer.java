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

import java.io.DataInputStream;
import java.io.IOException;

/**
 *  Produce the resource from its creation data.
 *  \details Any producer must support default resource types.
 */
public abstract class IResourceProducer
{
    /**
     *   Load construction data from an input stream.
     *   @param is Input stream. DataInputStream is used here for convenient.
     *   @param resourcetype The resource type.
     *   @return Construction data or null if it's not supported.
     *   @throws IOException If IO error occurs.
     */
    public final IResourceConstructionData LoadConstructionData(
        DataInputStream is ) throws IOException
    {
        byte    typeID = (byte)is.read();
        int     resourceCreationDataSize = 0;

        // resourceCreationDataSize = is.readUnsignedShort();
        resourceCreationDataSize |= is.read() << 8;
        resourceCreationDataSize |= (is.read() & 0x000000FF);
        
        if ( !IsSupported( typeID ) )
        {
            is.skip( resourceCreationDataSize );
            return null;
        }
        
        return LoadTypeBasedConstructionData( is, typeID );
    }
    
    public abstract boolean IsSupported( byte resourceType );
    
    /**
     *   Create a resource from its creation data.
     *   @param data Construction data.
     *   @return The resource created. Null if resource type is not supported.
     *   @throws Exception If creating process has exception.
     */
    public abstract IResource CreateResource( IResourceConstructionData data )
        throws IOException;
    
    /**
     *   Load construction data from an input stream.
     *   \details This function shouldn't be called manually.
     *   @param is Input stream. DataInputStream is used here for convenient.
     *   @param resourcetype The resource type.
     *   @return Construction data or null if it's not supported.
     *   @throws IOException If IO error occurs.
     */
    public abstract IResourceConstructionData LoadTypeBasedConstructionData(
        DataInputStream is, int resourceType ) throws IOException;
}
