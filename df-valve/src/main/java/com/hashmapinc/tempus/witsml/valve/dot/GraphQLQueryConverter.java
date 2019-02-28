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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

//This class takes a WITSML Query search and converts it to a GraphQL search
class GraphQLQueryConverter {

    private final String delimiter = " ";
    private final QueryBuilder builder = new QueryBuilder();

    /**
     * Converts a WITSML search query to GraphQL
     *
     * @param wmlObject The search object
     * @return the object
     * @throws IOException Thrown if there is an error in creation of the query
     */
    public String convertQuery(AbstractWitsmlObject wmlObject) throws IOException {
        switch (wmlObject.getObjectType()){
            case "well":
                this.createWellQuery(wmlObject.getJSONString("1.4.1.1"));
                break;
            case "wellbore":
                this.createWellboreQuery(wmlObject.getJSONString("1.4.1.1"));
                break;
            case "trajectory":
                String json1411 = wmlObject.getJSONString("1.4.1.1"); // needed for some query args. some fields are lost in 2.0
                String json20 = wmlObject.getJSONString("2.0");
                return getTrajectoryQuery(json1411, json20);
            default:
                return null;
        }
        return this.builder.GetGraphQLQuery();
    }

    /**
     * this function converts 2.0 trajectory json
     * into a graphql query "select *"-style query
     *
     * @param jsonString1411 - json 1411 string of object to query for
     * @param jsonString20 - json 20 string of object to query for
     *
     * @return graphql query string
     */
    private static String getTrajectoryQuery(String jsonString1411, String jsonString20) {
        // payload json object for building full query
        JSONObject payload = new JSONObject();

        // parse json strings
        JSONObject objTrajectoryJson = new JSONObject(jsonString1411); // 1.4.1.1
        JSONObject trajectoryJson = new JSONObject(jsonString20); // 2.0

        // ====================================================================
        // get trajectory query fields
        // ====================================================================
        JSONObject trajQueryFields = new JSONObject();
        // uuid
        if (trajectoryJson.has("uuid") && !JsonUtil.isEmpty(trajectoryJson.get("uuid")))
            trajQueryFields.put("uuid", trajectoryJson.get("uuid"));

        // uuidWell
        if (trajectoryJson.has("uuidWell") && !JsonUtil.isEmpty(trajectoryJson.get("uuidWell")))
            trajQueryFields.put("uuidWell", trajectoryJson.get("uuidWell"));

        // uuidWellbore
        if (trajectoryJson.has("uuidWellbore") && !JsonUtil.isEmpty(trajectoryJson.get("uuidWellbore")))
            trajQueryFields.put("uuidWellbore", trajectoryJson.get("uuidWellbore"));

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
        if (!JsonUtil.isEmpty(stationsJson) && stationsJson.getJSONObject(0).get("uid") != null) {
            // get list of UIDs
            JSONArray stationUids = new JSONArray();
            for (int i = 0; i < stationsJson.length(); i++) {
                stationUids.put(stationsJson.getJSONObject(i).getString("uid"));
            }
            stationQueryFields.put("uids", stationUids);
        // non uid-based station query
        } else {
            // lastUpdateTimeUtc
            if (trajectoryJson.has("lastUpdateTimeUtc") && !JsonUtil.isEmpty(trajectoryJson.get("lastUpdateTimeUtc")))
                stationQueryFields.put("lastUpdateTimeUtc", trajectoryJson.get("lastUpdateTimeUtc"));

            // mdMn
            if (trajectoryJson.has("mdMn") && !JsonUtil.isEmpty(trajectoryJson.get("mdMn")))
                stationQueryFields.put("mdMn", trajectoryJson.get("mdMn"));

            // mdMx
            if (trajectoryJson.has("mdMx") && !JsonUtil.isEmpty(trajectoryJson.get("mdMx")))
                stationQueryFields.put("mdMx", trajectoryJson.get("mdMx"));
        }

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

    private void createWellQuery(String jsonObj) throws IOException{
        JSONObject obj = new JSONObject(jsonObj);
        List<String> keysToOmit = new ArrayList<>();
        Map<String, String> keysToRename = new HashMap<>();
        keysToOmit.add("customData");
        keysToOmit.add("commonData");
        keysToOmit.add("dTimLastChange");
        keysToOmit.add("dTimCreation");
        keysToOmit.add("defaultDatum");
        StringBuilder querybuilder = new StringBuilder();
        querybuilder.append("query WellQuery($wellArgument: WellArgument) ");
        this.builder.addVariableGroup("wellArgument");
        querybuilder.append(delimiter);
        querybuilder.append("{");
        querybuilder.append(delimiter);
        querybuilder.append("wells(wellArgument: $wellArgument)");
        querybuilder.append("{");
        querybuilder.append(delimiter);
        String indentStr = "";
        querybuilder.append(this.getQuery(obj, indentStr, "well", keysToOmit, "",keysToRename));
        querybuilder.append(delimiter);
        querybuilder.append("}");
        querybuilder.append(delimiter);
        querybuilder.append("}");
        this.builder.setQuery(querybuilder.toString());
    }

    private void createWellboreQuery(String jsonObj) throws IOException{
        JSONObject obj = new JSONObject(jsonObj);
        List<String> keysToOmit = new ArrayList<>();
        Map<String, String> keysToRename = new HashMap<>();
        keysToOmit.add("parentUid");
        keysToOmit.add("customData");
        keysToOmit.add("dTimLastChange");
        keysToOmit.add("dTimCreation");
        keysToOmit.add("defaultDatum");
        keysToRename.put("/parentWellbore/value", "title");
        keysToOmit.add("extensionAny");
        StringBuilder querybuilder = new StringBuilder();
        querybuilder.append("query WellboreQuery($wellboreArgument: WellboreArgument) ");
        this.builder.addVariableGroup("wellboreArgument");
        querybuilder.append(delimiter);
        querybuilder.append("{");
        querybuilder.append(delimiter);
        querybuilder.append("wellbores(wellboreArgument: $wellboreArgument)");
        querybuilder.append("{");
        querybuilder.append(delimiter);
        String indentStr = "";
        querybuilder.append(this.getQuery(obj, indentStr, "wellbore", keysToOmit, "", keysToRename));
        querybuilder.append(delimiter);
        querybuilder.append("}");
        querybuilder.append(delimiter);
        querybuilder.append("}");
        this.builder.setQuery(querybuilder.toString());
    }

    private String getQuery(JSONObject jsonWitsml, String indent, String wmlObjType, List<String> keysToOmit, String currentPath, Map<String, String> keysToRename) {
        Set<String> keyset = jsonWitsml.keySet();
        ArrayList<String> queryKeys = new ArrayList<>();
        HashMap variables = new HashMap();
        for (String key : keyset) {
            String localPath = currentPath + "/" + key;
            if (keysToOmit.contains(key)){
                continue;
            }
            Object queryObj = jsonWitsml.get(key);
            if (queryObj instanceof JSONObject) {
                JSONObject subObj = (JSONObject)queryObj;
                queryKeys.add(indent + key);
                queryKeys.add(indent + "{");
                queryKeys.add(this.getQuery(subObj, indent, wmlObjType, keysToOmit, localPath, keysToRename));
                queryKeys.add(indent + "}");
                continue;
            }
            if (queryObj instanceof JSONArray) {
                JSONArray queryArray = (JSONArray)queryObj;
                for (int i = 0; i < queryArray.length(); ++i) {
                    Object arrObj = queryArray.get(i);
                    if (arrObj instanceof JSONObject) {
                        queryKeys.add(indent + key);
                        JSONObject subObj = (JSONObject) arrObj;
                        queryKeys.add(indent + "{");
                        queryKeys.add(this.getQuery(subObj, indent, wmlObjType, keysToOmit, localPath, keysToRename));
                        queryKeys.add(indent + "}");
                    }
                }
                continue;
            }
            String value = jsonWitsml.get(key).toString();
            if (!"".equals(value) && !"null".equals(value) && !keysToOmit.contains(key)) {
                this.builder.addVariable(wmlObjType + "Argument", key, value);
            }
            if (keysToRename.containsKey(localPath)){
                key = keysToRename.get(localPath);
            }
            queryKeys.add(indent + key);
        }
        for (String key : keysToOmit){
            queryKeys.remove(key);
        }
        return String.join(this.delimiter, queryKeys);
    }

    // Internal class that handles the structuring and serialization of the GraphQL query
    class QueryBuilder
    {
        private String query;
        private Map<String, Map<String, String>> variables = new HashMap();

        public String getQuery() { return query; }

        public void setQuery(String query)
        {
            this.query = query;
        }

        public Map<String, Map<String, String>> getVariables() {
            return variables;
        }

        public void addVariableGroup(String group) {
            variables.put(group, new HashMap());
        }

        public void addVariable(String group, String key, String value) {
            ((Map)variables.get(group)).put(key, value);
        }

        public String GetGraphQLQuery() throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            StringWriter query = new StringWriter();
            objectMapper.writeValue(query, this);
            return query.toString();
        }
    }
}
