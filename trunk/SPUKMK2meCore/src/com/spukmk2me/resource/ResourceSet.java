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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import com.spukmk2me.DoublyLinkedList;
import com.spukmk2me.NamedList;
import com.spukmk2me.resource.IResource;
import com.spukmk2me.resource.IResourceConstructionData;
/* $if SPUKMK2ME_DEBUG$ */
import com.spukmk2me.debug.Logger;
/* $endif$ */

public final class ResourceSet
{
    public ResourceSet()
    {
        m_resources = new Hashtable();
    }
    
    /**
     *   Add a new resource.
     *   \details The resource won't be added if there's
     *  another resource with the same name and the same type.
     *   @param resource Resource which is going to be added.
     *  null is not accepted.
     *   @return If the resource is added successfully.
     */
    public boolean AddResource( IResource resource )
    {
        int resType = resource.GetResourceType();
        
        /* $if SPUKMK2ME_DEBUG$ */
        /* $endif$ */
        CreateResourceListIfNeeded( resType );
        return GetResourceList( resType ).
            add( resource, resource.GetProxyName() );
    }
    
    /**
     *   Get the resource with corresponding name and type.
     *   @param name Proxy name of the resource.
     *   @param type Type of the resource.
     *   @return Pointer to the resource, or null if resource is not found.
     */
    public IResource GetResource( String name, int type )
    {
        NamedList list = GetResourceList( type );
        
        if ( list == null )
            return null;
        
        return (IResource)list.get( name );
    }
    
    /**
     *   Remove the resource.
     *   @param name Proxy name of the removed resource
     *   @param type Type of the resource
     *   @return True if the resource is in the set.
     */
    public boolean RemoveResource( String name, int type )
    {
        NamedList list = GetResourceList( type );
        
        if ( list == null )
            return false;
        
        return list.remove( name );
    }
    
    public void Clear()
    {
        m_resources.clear();
    }
    
    public int GetNumberOfResources( int type )
    {
        NamedList list = GetResourceList( type );
        
        if ( list == null )
            return 0;
        
        return list.length();
    }
    
    public IResource GetResource( int index, int type )
    {
        NamedList list = GetResourceList( type );
        
        if ( list == null )
            return null;
        
        if ( (index < 0) || (index >= list.length()) )
            return null;
        
        DoublyLinkedList.Iterator itr = list.getObjectIterator();
        
        for ( int i = index; i != 0; --i )
            itr.fwrd();
        
        return (IResource)itr.data();
    }
    
    public int GetResourceIndex( IResource resource )
    {
        if ( resource == null )
            return -1;
        
        NamedList list = GetResourceList( resource.GetResourceType() );
        
        if ( list != null )
        {   
            DoublyLinkedList.Iterator itr = list.getObjectIterator();
            
            for ( int i = 0; i != list.length(); ++i )
            {
                if ( itr.data() == resource )
                    return i;
                
                itr.fwrd();
            }
        }
        
        return -1;
    }
    
    /**
     *  Clear the resource lists and load new resources from input stream.
     *  This is the optimized version which will be expected to run on low-spec
     * mobile devices.
     */
    public void Load( InputStream is, IResourceProducer producer )
        throws IOException
    {
        Clear();

        DataInputStream dis = new DataInputStream( is );
        
        // IImageResource
        int n = dis.readInt();

        if ( n != 0 )
        {
            IResourceConstructionData constructionData;
            IResource resource;
            
            for ( int i = 0; i != n; ++i )
            {
                constructionData = producer.LoadConstructionData( dis );
                resource = producer.CreateResource( constructionData );
                /* $if SPUKMK2ME_SCENESAVER$ */
                resource.SetConstructionData( constructionData );
                /* $endif$ */
                AddResource( resource );
            }
        }

        // ISubImages
        n = dis.readInt();

        if ( n != 0 )
        {
            IResourceConstructionData constructionData;
            IResource resource;
            
            for ( int i = 0; i != n; ++i )
            {
                constructionData = producer.LoadConstructionData( dis );
                resource = producer.CreateResource( constructionData );
                /* $if SPUKMK2ME_SCENESAVER$ */
                resource.SetConstructionData( constructionData );
                /* $endif$ */
                AddResource( resource );
            }
        }

        // BitmapFont
        n = dis.readInt();
        
        if ( n != 0 )
        {
            IResourceConstructionData constructionData;
            IResource resource;
            
            for ( int i = 0; i != n; ++i )
            {
                constructionData = producer.LoadConstructionData( dis );
                resource = producer.CreateResource( constructionData );
                /* $if SPUKMK2ME_SCENESAVER$ */
                resource.SetConstructionData( constructionData );
                /* $endif$ */
                AddResource( resource );
            }
        }
    }
    
    /**
     *  Save resources to an output stream.
     *  @param os Output stream.
     *  @throws IOException If IO error occurs.
     */
    /* $if SPUKMK2ME_SCENESAVER$ */
    public void Save( OutputStream os, IResourceExporter exporter )
        throws IOException
    {
        DataOutputStream dos = new DataOutputStream( os );
        DoublyLinkedList.Iterator i;
        NamedList list;
        int length;

        // IImage resource
        list    = GetResourceList( IResource.RT_IMAGERESOURCE );
        
        if ( list != null )
        {
            i       = list.getObjectIterator();
            length  = list.length();
            dos.writeInt( length );
    
            while ( length-- != 0 )
            {
                exporter.SaveResourceConstructionData(
                    dos, ((IResource)i.data()).GetConstructionData() );
                i.fwrd();
            }
        }
        else
            dos.writeInt( 0 );

        // ISubImage
        list    = GetResourceList( IResource.RT_IMAGE );
        
        if ( list != null )
        {
            i       = list.getObjectIterator();
            length  = list.length();
            dos.writeInt( length );
    
            while ( length-- != 0 )
            {
                exporter.SaveResourceConstructionData(
                    dos, ((IResource)i.data()).GetConstructionData() );
                i.fwrd();
            }
        }
        else
            dos.writeInt( 0 );

        // Bitmap font
        list    = GetResourceList( IResource.RT_BITMAPFONT );
        
        if ( list != null )
        {
            i       = list.getObjectIterator();
            length  = list.length();
            dos.writeInt( length );
    
            while ( length-- != 0 )
            {
                exporter.SaveResourceConstructionData(
                    dos, ((IResource)i.data()).GetConstructionData() );
                i.fwrd();
            }
        }
        else
            dos.writeInt( 0 );
    }
    /* $endif$ */
    
    private void CreateResourceListIfNeeded( int resourceType )
    {
        Integer key = new Integer( resourceType );
        
        if ( !m_resources.containsKey( key ) )
            m_resources.put( key, new NamedList() );
    }
    
    private NamedList GetResourceList( int resourceType )
    {
        Integer key = new Integer( resourceType );
        
        return (NamedList)m_resources.get( key );
    }
    
    private Hashtable m_resources;
}
