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

import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class DotAuth {
	public final String URL;
	public final String API_KEY;

	public DotAuth(
		String URL,
		String API_KEY
	) {
		this.URL = URL;
		this.API_KEY = API_KEY;
	}

	/**
	 * This function gets a JWT from the auth endpoint of the DoT server
	 * @param username - String username for basic auth
	 * @param password - String password for basic auth
	 * @return jwt - DecodedJWT obtained from the auth call
	 * @throws UnirestException
	 */
	public DecodedJWT getJWT(String username, String password) throws UnirestException {
		// build the userinfo string
		String userinfo = "{\"account\":\"" + username + "\", \"password\":\"" + password + "\"}";

		// send the response
		HttpResponse<JsonNode> response = Unirest.post(URL)
				.header("accept", "application/json")
				.header("Ocp-Apim-Subscription-Key", this.API_KEY)
				.body(userinfo).asJson();

		// get the token string
		String tokenString = response
			.getBody()
			.getObject()
			.getString("jwt");

		// return the decoded tokenstring
		return JWT.decode(tokenString);
	}
}
