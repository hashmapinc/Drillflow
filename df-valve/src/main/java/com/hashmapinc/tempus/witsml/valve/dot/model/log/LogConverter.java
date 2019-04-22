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

import com.hashmapinc.tempus.witsml.valve.dot.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


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
    public static JSONObject convertToChannelSet1411( com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog witsmlObj ) {
        List<JSONObject> jsonItems;

        objLogJO = new JSONObject( witsmlObj.getJSONString("1.4.1.1") );
        String removedEmpties = JsonUtil.removeEmpties(objLogJO);

        renameString("pass", "passNumber");
        renameString("indexType", "timeDepth");
        renameString("serviceCompany", "loggingCompanyName");

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

    private static void renameString(String oldName, String newName) {
        if (objLogJO.has(oldName)) {
            String passValue = objLogJO.getString(oldName);
            objLogJO.remove(oldName);
            objLogJO.put(newName, passValue);
        }
    }
    // ********************************************************************************************

}
