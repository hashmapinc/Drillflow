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
package com.hashmapinc.tempus.witsml.valve.dot;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

//This class takes a WITSML Query search and converts it to a GraphQL search
class GraphQLQueryConverter {

    /**
     * Converts a WITSML search query to GraphQL
     *
     * @param wmlObject The search object
     * @return the object
     * @throws IOException Thrown if there is an error in creation of the query
     */
    public static String getQuery(AbstractWitsmlObject wmlObject) throws IOException {
        switch (wmlObject.getObjectType()){
            case "well":
                return getWellQuery(wmlObject);
            case "wellbore":
                return createWellboreQuery(wmlObject.getJSONString("1.4.1.1"));
            default:
                return null;
        }
    }

    public static String getQuery(AbstractWitsmlObject wmlObject, String uuid){
        return getTrajectoryQuery(wmlObject, uuid);
    }

    /**
     * this function converts 2.0 trajectory json
     * into a graphql query "select *"-style query
     *
     * @param wmlObject - the trajectory awo build a query from
     *
     * @return graphql query string
     */
    private static String getTrajectoryQuery(AbstractWitsmlObject wmlObject, String wbUuid) {
        // payload json object for building full query
        JSONObject payload = new JSONObject();

        // parse json strings
        String jsonString1411 =         wmlObject.getJSONString("1.4.1.1"); // needed for some query args. some fields are lost in 2.0
        String jsonString20 =           wmlObject.getJSONString("2.0");
        JSONObject objTrajectoryJson =  new JSONObject(jsonString1411); // 1.4.1.1
        JSONObject trajectoryJson =     new JSONObject(jsonString20); // 2.0

        // ====================================================================
        // get trajectory query fields
        // ====================================================================
        JSONObject trajQueryFields = new JSONObject();
        // uuidWellbore
        // TODO: check for empty wbUuid
        trajQueryFields.put("uuidWellbore", wbUuid);

        // uuid
        if (trajectoryJson.has("uuid") && !JsonUtil.isEmpty(trajectoryJson.get("uuid")))
            trajQueryFields.put("uuid", trajectoryJson.get("uuid"));

        // uuidWell
        if (trajectoryJson.has("uuidWell") && !JsonUtil.isEmpty(trajectoryJson.get("uuidWell")))
            trajQueryFields.put("uuidWell", trajectoryJson.get("uuidWell"));

        // name
        if (objTrajectoryJson.has("name") && !JsonUtil.isEmpty(objTrajectoryJson.get("name")))
            trajQueryFields.put("name", objTrajectoryJson.get("name"));

        // nameWell
        if (objTrajectoryJson.has("nameWell") && !JsonUtil.isEmpty(objTrajectoryJson.get("nameWell")))
            trajQueryFields.put("nameWell", objTrajectoryJson.get("nameWell"));

        // nameWellbore
        if (objTrajectoryJson.has("nameWellbore") && !JsonUtil.isEmpty(objTrajectoryJson.get("nameWellbore")))
            trajQueryFields.put("nameWellbore", objTrajectoryJson.get("nameWellbore"));

        // growingStatus
        if (trajectoryJson.has("growingStatus") && !JsonUtil.isEmpty(trajectoryJson.get("growingStatus")))
            trajQueryFields.put("growingStatus", trajectoryJson.get("growingStatus"));

        // lastUpdateTimeUtc
        if (trajectoryJson.has("lastUpdateTimeUtc") && !JsonUtil.isEmpty(trajectoryJson.get("lastUpdateTimeUtc")))
            trajQueryFields.put("lastUpdateTimeUtc", trajectoryJson.get("lastUpdateTimeUtc"));
        // ====================================================================


        // ====================================================================
        // get trajectory station query fields
        // ====================================================================
        JSONObject stationQueryFields = new JSONObject();
        JSONArray stationsJson = (
                trajectoryJson.has("trajectoryStation")
                        ? trajectoryJson.getJSONArray("trajectoryStation")
                        : new JSONArray()
        );

        // support query by uid OR query by filters, not both
        // uid-based station query
        /*if (!JsonUtil.isEmpty(stationsJson) && stationsJson.getJSONObject(0).get("uid") != null) {
            // get list of UIDs
            JSONArray stationUids = new JSONArray();
            for (int i = 0; i < stationsJson.length(); i++) {
                stationUids.put(stationsJson.getJSONObject(i).getString("uid"));
            }
            stationQueryFields.put("uids", stationUids);
        // non uid-based station query
        } else {*/
        // lastUpdateTimeUtc
        if (trajectoryJson.has("lastUpdateTimeUtc") && !JsonUtil.isEmpty(trajectoryJson.get("lastUpdateTimeUtc")))
            stationQueryFields.put("lastUpdateTimeUtc", trajectoryJson.get("lastUpdateTimeUtc"));

        // mdMn
        if (trajectoryJson.has("mdMn") && !JsonUtil.isEmpty(trajectoryJson.get("mdMn")))
            stationQueryFields.put("mdMn", trajectoryJson.get("mdMn"));

        // mdMx
        if (trajectoryJson.has("mdMx") && !JsonUtil.isEmpty(trajectoryJson.get("mdMx")))
            stationQueryFields.put("mdMx", trajectoryJson.get("mdMx"));
        //}

        // ====================================================================

        // build variables section
        JSONObject variables = new JSONObject();
        variables.put("trajArg", trajQueryFields);
        variables.put("trajStationArg", stationQueryFields);
        payload.put("variables", variables);

        // build query section of payload
        payload.put("query", GraphQLQueryConstants.TRAJECTORY_QUERY);

        // return payload
        return payload.toString(2);
    }

