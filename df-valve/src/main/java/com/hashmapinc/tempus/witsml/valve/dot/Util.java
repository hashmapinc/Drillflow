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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Util {

    /**
     * This function merges the fields from src that
     * are missing from dest. Only fields that exist in dest
     * but have an empty value are merged.
     *
     * @param dest - JSONObject to merge into. Existing values here are unchanged
     * @param src - src to merge from for blank values in dest
     * @return dest - fully merged dest
     */
    public static JSONObject merge (
        JSONObject dest,
        JSONObject src
    ) {
        // iterate through keys and merge in place
        Iterator<String> keys = dest.keys();
        ArrayList<String> keysToRemove = new ArrayList<String>(); // tracks keys that should be removed in cleanup
        String key;
        while (keys.hasNext()) {
            key = keys.next(); // get key

            // check that the response has a value for this key.
            if (!src.has(key)) {
                // if the dest is empty, remember to remove this key in the cleanup
                if (dest.isNull(key) || "".equals(dest.get(key)))
                    keysToRemove.add(key);
                continue;
            }

            // get current objects
            Object destObj = dest.get(key);
            Object srcObj = src.get(key);


            // do merging below for each possible value object type for this key
            if ( // handle nested objects
                destObj instanceof JSONObject &&
                srcObj  instanceof JSONObject
            ) {
                merge((JSONObject) destObj, (JSONObject) srcObj); // recursively copy into destObj
                dest.put(key, destObj); // update dest with the updated value for this key

            } else if ( // handle JSONArrays
                destObj instanceof JSONArray &&
                srcObj  instanceof JSONArray
            ) {
                JSONArray destArr = dest.getJSONArray(key);
                JSONArray srcArr = src.getJSONArray(key);
                if (destArr.length() != 0 && srcArr.length() != 0)
                    dest.put(key, srcArr); // TODO: deep merging on sub objects

            } else if (
                dest.isNull(key) || "".equals(destObj) // dest val is empty
            ) { // handle all basic values (non array, non nested objects)
                dest.put(key, srcObj);
            }

            // if after all the merging the dest value is still empty, add the key to list of removable fields
            if (dest.isNull(key) || "".equals(dest.get(key)))
                keysToRemove.add(key);
        }

        // cleanup fields
        for (String removableKey : keysToRemove)
            dest.remove(removableKey);

        // return the dest
        return dest;
    }
}
