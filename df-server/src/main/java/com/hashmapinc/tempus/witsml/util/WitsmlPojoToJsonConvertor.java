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
package com.hashmapinc.tempus.witsml.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell;
import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetFromStoreRequest;
import com.hashmapinc.tempus.witsml.server.api.model.WMLS_WellObjectToObj;

public class WitsmlPojoToJsonConvertor {

	public String pojotojsonConvertor(WMLS_GetFromStoreRequest wmls_GetFromStoreRequest) {

		ObjectMapper mapper = new ObjectMapper();

		try {
			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(wmls_GetFromStoreRequest);
			System.out.println(jsonInString);
			return jsonInString;
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}

	public JSONObject jsonStringToJsonObject(String jsonString) {
		return new JSONObject(jsonString);

	}

	public ObjWell JsonStringToWellObjectConvert(String json) throws IOException {
		ObjWell well = new ObjectMapper().readValue(json, ObjWell.class);
		return well;
	}

	public String populatedJSONFromResponseJSON(JSONObject sourceJson, JSONObject targetJson) {

		List<String> keyList = new ArrayList<>();
		Iterator<?> iterator = sourceJson.keys();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			Object value = sourceJson.get(key.toString());
			if (value.equals(null) || value.equals("")) {
				// taking empty keys and creating a array list.
				keyList.add(key.toString());

			}

		}

		// keyList contains all the keys from json1 whose value is either empty or null

		JSONObject josnObjectresponse = new JSONObject();
		JSONArray responseJsonArray = new JSONArray();

		// iterating over json2 and looking for keys from json1 which are null or empty

		keyList.forEach(key -> {

			josnObjectresponse.put(key, targetJson.get(key).toString());
		});

		responseJsonArray.put(josnObjectresponse); // creating a json array which contains only the populated fields which were empty in request json.
													
		return responseJsonArray.toString(); // converting json array to json string

	}

	public String ObjToXMLConvertor(WMLS_WellObjectToObj wellObj) {
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(WMLS_WellObjectToObj.class);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();

			jaxbMarshaller.marshal(wellObj, sw);

			return sw.toString();

		} catch (JAXBException e) {
			return e.getMessage();
		}
	}

}