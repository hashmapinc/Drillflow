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
