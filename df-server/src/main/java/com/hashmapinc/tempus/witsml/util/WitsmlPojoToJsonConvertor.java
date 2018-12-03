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

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell;
import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetFromStoreRequest;

public class WitsmlPojoToJsonConvertor {
	
	public String pojotojsonConvertor(WMLS_GetFromStoreRequest wmls_GetFromStoreRequest)
	{
		
		ObjectMapper mapper = new ObjectMapper();
		
		try
		{
			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(wmls_GetFromStoreRequest);
			System.out.println(jsonInString);
			return jsonInString;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return e.getMessage();
		}
		
		
	}
	
	public JSONObject jsonStringToJsonObject(String jsonString)
	{
		return new JSONObject(jsonString);
		
		
	}
	
	 public ObjWell JsonStringToWellObjectConvert(String json) throws IOException {
	        ObjWell well = new ObjectMapper().readValue(json, ObjWell.class);
	        return well;
	    }

}
