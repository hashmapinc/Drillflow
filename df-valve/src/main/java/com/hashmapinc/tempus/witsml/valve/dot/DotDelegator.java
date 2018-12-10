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

public class DotDelegator {
    private static final Logger LOG = Logger.getLogger(DotDelegator.class.getName());

    public final String URL;
    public final String API_KEY;

    public DotDelegator(String url, String apiKey) {
        this.URL = url;
        this.API_KEY = apiKey;
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
    ) throws ValveException, UnirestException {
        LOG.info("DELETING " + witsmlObj.toString() + " in DotDelegator.");
        String uid = witsmlObj.getUid(); // get uid for delete call
        String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
        String endpoint; // endpoint to send the DELETE call to

        // construct the endpoint for each object type
        switch (objectType) { // TODO: add support for wellbore, log, and trajectory
            case "well":
                endpoint = this.URL + "/witsml/wells/" + uid;
                break;
            case "wellbore":
                endpoint = this.URL + "/witsml/wellbores/" + uid;
                break;
            default:
                throw new ValveException("Unsupported object type<" + objectType + "> for DELETE");
        }

        // make the DELETE call.
        HttpResponse<String> response = Unirest
            .delete(endpoint)
            .header("accept", "application/json")
            .header("Authorization", tokenString)
            .header("Ocp-Apim-Subscription-Key", this.API_KEY)
            .asString();

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status) {
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
     * @param tokenString - auth string for rest calls
     */
    public void updateObject(
        AbstractWitsmlObject witsmlObj,
        String tokenString
    ) throws ValveException, ValveAuthException, UnirestException {
        LOG.info("UPDATING " + witsmlObj.toString() + " in DotDelegator.");
        String uid = witsmlObj.getUid(); // get uid for delete call
        String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
        String endpoint; // endpoint to send the UPDATE call to

        // construct the endpoint for each object type
        switch (objectType) { // TODO: add support for log and trajectory
            case "well":
                endpoint = this.URL + "/witsml/wells/" + uid;
                break;
            case "wellbore":
                endpoint = this.URL + "/witsml/wellbores/" + uid;
                break;
            default:
                throw new ValveException("Unsupported object type<" + objectType + "> for UPDATE");
        }

        // make the UPDATE call.
        String payload = witsmlObj.getJSONString("1.4.1.1");
        HttpResponse<String> response = Unirest
            .put(endpoint)
            .header("accept", "application/json")
            .header("Authorization", tokenString)
            .header("Ocp-Apim-Subscription-Key", this.API_KEY)
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
    ) throws ValveException, UnirestException {
        LOG.info("CREATING " + witsmlObj.toString() + " in DotDelegator.");
        String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
        String endpoint; // endpoint to send the call to

        // construct the endpoint for each object type
        switch (objectType) { // TODO: add support for log and trajectory
            case "well":
                endpoint = this.URL + "/witsml/wells/";
                break;
            case "wellbore":
                endpoint = this.URL + "/witsml/wellbores/";
                break;
            default:
                throw new ValveException("Unsupported object type<" + objectType + "> for CREATE");
        }

        // make the UPDATE call.
        String payload = witsmlObj.getJSONString("1.4.1.1");
        HttpResponse<String> response = Unirest
            .post(endpoint)
            .header("accept", "application/json")
            .header("Authorization", tokenString)
            .header("Ocp-Apim-Subscription-Key", this.API_KEY)
            .body(payload)
            .asString();

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status) {
            LOG.info("Received successful status code from DoT POST call: " + status);
            return new JsonNode(response.getBody()).getObject().getString("uid");
        } else {
            LOG.warning("Received failure status code from DoT POST: " + status);
            LOG.warning("POST response: " + response.getBody());
            throw new ValveException(response.getBody());
        }
    }
}