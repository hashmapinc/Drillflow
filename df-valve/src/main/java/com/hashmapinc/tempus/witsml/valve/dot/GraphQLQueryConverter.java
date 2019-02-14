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
                String json = wmlObject.getJSONString("2.0");
                return getTrajectoryQuery(json);
            default:
                return null;
        }
        return this.builder.GetGraphQLQuery();
    }

    /**
     * this function converts 2.0 trajectory json
     * into a graphql query
     *
     * @param jsonString - json string version of the query to execute
     * @return graphql query string
     */
    private static String getTrajectoryQuery(String jsonString) {
        // payload json object for building full query
        JSONObject payload = new JSONObject();

        // parse json string
        JSONObject trajectoryJson = new JSONObject(jsonString);

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
        if (trajectoryJson.has("name") && !JsonUtil.isEmpty(trajectoryJson.get("name")))
            trajQueryFields.put("name", trajectoryJson.get("name"));

        // nameWell
        if (trajectoryJson.has("nameWell") && !JsonUtil.isEmpty(trajectoryJson.get("nameWell")))
            trajQueryFields.put("nameWell", trajectoryJson.get("nameWell"));

        // nameWellbore
        if (trajectoryJson.has("nameWellbore") && !JsonUtil.isEmpty(trajectoryJson.get("nameWellbore")))
            trajQueryFields.put("nameWellbore", trajectoryJson.get("nameWellbore"));

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
            ArrayList<String> stationUids = new ArrayList<>();
            for (int i = 0; i < stationsJson.length(); i++) {
                stationUids.add(stationsJson.getJSONObject(i).getString("uid"));
            }

        // non uid-based station query
        } else {

        }

        // lastUpdateTimeUtc
        if (trajectoryJson.has("uuid") && !JsonUtil.isEmpty(trajectoryJson.get("uuid")))
            trajQueryFields.put("uuid", trajectoryJson.get("uuid"));

        // mdMn
        if (trajectoryJson.has("uuid") && !JsonUtil.isEmpty(trajectoryJson.get("uuid")))
            trajQueryFields.put("uuid", trajectoryJson.get("uuid"));

        // mdMx
        if (trajectoryJson.has("uuid") && !JsonUtil.isEmpty(trajectoryJson.get("uuid")))
            trajQueryFields.put("uuid", trajectoryJson.get("uuid"));

        // ====================================================================


        // ====================================================================
        // get trajectory fields fragment (this is long and sucks, we know)
        // ====================================================================
        StringBuilder trajFieldsFragment = new StringBuilder();
        trajFieldsFragment.append("fragment trajFields on TrajectoryType {");

        if(trajectoryJson.has("aliases")) {
            trajFieldsFragment.append("    aliases {");
            if(JsonUtil.isEmpty(trajectoryJson.get("aliases")) || trajectoryJson.getJSONObject("aliases").has("authority"))
                trajFieldsFragment.append("        authority");
            if(JsonUtil.isEmpty(trajectoryJson.get("aliases")) || trajectoryJson.getJSONObject("aliases").has("description"))
                trajFieldsFragment.append("        description");
            if(JsonUtil.isEmpty(trajectoryJson.get("aliases")) || trajectoryJson.getJSONObject("aliases").has("identifier"))
                trajFieldsFragment.append("        identifier");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("aziRef"))
            trajFieldsFragment.append("    aziRef");

        if(trajectoryJson.has("aziVertSect")) {
            trajFieldsFragment.append("    aziVertSect {");
            if(JsonUtil.isEmpty(trajectoryJson.get("aziVertSect")) || trajectoryJson.getJSONObject("aziVertSect").has("uom"))
                trajFieldsFragment.append("        uom");
            if(JsonUtil.isEmpty(trajectoryJson.get("aziVertSect")) || trajectoryJson.getJSONObject("aziVertSect").has("value"))
                trajFieldsFragment.append("        value");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("citation")) {
            trajFieldsFragment.append("    citation {");
            if(JsonUtil.isEmpty(trajectoryJson.get("citation")) || trajectoryJson.getJSONObject("citation").has("creation"))
                trajFieldsFragment.append("        creation");
            if(JsonUtil.isEmpty(trajectoryJson.get("citation")) || trajectoryJson.getJSONObject("citation").has("description"))
                trajFieldsFragment.append("        description");
            if(JsonUtil.isEmpty(trajectoryJson.get("citation")) || trajectoryJson.getJSONObject("citation").has("descriptiveKeywords"))
                trajFieldsFragment.append("        descriptiveKeywords");
            if(JsonUtil.isEmpty(trajectoryJson.get("citation")) || trajectoryJson.getJSONObject("citation").has("editor"))
                trajFieldsFragment.append("        editor");
            if(JsonUtil.isEmpty(trajectoryJson.get("citation")) || trajectoryJson.getJSONObject("citation").has("format"))
                trajFieldsFragment.append("        format");
            if(JsonUtil.isEmpty(trajectoryJson.get("citation")) || trajectoryJson.getJSONObject("citation").has("lastUpdate"))
                trajFieldsFragment.append("        lastUpdate");
            if(JsonUtil.isEmpty(trajectoryJson.get("citation")) || trajectoryJson.getJSONObject("citation").has("originator"))
                trajFieldsFragment.append("        originator");
            if(JsonUtil.isEmpty(trajectoryJson.get("citation")) || trajectoryJson.getJSONObject("citation").has("title"))
                trajFieldsFragment.append("        title");
            if(JsonUtil.isEmpty(trajectoryJson.get("citation")) || trajectoryJson.getJSONObject("citation").has("versionString"))
                trajFieldsFragment.append("        versionString");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("commonData")) {
            trajFieldsFragment.append("    commonData {");

            if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) || trajectoryJson.getJSONObject("commonData").has("acquisitionTimeZone")) {
                trajFieldsFragment.append("        acquisitionTimeZone {");
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("acquisitionTimeZone")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONObject("acquisitionTimeZone").has("dTim")) {
                    trajFieldsFragment.append("            dTim");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("acquisitionTimeZone")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONObject("acquisitionTimeZone").has("value")) {
                    trajFieldsFragment.append("            value");
                }
                trajFieldsFragment.append("        }");
            }

            if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) || trajectoryJson.getJSONObject("commonData").has("comments"))
                trajFieldsFragment.append("        comments");


            if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) || trajectoryJson.getJSONObject("commonData").has("defaultDatum")) {
                trajFieldsFragment.append("        defaultDatum {");
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("defaultDatum")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONObject("defaultDatum").has("uidRef")) {
                    trajFieldsFragment.append("            uidRef");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("defaultDatum")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONObject("defaultDatum").has("value")) {
                    trajFieldsFragment.append("            value");
                }
                trajFieldsFragment.append("        }");
            }

            if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) || trajectoryJson.getJSONObject("commonData").has("extensionAny"))
                trajFieldsFragment.append("        extensionAny");

            if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) || trajectoryJson.getJSONObject("commonData").has("extensionNameValue")) {
                trajFieldsFragment.append("        extensionNameValue {");
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONObject("extensionNameValue").has("dataType")) {
                    trajFieldsFragment.append("            dataType");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONObject("extensionNameValue").has("description")) {
                    trajFieldsFragment.append("            description");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONObject("extensionNameValue").has("dTim")) {
                    trajFieldsFragment.append("            dTim");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONObject("extensionNameValue").has("index")) {
                    trajFieldsFragment.append("            index");
                }

                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONObject("extensionNameValue").has("md")) {
                    trajFieldsFragment.append("            md {");
                    if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").getJSONObject("extensionNameValue").get("md")) ||
                            trajectoryJson.getJSONObject("commonData").getJSONObject("extensionNameValue").getJSONObject("md").has("datum")) {
                        trajFieldsFragment.append("                datum");
                    }if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").getJSONObject("extensionNameValue").get("md")) ||
                            trajectoryJson.getJSONObject("commonData").getJSONObject("extensionNameValue").getJSONObject("md").has("uom")) {
                        trajFieldsFragment.append("                uom");
                    }if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").getJSONObject("extensionNameValue").get("md")) ||
                            trajectoryJson.getJSONObject("commonData").getJSONObject("extensionNameValue").getJSONObject("md").has("value")) {
                        trajFieldsFragment.append("                value");
                    }
                    trajFieldsFragment.append("            }");
                }

                trajFieldsFragment.append("            measureClass");
                trajFieldsFragment.append("            name");
                trajFieldsFragment.append("            uid");
                trajFieldsFragment.append("            value {");
                trajFieldsFragment.append("                uom");
                trajFieldsFragment.append("                value");
                trajFieldsFragment.append("            }");
                trajFieldsFragment.append("        }");
            }

            if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) || trajectoryJson.getJSONObject("commonData").has("itemState"))
                trajFieldsFragment.append("        itemState");

            if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) || trajectoryJson.getJSONObject("commonData").has("privateGroupOnly"))
                trajFieldsFragment.append("        privateGroupOnly");

            if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) || trajectoryJson.getJSONObject("commonData").has("serviceCategory"))
                trajFieldsFragment.append("        serviceCategory");

            if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) || trajectoryJson.getJSONObject("commonData").has("sourceName"))
                trajFieldsFragment.append("        sourceName");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    creationTimeUtc");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    customData");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    definitive");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    dispEwVertSectOrig {");
        trajFieldsFragment.append("        uom");
        trajFieldsFragment.append("        value");
        trajFieldsFragment.append("    }");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    dispNsVertSectOrig {");
        trajFieldsFragment.append("        uom");
        trajFieldsFragment.append("        value");
        trajFieldsFragment.append("    }");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    dTimTrajEnd");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    dTimTrajStart");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    existenceKind");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    extensionNameValue {");
        trajFieldsFragment.append("        dataType");
        trajFieldsFragment.append("        description");
        trajFieldsFragment.append("        dTim");
        trajFieldsFragment.append("        index");
        trajFieldsFragment.append("        md {");
        trajFieldsFragment.append("            datum");
        trajFieldsFragment.append("            uom");
        trajFieldsFragment.append("            value");
        trajFieldsFragment.append("        }");
        trajFieldsFragment.append("        measureClass");
        trajFieldsFragment.append("        name");
        trajFieldsFragment.append("        uid");
        trajFieldsFragment.append("        value {");
        trajFieldsFragment.append("            uom");
        trajFieldsFragment.append("            value");
        trajFieldsFragment.append("        }");
        trajFieldsFragment.append("    }");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    finalTraj");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    gridConUsed {");
        trajFieldsFragment.append("        uom");
        trajFieldsFragment.append("        value");
        trajFieldsFragment.append("    }");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    gridCorUsed {");
        trajFieldsFragment.append("        uom");
        trajFieldsFragment.append("        value");
        trajFieldsFragment.append("    }");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    growingStatus");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    lastUpdateTimeUtc");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    magDeclUsed {");
        trajFieldsFragment.append("        uom");
        trajFieldsFragment.append("        value");
        trajFieldsFragment.append("    }");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    mdMn {");
        trajFieldsFragment.append("        datum");
        trajFieldsFragment.append("        uom");
        trajFieldsFragment.append("        value");
        trajFieldsFragment.append("    }");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    mdMx {");
        trajFieldsFragment.append("        datum");
        trajFieldsFragment.append("        uom");
        trajFieldsFragment.append("        value");
        trajFieldsFragment.append("    }");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    memory");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    name");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    nameWell");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    nameWellbore");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    objectVersion");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    parentTrajectory {");
        trajFieldsFragment.append("        contentType");
        trajFieldsFragment.append("        title");
        trajFieldsFragment.append("        uidRef");
        trajFieldsFragment.append("        uri");
        trajFieldsFragment.append("        uuid");
        trajFieldsFragment.append("        uuidAuthority");
        trajFieldsFragment.append("        versionString");
        trajFieldsFragment.append("    }");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    schemaVersion");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    serviceCompany");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    uid");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    uuid");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    uuidWell");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    uuidWellbore");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    wellbore {");
        trajFieldsFragment.append("        contentType");
        trajFieldsFragment.append("        title");
        trajFieldsFragment.append("        uidRef");
        trajFieldsFragment.append("        uri");
        trajFieldsFragment.append("        uuid");
        trajFieldsFragment.append("        uuidAuthority");
        trajFieldsFragment.append("        versionString");
        trajFieldsFragment.append("    }");
        if(trajectoryJson.has("asdfasdfasdf"))
            trajFieldsFragment.append("    trajectoryStation(trajectoryStationArgument: $trajStationArg) {");
        trajFieldsFragment.append("        ...stationFields");
        trajFieldsFragment.append("    }");
        trajFieldsFragment.append("}");
        // ====================================================================


        // get station response fields
        StringBuilder stationFieldsFragment = new StringBuilder();


        // build variables section
        payload.put("variables", variables);

        // build query section of payload
        String queryString = String.join("\n",
                "query TrajectoryQuery($trajArg: TrajectoryArgument, $trajStationArg: TrajectoryStationArgument) {",
                "	trajectories(trajectoryArgument: $trajArg) {",
                "		...trajFields",
                "	}",
                "}",
                "",
                "fragment trajFields on TrajectoryType {",
                String.join("\n    ", trajFields),
                "    trajectoryStation(trajectoryStationArgument: $trajStationArg) {",
                "        ...stationFields",
                "    }",
                "}",
                "",
                "fragment stationFields on TrajectoryStation {",
                String.join("\n    ", stationFields),
                "}"
        );

        payload.put("query", queryString);


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
