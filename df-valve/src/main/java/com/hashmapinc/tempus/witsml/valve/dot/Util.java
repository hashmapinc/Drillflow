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

import java.util.Iterator;

public class Util {

    public static JSONObject merge (JSONObject query, JSONObject response){
        return merge(query, response, new JSONObject());
    }

    private static JSONObject merge(JSONObject query, JSONObject response, JSONObject result) {

        Iterator<String> keys = query.keys();

        Object queryObj;
        Object respObj;

        while (keys.hasNext()) {
            String next = keys.next();

            if (query.isNull(next)) continue;
            queryObj = query.get(next);

            if (!response.has(next)) continue;
            respObj = response.get(next);

            if (queryObj instanceof JSONObject && respObj instanceof JSONObject) {
                merge((JSONObject) queryObj, (JSONObject) respObj, result);
            } else if (queryObj instanceof String && respObj instanceof String){
                if (("").equals(queryObj) && !("").equals(respObj)){
                    result.put(next,respObj);
                }
            } else if (queryObj instanceof Float && respObj instanceof Float){
                //TODO: This is an issue, if there is legitimately 0.0 values, they will be filtered. This should be handled in WOL
                if (queryObj.equals(0.0) && !respObj.equals(0.0)){
                    result.put(next, respObj);
                }
            } else if (queryObj instanceof JSONArray && respObj instanceof JSONArray) {
                JSONArray queryArr = (JSONArray) queryObj;
                JSONArray respArr = (JSONArray) respObj;
                //TODO: This is a straight up hack, this should additionally merge sub-objects
                if (respArr.length() == 0 || queryArr.length() == 0) continue;
                if (queryArr.length() == 0) continue;

                for (int i = 0; i < respArr.length(); i++) {
                    respArr.getJSONObject(i);
                }

                result.put(next, respArr);
            }
        }
        return result;
    }
}
