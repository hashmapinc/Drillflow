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
package com.hashmapinc.tempus.witsml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;


public class WitsmlObjectParserTests {

	@Test
	public void parse_shouldParseValidXML_log1311() throws IOException {
        String version = "1.3.1.1";
        String objectType = "log";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/log1311.xml")));

        // parse the object
        List<AbstractWitsmlObject> parseResults = null;
        try {
            parseResults = WitsmlObjectParser.parse(objectType, validXML, version);
            for (AbstractWitsmlObject parseResult: parseResults) {
                assertThat(parseResult.getObjectType().toLowerCase()).isEqualTo(objectType);
            }  
        } catch (Exception e) {
            fail(e.toString());
        }

        // assert that the object is not null
        assertThat(
            parseResults
        ).isNotNull();
	}

	@Test
	public void parse_shouldParseValidXML_log1411() throws IOException {
        String version = "1.4.1.1";
        String objectType = "log";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/log1411.xml")));

        // parse the object
        List<AbstractWitsmlObject> parseResults = null;
        try {
            parseResults = WitsmlObjectParser.parse(objectType, validXML, version); 
            for (AbstractWitsmlObject parseResult: parseResults) {
                assertThat(parseResult.getObjectType().toLowerCase()).isEqualTo(objectType);
            } 
        } catch (Exception e) {
            fail(e.toString());
        }

        // assert that the object is not null
        assertThat(
            parseResults
        ).isNotNull();
	}

	@Test
	public void parse_shouldParseValidXML_trajectory1311() throws IOException {
        String version = "1.3.1.1";
        String objectType = "trajectory";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/trajectory1311.xml")));

        // parse the object
        List<AbstractWitsmlObject> parseResults = null;
        try {
            parseResults = WitsmlObjectParser.parse(objectType, validXML, version);
            for (AbstractWitsmlObject parseResult: parseResults) {
                assertThat(parseResult.getObjectType().toLowerCase()).isEqualTo(objectType);
            }  
        } catch (Exception e) {
            fail(e.toString());
        }

        // assert that the object is not null
        assertThat(
            parseResults
        ).isNotNull();
	}

	@Test
	public void parse_shouldParseValidXML_trajectory1411() throws IOException {
        String version = "1.4.1.1";
        String objectType = "trajectory";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/trajectory1411.xml")));

        // parse the object
        List<AbstractWitsmlObject> parseResults = null;
        try {
            parseResults = WitsmlObjectParser.parse(objectType, validXML, version);  
            for (AbstractWitsmlObject parseResult: parseResults) {
                assertThat(parseResult.getObjectType().toLowerCase()).isEqualTo(objectType);
            }
        } catch (Exception e) {
            fail(e.toString());
        }

        // assert that the object is not null
        assertThat(
            parseResults
        ).isNotNull();
	}

	@Test
	public void parse_shouldParseValidXML_well1311() throws IOException {
        String version = "1.3.1.1";
        String objectType = "well";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1311.xml")));

        // parse the object
        List<AbstractWitsmlObject> parseResults = null;
        try {
            parseResults = WitsmlObjectParser.parse(objectType, validXML, version); 
            for (AbstractWitsmlObject parseResult: parseResults) {
                assertThat(parseResult.getObjectType().toLowerCase()).isEqualTo(objectType);
            } 
        } catch (Exception e) {
            fail(e.toString());
        }

        // assert that the object is not null
        assertThat(
            parseResults
        ).isNotNull();
	}

	@Test
	public void parse_shouldParseValidXML_well1411() throws IOException {
        String version = "1.4.1.1";
        String objectType = "well";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1411.xml")));

        // parse the object
        List<AbstractWitsmlObject> parseResults = null;
        try {
            parseResults = WitsmlObjectParser.parse(objectType, validXML, version);
            for (AbstractWitsmlObject parseResult: parseResults) {
                assertThat(parseResult.getObjectType().toLowerCase()).isEqualTo(objectType);
            }  
        } catch (Exception e) {
            fail(e.toString());
        }

        // assert that the object is not null
        assertThat(
            parseResults
        ).isNotNull();
	}

	@Test
	public void parse_shouldParseValidXML_wellbore1311() throws IOException {
        String version = "1.3.1.1";
        String objectType = "wellbore";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/wellbore1311.xml")));

        // parse the object
        List<AbstractWitsmlObject> parseResults = null;
        try {
            parseResults = WitsmlObjectParser.parse(objectType, validXML, version); 
            for (AbstractWitsmlObject parseResult: parseResults) {
                assertThat(parseResult.getObjectType().toLowerCase()).isEqualTo(objectType);
            } 
        } catch (Exception e) {
            fail(e.toString());
        }

        // assert that the object is not null
        assertThat(
            parseResults
        ).isNotNull();
	}

	@Test
	public void parse_shouldParseValidXML_wellbore1411() throws IOException {
        String version = "1.4.1.1";
        String objectType = "wellbore";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/wellbore1411.xml")));

        // parse the object
        List<AbstractWitsmlObject> parseResults = null;
        try {
            parseResults = WitsmlObjectParser.parse(objectType, validXML, version);  
            for (AbstractWitsmlObject parseResult: parseResults) {
                assertThat(parseResult.getObjectType().toLowerCase()).isEqualTo(objectType);
            }
        } catch (Exception e) {
            fail(e.toString());
        }

        // assert that the object is not null
        assertThat(
            parseResults
        ).isNotNull();
	}
}
