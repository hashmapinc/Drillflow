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

import java.util.logging.Logger;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
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

    public final String URL;
    public final String API_KEY;

    public DotDelegator(String url, String apiKey) {
        this.URL = url;
        this.API_KEY = apiKey;
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
                endpoint = this.URL + "/democore/well/v2/witsml/wells/";
                break;
            case "wellbore":
                endpoint = this.URL + "/democore/wellbore/v1/witsml/wellbores/";
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
     * @param tokenString - auth string for rest calls
     */
    public void deleteObject(
        AbstractWitsmlObject witsmlObj,
        String tokenString
    ) throws ValveException, UnirestException, ValveAuthException {
        LOG.info("DELETING " + witsmlObj.toString() + " in DotDelegator.");
        String uid = witsmlObj.getUid(); // get uid for delete call
        String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
        String endpoint = this.getEndpoint(objectType) + uid; // add uid for delete call
 
        // create request
        HttpRequest request = Unirest.delete(endpoint);
        if ("wellbore".equals(objectType))
            request.queryString("uidWell", witsmlObj.getParentUid()); // TODO: ensure parent uid exists?

        // make the DELETE call.
        HttpResponse<String> response = request
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + tokenString)
            .asString();

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status || 204 == status) {
            LOG.info("Received successful status code from DoT DELETE call: " + status);
        } else if (401 == status) {
            LOG.warning("Bad auth token.");
            throw new ValveAuthException("Bad JWT");
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
     * @param tokenString - auth string for rest calls
     */
    public void updateObject(
        AbstractWitsmlObject witsmlObj,
        String tokenString
    ) throws ValveException, ValveAuthException, UnirestException {
        LOG.info("UPDATING " + witsmlObj.toString() + " in DotDelegator.");
        String uid = witsmlObj.getUid(); // get uid for delete call
        String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
        String endpoint = this.getEndpoint(objectType) + uid; // add uid for update call

        // make the UPDATE call.
        String payload = witsmlObj.getJSONString("1.4.1.1");
        HttpResponse<String> response = Unirest.put(endpoint)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + tokenString)
            .body(payload)
            .asString();

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status) {
            LOG.info("UPDATE for " + witsmlObj + " was successful with REST status code: " + status);
        } else if (401 == status) {
            LOG.warning("Bad auth token.");
            throw new ValveAuthException("Bad JWT");
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
     * @param tokenString - string of the JWT to do auth with
     * @return
     */
    public String createObject(
        AbstractWitsmlObject witsmlObj,
        String tokenString
    ) throws ValveException, ValveAuthException, UnirestException {
        LOG.info("CREATING " + witsmlObj.toString() + " in DotDelegator.");
        String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
        String uid = witsmlObj.getUid();
        String endpoint = this.getEndpoint(objectType);

        // build the create request
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

        // get the request response.
        String payload = witsmlObj.getJSONString("1.4.1.1");
        HttpResponse<String> response = request
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + tokenString)
            .body(payload)
            .asString();

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status) {
            LOG.info("Received successful status code from DoT create call: " + status);
            return uid.isEmpty() ? new JsonNode(response.getBody()).getObject().getString("uid") : uid;
        } else if (401 == status) {
            LOG.warning("Bad auth token.");
            throw new ValveAuthException("Bad JWT");
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
     * @param tokenString - string of the JWT to do auth with
     * @return get results AbstractWitsmlObject
     */
    public AbstractWitsmlObject getObject(
            AbstractWitsmlObject witsmlObject,
            String tokenString
    ) throws ValveException, ValveAuthException, UnirestException {
        LOG.info("GETing " + witsmlObject.toString() + " in DotDelegator.");
        String uid = witsmlObject.getUid();
        String objectType = witsmlObject.getObjectType();
        String endpoint = this.getEndpoint(objectType) + uid; // add uid for rest call

        // build request
        HttpRequest request = Unirest.get(endpoint);
        if ("wellbore".equals(objectType))
            request.queryString("uidWell", witsmlObject.getParentUid()); // TODO: check that parent uid exists?

        // get response
        HttpResponse<String> response = request
            .header("accept", "application/json")
            .header("Authorization", "Bearer " + tokenString)
            .asString();

        LOG.info("Response for GetObject: " + response.getBody());

        int status = response.getStatus();

        if (201 == status || 200 == status) {
            LOG.info("Successfully executed GET for query object=" + witsmlObject.toString());
            // get an abstractWitsmlObject from merging the query and the result JSON objects
            JSONObject queryJSON = new JSONObject(witsmlObject.getJSONString("1.4.1.1"));
            JSONObject responseJSON = new JsonNode(response.getBody()).getObject();
            return DotTranslator.translateQueryResponse(queryJSON, responseJSON, objectType);
        } else if (401 == status) {
            LOG.warning("Bad auth token.");
            throw new ValveAuthException("Bad JWT");
        } else {
            LOG.warning("Received status code from GET call to DoT: " + status);
            LOG.warning("GET response: " + response.getBody());
            throw new ValveException(response.getBody());
        }
    }
}