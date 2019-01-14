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

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlVersionTransformer;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;

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
    public ArrayList<AbstractWitsmlObject> convert(String response, String objectType) throws IOException {
        switch (objectType){
            case "well":
                return convertWellResponse(response);
            default:
                return null;
        }
    }

    // Converts a Well GraphQL response
    private ArrayList<AbstractWitsmlObject> convertWellResponse(String response) throws IOException {
        JSONObject responseJSON = new JSONObject(response);
        ArrayList<AbstractWitsmlObject> foundObjects = new ArrayList<>();

        if (!responseJSON.has("data")){
            return null;
        }

        JSONObject data = (JSONObject)responseJSON.get("data");
        JSONArray wells = (JSONArray) data.get("wells");

        for(int i = 0; i < wells.length(); i++){
            ObjWell foundWell = WitsmlMarshal.deserializeFromJSON(wells.get(i).toString(), ObjWell.class);
            if (foundWell.getUid() != null) {
                foundObjects.add(foundWell);
            }
        }

        return foundObjects;
    }
}
