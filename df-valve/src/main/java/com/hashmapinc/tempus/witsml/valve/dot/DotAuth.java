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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hashmapinc.tempus.witsml.valve.constants.Constants;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class DotAuth {
	public final String URL;
	public final String API_KEY;
	private Map<String, DecodedJWT> cache = new HashMap<String, DecodedJWT>();
	
	/**
	 * DotAuth constructor
	 * 
	 * @param URL
	 * @param API_KEY
	 */
	public DotAuth(String URL, String API_KEY) {
		this.URL = URL;
		this.API_KEY = API_KEY;
	}

	/**
	 * Generate Token for given creds and save the token in cache
	 * 
	 * @param username
	 * @param password
	 * @return JWT Token
	 * @throws UnirestException
	 */
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

	/**
	 * Request JWT token for the given creds. Return the token from cache if exists
	 * else generate a new one.
	 * 
	 * @param username
	 * @param password
	 * @return JWT Token from cache if exists and not expired else new generated
	 *         token
	 * @throws UnirestException
	 */
	public DecodedJWT getJWT(String username, String password) throws UnirestException {
		// check if the Token exists in the Cache.
		if (cache.containsKey(username) && !isTokenExpired(username)) {
			return cache.get(username);
		} else {
			DecodedJWT decodedJWTResponse = refreshToken(username, password);
			// return the decoded JWT Token.
			return decodedJWTResponse;
		}
	}

	/**
	 * Check if JWT Token expired for given username
	 * 
	 * @param username
	 * @return true if token expired else false
	 */
	private boolean isTokenExpired(String username) {
		boolean result = false;
		DecodedJWT decodedJWT = cache.get(username);
		Date tokenExpiredDate = decodedJWT.getExpiresAt();
		Date now = new Date();
		long timeRemaining = tokenExpiredDate.getTime()-now.getTime();
		if (timeRemaining <= Constants.JWT_TOKEN_EXPIRY_BUFFER) {
			result = true;
		}
		return result;
	}
}