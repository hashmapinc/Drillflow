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

import javax.json.JsonObject;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class DotTranslator {
	private static final Logger LOG = Logger.getLogger(DotTranslator.class.getName());

	private DotAuth dotAuth;

	/**
	 * This function takes the object, converts it to WITSML 1.4.1.1 if needed, then
	 * returns a JSON string of that object for rest calls
	 * 
	 * @param qc - Query context containing the object information needed
	 * @return jsonString - String serialization of a JSON version of the 1.4.1.1
	 *         witsml objecr
	 */
	public String get1411JSONString(AbstractWitsmlObject obj) {
		LOG.info("Getting 1.4.1.1 json string for object: " + obj.toString());
		return obj.getJSONString("1.4.1.1");
	}

	public JSONObject getWellResponse(QueryContext qc) throws UnirestException {
		AbstractWitsmlObject obj = qc.WITSML_OBJECTS.get(0); // converting witsml object to abstract object.
		String objectJSON = get1411JSONString(obj); // converting Abstract Object to jsonString
		String UID = obj.getUid();

		HttpResponse<JsonNode> getFromStoreResponse = Unirest
				.get("http://witsml-qa.hashmapinc.com:8080/witsml/wells/" + UID + "")
				.header("accept", "application/json").header("Content-Type", "application/json")
				.header("Authorization", this.dotAuth.getJWT(qc.USERNAME, qc.PASSWORD).getToken()).asJson();

		return getFromStoreResponse.getBody().getObject();
	}
}