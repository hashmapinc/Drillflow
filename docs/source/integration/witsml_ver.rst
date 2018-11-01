##########################
Setting the WITSML Version
##########################

This guide documents how to set the WITSML version that the server reports as SUPPORTED by the WMLS_GetVersion API.

*******
In Code
*******

The WITSML version can be set in code by modifying the application.properties file. Find the line called:

``wmls.version``

This allows you to set the property at design time by just adding

``=1.3.1.1``

To indicate that your implementation only support 1.3.1.1.

The response to WMLS_GetVersion will return whatever is put as the value to wmls.version.

*********************
While running the JAR
*********************

The version can be set at runtime by running the server jar with the following argument:

``--wmls.version=<supported versions(s)>``

The complete command would look like the following:

``java -jar server-0.0.1-SNAPSHOT.jar --wmls.version=1.3.1.1,1.4.1.1``

This will start the server and respond with **1.3.1.1,1.4.1.1** to the WMLS_GetVersion request.
