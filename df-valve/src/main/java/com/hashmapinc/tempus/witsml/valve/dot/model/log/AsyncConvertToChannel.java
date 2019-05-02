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

    private JSONObject channelItemGlobal;
    private JSONObject objLogJOGlobal;

    @Async
    public CompletableFuture<JSONObject> convertToChannel1411(
            JSONObject channelItem, JSONObject objLogJO) {

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
        if (objLogJO.has("timeDepth")) {
            channelItem.put("timeDepth", objLogJO.getString("timeDepth"));
        }

        // JAXB transform of WITSML XML "mnemonic" was performed & an object containing
        // ************** "naming system" and "value" resulted; this is a mandatory
        //                DoT object & eventually, "mnemonic" needs to be created with
        //                just the "value" as it is a mandatory DoT object, also.
        String mnemonicToUse = "";
        if (channelItem.has("mnemonic")) {
            JSONObject workJO = channelItem.getJSONObject("mnemonic");
            if (workJO.has("namingSystem")) {
                Object namingSys = workJO.get("namingSystem");
                channelItem.getJSONObject("mnemonic").put("namingSystem", namingSys);
            } else {
                channelItem.getJSONObject("mnemonic").put("namingSystem", "");
            }
            channelItem.put("mnemAlias", channelItem.getJSONObject("mnemonic"));
            JSONObject temp = channelItem.getJSONObject("mnemAlias");
            mnemonicToUse = temp.getString("value");
            channelItem.remove("mnemonic");
        }

        // ************ index created JSON array ************
        channelItemGlobal = channelItem;
        objLogJOGlobal = objLogJO;
        createJSONArray();
        // ********** citation created JSON object **********
        createJSONObject();

        channelItemGlobal.put("mnemonic", mnemonicToUse);
        return CompletableFuture.completedFuture(channelItemGlobal);
    }

    // ********** citation created JSON object **********
    private void createJSONObject() {
        JSONObject workObj;
        if (channelItemGlobal.has("name")) {
            workObj = new JSONObject();
            workObj.put("title", channelItemGlobal.get("name"));
            channelItemGlobal.remove("name");
            channelItemGlobal.put("citation", workObj);
        } else {
            // kludge for now since the incoming WITSML
            // object has no name for logCurveInfo
            workObj = new JSONObject();
            workObj.put("title", "TQA");
            channelItemGlobal.put("citation", workObj);
        }
    }

    // ************** index created JSON array **************
    private void createJSONArray() {
        JSONArray workArray = new JSONArray();
        JSONObject workObj = new JSONObject();
        // typeLogData was converted to dataType by marshal;
        // however, still need it within index as uom
        if (channelItemGlobal.has("dataType")) {
            workObj.put("uom",
                    channelItemGlobal.get("dataType"));
        }
        if (objLogJOGlobal.has("timeDepth")) {
            workObj.put("indexType",
                    objLogJOGlobal.getString("timeDepth"));
        }
        if (objLogJOGlobal.has("direction")) {
            workObj.put("direction",
                    objLogJOGlobal.getString("direction"));
        }
        if (objLogJOGlobal.has("indexCurve")) {
            workObj.put("mnemonic",
                    objLogJOGlobal.getString("indexCurve"));
        } else {
            workObj.put("mnemonic", "index");
        }
        // Kludges
        workObj.put("direction","increasing");
            workArray.put(workObj);
            channelItemGlobal.put("index", workArray);
    }



    private static void renameString(String oldName,
                                     String newName,
                                     JSONObject jsonItem) {
        if (jsonItem.has(oldName)) {
            String passValue = jsonItem.getString(oldName);
            jsonItem.remove(oldName);
            jsonItem.put(newName, passValue);
        }
    }
}
