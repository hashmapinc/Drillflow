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

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.valve.AbstractTranslator;
import com.hashmapinc.tempus.witsml.valve.AbstractDelegator;

import java.util.Map;

public abstract class AbstractValve {
    AbstractDelegator delegator;
    AbstractTranslator translator;
    /**
     * Retrieve the name of the valve
     * @return The name of the valve
     */
    public abstract String getName();

    /**
     * Retrieve the description of the valve
     * @return The description of the valve
     */
    public abstract String getDescription();

    /**
     * Gets the object based on the query from the WITSML STORE API
     * @param query POJO representing the query that was received
     * @param options The options that were received as part of the query
     * @return The resultant object from the query
     */
    public abstract AbstractWitsmlObject getObject(AbstractWitsmlObject query, Map<String, String> options);

    /**
     * Creates an object
     * @param query POJO representing the object that was received
     * @return the UID of the newly created object
     */
    public abstract String createObject(AbstractWitsmlObject query);

    /**
     * Deletes an object
     * @param query POJO representing the object that was received
     */
    public abstract void deleteObject(AbstractWitsmlObject query);

    /**
     * Updates an already existing object
     * @param query POJO representing the object that was received
     */
    public abstract void updateObject(AbstractWitsmlObject query);
}
