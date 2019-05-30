/**
 * Copyright © 2018-2019 Hashmap, Inc
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

import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetCapResponse;
import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetFromStoreResponse;
import com.hashmapinc.tempus.witsml.valve.ObjectSelectionConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreImplTests {

	@Autowired
	private StoreImpl witsmlServer;

	private String minWellQueryTemplate =
			"<wells xmlns=\"http://www.witsml.org/schemas/1series\" " +
			"version=\"1.4.1.1\">" +
			"<well/>" +
			"</wells>";

	private String logQueryTemplate =
			"<logs xmlns=\"http://www.witsml.org/schemas/131\" version=\"1.3.1.1\">\n" +
					"<log uidWell=\"Energistics-well-0001\" uidWellbore=\"Energistics-w1-wellbore-0001\" uid=\"HM_fb49c4b1-6638-452d-8043-bba10f109179\">\n" +
					"    <startIndex uom=\"ft\">0</startIndex>\n" +
					"    <endIndex uom=\"ft\">5050</endIndex>\n" +
					"    <logCurveInfo>\n" +
					"      <mnemonic>MTIN</mnemonic>\n" +
					"      <unit></unit>\n" +
					"    </logCurveInfo>\n" +
					"    <logCurveInfo>\n" +
					"      <mnemonic>MTOUT</mnemonic>\n" +
					"      <unit></unit>\n" +
					"    </logCurveInfo>\n" +
					"    <logCurveInfo>\n" +
					"      <mnemonic>MIN_5FT</mnemonic>\n" +
					"      <unit></unit>\n" +
					"    </logCurveInfo>\n" +
					"    <logCurveInfo>\n" +
					"      <mnemonic>MOTOR_RPM</mnemonic>\n" +
					"      <unit></unit>\n" +
					"    </logCurveInfo>\n" +
					"    <logCurveInfo>\n" +
					"      <mnemonic>MRIN</mnemonic>\n" +
					"      <unit></unit>\n" +
					"    </logCurveInfo>\n" +
					"    <logData>\n" +
					"      <data />\n" +
					"    </logData>\n" +
					"  </log>\n" +
					"</logs>";

	@Test
	public void contextLoads() {
		assertThat(witsmlServer).isNotNull();
	}

	@Test
	public void addToStoreShouldHandleBadInput() {
		assertThat(this.witsmlServer.addToStore("WMLtypeIn", "XMLin", "OptionsIn", "CapabilitiesIn").getResult())
				.isEqualTo((short) -486);
	}

	@Test
	public void getVersionShouldReturnDefaultVersion(){
		assertThat(this.witsmlServer.getVersion().getResult()).contains("1.3.1.1,1.4.1.1");
	}

	@Test
	public void getBaseMsgShouldReturnATextualDescription(){
		assertThat(this.witsmlServer.getBaseMsg((short)412).getResult()).contains("add");
	}

	@Test
	public void getBaseMsgShouldReturnATextualDescriptionForANegativeNumber(){
		assertThat(this.witsmlServer.getBaseMsg((short)-412).getResult()).contains("add");
	}

	@Test
	public void getCapShouldReturnAnXMLForACorrectVersion(){
		WMLS_GetCapResponse resp = this.witsmlServer.getCap("dataVersion=1.3.1.1");
		assertThat(resp).isNotNull();
		assertThat(resp.getCapabilitiesOut()).contains("<name>");
		assertThat(resp.getResult()).isEqualTo((short)1);
	}

	@Test
	public void getCapShouldReturn424ForAnIncorrectVersion(){
		WMLS_GetCapResponse resp = this.witsmlServer.getCap("dataValue=7");
		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)-424);
		assertThat(resp.getCapabilitiesOut()).isNull();
	}

	@Test
	public void getCapShouldReturnTheCorrectErrorForAnEmptyValue(){
		WMLS_GetCapResponse resp = this.witsmlServer.getCap("");
		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)-424);
		assertThat(resp.getCapabilitiesOut()).isNull();
	}

	// ***************** GET FROM STORE TESTS ***************** //
	@Test
	public void getFrStoreRespWellTrueSucceed(){
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"well",
				minWellQueryTemplate,
				"requestObjectSelectionCapability=true",
				""
		);

		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)1);
		assertThat(resp.getXMLout()).isEqualTo(ObjectSelectionConstants.WELL_OBJ_SELECTION);
	}

	@Test
	public void getFrStoreRespWellboreTrueSucceed(){
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"wellbore",
				minWellQueryTemplate,
				"requestObjectSelectionCapability=true",
				""
		);

		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)1);
		assertThat(resp.getXMLout()).isEqualTo(ObjectSelectionConstants.WELLBORE_OBJ_SELECTION);
	}

	@Test
	public void getFrStoreRespTrajTrueSucceed(){
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"trajectory",
				minWellQueryTemplate,
				"requestObjectSelectionCapability=true",
				""
		);

		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)1);
		assertThat(resp.getXMLout()).isEqualTo(ObjectSelectionConstants.TRAJECTORY_OBJ_SELECTION);
	}

	@Test
	public void getFrStoreRespInvalidTypeFail(){
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"trapezoid",
				minWellQueryTemplate,
				"requestObjectSelectionCapability=true",
				""
		);
		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)-487);
	}

	@Test
	public void getFrStoreRespTrajValueOtherThanTrueOrNoneFail(){
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"trajectory",
				minWellQueryTemplate,
				"requestObjectSelectionCapability=notTrue",
				""
		);
		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)-427);
	}

	@Test
	public void getFrStoreRespNoMinQueryTemplateFail(){
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"trajectory",
				"",
				"requestObjectSelectionCapability=true",
				""
		);
		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)-408);
	}

	@Test
	public void getFrStoreRespNoTypeInFail(){
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"",
				minWellQueryTemplate,
				"requestObjectSelectionCapability=true",
				""
		);
		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)-407);
	}

	@Test
	public void getFrStoreRespNoVersionFail(){
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"well",
				// Modified minimum Well query template to have no version
				"<wells xmlns=\"http://www.witsml.org/schemas/1series\" " +
						"version=\"\">" +
						"<well/>" +
						"</wells>",
				"requestObjectSelectionCapability=true",
				""
		);
		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)-468);
	}

	@Test
	public void getFrStoreRespNullKeyFail(){
		// This test will succeed for any valid type; just alternating value of
		// requestObjectSelectionCapability.
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"well",
				minWellQueryTemplate,
				"requestObjectSelectionCapability=",
				""
		);

		assertThat(resp).isNotNull();
		// Since object selection capability is null, the code returns NULL
		assertThat(resp.getResult()).isEqualTo((short)-411);
	}

	@Test
	public void getFrStoreRespWhitespaceKeyFail(){
		// This test will succeed for any valid type; just alternating value of
		// requestObjectSelectionCapability.
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"well",
				minWellQueryTemplate,
				"requestObjectSelectionCapability=  ",
				""
		);
		assertThat(resp).isNotNull();
		// Since object selection capability is null, the code returns NULL
		assertThat(resp.getResult()).isEqualTo((short)-1001);
	}

	@Test
	public void getFrStoreRespXMLHasUIDFail() {
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"trajectory",
				"<trajectorys" +
						"        xmlns=\"http://www.witsml.org/schemas/1series\"" +
						"        version=\"1.4.1.1\">" +
						"    <trajectory uidWell=\"uidWell\" uidWellbore=\"uidWellbore\" uid=\"\">" +
						"        <nameWell/>" +
						"        <nameWellbore/>" +
						"        <name>HM_Plan #2</name>" +
						"        <dTimTrajStart/>" +
						"        <dTimTrajEnd/>" +
						"        <mdMn/>" +
						"        <mdMx/>" +
						"        <serviceCompany/>" +
						"        <magDeclUsed/>" +
						"        <gridCorUsed/>" +
						"        <aziVertSect/>" +
						"        <dispNsVertSectOrig/>" +
						"        <dispEwVertSectOrig/>" +
						"        <definitive/>" +
						"        <memory/>" +
						"        <finalTraj/>" +
						"        <aziRef/>" +
						"        <trajectoryStation uid=\"34ht5\"/>" +
						"        <commonData>" +
						"            <itemState/>" +
						"            <comments/>" +
						"        </commonData>" +
						"    </trajectory>" +
						"</trajectorys>",
				"requestObjectSelectionCapability=true",
				""
		);
		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)-428);
	}

	@Test
	public void getFrStoreRespMoreThanOneOptionsInFail() {
		WMLS_GetFromStoreResponse resp = this.witsmlServer.getFromStore(
				"well",
				minWellQueryTemplate,
				"requestObjectSelectionCapability=true;dataVersion=1.4.1.1",
				""
		);
		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short)-427);
	}

}
