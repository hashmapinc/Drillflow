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


        // ====================================================================
        // get trajectory fields fragment (this is long and sucks, we know)
        // ====================================================================
        StringBuilder trajFieldsFragment = new StringBuilder();
        trajFieldsFragment.append("fragment trajFields on TrajectoryType {");

        if(trajectoryJson.has("aliases")) { // array
            trajFieldsFragment.append("    aliases {");
            if(JsonUtil.isEmpty(trajectoryJson.get("aliases")) || trajectoryJson.getJSONArray("aliases").getJSONObject(0).has("authority"))
                trajFieldsFragment.append("        authority");
            if(JsonUtil.isEmpty(trajectoryJson.get("aliases")) || trajectoryJson.getJSONArray("aliases").getJSONObject(0).has("description"))
                trajFieldsFragment.append("        description");
            if(JsonUtil.isEmpty(trajectoryJson.get("aliases")) || trajectoryJson.getJSONArray("aliases").getJSONObject(0).has("identifier"))
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
                trajFieldsFragment.append("        acquisitionTimeZone {"); // array
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("acquisitionTimeZone")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONArray("acquisitionTimeZone").getJSONObject(0).has("dTim")) {
                    trajFieldsFragment.append("            dTim");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("acquisitionTimeZone")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONArray("acquisitionTimeZone").getJSONObject(0).has("value")) {
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
                trajFieldsFragment.append("        extensionNameValue {"); // array
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("dataType")) {
                    trajFieldsFragment.append("            dataType");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("description")) {
                    trajFieldsFragment.append("            description");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("dTim")) {
                    trajFieldsFragment.append("            dTim");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("index")) {
                    trajFieldsFragment.append("            index");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("md")) {
                    trajFieldsFragment.append("            md {");
                    if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                            trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("datum")) {
                        trajFieldsFragment.append("                datum");
                    }if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                            trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("uom")) {
                        trajFieldsFragment.append("                uom");
                    }if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                            trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("value")) {
                        trajFieldsFragment.append("                value");
                    }
                    trajFieldsFragment.append("            }");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("measureClass")) {
                    trajFieldsFragment.append("            measureClass");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("name")) {
                    trajFieldsFragment.append("            name");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("uid")) {
                    trajFieldsFragment.append("            uid");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("value")) {
                    trajFieldsFragment.append("            value {");
                    if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).get("value")) ||
                            trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value").has("uom")) {
                        trajFieldsFragment.append("                uom");
                    }
                    if(JsonUtil.isEmpty(trajectoryJson.get("commonData")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).get("value")) ||
                            trajectoryJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value").has("value")) {
                        trajFieldsFragment.append("                value");
                    }
                    trajFieldsFragment.append("            }");
                }
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

        if(trajectoryJson.has("creationTimeUtc"))
            trajFieldsFragment.append("    creationTimeUtc");

        if(trajectoryJson.has("customData"))
            trajFieldsFragment.append("    customData");

        if(trajectoryJson.has("definitive"))
            trajFieldsFragment.append("    definitive");

        if(trajectoryJson.has("dispEwVertSectOrig")) {
            trajFieldsFragment.append("    dispEwVertSectOrig {");
            if(JsonUtil.isEmpty(trajectoryJson.get("dispEwVertSectOrig")) || trajectoryJson.getJSONObject("dispEwVertSectOrig").has("uom"))
                trajFieldsFragment.append("        uom");
            if(JsonUtil.isEmpty(trajectoryJson.get("dispEwVertSectOrig")) || trajectoryJson.getJSONObject("dispEwVertSectOrig").has("value"))
                trajFieldsFragment.append("        value");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("dispNsVertSectOrig")) {
            trajFieldsFragment.append("    dispNsVertSectOrig {");
            if(JsonUtil.isEmpty(trajectoryJson.get("dispEwVertSectOrig")) || trajectoryJson.getJSONObject("dispEwVertSectOrig").has("uom"))
                trajFieldsFragment.append("        uom");
            if(JsonUtil.isEmpty(trajectoryJson.get("dispEwVertSectOrig")) || trajectoryJson.getJSONObject("dispEwVertSectOrig").has("value"))
                trajFieldsFragment.append("        value");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("dTimTrajEnd"))
            trajFieldsFragment.append("    dTimTrajEnd");

        if(trajectoryJson.has("dTimTrajStart"))
            trajFieldsFragment.append("    dTimTrajStart");

        if(trajectoryJson.has("existenceKind"))
            trajFieldsFragment.append("    existenceKind");

        if(trajectoryJson.has("extensionNameValue")) { // array
            trajFieldsFragment.append("    extensionNameValue {");
            if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) || trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).has("dataType"))
                trajFieldsFragment.append("        dataType");
            if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) || trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).has("description"))
                trajFieldsFragment.append("        description");
            if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) || trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).has("dTim"))
                trajFieldsFragment.append("        dTim");
            if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) || trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).has("index"))
                trajFieldsFragment.append("        index");
            if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) || trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).has("md")) {
                trajFieldsFragment.append("        md {");
                if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                        trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("datum")) {
                    trajFieldsFragment.append("            datum");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                        trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("uom")) {
                    trajFieldsFragment.append("            uom");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                        trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("value")) {
                    trajFieldsFragment.append("            value");
                }
                trajFieldsFragment.append("        }");
            }
            if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) || trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).has("measureClass"))
                trajFieldsFragment.append("        measureClass");
            if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) || trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).has("name"))
            trajFieldsFragment.append("        name");
            if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) || trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).has("uid"))
            trajFieldsFragment.append("        uid");
            if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) || trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).has("value")) {
                trajFieldsFragment.append("        value {");
                if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).get("value")) ||
                        trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value").has("uom")) {
                    trajFieldsFragment.append("            uom");
                }
                if(JsonUtil.isEmpty(trajectoryJson.get("extensionNameValue")) ||
                        JsonUtil.isEmpty(trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).get("value")) ||
                        trajectoryJson.getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value").has("value")) {
                    trajFieldsFragment.append("            value");
                }
                trajFieldsFragment.append("        }");
            }
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("finalTraj"))
            trajFieldsFragment.append("    finalTraj");

        if(trajectoryJson.has("gridConUsed")) {
            trajFieldsFragment.append("    gridConUsed {");
            if(JsonUtil.isEmpty(trajectoryJson.get("gridConUsed")) || trajectoryJson.getJSONObject("gridConUsed").has("uom"))
                trajFieldsFragment.append("        uom");
            if(JsonUtil.isEmpty(trajectoryJson.get("gridConUsed")) || trajectoryJson.getJSONObject("gridConUsed").has("value"))
                trajFieldsFragment.append("        value");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("gridCorUsed")) {
            trajFieldsFragment.append("    gridCorUsed {");
            if(JsonUtil.isEmpty(trajectoryJson.get("gridCorUsed")) || trajectoryJson.getJSONObject("gridCorUsed").has("uom"))
                trajFieldsFragment.append("        uom");
            if(JsonUtil.isEmpty(trajectoryJson.get("gridCorUsed")) || trajectoryJson.getJSONObject("gridCorUsed").has("value"))
                trajFieldsFragment.append("        value");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("growingStatus"))
            trajFieldsFragment.append("    growingStatus");

        if(trajectoryJson.has("lastUpdateTimeUtc"))
            trajFieldsFragment.append("    lastUpdateTimeUtc");

        if(trajectoryJson.has("magDeclUsed")) {
            trajFieldsFragment.append("    magDeclUsed {");
            if(JsonUtil.isEmpty(trajectoryJson.get("magDeclUsed")) || trajectoryJson.getJSONObject("magDeclUsed").has("uom"))
                trajFieldsFragment.append("        uom");
            if(JsonUtil.isEmpty(trajectoryJson.get("magDeclUsed")) || trajectoryJson.getJSONObject("magDeclUsed").has("value"))
                trajFieldsFragment.append("        value");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("mdMn")) {
            trajFieldsFragment.append("    mdMn {");
            if(JsonUtil.isEmpty(trajectoryJson.get("mdMn")) || trajectoryJson.getJSONObject("mdMn").has("datum"))
                trajFieldsFragment.append("        datum");
            if(JsonUtil.isEmpty(trajectoryJson.get("mdMn")) || trajectoryJson.getJSONObject("mdMn").has("uom"))
                trajFieldsFragment.append("        uom");
            if(JsonUtil.isEmpty(trajectoryJson.get("mdMn")) || trajectoryJson.getJSONObject("mdMn").has("value"))
                trajFieldsFragment.append("        value");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("mdMx")) {
            trajFieldsFragment.append("    mdMx {");
            if(JsonUtil.isEmpty(trajectoryJson.get("mdMx")) || trajectoryJson.getJSONObject("mdMx").has("datum"))
                trajFieldsFragment.append("        datum");
            if(JsonUtil.isEmpty(trajectoryJson.get("mdMx")) || trajectoryJson.getJSONObject("mdMx").has("uom"))
                trajFieldsFragment.append("        uom");
            if(JsonUtil.isEmpty(trajectoryJson.get("mdMx")) || trajectoryJson.getJSONObject("mdMx").has("value"))
                trajFieldsFragment.append("        value");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("memory"))
            trajFieldsFragment.append("    memory");

        if(trajectoryJson.has("name"))
            trajFieldsFragment.append("    name");

        if(trajectoryJson.has("nameWell"))
            trajFieldsFragment.append("    nameWell");

        if(trajectoryJson.has("nameWellbore"))
            trajFieldsFragment.append("    nameWellbore");

        if(trajectoryJson.has("objectVersion"))
            trajFieldsFragment.append("    objectVersion");

        if(trajectoryJson.has("parentTrajectory")) {
            trajFieldsFragment.append("    parentTrajectory {");
            if(JsonUtil.isEmpty(trajectoryJson.get("parentTrajectory")) || trajectoryJson.getJSONObject("parentTrajectory").has("contentType"))
                trajFieldsFragment.append("        contentType");
            if(JsonUtil.isEmpty(trajectoryJson.get("parentTrajectory")) || trajectoryJson.getJSONObject("parentTrajectory").has("title"))
                trajFieldsFragment.append("        title");
            if(JsonUtil.isEmpty(trajectoryJson.get("parentTrajectory")) || trajectoryJson.getJSONObject("parentTrajectory").has("uidRef"))
                trajFieldsFragment.append("        uidRef");
            if(JsonUtil.isEmpty(trajectoryJson.get("parentTrajectory")) || trajectoryJson.getJSONObject("parentTrajectory").has("uri"))
                trajFieldsFragment.append("        uri");
            if(JsonUtil.isEmpty(trajectoryJson.get("parentTrajectory")) || trajectoryJson.getJSONObject("parentTrajectory").has("uuid"))
                trajFieldsFragment.append("        uuid");
            if(JsonUtil.isEmpty(trajectoryJson.get("parentTrajectory")) || trajectoryJson.getJSONObject("parentTrajectory").has("uuidAuthority"))
                trajFieldsFragment.append("        uuidAuthority");
            if(JsonUtil.isEmpty(trajectoryJson.get("parentTrajectory")) || trajectoryJson.getJSONObject("parentTrajectory").has("versionString"))
                trajFieldsFragment.append("        versionString");
            trajFieldsFragment.append("    }");
        }

        if(trajectoryJson.has("schemaVersion"))
            trajFieldsFragment.append("    schemaVersion");

        if(trajectoryJson.has("serviceCompany"))
            trajFieldsFragment.append("    serviceCompany");

        // always snag uid
        trajFieldsFragment.append("    uid");

        if(trajectoryJson.has("uuid"))
            trajFieldsFragment.append("    uuid");

        if(trajectoryJson.has("uuidWell"))
            trajFieldsFragment.append("    uuidWell");

        if(trajectoryJson.has("uuidWellbore"))
            trajFieldsFragment.append("    uuidWellbore");

        if(trajectoryJson.has("wellbore")) {
            trajFieldsFragment.append("    wellbore {");
            if(JsonUtil.isEmpty(trajectoryJson.get("wellbore")) || trajectoryJson.getJSONObject("wellbore").has("contentType"))
                trajFieldsFragment.append("        contentType");
            if(JsonUtil.isEmpty(trajectoryJson.get("wellbore")) || trajectoryJson.getJSONObject("wellbore").has("title"))
                trajFieldsFragment.append("        title");
            if(JsonUtil.isEmpty(trajectoryJson.get("wellbore")) || trajectoryJson.getJSONObject("wellbore").has("uidRef"))
                trajFieldsFragment.append("        uidRef");
            if(JsonUtil.isEmpty(trajectoryJson.get("wellbore")) || trajectoryJson.getJSONObject("wellbore").has("uri"))
                trajFieldsFragment.append("        uri");
            if(JsonUtil.isEmpty(trajectoryJson.get("wellbore")) || trajectoryJson.getJSONObject("wellbore").has("uuid"))
                trajFieldsFragment.append("        uuid");
            if(JsonUtil.isEmpty(trajectoryJson.get("wellbore")) || trajectoryJson.getJSONObject("wellbore").has("uuidAuthority"))
                trajFieldsFragment.append("        uuidAuthority");
            if(JsonUtil.isEmpty(trajectoryJson.get("wellbore")) || trajectoryJson.getJSONObject("wellbore").has("versionString"))
                trajFieldsFragment.append("        versionString");
            trajFieldsFragment.append("    }");
        }

        trajFieldsFragment.append("    trajectoryStation(trajectoryStationArgument: $trajStationArg) {");
        trajFieldsFragment.append("        ...stationFields");
        trajFieldsFragment.append("    }");
        trajFieldsFragment.append("}");
        // ====================================================================



        // ====================================================================
        // get station response fields
        // ====================================================================
        StringBuilder stationFieldsFragment = new StringBuilder();
        stationFieldsFragment.append("fragment stationFields on TrajectoryStation {");

        if (!JsonUtil.isEmpty(stationsJson) && stationsJson.getJSONObject(0).has("axialMagInterferenceCorUsed"))
            stationFieldsFragment.append("    axialMagInterferenceCorUsed");

        stationFieldsFragment.append("    azi {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    calcAlgorithm");
        stationFieldsFragment.append("    commonData {");
        stationFieldsFragment.append("        acquisitionTimeZone {"); // array
        stationFieldsFragment.append("            dTim");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        comments");
        stationFieldsFragment.append("        defaultDatum {");
        stationFieldsFragment.append("            uidRef");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        extensionAny");
        stationFieldsFragment.append("        extensionNameValue {"); // array
        stationFieldsFragment.append("            dataType");
        stationFieldsFragment.append("            description");
        stationFieldsFragment.append("            dTim");
        stationFieldsFragment.append("            index");
        stationFieldsFragment.append("            md {");
        stationFieldsFragment.append("                datum");
        stationFieldsFragment.append("                uom");
        stationFieldsFragment.append("                value");
        stationFieldsFragment.append("            }");
        stationFieldsFragment.append("            measureClass");
        stationFieldsFragment.append("            name");
        stationFieldsFragment.append("            uid");
        stationFieldsFragment.append("            value {");
        stationFieldsFragment.append("                uom");
        stationFieldsFragment.append("                value");
        stationFieldsFragment.append("            }");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        itemState");
        stationFieldsFragment.append("        privateGroupOnly");
        stationFieldsFragment.append("        serviceCategory");
        stationFieldsFragment.append("        sourceName");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    corUsed {");
        stationFieldsFragment.append("        dirSensorOffset {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        gravAxialAccelCor {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        gravTran1AccelCor {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        gravTran2AccelCor {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        magAxialDrlstrCor {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        magAxialMSACor {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        magTran1DrlstrCor {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        magTran1MSACor {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        magTran2DrlstrCor {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        magTran2MSACor {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        sagAziCor {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        sagIncCor {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        stnGridConUsed {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        stnGridCorUsed {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        stnMagDeclUsed {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    cosagCorUsed");
        stationFieldsFragment.append("    creationTimeUtc");
        stationFieldsFragment.append("    dipAngleUncert {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    dispEw {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    dispNs {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    dls {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    dTimStn");
        stationFieldsFragment.append("    extensionNameValue {");
        stationFieldsFragment.append("        dataType");
        stationFieldsFragment.append("        description");
        stationFieldsFragment.append("        dTim");
        stationFieldsFragment.append("        index");
        stationFieldsFragment.append("        md {");
        stationFieldsFragment.append("            datum");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        measureClass");
        stationFieldsFragment.append("        name");
        stationFieldsFragment.append("        uid");
        stationFieldsFragment.append("        value {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    geoModelUsed");
        stationFieldsFragment.append("    gravAccelCorUsed");
        stationFieldsFragment.append("    gravTotalFieldReference {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    gravTotalUncert {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    gtf {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    incl {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    infieldRefCorUsed");
        stationFieldsFragment.append("    inHoleRefCorUsed");
        stationFieldsFragment.append("    interpolatedInfieldRefCorUsed");
        stationFieldsFragment.append("    iscwsaToolErrorModel {");
        stationFieldsFragment.append("        contentType");
        stationFieldsFragment.append("        title");
        stationFieldsFragment.append("        uidRef");
        stationFieldsFragment.append("        uri");
        stationFieldsFragment.append("        uuid");
        stationFieldsFragment.append("        uuidAuthority");
        stationFieldsFragment.append("        versionString");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    lastUpdateTimeUtc");
        stationFieldsFragment.append("    location {");
        stationFieldsFragment.append("        description");
        stationFieldsFragment.append("        easting {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        extensionNameValue {");
        stationFieldsFragment.append("            dataType");
        stationFieldsFragment.append("            description");
        stationFieldsFragment.append("            dTim");
        stationFieldsFragment.append("            index");
        stationFieldsFragment.append("            md {");
        stationFieldsFragment.append("                datum");
        stationFieldsFragment.append("                uom");
        stationFieldsFragment.append("                value");
        stationFieldsFragment.append("            }");
        stationFieldsFragment.append("            measureClass");
        stationFieldsFragment.append("            name");
        stationFieldsFragment.append("            uid");
        stationFieldsFragment.append("            value {");
        stationFieldsFragment.append("                uom");
        stationFieldsFragment.append("                value");
        stationFieldsFragment.append("            }");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        latitude {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        localX {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        localY {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        longitude {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        northing {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        original");
        stationFieldsFragment.append("        projectedX {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        projectedY {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        southing {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        uid");
        stationFieldsFragment.append("        wellCRS {");
        stationFieldsFragment.append("            uidRef");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        westing {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    magDipAngleReference {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    magDrlstrCorUsed");
        stationFieldsFragment.append("    magModelUsed");
        stationFieldsFragment.append("    magModelValid");
        stationFieldsFragment.append("    magTotalFieldReference {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    magTotalUncert {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    magXAxialCorUsed");
        stationFieldsFragment.append("    manuallyEntered");
        stationFieldsFragment.append("    matrixCov {");
        stationFieldsFragment.append("        biasE {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        biasN {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        biasVert {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        varianceEE {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        varianceEVert {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        varianceNE {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        varianceNN {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        varianceNVert {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        varianceVertVert {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    md {");
        stationFieldsFragment.append("        datum");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    mdDelta {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    modelToolError");
        stationFieldsFragment.append("    mSACorUsed");
        stationFieldsFragment.append("    mtf {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    rateBuild {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    rateTurn {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    rawData {");
        stationFieldsFragment.append("        gravAxialRaw {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        gravTran1Raw {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        gravTran2Raw {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        magAxialRaw {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        magTran1Raw {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        magTran2Raw {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    sagCorUsed");
        stationFieldsFragment.append("    sourceStation {");
        stationFieldsFragment.append("        stationReference");
        stationFieldsFragment.append("        trajectoryParent {");
        stationFieldsFragment.append("            uidRef");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        wellboreParent {");
        stationFieldsFragment.append("            uidRef");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    statusTrajStation");
        stationFieldsFragment.append("    target {");
        stationFieldsFragment.append("        uidRef");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    tvd {");
        stationFieldsFragment.append("        datum");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    tvdDelta {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    typeSurveyTool");
        stationFieldsFragment.append("    typeTrajStation");
        stationFieldsFragment.append("    uid");
        stationFieldsFragment.append("    valid {");
        stationFieldsFragment.append("        gravTotalFieldCalc {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        magDipAngleCalc {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("        magTotalFieldCalc {");
        stationFieldsFragment.append("            uom");
        stationFieldsFragment.append("            value");
        stationFieldsFragment.append("        }");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("    vertSect {");
        stationFieldsFragment.append("        uom");
        stationFieldsFragment.append("        value");
        stationFieldsFragment.append("    }");
        stationFieldsFragment.append("}");
        // ====================================================================

        // build variables section
        JSONObject variables = new JSONObject();
        variables.put("trajArg", trajQueryFields);
        variables.put("trajStationArg", stationQueryFields);
        payload.put("variables", variables);

        // build query section of payload
        String queryString = String.join("\n",
                "query TrajectoryQuery($trajArg: TrajectoryArgument, $trajStationArg: TrajectoryStationArgument) {",
                "	trajectories(trajectoryArgument: $trajArg) {",
                "		...trajFields",
                "	}",
                "}",
                "",
                trajFieldsFragment.toString(),
                "",
                stationFieldsFragment.toString()
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
