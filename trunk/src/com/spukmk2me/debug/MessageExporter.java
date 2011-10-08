package com.spukmk2me.debug;

/**
 *  Interface for exporting debug messages
 */
public interface MessageExporter
{
    /**
     *  Initialise the exporter.
     *  \details This function must not be called manually.
     *  @return 0 if the exporter is initialised successfully. Other values
     * indicate there is a problem with initialisation.
     */
    public byte Initialise();

    /**
     *  Finialise the exporter.
     *  \details This function must release all occupied resources and finish
     * working. This function also must not be called manually.
     *  @return 0 if the exporter is closed successfully. Otherwise return
     * non-zero values.
     */
    public byte Finalise();

    /**
     *  Export a message.
     *  \details This function must work only between the calls to Initialise()
     * and Finialise().
     *  @param message The message to export.
     */
    public void ExportMessage( String message );
}
