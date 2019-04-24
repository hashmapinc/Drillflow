/**
 * Copyright © 2018-2019 Hashmap, Inc
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
package com.hashmapinc.tempus.witsml.valve.dot.model.log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hashmapinc.tempus.witsml.valve.dot.JsonUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LogConverter extends com.hashmapinc.tempus.WitsmlObjects.Util.LogConverter {
    protected static JSONObject objLogJO;
    protected static JSONObject ObjLogJOTrimmed = new JSONObject();
    protected static JSONObject payloadJOchannelSet = new JSONObject();
    protected static JSONObject workObj;
    protected static JSONArray  payloadJOchannels = new JSONArray();

    /**
     * convertToChannelSet1311 takes in a v1.3.1.1 JSON strong
     *          that was produced client-side from SOAP WITSML XML
     *          & translates as nedessary to adhere to DoT's
     *          "Create a new ChannelSet" API.
     * @param jsonString Represents the client's WITSML object (complete)
     *                   It is a JSON String created from the ObjLog1311
     * @return JSON String representing the conversion
     */
    public static String convertToChannelSet1311( String jsonString ) {
        // return the payload for creating a 1.3.1.1 ChannelSet
        return null;
    }

    /**
     * convertToChannelSet1411 takes in a v1.4.1.1 WITSML object
     *          that was produced client-side from SOAP WITSML XML
     *          & translates as necessary to adhere to DoT's
     *          "Create a new ChannelSet" API.
     *
     * Conversion at this stage exists to handle data business rules.
     *
     * @param witsmlObj Represents the client's WITSML object (complete).
     *                  This object has been marshalled by JAXB from the raw XML sent by the client.
     * @return JSON String representing the conversion
     */
    //public static String convertToChannelSet1411( String jsonString ) {
    public static JSONObject convertToChannelSet1411( com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog witsmlObj )
                                throws IOException {
        List<JSONObject> jsonItems;
/*
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(witsmlObj.getJSONString("1.4.1.1"));
        ObjectNode objectNode = removeEmptyFields((ObjectNode) actualObj);
*/
        objLogJO = new JSONObject( witsmlObj.getJSONString("1.4.1.1") );

        removeNullsFrom(objLogJO);

        String removedEmpties = JsonUtil.removeEmpties(objLogJO);
        //removeEmptyFields((ObjectNode)objLogJO);


        renameString("pass", "passNumber", objLogJO);
        renameString("indexType", "timeDepth", objLogJO);
        renameString("serviceCompany", "loggingCompanyName", objLogJO);

        // ****************************** citation created JSON object ********************************
        boolean createdAnObject = false;
        if ( objLogJO.has("name") && !objLogJO.get("name").equals(null) ) {
            if (!createdAnObject) {
                workObj = new JSONObject();
                createdAnObject = true;
            }
            workObj.put("title", objLogJO.getString("name"));
            objLogJO.remove("name");
        }
        if ( objLogJO.has("description") && !objLogJO.get("description").equals(null) ) {
            if (!createdAnObject) {
                workObj = new JSONObject();
                createdAnObject = true;
            }
            workObj.put("description", objLogJO.getString("description"));
            objLogJO.remove("description");
        }
        if ( createdAnObject ) {
            objLogJO.put("citation", workObj);
            createdAnObject = false;
        }
        // ********************************** hardcoded logParam ************************************
        // Biz Rule: When logParam is present, the "index" field is required; otherwise, API fails
        //           Still debugging why logParam comes in garbled with tab + 2 newlines
        //           TEMPORARY WORKAROUND
        if (objLogJO.has("logParam")) {
            objLogJO.remove("logParam");
        }
        // ========================================= channels =========================================
        // NOTE: Since ChannelSet will be created first, all data shown in the channels API that was
        // ***** created through the ChannelSet does not need to be added/updated again.
        if ( objLogJO.has("logCurveInfo")
                && objLogJO.getJSONArray("logCurveInfo").length() > 0 ) {

        // move all "logCurveInfo" into a JSON array for asynchronous processing
        JSONArray workArray = objLogJO.getJSONArray("logCurveInfo");
        objLogJO.remove("logCurveInfo");

        AsyncConvertToChannel asyncConvertToChannel = new AsyncConvertToChannel();
        for (int i=0; i<workArray.length(); ++i) {
            JsonUtil.removeEmpties(workArray.getJSONObject(i));
            asyncConvertToChannel.convertToChannel1411(workArray.getJSONObject(i), objLogJO);
        }
        // put the converted logCurveInfo back
        objLogJO.put("logCurveInfo", workArray);
    } // End of logCurveInfo

    // Now the JSON should be mapped according to DoT's API expectations
    // Those expectations match the POJOs generated from DoT's API
    // TODO Create the POJOs using Jackson

    // return the payloads for creating a 1.4.1.1 ChannelSet & updating the Channel Set
    // with 1.4.1.1 Channel data
    return objLogJO;
}

    protected static void renameString(String oldName, String newName, JSONObject objForRename) {
        if (objForRename.has(oldName)) {
            String passValue = objForRename.getString(oldName);
            objForRename.remove(oldName);
            objForRename.put(newName, passValue);
        }
    }
    // ********************************************************************************************
    public static void removeNullsFrom(@Nullable JSONObject object) throws JSONException {
        if (object != null) {
            Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object o = object.get(key);
                if (o == null || o == JSONObject.NULL) {
                    iterator.remove();
                } else {
                    removeNullsFrom(o);
                }
            }
        }
    }

    public static void removeNullsFrom(@Nullable JSONArray array) throws JSONException {
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                Object o = array.get(i);
                if (o == null || o == JSONObject.NULL) {
                    array.remove(i);
                } else {
                    removeNullsFrom(o);
                }
            }
        }
    }

    public static void removeNullsFrom(@NonNull Object o)
                                    throws JSONException {
        if (o instanceof JSONObject) {
            removeNullsFrom((JSONObject) o);
        } else if (o instanceof JSONArray) {
            removeNullsFrom((JSONArray) o);
        }
    }

    public static JsonNode toJsonNode(JsonObject jsonObject)
                                    throws IOException {

        // Parse a JsonObject into a JSON string
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.writeObject(jsonObject);
        }
        String json = stringWriter.toString();

        // Parse a JSON string into a JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        return jsonNode;
    }

    /**
     * Removes empty fields from the given JSON object node.
     * @param jsonNode an object node
     * @return the object node with empty fields removed
     */
    public static ObjectNode removeEmptyFields(final ObjectNode jsonNode) {
        ObjectNode ret = new ObjectMapper().createObjectNode();
        Iterator<Map.Entry<String, JsonNode>> iter = jsonNode.fields();

        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();
            String key = entry.getKey();
            JsonNode value = entry.getValue();

            if (value instanceof ObjectNode) {
                Map<String, ObjectNode> map = new HashMap<String, ObjectNode>();
                map.put(key, removeEmptyFields((ObjectNode)value));
                ret.setAll(map);
            }
            else if (value instanceof ArrayNode) {
                ret.set(key, removeEmptyFields((ArrayNode)value));
            }
            else if (value.asText() != null && !value.asText().isEmpty()) {
                ret.set(key, value);
            }
        }

        return ret;
    }

    /**
     * Removes empty fields from the given JSON array node.
     * @param array an array node
     * @return the array node with empty fields removed
     */
    public static ArrayNode removeEmptyFields(ArrayNode array) {
        ArrayNode ret = new ObjectMapper().createArrayNode();
        Iterator<JsonNode> iter = array.elements();

        while (iter.hasNext()) {
            JsonNode value = iter.next();

            if (value instanceof ArrayNode) {
                ret.add(removeEmptyFields((ArrayNode)(value)));
            }
            else if (value instanceof ObjectNode) {
                ret.add(removeEmptyFields((ObjectNode)(value)));
            }
            else if (value != null && !value.textValue().isEmpty()){
                ret.add(value);
            }
        }

        return ret;
    }
}