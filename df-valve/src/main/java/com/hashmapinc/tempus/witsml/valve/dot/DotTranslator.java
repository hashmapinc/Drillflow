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

import com.fasterxml.jackson.databind.ObjectMapper;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.QueryContext;

public class DotTranslator {
    private static final Logger LOG = Logger.getLogger(DotTranslator.class.getName());


    /**
     * this function gets a 1.4.1.1 json string of the object in qc
     * @param qc - QueryContext holding the object to convert
     * @return jsonString - string 1.4.1.1 json value
     */
    private String getWellJSON(QueryContext qc) {
        return null;
    }

    /**
     * this function gets a 1.4.1.1 json string of the object in qc
     * @param qc - QueryContext holding the object to convert
     * @return jsonString - string 1.4.1.1 json value
     */
    private String getWellboreJSON(QueryContext qc) {
        return null;
    }

    /**
     * this function gets a 1.4.1.1 json string of the object in qc
     * @param qc - QueryContext holding the object to convert
     * @return jsonString - string 1.4.1.1 json value
     */
    private String getTrajectoryJSON(QueryContext qc) {
        return null;
    }

    /**
     * this function gets a 1.4.1.1 json string of the object in qc
     * @param qc - QueryContext holding the object to convert
     * @return jsonString - string 1.4.1.1 json value
     */
    private String getLogJSON(QueryContext qc) {
        return null;
    }



    /**
     * This function takes the object in the qc, converts it to 
     * WITSML 1.4.1.1 if needed, then returns a JSON string of that object
     * for rest calls
     * @param qc - Query context containing the object information needed
     * @return jsonString - String serialization of a JSON version of the 1.4.1.1 witsml objecr
     */
    public String get1411JSONString(QueryContext qc) {
        String objectType = qc.OBJECT_TYPE;

        // handle each object
        switch (objectType) {
            case "well":
                return getWellJSON(qc);
            case "wellbore":
                return getWellboreJSON(qc);
            case "trajectory":
                return getTrajectoryJSON(qc);
            case "log":
                return getLogJSON(qc);
            default:
                LOG.warning("Unsupported object type: " + objectType);
                return null;
        }
    }
}