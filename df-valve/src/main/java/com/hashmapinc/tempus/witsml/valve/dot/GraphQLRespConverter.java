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
import com.hashmapinc.tempus.WitsmlObjects.Util.TrajectoryConverter;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore;
import com.hashmapinc.tempus.WitsmlObjects.v20.Trajectory;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class GraphQLRespConverter {

    /**
     * Converts a GraphQL repopnse from DoT to a list of singular Abstract witsml objects
     * @param response the GraphQL response from DoT
     * @param objectType the Type of the object received
     * @return An ArrayList of singular objects found
     * @throws IOException
     */
    public static ArrayList<AbstractWitsmlObject> convert (
        String response,
        String objectType
    ) throws IOException, DatatypeConfigurationException {
        JSONObject responseJSON = new JSONObject(response);

        if (!responseJSON.has("data")){
            return null;
        }

        JSONObject data = (JSONObject)responseJSON.get("data");
        switch (objectType){
            case "well":
                return getWells(data);
            case "wellbore":
                return getWellbores(data);
            case "trajectory":
                return getTrajectories(data);
            default:
                return null;
        }
    }

    private static ArrayList<AbstractWitsmlObject> getWells(JSONObject data) throws IOException {
        ArrayList<AbstractWitsmlObject> foundObjects = new ArrayList<>();
        if (!data.has("wells")){
            return null;
        }
        if (data.get("wells") == null)
            return null;
        JSONArray wells = (JSONArray) data.get("wells");

        for(int i = 0; i < wells.length(); i++){
            ObjWell foundWell = WitsmlMarshal.deserializeFromJSON(wells.get(i).toString(), ObjWell.class);
            if (foundWell.getUid() != null) {
                foundObjects.add(foundWell);
            }
        }

        return foundObjects;
    }

    private static ArrayList<AbstractWitsmlObject> getWellbores(JSONObject data) throws IOException {
        ArrayList<AbstractWitsmlObject> foundObjects = new ArrayList<>();
        if (!data.has("wellbores")){
            return null;
        }
        if (data.get("wellbores") == null)
            return null;
        JSONArray wells = (JSONArray) data.get("wellbores");

        for(int i = 0; i < wells.length(); i++){
            ObjWellbore foundWell = WitsmlMarshal.deserializeFromJSON(wells.get(i).toString(), ObjWellbore.class);
            if (foundWell.getUid() != null) {
                foundObjects.add(foundWell);
            }
        }

        return foundObjects;
    }

    private static ArrayList<AbstractWitsmlObject> getTrajectories(JSONObject data) throws IOException, DatatypeConfigurationException {
        ArrayList<AbstractWitsmlObject> foundObjects = new ArrayList<>();

        if (!data.has("trajectories"))
            return null;

        JSONArray trajectories = (JSONArray) data.get("trajectories");
        if (trajectories == null)
            return null;

        for(int i = 0; i < trajectories.length(); i++) {
            try {
                Trajectory trajectory = WitsmlMarshal.deserializeFromJSON(trajectories.get(i).toString(), Trajectory.class);
                foundObjects.add(TrajectoryConverter.convertTo1411(trajectory));
            } catch (Exception e) {
                // TODO: Remove
            }
        }

        return foundObjects;
    }

    public static String getUUid(JSONObject response){
        if (!response.has("data")){
            return null;
        }
        JSONObject data = (JSONObject)response.get("data");
        if (!data.has("wellbores")){
            return null;
        }
        if (data.get("wellbores") == null)
            return null;
        JSONArray wells = (JSONArray) data.get("wellbores");

        return ((JSONObject)wells.get(0)).getString("uuid");
    }
}
