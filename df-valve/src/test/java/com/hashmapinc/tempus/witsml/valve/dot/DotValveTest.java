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

import com.auth0.jwt.JWT;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;

public class DotValveTest {
	private DotClient mockClient;
    private DotDelegator mockDelegator;
    private DotValve valve;

	@Before
	public void doSetup() {
		this.mockClient = mock(DotClient.class);
		this.mockDelegator = mock(DotDelegator.class);
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
	public void shouldGetSingleObject() throws Exception {
		// build well list
		ArrayList<AbstractWitsmlObject> witsmlObjects;
		witsmlObjects = new ArrayList<>();
		ObjWell well = new ObjWell();
		well.setName("well-1");
		well.setUid("well-1");
		witsmlObjects.add(well);

		// build query context
		QueryContext qc = new QueryContext(
			"1.3.1.1",
			"well",
			null,
			"",
			witsmlObjects,
			"goodUsername",
			"goodPassword",
			"shouldGetSingleObject" // exchange ID
		);

		// mock delegator behavior
		when(
			this.mockDelegator.getObject(well, qc.USERNAME, qc.PASSWORD,qc.EXCHANGE_ID, this.mockClient)
		).thenReturn(well);

		// test
		String expected = well.getXMLString("1.3.1.1");
		String actual = this.valve.getObject(qc);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldGetEmptyObject() throws Exception {
		// build well list
		ArrayList<AbstractWitsmlObject> witsmlObjects;
		witsmlObjects = new ArrayList<>();
		ObjWell well = new ObjWell();
		well.setName("well-1");
		well.setUid("well-1");
		witsmlObjects.add(well);

		// build query context
		QueryContext qc = new QueryContext(
			"1.3.1.1",
			"well",
			null,
			"",
			witsmlObjects,
			"goodUsername",
			"goodPassword",
			"shouldGetEmptyObject" // exchange ID
		);

		// mock delegator behavior
		when(
			this.mockDelegator.getObject(well, qc.USERNAME, qc.PASSWORD,qc.EXCHANGE_ID, this.mockClient)
		).thenReturn(null);

		// test
		String expected = WitsmlMarshal.serialize(new ObjWells());
		String actual = this.valve.getObject(qc);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldGetPluralObject() throws Exception {
		// build witsmlObjects list
		ArrayList<AbstractWitsmlObject> witsmlObjects;
		witsmlObjects = new ArrayList<>();

		ObjWellbore wellboreA = new ObjWellbore();
		wellboreA.setName("wellbore-A");
		wellboreA.setUid("wellbore-A");
		witsmlObjects.add(wellboreA);

		ObjWellbore wellboreB = new ObjWellbore();
		wellboreB.setName("wellbore-B");
		wellboreB.setUid("wellbore-B");
		witsmlObjects.add(wellboreB);


		// build query context
		QueryContext qc = new QueryContext(
			"1.3.1.1",
			"wellbore",
			null,
			"",
			witsmlObjects,
			"goodUsername",
			"goodPassword",
			"shouldGetPluralObject" // exchange ID
		);


		// mock delegator behavior
		when(
			this.mockDelegator.getObject(wellboreA, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.mockClient)
		).thenReturn(wellboreA);
		when(
			this.mockDelegator.getObject(wellboreB, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.mockClient)
		).thenReturn(wellboreB);


		// test
		String expected = // expected = merge wellboreA and wellbore B
			wellboreA.getXMLString("1.3.1.1").replace("</wellbores>", "") +
			wellboreB.getXMLString("1.3.1.1").replace(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
				"<wellbores version=\"1.3.1.1\" xmlns=\"http://www.witsml.org/schemas/131\">",
				""
			);
		String actual = this.valve.getObject(qc);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldCreateSingleObject() throws Exception {
		// build witsmlObjects list
		ArrayList<AbstractWitsmlObject> witsmlObjects;
		witsmlObjects = new ArrayList<>();

		ObjWellbore wellboreA = new ObjWellbore();
		wellboreA.setName("wellbore-A");
		wellboreA.setUid("wellbore-A");
		witsmlObjects.add(wellboreA);


		// build query context
		QueryContext qc = new QueryContext(
			"1.3.1.1",
			"wellbore",
			null,
			"",
			witsmlObjects,
			"goodUsername",
			"goodPassword",
			"shouldCreateSingleObject" // exchange ID
		);


		// mock delegator behavior
		when(
			this.mockDelegator.createObject(wellboreA, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.mockClient)
		).thenReturn(wellboreA.getUid());


		// test
		String expected = wellboreA.getUid();
		String actual = this.valve.createObject(qc);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldCreateTrajectory() throws Exception {
		// build witsmlObjects list
		ArrayList<AbstractWitsmlObject> witsmlObjects;
		witsmlObjects = new ArrayList<>();

		ObjTrajectory traj = new ObjTrajectory();
		traj.setUid("traj-A");
		traj.setName("traj-A");

		witsmlObjects.add(traj);

		// build query context
		QueryContext qc = new QueryContext(
			"1.3.1.1",
			"trajectory",
			null,
			"",
			witsmlObjects,
			"goodUsername",
			"goodPassword",
			"shouldCreateTrajectory" // exchange ID
		);


		// mock delegator behavior
		when(
			this.mockDelegator.createObject(traj, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.mockClient)
		).thenReturn(traj.getUid());


		// test
		String expected = traj.getUid();
		String actual = this.valve.createObject(qc);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldCreatePluralObject() throws Exception {
		// build witsmlObjects list
		ArrayList<AbstractWitsmlObject> witsmlObjects;
		witsmlObjects = new ArrayList<>();

		ObjWellbore wellboreA = new ObjWellbore();
		wellboreA.setName("wellbore-A");
		wellboreA.setUid("wellbore-A");
		witsmlObjects.add(wellboreA);

		ObjWellbore wellboreB = new ObjWellbore();
		wellboreB.setName("wellbore-B");
		wellboreB.setUid("wellbore-B");
		witsmlObjects.add(wellboreB);


		// build query context
		QueryContext qc = new QueryContext(
			"1.3.1.1",
			"wellbore",
			null,
			"",
			witsmlObjects,
			"goodUsername",
			"goodPassword",
			"shouldCreatePluralObject" // exchange ID
		);


		// mock delegator behavior
		when(
			this.mockDelegator.createObject(wellboreA, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.mockClient)
		).thenReturn(wellboreA.getUid());
		when(
			this.mockDelegator.createObject(wellboreB, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.mockClient)
		).thenReturn(wellboreB.getUid());


		// test
		String expected = wellboreA.getUid() + "," + wellboreB.getUid();
		String actual = this.valve.createObject(qc);
		assertEquals(expected, actual);
	}

	@Test
	public void shouldDeleteSingleObject() throws Exception {
		// build witsmlObjects list
		ArrayList<AbstractWitsmlObject> witsmlObjects;
		witsmlObjects = new ArrayList<>();

		ObjWellbore wellboreA = new ObjWellbore();
		wellboreA.setName("wellbore-A");
		wellboreA.setUid("wellbore-A");
		witsmlObjects.add(wellboreA);


		// build query context
		QueryContext qc = new QueryContext(
			"1.3.1.1",
			"wellbore",
			null,
			"",
			witsmlObjects,
			"goodUsername",
			"goodPassword",
			"shouldDeleteSingleObject" // exchange ID
		);


		// test getObject
		this.valve.deleteObject(qc);


		// verify
		verify(this.mockDelegator).deleteObject(wellboreA, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.mockClient);
		verifyNoMoreInteractions(this.mockDelegator);
	}

	@Test
	public void shouldDeletePluralObject() throws Exception {
		// build witsmlObjects list
		ArrayList<AbstractWitsmlObject> witsmlObjects;
		witsmlObjects = new ArrayList<>();

		ObjWellbore wellboreA = new ObjWellbore();
		wellboreA.setName("wellbore-A");
		wellboreA.setUid("wellbore-A");
		witsmlObjects.add(wellboreA);

		ObjWellbore wellboreB = new ObjWellbore();
		wellboreB.setName("wellbore-B");
		wellboreB.setUid("wellbore-B");
		witsmlObjects.add(wellboreB);


		// build query context
		QueryContext qc = new QueryContext(
			"1.3.1.1",
			"wellbore",
			null,
			"",
			witsmlObjects,
			"goodUsername",
			"goodPassword",
			"shouldDeletePluralObject" // exchange ID
		);


		// test getObject
		this.valve.deleteObject(qc);


		// verify
		verify(this.mockDelegator).deleteObject(wellboreA, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.mockClient);
		verify(this.mockDelegator).deleteObject(wellboreB, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.mockClient);
		verifyNoMoreInteractions(this.mockDelegator);
	}

	@Test
	public void shouldUpdateObject() throws Exception {
		// build witsmlObjects list
		ArrayList<AbstractWitsmlObject> witsmlObjects;
		witsmlObjects = new ArrayList<>();

		ObjWellbore wellboreA = new ObjWellbore();
		wellboreA.setName("wellbore-A");
		wellboreA.setUid("wellbore-A");
		witsmlObjects.add(wellboreA);

		// build query context
		QueryContext qc = new QueryContext(
			"1.3.1.1",
			"wellbore",
			null,
			"",
			witsmlObjects,
			"goodUsername",
			"goodPassword",
			"shouldUpdateObject" // exchange ID
		);


		// test getObject
		this.valve.updateObject(qc);


		// verify
		verify(this.mockDelegator).updateObject(wellboreA, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.mockClient);
		verifyNoMoreInteractions(this.mockDelegator);
	}

	@Test
	public void shouldSucceedAuthenticate() throws Exception {
		// mock behavior
		when(this.mockClient.getJWT("goodUsername", "goodPassword"))
			.thenReturn(JWT.decode( // using dummy token string from https://jwt.io/
				"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiR29vZCBUb2tlbiJ9.II4bNgtahHhV4jl7dgGn8CGjVxZWwBMZht-4LXeqB_Y"
			)
		);

		// test
		this.valve.authenticate("goodUsername", "goodPassword");

		// verify
		verify(this.mockClient).getJWT("goodUsername", "goodPassword");
		verifyNoMoreInteractions(this.mockClient);
	}

	@Test(expected = ValveAuthException.class)
	public void shouldFailAuthenticate() throws ValveAuthException {
		// add mock behavior
		when(this.mockClient.getJWT("badUsername", "badPassword"))
			.thenThrow(new ValveAuthException(""));

		// test
		this.valve.authenticate("badUsername", "badPassword");
	}

	@Test
	public void shouldGetCap() {
		// get cap
		Map<String, AbstractWitsmlObject[]> cap = this.valve.getCap();

		// verify keys
		assertFalse(cap.isEmpty());
		assertTrue(cap.containsKey("WMLS_AddToStore"));
		assertTrue(cap.containsKey("WMLS_GetFromStore"));
		assertTrue(cap.containsKey("WMLS_DeleteFromStore"));
		assertTrue(cap.containsKey("WMLS_UpdateInStore"));


		// get values
		AbstractWitsmlObject[] actualAddObjects = 	 cap.get("WMLS_AddToStore");
		AbstractWitsmlObject[] actualGetObjects = 	 cap.get("WMLS_GetFromStore");
		AbstractWitsmlObject[] actualDeleteObjects = cap.get("WMLS_DeleteFromStore");
		AbstractWitsmlObject[] actualUpdateObjects = cap.get("WMLS_UpdateInStore");

		// verify values
		assertEquals("well", 	   actualAddObjects[0].getObjectType());
		assertEquals("wellbore",   actualAddObjects[1].getObjectType());
		assertEquals("trajectory", actualAddObjects[2].getObjectType());
		assertEquals("well", 	   actualGetObjects[0].getObjectType());
		assertEquals("wellbore",   actualGetObjects[1].getObjectType());
		assertEquals("well", 	   actualDeleteObjects[0].getObjectType());
		assertEquals("wellbore",   actualDeleteObjects[1].getObjectType());
		assertEquals("well", 	   actualUpdateObjects[0].getObjectType());
		assertEquals("wellbore",   actualUpdateObjects[1].getObjectType());
	}
}








