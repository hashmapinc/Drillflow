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

import org.junit.Before;
import org.junit.Test;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerConfigurationException;

public class DotValveTest {
	private String username;
    private String password;
    private DotValve valve;

	@Before
	public void doSetup() throws Exception {
		this.username = "admin";
        this.password = "12345";
        HashMap<String, String> config = new HashMap<>();
        config.put("baseurl", "http://witsml-qa.hashmapinc.com:8080/"); // TODO: MOCK THIS
        config.put("apikey", "COOLAPIKEY");
		valve = new DotValve(config);
	}

	@Test
	public void createObjectWell1311() throws IOException, JAXBException{
        // get query context
        String well1311XML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1311.xml")));
        List<AbstractWitsmlObject> witsmlObjects = (List<AbstractWitsmlObject>) (List<?>)((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells)WitsmlMarshal.deserialize(well1311XML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell.class)).getWell();
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
        assertEquals("w-12", uid);
	}

    @Test
    public void createObjectWell1411() throws IOException, JAXBException{
        // get query context
        String well1411XML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1411.xml")));
        List<AbstractWitsmlObject> witsmlObjects = (List<AbstractWitsmlObject>) (List<?>)((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells)WitsmlMarshal.deserialize(well1411XML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class)).getWell();
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
        assertEquals("w-12", uid);
    }
}









