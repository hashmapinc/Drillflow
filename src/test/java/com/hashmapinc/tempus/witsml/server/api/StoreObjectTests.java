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
package com.hashmapinc.tempus.witsml.server.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreObjectTests {

	@Autowired
	private StoreObject storeObject;

	@Test
	public void contextLoads() {
		assertThat(storeObject).isNotNull();
	}

	@Test
	public void parseInPlace_shouldParseValidXML_log1311() throws IOException {
        String version = "1.3.1.1";
        String objectType = "log";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/log1311.xml")));

        // clean the object first
        this.storeObject.empty();

        // assert that the parsing happens without error
        assertThat(
            this.storeObject.parseInPlace(objectType, validXML, version)
        ).isEqualTo(false);

        // assert that the storeObject now contains the object
        assertThat(
            this.storeObject.log1311
        ).isNotNull();
	}

	@Test
	public void parseInPlace_shouldParseValidXML_log1411() throws IOException {
        String version = "1.4.1.1";
        String objectType = "log";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/log1411.xml")));

        // clean the object first
        this.storeObject.empty();

        // assert that the parsing happens without error
        assertThat(
            this.storeObject.parseInPlace(objectType, validXML, version)
        ).isEqualTo(false);

        // assert that the storeObject now contains the object
        assertThat(
            this.storeObject.log1411
        ).isNotNull();
	}

	@Test
	public void parseInPlace_shouldParseValidXML_trajectory1311() throws IOException {
        String version = "1.3.1.1";
        String objectType = "trajectory";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/trajectory1311.xml")));

        // clean the object first
        this.storeObject.empty();

        // assert that the parsing happens without error
        assertThat(
            this.storeObject.parseInPlace(objectType, validXML, version)
        ).isEqualTo(false);

        // assert that the storeObject now contains the object
        assertThat(
            this.storeObject.trajectory1311
        ).isNotNull();
	}

	@Test
	public void parseInPlace_shouldParseValidXML_trajectory1411() throws IOException {
        String version = "1.4.1.1";
        String objectType = "trajectory";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/trajectory1411.xml")));

        // clean the object first
        this.storeObject.empty();

        // assert that the parsing happens without error
        assertThat(
            this.storeObject.parseInPlace(objectType, validXML, version)
        ).isEqualTo(false);

        // assert that the storeObject now contains the object
        assertThat(
            this.storeObject.trajectory1411
        ).isNotNull();
	}

	@Test
	public void parseInPlace_shouldParseValidXML_well1311() throws IOException {
        String version = "1.3.1.1";
        String objectType = "well";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1311.xml")));

        // clean the object first
        this.storeObject.empty();

        // assert that the parsing happens without error
        assertThat(
            this.storeObject.parseInPlace(objectType, validXML, version)
        ).isEqualTo(false);

        // assert that the storeObject now contains the object
        assertThat(
            this.storeObject.well1311
        ).isNotNull();
	}

	@Test
	public void parseInPlace_shouldParseValidXML_well1411() throws IOException {
        String version = "1.4.1.1";
        String objectType = "well";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/well1411.xml")));

        // clean the object first
        this.storeObject.empty();

        // assert that the parsing happens without error
        assertThat(
            this.storeObject.parseInPlace(objectType, validXML, version)
        ).isEqualTo(false);

        // assert that the storeObject now contains the object
        assertThat(
            this.storeObject.well1411
        ).isNotNull();
	}

	@Test
	public void parseInPlace_shouldParseValidXML_wellbore1311() throws IOException {
        String version = "1.3.1.1";
        String objectType = "wellbore";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/wellbore1311.xml")));

        // clean the object first
        this.storeObject.empty();

        // assert that the parsing happens without error
        assertThat(
            this.storeObject.parseInPlace(objectType, validXML, version)
        ).isEqualTo(false);

        // assert that the storeObject now contains the object
        assertThat(
            this.storeObject.wellbore1311
        ).isNotNull();
	}

	@Test
	public void parseInPlace_shouldParseValidXML_wellbore1411() throws IOException {
        String version = "1.4.1.1";
        String objectType = "wellbore";
        String validXML = new String(Files.readAllBytes(Paths.get("src/test/resources/wellbore1411.xml")));

        // clean the object first
        this.storeObject.empty();

        // assert that the parsing happens without error
        assertThat(
            this.storeObject.parseInPlace(objectType, validXML, version)
        ).isEqualTo(false);

        // assert that the storeObject now contains the object
        assertThat(
            this.storeObject.wellbore1411
        ).isNotNull();
	}
}
