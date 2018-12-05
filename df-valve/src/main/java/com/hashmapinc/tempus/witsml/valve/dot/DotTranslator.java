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

import java.io.IOException;
import java.util.logging.Logger;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;

import org.json.JSONObject;

public class DotTranslator {
    private static final Logger LOG = Logger.getLogger(DotTranslator.class.getName());

    /**
     * This function takes the object, converts it to 
     * WITSML 1.4.1.1 if needed, then returns a JSON string of that object
     * for rest calls
     * @param qc - Query context containing the object information needed
     * @return jsonString - String serialization of a JSON version of the 1.4.1.1 witsml objecr
     */
    public String get1411JSONString(AbstractWitsmlObject obj) {
        LOG.info("Getting 1.4.1.1 json string for object: " + obj.toString());
        return obj.getJSONString("1.4.1.1");
    }

    /**
     * merges response into query and returns the parsed 1.4.1.1 object
     * 
     * @param query    - JSON object representing the query
     * @param response - JSON object representing the response from DoT
     * @return obj - parsed abstract object
     */
    public AbstractWitsmlObject translateQueryResponse(
        JSONObject query, 
        JSONObject response
    ) throws IOException {
        LOG.info("Translating query response.");

        // merge the responseJSON with the query
        LOG.info("Merging query and response into single object");
        for (Object key : query.keySet()) { // copy missing query fields from response
            String keyString = (String) key;
            LOG.info("Merging key <" + keyString + ">");
            if (null == query.get(keyString)) { // only fill in missing values
                Object val = response.get(keyString);
                query.put(keyString, val);
            }
        }

        // convert the queryJSON back to valid xml
        LOG.info("Converting merged query JSON to valid XML string");
        return WitsmlMarshal.deserializeFromJSON(query.toString(), com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class);
    }
}