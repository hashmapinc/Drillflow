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
import com.mashape.unirest.http.exceptions.UnirestException;

public class DotValve implements IValve {
    private static final Logger LOG = Logger.getLogger(DotValve.class.getName());
    final String NAME = "DoT"; // DoT = Drillops Town
    final String DESCRIPTION = "Valve for interaction with Drillops Town"; // DoT = Drillops Town
    DotDelegator delegator;
    DotTranslator translator;
    DotAuth auth;

    public DotValve() {
        this.delegator = new DotDelegator();
        this.translator = new DotTranslator();//please provide the authentication API here.
        this.auth = new DotAuth("http://localhost:8080/");
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
     * @param obj - AbstractWitsmlObject to create
     * @return the UID of the newly created object
     */
    @Override
    public String createObject(AbstractWitsmlObject obj) {
        // get a 1.4.1.1 json string
        String objectJSON = this.translator.get1411JSONString(obj);

        // get object UID and call PUT to create the object
        return null;
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
            //TODO: Remove the hardcoded apiKey
            DecodedJWT jwt = auth.getJWT("test", userName, password);
            return jwt != null;

        } catch (UnirestException e) {
            return false;
        }

    }
}