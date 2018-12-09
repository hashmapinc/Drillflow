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
package com.hashmapinc.tempus.witsml.valve;

import java.util.Map;
import java.util.List;
import java.util.function.Consumer;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.QueryContext;

public interface IValve {
    /**
     * Retrieve the name of the valve
     * @return The name of the valve
     */
    public String getName();

    /**
     * Retrieve the description of the valve
     * @return The description of the valve
     */
    public String getDescription();

    /**
     * Gets the object based on the query from the WITSML STORE API
     * @param qc - QueryContext needed to execute the getObject querying
     * @return The resultant object from the query in XML string format
     */
    public String getObject(QueryContext qc) throws ValveException;

    /**
     * Creates an object
     * 
     * @param qc - QueryContext needed to execute the createObject querying
     * @return the UID of the newly created object
     */
    public String createObject(QueryContext qc) throws ValveException;

    /**
     * Deletes an object
     * @param qc - QueryContext needed to execute the deleteObject querying
     */
    public void deleteObject(QueryContext qc) throws ValveException;

    /**
     * Updates an already existing object
     * @param qc - QueryContext needed to execute the updateObject querying
     */
    public void updateObject(QueryContext qc) throws ValveException;

    /**
     * Performs authentication
     * 
     * @param userName - basic auth username for authentication
     * @param password - basic auth password for authentication
     * @return status - boolean value; true = success, false = failure
     */
    public boolean authenticate(String userName, String password);

    /**
     * Return a map of FUNCTION_NAME->ARRAY_OF_SUPPORTED_OBJECTS
     * 
     * @return capabilities - map of FUNCTION_NAME->ARRAY_OF_SUPPORTED_OBJECTS
     */
    public Map<String, AbstractWitsmlObject[]> getCap();
}
