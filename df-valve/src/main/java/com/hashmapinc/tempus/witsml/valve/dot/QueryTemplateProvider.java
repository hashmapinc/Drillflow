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

import org.json.JSONObject;

public class QueryTemplateProvider {
    public static JSONObject getIDOnly(String wmlObjectType){
        switch(wmlObjectType){
            case "well":
                return getWellIDOnly();
            case "wellbore":
                return getWellboreIDOnly();
            case "trajectory":
                return getTrajectoryIDOnly();
        }
        return null;
    }

    private static JSONObject getWellIDOnly(){
        JSONObject wellQuery = getBaseIDOnly();
        wellQuery.put("numGovt", "");
        wellQuery.put("numAPI", "");
        JSONObject commonData = new JSONObject();
        commonData.put("dTimLastChange", "");
        wellQuery.put("commonData", commonData);
        return wellQuery;
    }

    private static JSONObject getWellboreIDOnly(){
        JSONObject wellboreQuery = getBaseIDOnly();
        JSONObject commonData = new JSONObject();
        commonData.put("dTimLastChange", "");
        wellboreQuery.put("commonData", commonData);
        return wellboreQuery;
    }

    private static JSONObject getTrajectoryIDOnly(){
        JSONObject trajectoryQuery = getBaseIDOnly();
        JSONObject commonData = new JSONObject();
        trajectoryQuery.put("objectGrowing", "");
        commonData.put("dTimLastChange", "");
        trajectoryQuery.put("commonData", commonData);
        return trajectoryQuery;
    }

    private static JSONObject getBaseIDOnly(){
        JSONObject queryJson = new JSONObject();
        queryJson.put("name","");
        queryJson.put("nameWellbore","");
        queryJson.put("nameWell","");
        queryJson.put("uid","");
        queryJson.put("uidWellbore","");
        queryJson.put("uidWell","");
        return queryJson;
    }
}