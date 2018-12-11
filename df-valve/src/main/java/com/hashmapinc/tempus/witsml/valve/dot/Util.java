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

    public static JSONObject merge(JSONObject query, JSONObject response, JSONObject result) {
        if (result == null){
            result = new JSONObject();
        }
        Iterator<String> keys = query.keys();

        Object obj1, obj2;

        while (keys.hasNext()) {
            String next = keys.next();

            if (query.isNull(next)) continue;
            obj1 = query.get(next);

            if (!response.has(next)) continue;
            obj2 = response.get(next);

            if (obj1 instanceof JSONObject && obj2 instanceof JSONObject) {
                merge((JSONObject) obj1, (JSONObject) obj2, result);
            } else if (obj1 instanceof String && obj2 instanceof String){
                if (obj1.equals("") && !obj2.equals("")){
                    result.put(next,obj2);
                }
            } else if (obj1 instanceof Float && obj2 instanceof Float){
                if (obj1.equals(0.0) && !obj2.equals(0.0)){
                    result.put(next, obj2);
                }
            } else if (obj1 instanceof JSONArray && obj2 instanceof JSONArray) {
                JSONArray arr1 = (JSONArray) obj1;
                JSONArray arr2 = (JSONArray) obj2;
                if (arr2.length() == 0) continue;
                result.put(next, arr2);
            }
        }
        return result;
    }
}