    private static String getWellQuery(AbstractWitsmlObject wmlObject){
        // payload json object for building full query
        JSONObject payload = new JSONObject();

        // parse json strings
        String jsonString1411 = wmlObject.getJSONString("1.4.1.1");
        JSONObject wellJson = new JSONObject(jsonString1411);

        // ====================================================================
        // get well query fields
        // ====================================================================
        JSONObject wellQueryFields = new JSONObject();
        // uid
        if (wellJson.has("uid") && !JsonUtil.isEmpty(wellJson.get("uid")))
            wellQueryFields.put("uid", wellJson.get("uid"));

        // name
        if (wellJson.has("name") && !JsonUtil.isEmpty(wellJson.get("name")))
            wellQueryFields.put("name", wellJson.get("name"));

        // numGovt
        if (wellJson.has("numGovt") && !JsonUtil.isEmpty(wellJson.get("numGovt")))
            wellQueryFields.put("numGovt", wellJson.get("numGovt"));

        // numAPI
        if (wellJson.has("numAPI") && !JsonUtil.isEmpty(wellJson.get("numAPI")))
            wellQueryFields.put("numAPI", wellJson.get("numAPI"));

        // dTimLastChange
        if (wellJson.has("commonData")){
            JSONObject commonData = (JSONObject)wellJson.get("commonData");
            if (commonData.has("dTimLastChange") && !JsonUtil.isEmpty(commonData.get("dTimLastChange")))
                wellQueryFields.put("lastUpdateTimeUtc", commonData.get("dTimLastChange"));
        }

        // build variables section
        JSONObject variables = new JSONObject();
        variables.put("wellArgument", wellQueryFields);
        payload.put("variables", variables);

        // build query section of payload
        payload.put("query", GraphQLQueryConstants.WELL_QUERY);

        // return payload
        return payload.toString(2);
    }

    public static String getUidUUIDMappingQuery(AbstractWitsmlObject wmlObject){
        JSONObject payload = new JSONObject();

        // parse json strings
        String jsonString1411 = wmlObject.getJSONString("1.4.1.1");
        JSONObject wmlObjJson = new JSONObject(jsonString1411);

        // ====================================================================
        // get trajectory query fields
        // ====================================================================
        JSONObject wellQueryFields = new JSONObject();
        // uidWellbore
        if (wmlObjJson.has("uidWellbore") && !JsonUtil.isEmpty(wmlObjJson.get("uidWellbore")))
            wellQueryFields.put("uid", wmlObjJson.get("uidWellbore"));

        // uidWell
        if (wmlObjJson.has("uidWell") && !JsonUtil.isEmpty(wmlObjJson.get("uidWell")))
            wellQueryFields.put("uidWell", wmlObjJson.get("uidWell"));

        // build variables section
        JSONObject variables = new JSONObject();
        variables.put("arg", wellQueryFields);
        payload.put("variables", variables);

        // build query section of payload
        payload.put("query", GraphQLQueryConstants.WELLBORE_UID_MAPPING_QUERY);

        // return payload
        return payload.toString(2);

    }

    private static String createWellboreQuery(String jsonObj) {

        // ====================================================================
        // get wellbore query fields
        // ====================================================================
        JSONObject wellboreQueryFields = new JSONObject();
        JSONObject wellboreJson = new JSONObject(jsonObj);
        // uid
        if (wellboreJson.has("uid") && !JsonUtil.isEmpty(wellboreJson.get("uid")))
            wellboreQueryFields.put("uid", wellboreJson.get("uid"));

        // uidWell
        if (wellboreJson.has("uidWell") && !JsonUtil.isEmpty(wellboreJson.get("uidWell")))
            wellboreQueryFields.put("uidWell", wellboreJson.get("uidWell"));

        // name
        if (wellboreJson.has("name") && !JsonUtil.isEmpty(wellboreJson.get("name")))
            wellboreQueryFields.put("name", wellboreJson.get("name"));

        // nameWell
        if (wellboreJson.has("nameWell") && !JsonUtil.isEmpty(wellboreJson.get("nameWell")))
            wellboreQueryFields.put("nameWell", wellboreJson.get("nameWell"));

        JSONObject payload = new JSONObject();
        payload.put("query", GraphQLQueryConstants.WELLBORE_QUERY);

        // build variables section
        JSONObject variables = new JSONObject();
        variables.put("wellboreArgument",wellboreQueryFields);
        payload.put("variables", variables);

        // return full open query with no variables
        return payload.toString(2);
    }
}
