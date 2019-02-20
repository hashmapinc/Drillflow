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
        // get station response fields (also long, also sucks. Please let us know if you can do this better).
        // ====================================================================
        // get first station entry for building fragment
        JSONObject stationJson = JsonUtil.isEmpty(stationsJson) ? new JSONObject() : stationsJson.getJSONObject(0);
        if (stationJson.has("uid"))
            stationJson.remove("uid"); // discard this before building query

        // give descriptive boolean name to the case when all station fields should be included
        boolean includeAllStationFields = JsonUtil.isEmpty(stationJson);

        // get string builder for constructing the fragment
        StringBuilder stationFieldsFragment = new StringBuilder();
        stationFieldsFragment.append("fragment stationFields on TrajectoryStation {");


        if (includeAllStationFields || stationJson.has("axialMagInterferenceCorUsed"))
            stationFieldsFragment.append("    axialMagInterferenceCorUsed");

        if (includeAllStationFields || stationJson.has("azi")) {
            stationFieldsFragment.append("    azi {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("azi")) || stationJson.getJSONObject("azi").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("azi")) || stationJson.getJSONObject("azi").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("calcAlgorithm"))
            stationFieldsFragment.append("    calcAlgorithm");


        if(includeAllStationFields || stationJson.has("commonData")) {
            stationFieldsFragment.append("    commonData {");

            if(includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("commonData")) ||
                    stationJson.getJSONObject("commonData").has("acquisitionTimeZone")) {
                stationFieldsFragment.append("        acquisitionTimeZone {"); // array
                if(includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("acquisitionTimeZone")) ||
                        stationJson.getJSONObject("commonData").getJSONArray("acquisitionTimeZone").getJSONObject(0).has("dTim")) {
                    stationFieldsFragment.append("            dTim");
                }
                if(includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("acquisitionTimeZone")) ||
                        stationJson.getJSONObject("commonData").getJSONArray("acquisitionTimeZone").getJSONObject(0).has("value")) {
                    stationFieldsFragment.append("            value");
                }
                stationFieldsFragment.append("        }");
            }

            if(includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("commonData")) ||
                    stationJson.getJSONObject("commonData").has("comments"))
                stationFieldsFragment.append("        comments");


            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("commonData")) || stationJson.getJSONObject("commonData").has("defaultDatum")) {
                stationFieldsFragment.append("        defaultDatum {");
                if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("defaultDatum")) ||
                        stationJson.getJSONObject("commonData").getJSONObject("defaultDatum").has("uidRef")) {
                    stationFieldsFragment.append("            uidRef");
                }
                if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("defaultDatum")) ||
                        stationJson.getJSONObject("commonData").getJSONObject("defaultDatum").has("value")) {
                    stationFieldsFragment.append("            value");
                }
                stationFieldsFragment.append("        }");
            }

            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("commonData")) || stationJson.getJSONObject("commonData").has("extensionAny"))
                stationFieldsFragment.append("        extensionAny");

            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("commonData")) || stationJson.getJSONObject("commonData").has("extensionNameValue")) {
                stationFieldsFragment.append("        extensionNameValue {"); // array
                if(includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("dataType")) {
                    stationFieldsFragment.append("            dataType");
                }
                if(includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("description")) {
                    stationFieldsFragment.append("            description");
                }
                if(includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("dTim")) {
                    stationFieldsFragment.append("            dTim");
                }
                if(includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("index")) {
                    stationFieldsFragment.append("            index");
                }
                if(includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("md")) {
                    stationFieldsFragment.append("            md {");
                    if(includeAllStationFields ||
                            JsonUtil.isEmpty(stationJson.get("commonData")) ||
                            JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                            stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("datum")) {
                        stationFieldsFragment.append("                datum");
                    }if(includeAllStationFields ||
                            JsonUtil.isEmpty(stationJson.get("commonData")) ||
                            JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                            stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("uom")) {
                        stationFieldsFragment.append("                uom");
                    }if(includeAllStationFields ||
                            JsonUtil.isEmpty(stationJson.get("commonData")) ||
                            JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                            stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("value")) {
                        stationFieldsFragment.append("                value");
                    }
                    stationFieldsFragment.append("            }");
                }
                if(includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("measureClass")) {
                    stationFieldsFragment.append("            measureClass");
                }
                if(includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("name")) {
                    stationFieldsFragment.append("            name");
                }
                if(includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("uid")) {
                    stationFieldsFragment.append("            uid");
                }
                if(includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("commonData")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                        stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).has("value")) {
                    stationFieldsFragment.append("            value {");
                    if(includeAllStationFields ||
                            JsonUtil.isEmpty(stationJson.get("commonData")) ||
                            JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).get("value")) ||
                            stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value").has("uom")) {
                        stationFieldsFragment.append("                uom");
                    }
                    if(includeAllStationFields ||
                            JsonUtil.isEmpty(stationJson.get("commonData")) ||
                            JsonUtil.isEmpty(stationJson.getJSONObject("commonData").get("extensionNameValue")) ||
                            JsonUtil.isEmpty(stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).get("value")) ||
                            stationJson.getJSONObject("commonData").getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value").has("value")) {
                        stationFieldsFragment.append("                value");
                    }
                    stationFieldsFragment.append("            }");
                }
                stationFieldsFragment.append("        }");
            }

            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("commonData")) || stationJson.getJSONObject("commonData").has("itemState"))
                stationFieldsFragment.append("        itemState");

            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("commonData")) || stationJson.getJSONObject("commonData").has("privateGroupOnly"))
                stationFieldsFragment.append("        privateGroupOnly");

            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("commonData")) || stationJson.getJSONObject("commonData").has("serviceCategory"))
                stationFieldsFragment.append("        serviceCategory");

            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("commonData")) || stationJson.getJSONObject("commonData").has("sourceName"))
                stationFieldsFragment.append("        sourceName");
            stationFieldsFragment.append("    }");
        }


        if (includeAllStationFields || stationJson.has("corUsed")) {
            stationFieldsFragment.append("    corUsed {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("dirSensorOffset")) {
                stationFieldsFragment.append("        dirSensorOffset {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("dirSensorOffset")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("dirSensorOffset").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("dirSensorOffset")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("dirSensorOffset").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("gravAxialAccelCor")) {
                stationFieldsFragment.append("        gravAxialAccelCor {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("gravAxialAccelCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("gravAxialAccelCor").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("gravAxialAccelCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("gravAxialAccelCor").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("gravTran1AccelCor")) {
                stationFieldsFragment.append("        gravTran1AccelCor {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("gravTran1AccelCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("gravTran1AccelCor").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("gravTran1AccelCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("gravTran1AccelCor").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("gravTran2AccelCor")) {
                stationFieldsFragment.append("        gravTran2AccelCor {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("gravTran2AccelCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("gravTran2AccelCor").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("gravTran2AccelCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("gravTran2AccelCor").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("magAxialDrlstrCor")) {
                stationFieldsFragment.append("        magAxialDrlstrCor {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magAxialDrlstrCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magAxialDrlstrCor").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magAxialDrlstrCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magAxialDrlstrCor").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("magAxialMSACor")) {
                stationFieldsFragment.append("        magAxialMSACor {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magAxialMSACor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magAxialMSACor").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magAxialMSACor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magAxialMSACor").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("magTran1DrlstrCor")) {
                stationFieldsFragment.append("        magTran1DrlstrCor {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magTran1DrlstrCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magTran1DrlstrCor").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magTran1DrlstrCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magTran1DrlstrCor").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("magTran1MSACor")) {
                stationFieldsFragment.append("        magTran1MSACor {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magTran1MSACor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magTran1MSACor").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magTran1MSACor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magTran1MSACor").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("magTran2DrlstrCor")) {
                stationFieldsFragment.append("        magTran2DrlstrCor {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magTran2DrlstrCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magTran2DrlstrCor").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magTran2DrlstrCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magTran2DrlstrCor").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("magTran2MSACor")) {
                stationFieldsFragment.append("        magTran2MSACor {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magTran2MSACor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magTran2MSACor").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("magTran2MSACor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("magTran2MSACor").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("sagAziCor")) {
                stationFieldsFragment.append("        sagAziCor {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("sagAziCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("sagAziCor").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("sagAziCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("sagAziCor").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("sagIncCor")) {
                stationFieldsFragment.append("        sagIncCor {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("sagIncCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("sagIncCor").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("sagIncCor")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("sagIncCor").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("stnGridConUsed")) {
                stationFieldsFragment.append("        stnGridConUsed {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("stnGridConUsed")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("stnGridConUsed").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("stnGridConUsed")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("stnGridConUsed").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("stnGridCorUsed")) {
                stationFieldsFragment.append("        stnGridCorUsed {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("stnGridCorUsed")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("stnGridCorUsed").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("stnGridCorUsed")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("stnGridCorUsed").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("corUsed")) || stationJson.getJSONObject("corUsed").has("stnMagDeclUsed")) {
                stationFieldsFragment.append("        stnMagDeclUsed {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("stnMagDeclUsed")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("stnMagDeclUsed").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("corUsed")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("corUsed").get("stnMagDeclUsed")) ||
                        stationJson.getJSONObject("corUsed").getJSONObject("stnMagDeclUsed").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("cosagCorUsed"))
            stationFieldsFragment.append("    cosagCorUsed");

        if (includeAllStationFields || stationJson.has("creationTimeUtc"))
            stationFieldsFragment.append("    creationTimeUtc");

        if (includeAllStationFields || stationJson.has("dipAngleUncert")) {
            stationFieldsFragment.append("    dipAngleUncert {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("dipAngleUncert")) || stationJson.getJSONObject("dipAngleUncert").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("dipAngleUncert")) || stationJson.getJSONObject("dipAngleUncert").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("dispEw")) {
            stationFieldsFragment.append("    dispEw {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("dispEw")) || stationJson.getJSONObject("dispEw").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("dispEw")) || stationJson.getJSONObject("dispEw").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("dispNs")) {
            stationFieldsFragment.append("    dispNs {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("dispNs")) || stationJson.getJSONObject("dispNs").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("dispNs")) || stationJson.getJSONObject("dispNs").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("dls")) {
            stationFieldsFragment.append("    dls {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("dls")) || stationJson.getJSONObject("dls").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("dls")) || stationJson.getJSONObject("dls").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("dTimStn"))
            stationFieldsFragment.append("    dTimStn");


        if (includeAllStationFields || stationJson.has("extensionNameValue")) { // array
            stationFieldsFragment.append("    extensionNameValue {");
            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) || stationJson.getJSONArray("extensionNameValue").getJSONObject(0).has("dataType"))
                stationFieldsFragment.append("        dataType");
            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) || stationJson.getJSONArray("extensionNameValue").getJSONObject(0).has("description"))
                stationFieldsFragment.append("        description");
            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) || stationJson.getJSONArray("extensionNameValue").getJSONObject(0).has("dTim"))
                stationFieldsFragment.append("        dTim");
            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) || stationJson.getJSONArray("extensionNameValue").getJSONObject(0).has("index"))
                stationFieldsFragment.append("        index");
            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) || stationJson.getJSONArray("extensionNameValue").getJSONObject(0).has("md")) {
                stationFieldsFragment.append("        md {");
                if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                        stationJson.getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("datum")) {
                    stationFieldsFragment.append("            datum");
                }
                if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                        stationJson.getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("uom")) {
                    stationFieldsFragment.append("            uom");
                }
                if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("extensionNameValue").getJSONObject(0).get("md")) ||
                        stationJson.getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("value")) {
                    stationFieldsFragment.append("            value");
                }
                stationFieldsFragment.append("        }");
            }
            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) || stationJson.getJSONArray("extensionNameValue").getJSONObject(0).has("measureClass"))
                stationFieldsFragment.append("        measureClass");
            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) || stationJson.getJSONArray("extensionNameValue").getJSONObject(0).has("name"))
                stationFieldsFragment.append("        name");
            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) || stationJson.getJSONArray("extensionNameValue").getJSONObject(0).has("uid"))
                stationFieldsFragment.append("        uid");
            if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) || stationJson.getJSONArray("extensionNameValue").getJSONObject(0).has("value")) {
                stationFieldsFragment.append("        value {");
                if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("extensionNameValue").getJSONObject(0).get("value")) ||
                        stationJson.getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value").has("uom")) {
                    stationFieldsFragment.append("            uom");
                }
                if(includeAllStationFields || JsonUtil.isEmpty(stationJson.get("extensionNameValue")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("extensionNameValue").getJSONObject(0).get("value")) ||
                        stationJson.getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value").has("value")) {
                    stationFieldsFragment.append("            value");
                }
                stationFieldsFragment.append("        }");
            }
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("geoModelUsed"))
            stationFieldsFragment.append("    geoModelUsed");

        if (includeAllStationFields || stationJson.has("gravAccelCorUsed"))
            stationFieldsFragment.append("    gravAccelCorUsed");

        if (includeAllStationFields || stationJson.has("gravTotalFieldReference")) {
            stationFieldsFragment.append("    gravTotalFieldReference {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("gravTotalFieldReference")) || stationJson.getJSONObject("gravTotalFieldReference").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("gravTotalFieldReference")) || stationJson.getJSONObject("gravTotalFieldReference").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("gravTotalUncert")) {
            stationFieldsFragment.append("    gravTotalUncert {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("gravTotalUncert")) || stationJson.getJSONObject("gravTotalUncert").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("gravTotalUncert")) || stationJson.getJSONObject("gravTotalUncert").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("gtf")) {
            stationFieldsFragment.append("    gtf {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("gtf")) || stationJson.getJSONObject("gtf").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("gtf")) || stationJson.getJSONObject("gtf").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("incl")) {
            stationFieldsFragment.append("    incl {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("incl")) || stationJson.getJSONObject("incl").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("incl")) || stationJson.getJSONObject("incl").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("infieldRefCorUsed"))
            stationFieldsFragment.append("    infieldRefCorUsed");

        if (includeAllStationFields || stationJson.has("inHoleRefCorUsed"))
            stationFieldsFragment.append("    inHoleRefCorUsed");

        if (includeAllStationFields || stationJson.has("interpolatedInfieldRefCorUsed"))
            stationFieldsFragment.append("    interpolatedInfieldRefCorUsed");

        if (includeAllStationFields || stationJson.has("iscwsaToolErrorModel")) {
            stationFieldsFragment.append("    iscwsaToolErrorModel {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("iscwsaToolErrorModel")) || stationJson.getJSONObject("iscwsaToolErrorModel").has("contentType"))
                stationFieldsFragment.append("        contentType");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("iscwsaToolErrorModel")) || stationJson.getJSONObject("iscwsaToolErrorModel").has("title"))
                stationFieldsFragment.append("        title");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("iscwsaToolErrorModel")) || stationJson.getJSONObject("iscwsaToolErrorModel").has("uidRef"))
                stationFieldsFragment.append("        uidRef");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("iscwsaToolErrorModel")) || stationJson.getJSONObject("iscwsaToolErrorModel").has("uri"))
                stationFieldsFragment.append("        uri");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("iscwsaToolErrorModel")) || stationJson.getJSONObject("iscwsaToolErrorModel").has("uuid"))
                stationFieldsFragment.append("        uuid");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("iscwsaToolErrorModel")) || stationJson.getJSONObject("iscwsaToolErrorModel").has("uuidAuthority"))
                stationFieldsFragment.append("        uuidAuthority");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("iscwsaToolErrorModel")) || stationJson.getJSONObject("iscwsaToolErrorModel").has("versionString"))
                stationFieldsFragment.append("        versionString");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("lastUpdateTimeUtc"))
            stationFieldsFragment.append("    lastUpdateTimeUtc");

        if (includeAllStationFields || stationJson.has("location")) {
            stationFieldsFragment.append("    location {"); // array
            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("description"))
                stationFieldsFragment.append("        description");

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("easting")) {
                stationFieldsFragment.append("        easting {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("easting")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("easting").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("easting")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("easting").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("extensionNameValue")) {
                stationFieldsFragment.append("        extensionNameValue {"); // array
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).has("dataType"))
                    stationFieldsFragment.append("            dataType");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).has("description"))
                    stationFieldsFragment.append("            description");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).has("dTim"))
                    stationFieldsFragment.append("            dTim");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).has("index"))
                    stationFieldsFragment.append("            index");

                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).has("md")) {
                    stationFieldsFragment.append("            md {");
                    if (includeAllStationFields ||
                            JsonUtil.isEmpty(stationJson.get("location")) ||
                            JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                            JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md")) ||
                            stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("datum"))
                        stationFieldsFragment.append("                datum");
                    if (includeAllStationFields ||
                            JsonUtil.isEmpty(stationJson.get("location")) ||
                            JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                            JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md")) ||
                            stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("uom"))
                        stationFieldsFragment.append("                uom");
                    if (includeAllStationFields ||
                            JsonUtil.isEmpty(stationJson.get("location")) ||
                            JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                            JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md")) ||
                            stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("md").has("value"))
                        stationFieldsFragment.append("                value");
                    stationFieldsFragment.append("            }");
                }
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).has("measureClass"))
                    stationFieldsFragment.append("            measureClass");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).has("name"))
                    stationFieldsFragment.append("            name");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).has("uid"))
                    stationFieldsFragment.append("            uid");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).has("value")) {
                    stationFieldsFragment.append("            value {");
                    if (includeAllStationFields ||
                            JsonUtil.isEmpty(stationJson.get("location")) ||
                            JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                            JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value")) ||
                            stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value").has("uom"))
                        stationFieldsFragment.append("                uom");
                    if (includeAllStationFields ||
                            JsonUtil.isEmpty(stationJson.get("location")) ||
                            JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("extensionNameValue")) ||
                            JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value")) ||
                            stationJson.getJSONArray("location").getJSONObject(0).getJSONArray("extensionNameValue").getJSONObject(0).getJSONObject("value").has("value"))
                        stationFieldsFragment.append("                value");
                    stationFieldsFragment.append("            }");
                }
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("latitude")) {
                stationFieldsFragment.append("        latitude {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("latitude")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("latitude").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("latitude")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("latitude").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("localX")) {
                stationFieldsFragment.append("        localX {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("localX")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("localX").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("localX")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("localX").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("localY")) {
                stationFieldsFragment.append("        localY {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("localY")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("localY").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("localY")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("localY").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("longitude")) {
                stationFieldsFragment.append("        longitude {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("longitude")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("longitude").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("longitude")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("longitude").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("northing")) {
                stationFieldsFragment.append("        northing {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("northing")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("northing").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("northing")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("northing").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("original"))
                stationFieldsFragment.append("        original");

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("projectedX")) {
                stationFieldsFragment.append("        projectedX {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("projectedX")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("projectedX").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("projectedX")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("projectedX").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("projectedY")) {
                stationFieldsFragment.append("        projectedY {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("projectedY")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("projectedY").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("projectedY")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("projectedY").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("southing")) {
                stationFieldsFragment.append("        southing {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("southing")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("southing").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("southing")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("southing").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("uid"))
                stationFieldsFragment.append("        uid");

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("wellCRS")) {
                stationFieldsFragment.append("        wellCRS {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("southing")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("wellCRS").has("uidRef"))
                    stationFieldsFragment.append("            uidRef");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("southing")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("wellCRS").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields ||
                    JsonUtil.isEmpty(stationJson.get("location")) ||
                    stationJson.getJSONArray("location").getJSONObject(0).has("westing")) {
                stationFieldsFragment.append("        westing {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("westing")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("westing").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("location")) ||
                        JsonUtil.isEmpty(stationJson.getJSONArray("location").getJSONObject(0).get("westing")) ||
                        stationJson.getJSONArray("location").getJSONObject(0).getJSONObject("westing").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }
            stationFieldsFragment.append("    }");
        }


        if (includeAllStationFields || stationJson.has("magDipAngleReference")) {
            stationFieldsFragment.append("    magDipAngleReference {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("magDipAngleReference")) || stationJson.getJSONObject("magDipAngleReference").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("magDipAngleReference")) || stationJson.getJSONObject("magDipAngleReference").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("magDrlstrCorUsed"))
            stationFieldsFragment.append("    magDrlstrCorUsed");

        if (includeAllStationFields || stationJson.has("magModelUsed"))
            stationFieldsFragment.append("    magModelUsed");

        if (includeAllStationFields || stationJson.has("magModelValid"))
            stationFieldsFragment.append("    magModelValid");

        if (includeAllStationFields || stationJson.has("magTotalFieldReference")) {
            stationFieldsFragment.append("    magTotalFieldReference {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("magTotalFieldReference")) || stationJson.getJSONObject("magTotalFieldReference").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("magTotalFieldReference")) || stationJson.getJSONObject("magTotalFieldReference").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("magTotalUncert")) {
            stationFieldsFragment.append("    magTotalUncert {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("magTotalUncert")) || stationJson.getJSONObject("magTotalUncert").has("uom"))
                stationFieldsFragment.append("        uom");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("magTotalUncert")) || stationJson.getJSONObject("magTotalUncert").has("value"))
                stationFieldsFragment.append("        value");
            stationFieldsFragment.append("    }");
        }

        if (includeAllStationFields || stationJson.has("magXAxialCorUsed"))
            stationFieldsFragment.append("    magXAxialCorUsed");

        if (includeAllStationFields || stationJson.has("manuallyEntered"))
            stationFieldsFragment.append("    manuallyEntered");

        if (includeAllStationFields || stationJson.has("matrixCov")) {
            stationFieldsFragment.append("    matrixCov {");
            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("matrixCov")) || stationJson.getJSONObject("magTotalUncert").has("biasE")) {
                stationFieldsFragment.append("        biasE {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("biasE")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("biasE").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("biasE")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("biasE").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("matrixCov")) || stationJson.getJSONObject("magTotalUncert").has("biasN")) {
                stationFieldsFragment.append("        biasN {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("biasN")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("biasN").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("biasN")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("biasN").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("matrixCov")) || stationJson.getJSONObject("magTotalUncert").has("biasVert")) {
                stationFieldsFragment.append("        biasVert {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("biasVert")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("biasVert").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("biasVert")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("biasVert").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("matrixCov")) || stationJson.getJSONObject("magTotalUncert").has("varianceEE")) {
                stationFieldsFragment.append("        varianceEE {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceEE")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceEE").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceEE")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceEE").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("matrixCov")) || stationJson.getJSONObject("magTotalUncert").has("varianceEVert")) {
                stationFieldsFragment.append("        varianceEVert {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceEVert")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceEVert").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceEVert")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceEVert").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("matrixCov")) || stationJson.getJSONObject("magTotalUncert").has("varianceNE")) {
                stationFieldsFragment.append("        varianceNE {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceNE")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceNE").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceNE")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceNE").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("matrixCov")) || stationJson.getJSONObject("magTotalUncert").has("varianceNN")) {
                stationFieldsFragment.append("        varianceNN {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceNN")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceNN").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceNN")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceNN").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("matrixCov")) || stationJson.getJSONObject("magTotalUncert").has("varianceNVert")) {
                stationFieldsFragment.append("        varianceNVert {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceNVert")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceNVert").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceNVert")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceNVert").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }

            if (includeAllStationFields || JsonUtil.isEmpty(stationJson.get("matrixCov")) || stationJson.getJSONObject("magTotalUncert").has("varianceVertVert")) {
                stationFieldsFragment.append("        varianceVertVert {");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceVertVert")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceVertVert").has("uom"))
                    stationFieldsFragment.append("            uom");
                if (includeAllStationFields ||
                        JsonUtil.isEmpty(stationJson.get("matrixCov")) ||
                        JsonUtil.isEmpty(stationJson.getJSONObject("matrixCov").get("varianceVertVert")) ||
                        stationJson.getJSONObject("matrixCov").getJSONObject("varianceVertVert").has("value"))
                    stationFieldsFragment.append("            value");
                stationFieldsFragment.append("        }");
            }
            stationFieldsFragment.append("    }");
        }

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

        stationFieldsFragment.append("    uid"); // always get station uid

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
