package com.hashmapinc.tempus.witsml.util;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
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

}
