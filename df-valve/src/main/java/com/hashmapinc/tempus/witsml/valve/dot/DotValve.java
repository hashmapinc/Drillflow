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

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.valve.IValve;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class DotValve implements IValve {
    private static final Logger LOG = Logger.getLogger(DotValve.class.getName());
    private final String NAME = "DoT"; // DoT = Drillops Town
    private final String DESCRIPTION = "Valve for interaction with Drillops Town"; // DoT = Drillops Town
    private final String URL; // url endpoint for sending requests
    private final String API_KEY; // url endpoint for sending requests
    private final DotTranslator TRANSLATOR;
    private final DotAuth AUTH;

    public DotValve(Map<String, String> config) {
        this.URL = config.get("baseurl"); 
        this.API_KEY = config.get("apikey"); 
        this.TRANSLATOR = new DotTranslator();
        this.AUTH = new DotAuth(this.URL, this.API_KEY);
    }

    /**
     * Retrieve the name of the valve
     * @return The name of the valve
     */
    @Override
    public String getName() {
        return this.NAME;
    }

    /**
     * Retrieve the description of the valve
     * @return The description of the valve
     */
    @Override
    public String getDescription() {
        return this.DESCRIPTION;
    }

    /**
     * Gets the object based on the query from the WITSML STORE API
     * 
     * @param qc - QueryContext needed to execute the getObject querying
     * @return The resultant object from the query in xml string format
     */
    @Override
    public String getObject(QueryContext qc) {
        return null;
    }

    /**
     * Creates an object
     * 
     * @param qc - query context to use for query execution
     * @return the UID of the newly created object
     */
    @Override
    public String createObject(QueryContext qc) {
        // get a 1.4.1.1 json string
        AbstractWitsmlObject obj = qc.WITSML_OBJECTS.get(0); // TODO: don't assume 1 object
        String objectJSON = this.TRANSLATOR.get1411JSONString(obj);

        // get the uid
        String uid = obj.getUid();

        // create endpoint
        String endpoint = this.URL + "/witsml/wells/" + uid;

        // send put 
        try {
            HttpResponse<JsonNode> response = Unirest.put(endpoint)
				.header("accept", "application/json")
				.header("Authorization", this.AUTH.getJWT(qc.USERNAME, qc.PASSWORD).getToken())
				.header("Ocp-Apim-Subscription-Key", this.API_KEY)
                .body(objectJSON).asJson();
            
            int status = response.getStatus();
            
            if (201 == status || 200 == status) {
                LOG.info("Succesfully put object: " + obj.toString());
                return uid;
            } else {
                LOG.warning("Recieved status code: " + status );
                return null;
            }
        } catch (Exception e) {
            //TODO: handle exception
            LOG.warning("Error while creating object in DoTValve: " + e);
            return null;
        }
    }

    /**
     * Deletes an object
     * @param query POJO representing the object that was received
     */
    @Override
    public void deleteObject(AbstractWitsmlObject query) {
    }

    /**
     * Updates an already existing object
     * @param query POJO representing the object that was received
     */
    @Override
    public void updateObject(AbstractWitsmlObject query) {
    }

    /**
     * Authenticates with the DotAuth class to get a JWT
     * @param userName The user name to authenticate with
     * @param password The password to authenticate with
     * @return True if successful, false if not
     */
    //TODO: This should throw an exception not be a boolean value so that a descriptive message can be logged/returned
    @Override
    public boolean authenticate(String userName, String password) {
        try {
            DecodedJWT jwt = AUTH.getJWT(userName, password);
            return jwt != null;
        } catch (UnirestException e) {
            return false;
        }

    }
}