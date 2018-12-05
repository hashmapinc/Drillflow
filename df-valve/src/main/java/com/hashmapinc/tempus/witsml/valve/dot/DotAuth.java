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

import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class DotAuth {
	public final String URL;
	public final String API_KEY;
	private Map<String, DecodedJWT> cache = new HashMap<String, DecodedJWT>();

	public DotAuth(String URL, String API_KEY) {
		this.URL = URL;
		this.API_KEY = API_KEY;

	}
	/*
	 * This method generates the Decoded JWT token based on username and password
	 * passed by the getJWT method.
	 * */
	public DecodedJWT refreshToken(String username, String password) throws UnirestException {

		String userinfo = "{\"account\":\"" + username + "\", \"password\":\"" + password + "\"}";

		// send the response
		HttpResponse<JsonNode> response = Unirest.post(URL).header("accept", "application/json")
				.header("Ocp-Apim-Subscription-Key", this.API_KEY).body(userinfo).asJson();

		// get the token string
		String tokenString = response.getBody().getObject().getString("jwt");
		
		DecodedJWT decodedJwtToken = JWT.decode(tokenString);
		
		cache.put(username, decodedJwtToken);

		return decodedJwtToken;

	}

	/*
	 * This method checks for the JWT token in cache
	 * if exists returns the cache else calls refresh token to generate the token
	 * 
	 */
	public DecodedJWT getJWT(String username, String password) throws UnirestException {
		//check if the Token exists in the Cache.
		if (cache.containsKey(username)) {

			return cache.get(username);

		} else {

			DecodedJWT decodedJWTResponse = this.refreshToken(username, password);
			

			// return the decoded JWT Token.
			return decodedJWTResponse;
		}
	}

}