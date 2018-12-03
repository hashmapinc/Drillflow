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

import java.util.logging.Logger;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class DotDelegator {
    private static final Logger LOG = Logger.getLogger(DotDelegator.class.getName());

    public final String URL;
    public final String API_KEY;

    public DotDelegator(String url, String apiKey) {
        this.URL = url;
        this.API_KEY = apiKey;
    }

    /**
     * Submits the object to the DoT rest API for creation
     * 
     * @param uid         - string uid of the object to create
     * @param objectJSON  - string json of the object to create
     * @param tokenString - string of the JWT to do auth with 
     * @return
     */
    public String addWellToStore(String uid, String objectJSON, String tokenString) {
        LOG.info("Adding well to store. UID=<" + uid + ">");

        // create endpoint
        String endpoint = this.URL + "/witsml/wells/" + uid;

        // send put
        try {
            HttpResponse<JsonNode> response = Unirest.put(endpoint).header("accept", "application/json")
                    .header("Authorization", tokenString)
                    .header("Ocp-Apim-Subscription-Key", this.API_KEY).body(objectJSON).asJson();

            int status = response.getStatus();

            if (201 == status || 200 == status) {
                LOG.info("Succesfully added well. UID=<" + uid + ">");
                return uid;
            } else {
                LOG.warning(
                    "Recieved status code from well put: " + status + "\n" + 
                    "URL=" + this.URL + "\n" +
                    "ObjectJSON=" + objectJSON + "\n"
                );
                return null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            LOG.warning("Error while creating object in DoTValve: " + e);
            return null;
        }
    }



    /**
     * Submits the object to the DoT rest API for creation
     * 
     * @param uid         - string uid of the object to create
     * @param objectJSON  - string json of the object to create
     * @param tokenString - string of the JWT to do auth with
     * @return
     */
    public String addWellboreToStore(String uid, String objectJSON, String tokenString) {
        LOG.info("Adding wellbore to store. UID=<" + uid + ">");

        // create endpoint
        String endpoint = this.URL + "/witsml/wellbores/" + uid;

        // send put
        try {
            HttpResponse<JsonNode> response = Unirest.put(endpoint)
                .header("accept", "application/json")
                .header("Authorization", tokenString)
                .header("Ocp-Apim-Subscription-Key", this.API_KEY)
                .body(objectJSON)
                .asJson();

            int status = response.getStatus();

            if (201 == status || 200 == status) {
                LOG.info("Succesfully added wellbore. UID=<" + uid + ">");
                return uid;
            } else {
                LOG.warning(
                    "Recieved status code from wellbore put: " + status + "\n" +
                    "URL=" + this.URL + "\n" + 
                    "ObjectJSON=" + objectJSON + "\n");
                return null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            LOG.warning("Error while creating object in DoTValve: " + e);
            return null;
        }
    }
}