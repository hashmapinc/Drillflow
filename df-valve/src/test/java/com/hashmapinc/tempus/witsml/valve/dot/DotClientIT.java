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

import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.auth0.jwt.interfaces.DecodedJWT;

import static org.junit.Assert.*;

public class DotClientIT {
	@Autowired
	private DotClient dotClient;
	private String username;
	private String password;

	@Before
	public void doSetup() {
		this.username = "admin";
		this.password = "12345";
		String apiKey = "test";
		String url = "https://witsml.hashmapinc.com:8443/"; // TODO: MOCK THIS
		String tokenPath = "token/jwt/v1/";
		dotClient = new DotClient(url, apiKey, tokenPath);
	}

	@Test
	public void authSuccessScenario() throws Exception {
		DecodedJWT response = dotClient.getJWT(username, password);
		System.out.println("decoded JWT is : " + response);
		assertNotNull(response);
	}

	@Test
	public void authCacheSuccessScenario() throws Exception {
		DecodedJWT response = dotClient.getJWT(username, password);
		assertNotNull(response);
		// Second fetch from the cache for the same credentials.
		DecodedJWT cacheResponse = dotClient.getJWT(username, password);
		assertNotNull(cacheResponse);
		assertEquals(cacheResponse.getToken(), response.getToken());
	}

	@Test(expected = ValveAuthException.class)
	public void authFailureScenario() throws ValveAuthException {
		String badPassword = this.password + "JUNK";
		DecodedJWT response = dotClient.getJWT(this.username, badPassword);
		assertNull(response);
	}
}
