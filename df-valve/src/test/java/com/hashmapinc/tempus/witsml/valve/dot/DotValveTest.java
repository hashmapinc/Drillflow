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

import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.witsml.QueryContext;

import com.hashmapinc.tempus.witsml.valve.ValveException;
import org.junit.Before;
import org.junit.Test;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import static org.junit.Assert.*;

public class DotValveTest {
	private String username;
    private String password;
    private DotValve valve;
	@Before
	public void doSetup() {
		this.username = "admin";
        this.password = "12345";
        HashMap<String, String> config = new HashMap<>();
        config.put("baseurl", "http://witsml-qa.hashmapinc.com:8080/"); // TODO: MOCK THIS
        config.put("apikey", "COOLAPIKEY");
		valve = new DotValve(config);
	}

    //=========================================================================
    // WELL
    //=========================================================================
	@Test
	public void createObjectWell1311() throws IOException, JAXBException, ValveException {
	    try {
            // get query context
            String well1311XML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1311.xml")));
            List<AbstractWitsmlObject> witsmlObjects = (List<AbstractWitsmlObject>) (List<?>) ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells) WitsmlMarshal.deserialize(well1311XML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell.class)).getWell();
            QueryContext qc = new QueryContext(
                    "1.3.1.1",
                    "well",
                    null,
                    well1311XML,
                    witsmlObjects,
                    this.username,
                    this.password
            );

            // create
            String uid = this.valve.createObject(qc);
            assertNotNull(uid);
            assertEquals("w-1311", uid);
        } catch (ValveException ve) {
            assertTrue(ve.getMessage().contains("already exists")); // accept the "already exists" response as valid behavior
        }
	}

    @Test
    public void createObjectWell1411() throws IOException, JAXBException, ValveException{
	    try {
            // get query context
            String well1411XML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1411.xml")));
            List<AbstractWitsmlObject> witsmlObjects = (List<AbstractWitsmlObject>) (List<?>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells) WitsmlMarshal.deserialize(well1411XML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class)).getWell();
            QueryContext qc = new QueryContext(
                    "1.4.1.1",
                    "well",
                    null,
                    well1411XML,
                    witsmlObjects,
                    this.username,
                    this.password
            );

            // create
            String uid = this.valve.createObject(qc);
            assertNotNull(uid);
            assertEquals("w-1411", uid);
        } catch (ValveException ve) {
	        assertTrue(ve.getMessage().contains("already exists")); // accept the "already exists" response as valid behavior
        }
    }

    @Test
    public void getObjectWell1311() throws IOException, JAXBException, ValveException {
        // add the object first
        this.createObjectWell1311();;

        // get query context
        String well1311XML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1311query.xml")));
        List<AbstractWitsmlObject> witsmlObjects = (List<AbstractWitsmlObject>) (List<?>) ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells) WitsmlMarshal
                .deserialize(well1311XML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell.class)).getWell();
        QueryContext qc = new QueryContext("1.3.1.1", "well", null, well1311XML, witsmlObjects, this.username, this.password);

        // get
        String xmlOut = this.valve.getObject(qc);assertNotNull(xmlOut);
        assertFalse(xmlOut.contains("ns0:wells"));
    }

    @Test
    public void getObjectWell1411() throws IOException, JAXBException, ValveException {
        // add the object first
        this.createObjectWell1411();;

        // get query context
        String well1411XML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1411query.xml")));
        List<AbstractWitsmlObject> witsmlObjects = (List<AbstractWitsmlObject>) (List<?>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells) WitsmlMarshal
                .deserialize(well1411XML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class)).getWell();
        QueryContext qc = new QueryContext("1.4.1.1", "well", null, well1411XML, witsmlObjects, this.username, this.password);

        // get
        String xmlOut = this.valve.getObject(qc);
        assertNotNull(xmlOut);
        assertFalse(xmlOut.contains("ns0:wells"));
    }

    @Test
    public void deleteObjectWell1311() throws IOException, JAXBException, ValveException {
	    // add the deletable object first
        try {
            this.createObjectWell1311();
        } catch (Exception e) {}

        // get query context
        String well1311XML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1311.xml")));
        List<AbstractWitsmlObject> witsmlObjects = (List<AbstractWitsmlObject>) (List<?>) ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells) WitsmlMarshal
                .deserialize(well1311XML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell.class)).getWell();
        QueryContext qc = new QueryContext(
            null,
            "well",
            null,
            well1311XML,
            witsmlObjects,
            this.username,
            this.password
        );

        // delete
        this.valve.deleteObject(qc);
    }

    @Test
    public void deleteObjectWell1411() throws IOException, JAXBException, ValveException {
        // add the deletable object first
        try {
            this.createObjectWell1411();
        } catch (Exception e) {}


        // get query context
        String well1411XML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1411.xml")));
        List<AbstractWitsmlObject> witsmlObjects = (List<AbstractWitsmlObject>) (List<?>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells) WitsmlMarshal
                .deserialize(well1411XML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class)).getWell();
        QueryContext qc = new QueryContext(
            "1.4.1.1",
            "well",
            null,
            well1411XML,
            witsmlObjects,
            this.username,
            this.password
        );

        // delete
        this.valve.deleteObject(qc);
    }
    //=========================================================================

    //=========================================================================
    // WELLBORE
    //=========================================================================
    @Test
    public void createObjectWellbore1311() throws IOException, JAXBException, ValveException {
	    try {
            // get query context
            String wellbore1311XML = new String(Files.readAllBytes(Paths.get("src/test/resources/wellbore1311.xml")));
            List<AbstractWitsmlObject> witsmlObjects = (List<AbstractWitsmlObject>) (List<?>) ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores) WitsmlMarshal
                    .deserialize(wellbore1311XML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore.class)).getWellbore();
            QueryContext qc = new QueryContext("1.3.1.1", "wellbore", null, wellbore1311XML, witsmlObjects, this.username,
                    this.password);

            // create
            String uid = this.valve.createObject(qc);
            assertNotNull(uid);
            assertEquals("b-1311-1", uid);
        } catch (ValveException ve) {
            assertTrue(ve.getMessage().contains("already exists")); // accept the "already exists" response as valid behavior
        }
    }

    @Test
    public void createObjectWellbore1411() throws IOException, JAXBException, ValveException {
	    try {
            // get query context
            String wellbore1411XML = new String(Files.readAllBytes(Paths.get("src/test/resources/wellbore1411.xml")));
            List<AbstractWitsmlObject> witsmlObjects = (List<AbstractWitsmlObject>) (List<?>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores) WitsmlMarshal
                    .deserialize(wellbore1411XML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore.class)).getWellbore();
            QueryContext qc = new QueryContext("1.4.1.1", "wellbore", null, wellbore1411XML, witsmlObjects, this.username,
                    this.password);

            // create
            String uid = this.valve.createObject(qc);
            assertNotNull(uid);
            assertEquals("b-1411-1", uid);
        } catch (ValveException ve) {
            assertTrue(ve.getMessage().contains("already exists")); // accept the "already exists" response as valid behavior
        }
    }
    //=========================================================================
}








