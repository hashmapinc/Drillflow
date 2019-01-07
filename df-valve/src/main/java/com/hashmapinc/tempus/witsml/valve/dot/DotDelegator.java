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
package com.hashmapinc.tempus.witsml.valve.dot;

import java.util.Map;
import java.util.logging.Logger;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONObject;

public class DotDelegator {
    private static final Logger LOG = Logger.getLogger(DotDelegator.class.getName());

    private final String URL;
    private final String WELL_PATH;
    private final String WB_PATH;


    /**
     * Map based constructor
     *
     * @param config - map with field values
     */
    public DotDelegator(Map<String, String> config) {
        this.URL = config.get("baseurl");
        this.WELL_PATH = config.get("well.path");
        this.WB_PATH = config.get("wellbore.path");
    }

    /**
     * returns the endpoint for each supported object type
     * @param objectType - well, wellbore, trajectory, or log
     * @return endpoint - String value to send requests to
     * @throws ValveException
     */
    private String getEndpoint(
        String objectType
    ) throws ValveException{
        // TODO: these should be injected in the DotDelegator constructor and not rely on a shared this.URL
        // get endpoint
        String endpoint;
        switch (objectType) { // TODO: add support for log and trajectory
            case "well":
                endpoint = this.URL + this.WELL_PATH;
                break;
            case "wellbore":
                endpoint = this.URL + this.WB_PATH;
                break;
            default:
                throw new ValveException("Unsupported object type<" + objectType + ">");
        }
        return endpoint;
    }

    /**
     * deletes the object from DoT
     *
     * @param witsmlObj - object to delete
     * @param username - auth username
     * @param password - auth password
     * @param client - DotClient to execute requests with
     */
    public void deleteObject(
        AbstractWitsmlObject witsmlObj,
        String username,
        String password,
        DotClient client
    ) throws ValveException, UnirestException, ValveAuthException {
        String uid = witsmlObj.getUid(); // get uid for delete call
        String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
        String endpoint = this.getEndpoint(objectType) + uid; // add uid for delete call
 
        // create request
        HttpRequest request = Unirest.delete(endpoint).header("Content-Type", "application/json");
        if ("wellbore".equals(objectType))
            request.queryString("uidWell", witsmlObj.getParentUid()); // TODO: ensure parent uid exists?

        // make the DELETE call.
        HttpResponse<String> response = client.makeRequest(request, username, password);

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status || 204 == status) {
            LOG.info("Received successful status code from DoT DELETE call: " + status);
        } else {
            LOG.warning("Received failure status code from DoT DELETE: " + status);
            LOG.warning("DELETE response: " + response.getBody());
            throw new ValveException("DELETE DoT REST call failed with status code: " + status);
        }
    }

    /**
     * updates the object in DoT
     *
     * @param witsmlObj - object to delete
     * @param username - auth username
     * @param password - auth password
     * @param client - DotClient to execute requests with
     */
    public void updateObject(
        AbstractWitsmlObject witsmlObj,
        String username,
        String password,
        DotClient client
    ) throws ValveException, ValveAuthException, UnirestException {
        String uid = witsmlObj.getUid(); // get uid for delete call
        String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
        String endpoint = this.getEndpoint(objectType) + uid; // add uid for update call

        // get witsmlObj as json string for request payload
        String payload = witsmlObj.getJSONString("1.4.1.1");

        // build the request
        HttpRequestWithBody request = Unirest.put(endpoint);
        request.header("Content-Type", "application/json");
        request.body(payload);

        // make the UPDATE call.
        HttpResponse<String> response = client.makeRequest(request, username, password);

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status) {
            LOG.info("UPDATE for " + witsmlObj + " was successful with REST status code: " + status);
        } else {
            LOG.warning("Received failure status code from DoT PUT: " + status);
            LOG.warning("PUT response: " + response.getBody());
            throw new ValveException(response.getBody());
        }
    }

    /**
     * Submits the object to the DoT rest API for creation
     *
     * @param witsmlObj - AbstractWitsmlObject to create
     * @param username - auth username
     * @param password - auth password
     * @param client - DotClient to execute requests with
     * @return
     */
    public String createObject(
        AbstractWitsmlObject witsmlObj,
        String username,
        String password,
        DotClient client
    ) throws ValveException, ValveAuthException, UnirestException {
        String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
        String uid = witsmlObj.getUid();
        String endpoint = this.getEndpoint(objectType);

        // get object as payload string
        String payload = witsmlObj.getJSONString("1.4.1.1");

        // build the request
        HttpRequestWithBody request;
        if (uid.isEmpty()){
            // create with POST and generate uid
            request = Unirest.post(endpoint);
        } else {
            // create with PUT using existing uid
            request = Unirest.put(endpoint + uid);

            // for objects that need it, provide parent uid as param
            if ("wellbore".equals(objectType))
                request.queryString("uidWell", witsmlObj.getParentUid()); // TODO: error handle this?
        }

        // add the header and payload
        request.header("Content-Type", "application/json");
        request.body(payload);

        // get the request response.
        HttpResponse<String> response = client.makeRequest(request, username, password);

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status) {
            LOG.info("Received successful status code from DoT create call: " + status);
            return uid.isEmpty() ? new JsonNode(response.getBody()).getObject().getString("uid") : uid;
        } else {
            LOG.warning("Received failure status code from DoT POST: " + status);
            LOG.warning("POST response: " + response.getBody());
            throw new ValveException(response.getBody());
        }
    }

    /**
     * Submits the query to the DoT rest API for object GETing
     *
     * @param witsmlObject - AbstractWitsmlObject to get
     * @param username - auth username
     * @param password - auth password
     * @param client - DotClient to execute requests with
     * @return get results AbstractWitsmlObject
     */
    public AbstractWitsmlObject getObject(
        AbstractWitsmlObject witsmlObject,
        String username,
        String password,
        DotClient client
    ) throws ValveException, ValveAuthException, UnirestException {
        String uid = witsmlObject.getUid();
        String objectType = witsmlObject.getObjectType();
        String endpoint = this.getEndpoint(objectType) + uid; // add uid for rest call

        // build request
        HttpRequest request = Unirest.get(endpoint);
        request.header("accept", "application/json");
        if ("wellbore".equals(objectType))
            request.queryString("uidWell", witsmlObject.getParentUid()); // TODO: check that parent uid exists?

        // get response
        HttpResponse<String> response = client.makeRequest(request, username, password);

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status) {
            LOG.info("Successfully executed GET for query object=" + witsmlObject.toString());
            // get an abstractWitsmlObject from merging the query and the result JSON objects
            JSONObject queryJSON = new JSONObject(witsmlObject.getJSONString("1.4.1.1"));
            JSONObject responseJSON = new JsonNode(response.getBody()).getObject();
            return DotTranslator.translateQueryResponse(queryJSON, responseJSON, objectType);
        } else {
            LOG.warning("Received status code from GET call to DoT: " + status);
            LOG.warning("GET response: " + response.getBody());
            throw new ValveException(response.getBody());
        }
    }
}