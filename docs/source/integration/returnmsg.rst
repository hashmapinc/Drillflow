#############################################
How to Define Return Messages from GetBaseMsg
#############################################

This guide documents how to add return messages from GetBaseMsg

*******
In Code
*******

The WITSML version can be set in code by modifying the basemessages.properties file

The file is a list of messages with the structure of:

``basemessage.<returncode>=<message>``

``<returncode>`` is the short returned from the operation for which the message is assigned
``<message>`` is the string message that should be returned for the corresponding short.