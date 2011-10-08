package com.spukmk2me.debug;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.BluetoothStateException;

import com.spukmk2me.Util;

public class BluetoothMessageExporter implements MessageExporter,
    DiscoveryListener
{
    public BluetoothMessageExporter()
    {
    }

    public byte Initialise()
    {
        // Phase 1: retrieve the client's address
        InputStream is = this.getClass().getResourceAsStream( CONFIG_FILE );
        byte[] buffer = new byte[ 255 ];

        try
        {
            m_clientBluetoothAddr = Util.ReadUTF8Line( is, buffer );
            is.close();
        } catch ( IOException e ) {
            System.out.println( "Bluetooth logger: Cannot read config file." );
            return -1;
        }

        buffer = null;

        // Phase 2: detect the client
        try
        {
            LocalDevice bluetoothDevice = LocalDevice.getLocalDevice();
            DiscoveryAgent agent        = bluetoothDevice.getDiscoveryAgent();

            m_inquiryCompleted = m_inquirySuccess = false;
            agent.startInquiry( DiscoveryAgent.GIAC, this );
        } catch ( BluetoothStateException e ) {
            System.out.println(
                "Problem when initialising bluetooth device." );
            return -2;
        }

        // Open an output stream
        ServiceRecord serviceRecord     = null;
        String clientConnectionString   = serviceRecord.getConnectionURL(
            ServiceRecord.AUTHENTICATE_ENCRYPT, false );



        return 0;
    }

    public byte Finalise()
    {
        if ( m_outputStream != null )
        {
            try
            {
                m_outputStream.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public void ExportMessage( String message )
    {
        try
        {
            m_outputStream.write( message.getBytes() );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void deviceDiscovered( RemoteDevice remoteDevice,
        DeviceClass deviceClass )
    {
        if ( remoteDevice.getBluetoothAddress().
            equals( m_clientBluetoothAddr ) )
        {
            // Need some checking here
            m_clientDevice = remoteDevice;
        }
    }

    public void inquiryCompleted( int discType )
    {
        m_inquirySuccess    = discType == DiscoveryListener.INQUIRY_COMPLETED;
        m_inquiryCompleted  = true;
    }

    public void serviceSearchCompleted( int i, int i1 )
    {
    }

    public void servicesDiscovered( int i, ServiceRecord[] srs )
    {
    }

    private static String CONFIG_FILE = "bluetoothlog.cfg";

    private RemoteDevice    m_clientDevice;
    private String          m_clientBluetoothAddr;
    private OutputStream    m_outputStream;
    private boolean         m_inquiryCompleted, m_inquirySuccess;
}
