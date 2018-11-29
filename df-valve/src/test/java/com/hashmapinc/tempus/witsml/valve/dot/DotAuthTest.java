
package com.hashmapinc.tempus.witsml.valve.dot;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.mashape.unirest.http.exceptions.UnirestException;

public class DotAuthTest {

	@Autowired
	private DotAuth dotAuth;

	private String username;
	private String password;
	private String apiKey;

	@Before
	public void doSetup() {
		this.username = "admin";
		this.password = "12345";
		this.apiKey = "test";
		dotAuth = new DotAuth("http://witsml-qa.hashmapinc.com:8080/");
	}

	@Test
	public void authSuccessScenario() throws UnirestException, Exception {

		DecodedJWT response = dotAuth.getJWT(apiKey, username, password);

		org.junit.Assert.assertNotNull(response);

	}

	@Test
	public void authFailureScenario() throws UnirestException {

		DecodedJWT response = dotAuth.getJWT(apiKey, "restricted", "6789");

		org.junit.Assert.assertNotNull(response);

	}

}
