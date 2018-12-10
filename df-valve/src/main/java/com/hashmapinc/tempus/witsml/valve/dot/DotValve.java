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

import java.util.ArrayList;
import java.util.logging.Logger;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.valve.IValve;
import com.hashmapinc.tempus.witsml.valve.ValveException;
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
    public String getObject(
            QueryContext qc
    ) throws ValveException {
        // TODO: move a ton of this to the delegator.
        // handle each object
        ArrayList<AbstractWitsmlObject> queryResponses = new ArrayList<AbstractWitsmlObject>();
        for (AbstractWitsmlObject witsmlObject: qc.WITSML_OBJECTS) {
            LOG.info("Getting object from store: " + witsmlObject.toString());
            String uid = witsmlObject.getUid();
            String objectType = witsmlObject.getObjectType();

            // create endpoint
            String endpoint = this.URL;
            switch (objectType) {
                case "well":
                    endpoint += "/witsml/wells/" + uid;
                    break;
                case "wellbore":
                    endpoint += "/witsml/wellbores/" + uid;
                    break;
                default:
                    String msg = "Unsupported object type <" + objectType + "> for GET";
                    LOG.warning(msg);
                    throw new ValveException(msg);
            }

            // send get
            try {
                HttpResponse<String> response = Unirest
                    .get(endpoint)
                    .header("accept", "application/json")
                    .header("Authorization", this.AUTH.getJWT(qc.USERNAME, qc.PASSWORD).getToken())
                    .header("Ocp-Apim-Subscription-Key", this.API_KEY)
                    .asString();

                int status = response.getStatus();

                if (201 == status || 200 == status) {
                    LOG.info("Successfully executed GET for query object=" + witsmlObject.toString());

                    // get an abstractWitsmlObject from merging the query and the result JSON objects
                    JSONObject queryJSON = new JSONObject(witsmlObject.getJSONString("1.4.1.1"));
                    JSONObject responseJSON = new JsonNode(response.getBody()).getObject();
                    AbstractWitsmlObject mergedResponse =
                        this.TRANSLATOR.translateQueryResponse(queryJSON, responseJSON);

                    // add the object to the response list
                    queryResponses.add(mergedResponse);
                } else {
                    LOG.warning("Received status code from GET call to DoT: " + status);
                    LOG.warning("GET response: " + response.getBody());
                    throw new ValveException(response.getBody());
                }
            } catch (Exception e) {
                // TODO: handle exception
                LOG.warning("Error while getting object in DoTValve: " + e);
                throw new ValveException(e.getMessage());
            }
        }

        // return consolidated XML response in proper version
        return this.TRANSLATOR.consolidateObjectsToXML(queryResponses, qc.CLIENT_VERSION);
    }

    /**
     * Creates an object
     * 
     * @param qc - query context to use for query execution
     * @return the UID of the newly created object
     */
    @Override
    public String createObject(
            QueryContext qc
    ) throws ValveException { // get auth token
        String tokenString;
        try {
            tokenString = this.AUTH.getJWT(qc.USERNAME, qc.PASSWORD).getToken();
        } catch (Exception e) {
            LOG.warning("Exception in createObject while authenticating: " + e.getMessage());
            throw new ValveException(e.getMessage());
        }

        // create each object
        ArrayList<String> uids = new ArrayList<>();
        try {
            for (AbstractWitsmlObject witsmlObject: qc.WITSML_OBJECTS)
                uids.add(this.DELEGATOR.createObject(witsmlObject, tokenString));
        } catch (Exception e) {
            LOG.warning("Exception in DotValve create object: " + e.getMessage());
            throw new ValveException(e.getMessage());
        }

        return uids.get(0); // TODO: handle plural return for creation.
    }

    /**
     * Deletes an object
     * @param qc - QueryContext with information needed to delete object
     */
    @Override
    public void deleteObject(
            QueryContext qc
    ) throws ValveException {
        // get auth token
        String tokenString;
        try {
            tokenString = this.AUTH.getJWT(qc.USERNAME, qc.PASSWORD).getToken();
        } catch (Exception e) {
            LOG.warning("Exception in deleteObject while authenticating: " + e.getMessage());
            throw new ValveException(e.getMessage());
        }

        // delete each object
        try {
            for (AbstractWitsmlObject witsmlObject : qc.WITSML_OBJECTS)
                this.DELEGATOR.deleteObject(witsmlObject, tokenString);
        } catch (UnirestException ue) {
            LOG.warning("Got UnirestException in DotValve delete object: " + ue.getMessage());
            throw new ValveException(ue.getMessage());
        }
    }

    /**
     * Updates an already existing object
     * @param qc - QueryContext needed to execute the deleteObject querying
     */
    @Override
    public void updateObject(
        QueryContext qc
    ) throws ValveException {
        LOG.info("Updating witsml objects" + qc.WITSML_OBJECTS.toString());

        // get auth token
        String tokenString;
        try {
            tokenString = this.AUTH.getJWT(qc.USERNAME, qc.PASSWORD).getToken();
        } catch (Exception e) {
            LOG.warning("Exception in deleteObject while authenticating: " + e.getMessage());
            throw new ValveException(e.getMessage());
        }

        // update each object
        try {
            for (AbstractWitsmlObject witsmlObject : qc.WITSML_OBJECTS)
                this.DELEGATOR.updateObject(witsmlObject, tokenString);
        } catch (UnirestException ue) {
            LOG.warning("Got UnirestException in DotValve update object: " + ue.getMessage());
            throw new ValveException(ue.getMessage());
        }
    }

    /**
	 * Authenticates with the DotAuth class to get a JWT
	 * 
	 * @param userName The user name to authenticate with
	 * @param password The password to authenticate with
	 * @throws ValveAuthException
	 */
	@Override
	public void authenticate(String userName, String password) throws ValveAuthException {
		try {
			AUTH.getJWT(userName, password);
		} catch (UnirestException e) {
			e.printStackTrace();
		} catch (ValveAuthException e) {
			throw new ValveAuthException(
					"Username : " + userName + " could not be authenticated. Error in generating JWT token");
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
            "GetFromStore",
            "DeleteFromStore",
            "UpdateInStore"
        }; 

        // supported objects for each function
        AbstractWitsmlObject well = new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell(); // 1311 is arbitrary
        AbstractWitsmlObject wellbore = new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore(); // 1311 is arbitrary
        AbstractWitsmlObject[][] supportedObjects = {
            {well, wellbore}, // ADD TO STORE OBJECTS
            {well, wellbore}, // GET FROM STORE OBJECTS
            {well, wellbore}, // DELETE FROM STORE OBJECTS
            {well, wellbore}, // UPDATE IN STORE OBJECTS
        };

        // populate cap
        for (int i = 0; i < funcs.length; i++) {
            cap.put(funcs[i], supportedObjects[i]);
        }

        return cap;
    }
}