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

import org.mockito.Mockito;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

public class DotValveTest {
	private DotClient mockClient;
    private DotDelegator mockDelegator;
    private DotValve valve;

	@Before
	public void doSetup() {
		this.mockClient = Mockito.mock(DotClient.class);
		this.mockDelegator = Mockito.mock(DotDelegator.class);
		this.valve = new DotValve(this.mockClient, this.mockDelegator); // inject mocks into valve
	}

	@Test
	public void shouldGetName() {
		assertEquals("DoT", this.valve.getName());
	}

	@Test
	public void shouldGetDescription() {
		assertEquals(
			"Valve for interaction with Drillops Town",
			this.valve.getDescription()
		);
	}

	@Test
	public void shouldGetSingleObject() {
	}

	@Test
	public void shouldGetPluralObject() {
	}

	@Test
	public void shouldCreateSingleObject() {
	}

	@Test
	public void shouldCreatePluralObject() {
	}

	@Test
	public void shouldDeleteSingleObject() {
	}

	@Test
	public void shouldDeletePluralObject() {
	}

	@Test
	public void shouldUpdateObject() {
	}

	@Test
	public void shouldSucceedAuthenticate() {
	}

	@Test(expected = ValveAuthException.class)
	public void shouldFailAuthenticate() throws ValveAuthException {
		// add mock behavior
		when(this.mockClient.getJWT("badUsername", "badPassword"))
			.thenThrow(new ValveAuthException(""));
		valve.authenticate("badUsername", "badPassword");
	}

	@Test
	public void shouldGetCap() {
	}
}








