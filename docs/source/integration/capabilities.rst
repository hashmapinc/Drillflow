#######################
How to Set Capabilities
#######################

This guide documents how to set the server capabilites that the server reports as SUPPORTED by the WMLS_GetCap API.

*******
In Code
*******

The WITSML version can be set in code by modifying the servercap.properties file.

``wmls.contactName``: The name of the person responsible for the server installation
``wmls.contactEmail``: The name of the person responsible for the server installation
``wmls.contactPhone``: The phone of the person responsible for the server installation

``wmls.changeDetectionPeriod``: The total amount of time elapsed for a change to be detected (required in 1.4.1.1)
``wmls.description``: The description of the server
``wmls.name``: The name of the server
``wmls.supportUomConversion``: 1.4.1.1 only, whether or not the server supports unit of measure conversions
``wmls.compressionMethod``: 1.4.1.1 only, the compression method used by the server
``wmls.cascadedDelete``: 1.4.1.1 only, whether cascaded deletes are supported