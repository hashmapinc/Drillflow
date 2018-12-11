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
import java.util.logging.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class DotAuth {
	private static final Logger LOG = Logger.getLogger(DotAuth.class.getName());
	private final String URL;
	private final String API_KEY;
	private Map<String, DecodedJWT> cache;

	/**
	 * DotAuth constructor
	 * 
	 * @param URL
	 * @param API_KEY
	 */
	public DotAuth(String URL, String API_KEY) {
		this.URL = URL;
		this.API_KEY = API_KEY;
		this.cache = new HashMap<String, DecodedJWT>();
	}

	/**
	 * Generate Token for given creds and save the token in cache
	 * 
	 * @param username
	 * @param password
	 */
	private void refreshToken(
		String username,
		String password
	) throws ValveAuthException {
		try {
			// build payload for authentication
			String payload = "{\"account\":\"" + username + "\", \"password\":\"" + password + "\"}";

			// send request
			HttpResponse<String> response = Unirest.post(URL + "/token/jwt/v1/")
				.header("accept", "application/json")
				.header("Ocp-Apim-Subscription-Key", this.API_KEY)
				.body(payload)
				.asString();

			// validate response
			int status = response.getStatus();
			LOG.fine("refreshToken call for " + username + "returned REST status: " + status); // fine = debug I guess. Stupid JDK 11
			if (201 == status || 200 == status) {
				// get JWT from response
				String tokenString = new JsonNode(response.getBody()).getObject().getString("jwt");
				DecodedJWT decodedJwtToken = JWT.decode(tokenString);
				cache.put(username, decodedJwtToken); // cache response
			} else {
				throw new ValveAuthException(response.getBody());
			}
		} catch (Exception e) {
			throw new ValveAuthException("Error refreshing token: " + e.getMessage(), e);
		}
	}

	/**
	 * Refresh the cache if necessary then return the JWT
	 * 
	 * @param username
	 * @param password
	 * @return JWT from auth endpoint
	 * @throws ValveAuthException
	 */
	public DecodedJWT getJWT(
		String username,
		String password
	) throws ValveAuthException {
		// refresh token if necessary
		if (!cache.containsKey(username) || isTokenExpired(username))
			refreshToken(username, password);

		return cache.get(username);
	}

	/**
	 * Refresh the cache if necessary then return the JWT
	 *
	 * @param username
	 * @param password
	 * @param forceRefresh - boolean indicating if a refresh should be performed
	 * @return JWT from auth endpoint
	 * @throws ValveAuthException
	 */
	public DecodedJWT getJWT(
		String username,
		String password,
		boolean forceRefresh
	) throws ValveAuthException {
		// refresh token if necessary
		if (forceRefresh || !cache.containsKey(username) || isTokenExpired(username))
			refreshToken(username, password);

		return cache.get(username);
	}

	/**
	 * Check if JWT Token will expire in the next BUFFER milliseconds
	 * 
	 * @param username
	 * @return true if token expires in the next BUFFER milliseconds
	 */
	private boolean isTokenExpired(String username) {
		// create buffer
		long JWT_TOKEN_EXPIRY_BUFFER = 5 * 60 * 1000; // 5 minute buffer

		// get time until token expires
		long timeUntilExpiration = cache.get(username).getExpiresAt().getTime() - (new Date()).getTime();

		// return true if token expires within the BUFFER time
		return timeUntilExpiration <= JWT_TOKEN_EXPIRY_BUFFER;
	}
}