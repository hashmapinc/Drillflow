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

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.valve.IValve;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DotValve implements IValve {
    private static final Logger LOG = Logger.getLogger(DotValve.class.getName());
    private final String NAME = "DoT"; // DoT = Drillops Town
    private final String DESCRIPTION = "Valve for interaction with Drillops Town"; // DoT = Drillops Town
    private final DotTranslator TRANSLATOR;
    private final DotAuth AUTH;
    private final DotDelegator DELEGATOR;
    private final String URL;
    private final String API_KEY;

    public DotValve(Map<String, String> config) {
        this.URL = config.get("baseurl");
        this.API_KEY = config.get("apikey"); 
        this.TRANSLATOR = new DotTranslator();
        this.AUTH = new DotAuth(this.URL, this.API_KEY);
        this.DELEGATOR = new DotDelegator(this.URL, this.API_KEY);
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
        // get the object and uid
        AbstractWitsmlObject obj = qc.WITSML_OBJECTS.get(0); //TODO: do not assume only one object
        LOG.info("Getting object from store: " + obj.toString());
        String uid = obj.getUid();

        // create endpoint
        String endpoint = this.URL + "/witsml/wells/" + uid;

        // send get
        try {
            HttpResponse<JsonNode> response = Unirest
                .get(endpoint)
                .header("accept", "application/json")
                .header("Authorization", this.AUTH.getJWT(qc.USERNAME, qc.PASSWORD).getToken())
                .header("Ocp-Apim-Subscription-Key", this.API_KEY)
                .asJson();
            
            int status = response.getStatus();

            if (201 == status || 200 == status) {
                LOG.info("Successfully executed GET object for query object=" + obj.toString());

                // get an abstractWitsmlObject from merging the query and the result JSON objects
                JSONObject queryJSON = new JSONObject(obj.getJSONString("1.4.1.1"));
                JSONObject responseJSON = response.getBody().getObject();
                AbstractWitsmlObject mergedResponse = this.TRANSLATOR.translateQueryResponse(queryJSON, responseJSON);

                // return the proper xml string for the client version
                if ("1.4.1.1".equals(qc.CLIENT_VERSION)) {
                    return mergedResponse.getXMLString(qc.CLIENT_VERSION); // no version translation required
                } else if ("1.3.1.1".equals(qc.CLIENT_VERSION)) {
                    return this.TRANSLATOR.get1311XMLString(mergedResponse); // version translation required
                } else {
                    return null;
                }
            } else {
                LOG.warning("Received status code from GET object: " + status);
                return null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            LOG.warning("Error while getting object in DoTValve: " + e);
            return null;
        }
    }

    /**
     * Creates an object
     * 
     * @param qc - query context to use for query execution
     * @return the UID of the newly created object
     */
    @Override
    public String createObject(QueryContext qc) {
        // get object information
        AbstractWitsmlObject obj = qc.WITSML_OBJECTS.get(0); // TODO: don't assume 1 object
        LOG.info("Creating object: " + obj.toString());
        String objectType = obj.getObjectType();
        String objectJSON = this.TRANSLATOR.get1411JSONString(obj);

        // get token
        String tokenString;
        try {
            tokenString = this.AUTH.getJWT(qc.USERNAME, qc.PASSWORD).getToken();
        } catch (Exception e) {
            LOG.warning("Received error getting token string for createObject: " + e);
            e.printStackTrace();
            return null;
        }

        // handle each supported object
        switch (objectType) {
            case "well":
                return this.DELEGATOR.addWellToStore(objectJSON, tokenString);
            case "wellbore":
                return this.DELEGATOR.addWellboreToStore(objectJSON, tokenString);
            default:
                LOG.warning("Unsupported type encountered in createObject. Type = <" + objectType + ">");
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

    /**
     * Return a map of FUNCTION_NAME->LIST_OF_SUPPORTED_OBJECTS
     * 
     * @return capabilities - map of FUNCTION_NAME->LIST_OF_SUPPORTED_OBJECTS
     */
    public Map<String, AbstractWitsmlObject[]> getCap() {
        // define capabilities map
        Map<String, AbstractWitsmlObject[]> cap = new HashMap<>(); 

        // array of supported functions
        String[] funcs = {
            "AddToStore",
            "GetFromStore"
        }; 

        // supported objects for each function
        AbstractWitsmlObject well = new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell(); // 1311 is arbitrary
        AbstractWitsmlObject wellbore = new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore(); // 1311 is arbitrary
        AbstractWitsmlObject[][] supportedObjects = {
            {well, wellbore}, // ADD TO STORE OBJECTS
            {well}, // GET FROM STORE OBJECTS
        };

        // populate cap
        for (int i = 0; i < funcs.length; i++) {
            cap.put(funcs[i], supportedObjects[i]);
        }

        return cap;
    }
}