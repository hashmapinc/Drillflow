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

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AsyncConvertToChannel {

    @Async
    public CompletableFuture<JSONObject> convertToChannel1411(JSONObject channelItem, JSONObject objLogJO) {

        // TODO I do not find "mnemonic_value", "minIndex", "maxIndex" nor "columnIndex"
        //      within channel/View.data. If I change "mnemonic" to "mnemonic_value",
        //      I get a fail response from DoT.
        //renameObject("mnemonic_value", "mnemonic", channelItem);

        // transform names in channel object per DoT API/mapping documentation
        // *********
        renameString("unit", "uom", channelItem);
        renameString("curveDescription", "description", channelItem);
        renameString("typeLogData", "dataType", channelItem);

        // JAXB transform of WITSML XML "indexType" to "timeDepth" was performed;
        // ************** so put this value into the channel item per DoT API
        if (objLogJO.has("timeDepth")
                && !objLogJO.get("timeDepth").equals(null)) {
            channelItem.put("timeDepth", objLogJO.getString("timeDepth"));
        }

        // JAXB transform of WITSML XML "mnemonic" was performed & an object containing
        // ************** "naming system" and "value" resulted; this is a mandatory
        //                DoT object & eventually, "mnemonic" needs to be created with
        //                just the "value" as it is a mandatory DoT object, also
        String mnemonicToUse = "";
        if (channelItem.has("mnemonic")
                && !channelItem.get("mnemonic").equals(null)) {
            JSONObject workJO = channelItem.getJSONObject("mnemonic");
            // allows "null" to be picked up
            Object namingSys = workJO.get("namingSystem");
            channelItem.getJSONObject("mnemonic").put("namingSystem", namingSys);
            channelItem.put("mnemAlias", channelItem.getJSONObject("mnemonic"));
            JSONObject temp = channelItem.getJSONObject("mnemAlias");
            mnemonicToUse = temp.getString("value");
            channelItem.remove("mnemonic");
        }

        // ********************* index created JSON array ***********************
        boolean createdAnArray = false;
        JSONArray workArray = null;
        JSONObject workObj = null;
        // typeLogData was converted to dataType by marshal operation;
        // however, still need it within index as uom
        if (channelItem.has("dataType")
                && !channelItem.get("dataType").equals(null)) {
            workObj = new JSONObject();
            workArray = new JSONArray();
            workObj.put("uom", channelItem.get("dataType"));
            //workArray.put(workObj);
            createdAnArray = true;
        }
        if (objLogJO.has("timeDepth")
                && !objLogJO.get("timeDepth").equals(null)) {
            if (!createdAnArray) {
                workArray = new JSONArray();
                workObj = new JSONObject();
                createdAnArray = true;
            }
            workObj.put("indexType", objLogJO.getString("timeDepth"));
            //workArray.put(workObj);
        }
        if (objLogJO.has("direction")
                && !objLogJO.get("direction").equals(null)) {
            if (!createdAnArray) {
                workArray = new JSONArray();
                workObj = new JSONObject();
                createdAnArray = true;
            }
            workObj.put("direction", objLogJO.getString("direction"));
            //workArray.put(workObj);
        }
        if (objLogJO.has("indexCurve")
                && !objLogJO.get("indexCurve").equals(null)) {
            if (!createdAnArray) {
                workArray = new JSONArray();
                workObj = new JSONObject();
                createdAnArray = true;
            }
            workObj.put("mnemonic", objLogJO.getString("indexCurve"));
            //workArray.put(workObj);
        } else {
            if (!createdAnArray) {
                workArray = new JSONArray();
                workObj = new JSONObject();
                createdAnArray = true;
            }
            workObj.put("mnemonic", "index");
            //workArray.put(workObj);
        }
        // Kludges
        workObj.put("direction","increasing");
        if (createdAnArray) {
            workArray.put(workObj);
            channelItem.put("index", workArray);
        }

        // ********************* citation created JSON object ***********************
        if (channelItem.has("name")
                && !(channelItem.getString("name").equals(null))) {
            workObj = new JSONObject();
            workObj.put("title", channelItem.get("name"));
            channelItem.remove("name");
            channelItem.put("citation", workObj);
        } else {
            // kludge for now since the witsml object coming in already has no name
            // for logCurveInfo
            workObj = new JSONObject();
            workObj.put("title", "TQA");
            channelItem.put("citation", workObj);
        }

        channelItem.put("mnemonic", mnemonicToUse);
        return CompletableFuture.completedFuture(channelItem);

    }

    private static void renameString(String oldName, String newName, JSONObject jsonItem) {
        if (jsonItem.has(oldName)) {
            String passValue = jsonItem.getString(oldName);
            jsonItem.remove(oldName);
            jsonItem.put(newName, passValue);
        }
    }

    private static void renameObject(String oldName, String newName, JSONObject jsonItem) {
        if (jsonItem.has(oldName)) {
            JSONObject passValue = jsonItem.getJSONObject(oldName);
            jsonItem.remove(oldName);
            jsonItem.put(newName, passValue);
        }
    }
}
