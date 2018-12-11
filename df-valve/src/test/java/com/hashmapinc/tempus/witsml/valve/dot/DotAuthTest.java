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

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mashape.unirest.http.exceptions.UnirestException;

public class DotAuthTest {
	@Autowired
	private DotAuth dotAuth;
	private String username;
	private String password;

	@Before
	public void doSetup() {
		this.username = "admin";
		this.password = "12345";
		String apiKey = "test";
		//String url = "http://localhost:8080/";
		String url = "http://witsml-qa.hashmapinc.com:8080/"; // TODO: MOCK THIS
		dotAuth = new DotAuth(url, apiKey);
	}

	@Test
	public void authSuccessScenario() throws UnirestException, Exception {
		DecodedJWT response = dotAuth.getJWT(username, password);
		System.out.println("decoded JWT is : " + response);
		assertNotNull(response);
	}

	@Test
	public void authCacheSuccessScenario() throws UnirestException, Exception {
		DecodedJWT response = dotAuth.getJWT(username, password);
		assertNotNull(response);
		// Second fetch from the cache for the same credentials.
		DecodedJWT cacheResponse = dotAuth.getJWT(username, password);
		assertNotNull(cacheResponse);
		assertEquals(cacheResponse.getToken(), response.getToken());
	}

	@Test
	public void authFailureScenario() throws UnirestException {
		try {
			String badPassword = this.password + "JUNK";
			DecodedJWT response = dotAuth.getJWT(this.username, badPassword);
			assertTrue(null == response);
		} catch (Exception e) {
			assertNotNull(true);
		}
	}
}
