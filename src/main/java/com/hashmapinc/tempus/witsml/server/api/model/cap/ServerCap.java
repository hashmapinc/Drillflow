/**
 * Copyright © 2018-2018 Hashmap, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hashmapinc.tempus.witsml.server.api.model.cap;

import com.hashmapinc.tempus.witsml.server.api.model.cap.v1311.*;
import com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.GrowingTimeoutPeriod;
import com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.ObjectWithConstraint;

import javax.xml.bind.*;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServerCap {

    // Contact information
    private String contactName;
    private String contactEmail;
    private String contactPhone;

    // Server information
    private String description;
    private String serverName;
    private String vendor;

    // Functions and details
    private int changeDetectionPeriod;
    private Map<String, Integer> growingTimeouts = new HashMap<>();
    private Map<String, List<DataObject>> functions = new HashMap<>();
    private boolean cascadedDelete;
    private boolean supportUomConversion;
    private String compressionMethod;

    /**
     * Add a Growing timeout period for a specific object (note: only applicable for 1.4.1.1)
     * @param objectName The name of the object for the timeout
     * @param timeout The timeout for the object
     */
    public void addGrowingTimeoutPeriod(String objectName, int timeout){
        growingTimeouts.put(objectName, timeout);
    }

    /**
     * Add a Growing timeout period for a specific object (note: only applicable for 1.4.1.1)
     * Note: If a timeout for the object exists, it will be removed, otherwise nothing will be done.
     * @param objectName The name of the object to remove the timeout for
     */
    public void removeGrowingTimeoutPeriod(String objectName){
        if (growingTimeouts.containsKey(objectName))
            growingTimeouts.remove(objectName);
    }

    /**
     * Returns the contact name (person) for the contact object of the Server Capabilities Object
     * @return the name of the contact
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Sets the contact name (person) for the contact object of the Server Capabilities Object
     * @param contactName the name of the contact person
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * Returns the contact email for the contact object of the Server Capabilities Object
     * @return the email of the contact
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Sets the contact email for the contact object of the Server Capabilities Object
     * @param contactEmail the email of the contact person
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    /**
     * Returns the contact phone for the contact object of the Server Capabilities Object
     * @return the phone of the contact
     */
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * Sets the contact phone for the contact object of the Server Capabilities Object
     * @param contactPhone the phone of the contact person
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    /**
     * Returns description of the server
     * @return the description of the server
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns vendor that provides the server implementation
     * @return the vendor of the server
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Sets the vendor that provides the server implementation
     * @param vendor the vendor of the server
     */
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    /**
     * Returns change detection period of the server (only applicable for 1.4.1.1)
     * @return the change detection period of the server
     */
    public int getChangeDetectionPeriod() {
        return changeDetectionPeriod;
    }

    /**
     * Sets the change detection period of the server (only applicable for 1.4.1.1)
     * @param changeDetectionPeriod the change detection period of the server
     */
    public void setChangeDetectionPeriod(int changeDetectionPeriod) {
        this.changeDetectionPeriod = changeDetectionPeriod;
    }

    /**
     * Returns whether or not this server supports cascading deletes (only applicable for 1.4.1.1)
     * @return Whether or not the server supports cascading deletes
     */
    public boolean isCascadedDelete() {
        return cascadedDelete;
    }

    /**
     * Sets whether or not this server supports cascading deletes (only applicable for 1.4.1.1)
     * @param cascadedDelete Whether or not the server supports cascading deletes (true yes, false no)
     */
    public void setCascadedDelete(boolean cascadedDelete) {
        this.cascadedDelete = cascadedDelete;
    }

    /**
     * Returns whether or not this server supports uom conversion (only applicable for 1.4.1.1)
     * @return Whether or not the server supports uom conversion
     */
    public boolean isSupportUomConversion() {
        return supportUomConversion;
    }

    /**
     * Sets whether or not this server supports uom conversion (only applicable for 1.4.1.1)
     * @param supportUomConversion Whether or not the server supports uom conversion (true yes, false no)
     */
    public void setSupportUomConversion(boolean supportUomConversion) {
        this.supportUomConversion = supportUomConversion;
    }

    /**
     * Returns the compression method used by the server (only applicable for 1.4.1.1)
     * @return the compression method used by the server
     */
    public String getCompressionMethod() {
        return compressionMethod;
    }

    /**
     * Sets the compression method used by the server (only applicable for 1.4.1.1)
     * @param compressionMethod the compression method used by the server
     */
    public void setCompressionMethod(String compressionMethod) {
        this.compressionMethod = compressionMethod;
    }

    /**
     * Returns the name of the server
     * @return the name of the server
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Sets the name of the server
     * @param serverName the name of the server
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Adds a function to the server capabilities
     * @param functionName The name of the method (for example: "WMLS_GetFromStore")
     * @param objects A List of Data Objects that are supported for the function
     */
    public void addFunction(String functionName, List<DataObject> objects){
        functions.put(functionName, objects);
    }

    /**
     * Removes a function from the capabilities. If the function exists it will be removed, otherwise nothing will
     * happen
     * @param functionName The name of the function to remove
     */
    public void removeFunction(String functionName) {
        functions.remove(functionName);
    }

    /**
     * Returns the WITSML object in the version specified
     * @param version The version of the XML that should be returned
     * @return The XML as a string
     * @throws Exception If the version is not supported
     */
    public String getWitsmlObject(String version) throws JAXBException, UnsupportedOperationException {
        if (!"1.3.1.1".equals(version) && !"1.4.1.1".equals(version)){
            throw new UnsupportedOperationException("Version not supported by the capabilites object");
        }
        if ("1.3.1.1".equals(version)){
            return get1311Object();
        }
        else {
            return get1411Object();
        }
    }

    /**
     * Creates the 1.3.1.1 XML version of the capabilities object.
     * @return A string representing the 1.3.1.1 XML version of the capabilities object.
     * @throws JAXBException An error occurred in marshalling the object to XML
     */
    private String get1311Object() throws JAXBException {
        // Create root
        ObjCapServers servers = new ObjCapServers();
        servers.setVersion("1.3.1");

        // Create server
        ObjCapServer server = new ObjCapServer();
        server.setApiVers("1.3.1");

        // Create and Add Contact
        CsContact contact = new CsContact();
        contact.setName(contactName);
        contact.setEmail(contactEmail);
        contact.setPhone(contactPhone);
        server.setContact(contact);

        // Add server information
        server.setDescription(description);
        server.setName(serverName);
        server.setVendor(vendor);
        server.setSchemaVersion("1.3.1.1");

        // Create functions
        List<CsFunction> function = server.getFunction();
        Iterator<Map.Entry<String, List<DataObject>>> it = functions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<DataObject>> pair = it.next();
            CsFunction localFunction = new CsFunction();
            localFunction.setName(pair.getKey());

            // Add objects to function
            List<String> localDataObjects = localFunction.getDataObject();
            List<DataObject> dataObjects = pair.getValue();
            if (dataObjects != null) {
                for (DataObject dataObject : dataObjects) {
                    localDataObjects.add(dataObject.getName());
                }
            }

            // Add function to list
            function.add(localFunction);
            it.remove();
        }

        // Add server to plural element
        servers.setCapServer(server);

        // Marshal the object to XML
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjCapServers.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter writer = new StringWriter();

        // Create object factory and the root element
        ObjectFactory factory = new ObjectFactory();
        JAXBElement finalServers = factory.createCapServers(servers);

        // Create and return the XML string
        jaxbMarshaller.marshal(finalServers, writer);
        return writer.toString();
    }

    /**
     * Creates the 1.4.1.1 XML version of the capabilities object.
     * @return A string representing the 1.4.1.1 XML version of the capabilities object.
     * @throws JAXBException An error occurred in marshalling the object to XML
     */
    private String get1411Object() throws JAXBException {
        // Create root
        com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.ObjCapServers servers = new  com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.ObjCapServers();
        servers.setVersion("1.4.1");

        // Create server
        com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.ObjCapServer server = new com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.ObjCapServer();
        server.setApiVers("1.4.1");

        // Add Contact
        com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.CsContact contact = new com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.CsContact();
        contact.setName(contactName);
        contact.setEmail(contactEmail);
        contact.setPhone(contactPhone);
        server.setContact(contact);

        // Add Server information
        server.setDescription(description);
        server.setName(serverName);
        server.setVendor(vendor);
        server.setSchemaVersion("1.4.1.1");

        // Add Timeout Periods
        List<GrowingTimeoutPeriod> timeoutPeriods = server.getGrowingTimeoutPeriod();
        Iterator<Map.Entry<String, Integer>> timeoutIterator = growingTimeouts.entrySet().iterator();
        while (timeoutIterator.hasNext()) {
            Map.Entry<String, Integer> pair = timeoutIterator.next();
            GrowingTimeoutPeriod timeoutPeriod = new GrowingTimeoutPeriod();
            timeoutPeriod.setDataObject(pair.getKey());
            timeoutPeriod.setValue(pair.getValue());
            timeoutPeriods.add(timeoutPeriod);
            timeoutIterator.remove();
        }

        // Add Server functionality
        server.setCascadedDelete(cascadedDelete);
        server.setSupportUomConversion(supportUomConversion);
        server.setCompressionMethod(compressionMethod);

        // Add functions
        List<com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.CsFunction> function = server.getFunction();
        Iterator<Map.Entry<String, List<DataObject>>> it = functions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<DataObject>> pair = it.next();

            // Add Function Support
            com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.CsFunction localFunction = new com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.CsFunction();
            localFunction.setName(pair.getKey());

            // Add Object Support
            List<ObjectWithConstraint> localDataObjects = localFunction.getDataObject();
            List<DataObject> dataObjects = pair.getValue();
            if (dataObjects != null) {
                for (DataObject dataObject : dataObjects) {

                    // Create object
                    ObjectWithConstraint localObject = new ObjectWithConstraint();
                    localObject.setValue(dataObject.getName());

                    // Set object constraints
                    localObject.setMaxDataNodes(dataObject.getMaxDataNodes());
                    localObject.setMaxDataPoints(dataObject.getMaxDataPoints());
                    localDataObjects.add(localObject);
                }
            }

            // Add function to collection
            function.add(localFunction);
            it.remove();
        }

        // Add server to root plural element
        servers.setCapServer(server);

        // Marshal the object to XML
        JAXBContext jaxbContext = JAXBContext.newInstance(com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.ObjCapServers.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter writer = new StringWriter();

        // Create object factory and root element
        com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.ObjectFactory factory = new com.hashmapinc.tempus.witsml.server.api.model.cap.v1411.ObjectFactory();
        JAXBElement finalServers = factory.createCapServers(servers);

        // Create and return the XML the string
        jaxbMarshaller.marshal(finalServers, writer);
        return writer.toString();
    }
}
