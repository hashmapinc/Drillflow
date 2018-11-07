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

import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetCapResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WitsmlServerApplicationTests {

	@Autowired
	private StoreImpl witsmlServer;

	@Test
	public void contextLoads() {
		assertThat(witsmlServer).isNotNull();
	}

	@Test
	public void getVersionShouldReturnDefaultVersion(){
		assertThat(this.witsmlServer.getVersion()).contains("1.3.1.1,1.4.1.1");
	}

	@Test
	public void getBaseMsgShouldReturnATextualDescription(){
		assertThat(this.witsmlServer.getBaseMsg((short)412)).contains("add");
	}

	@Test
	public void getBaseMsgShouldReturnATextualDescriptionForANegativeNumber(){
		assertThat(this.witsmlServer.getBaseMsg((short)-412)).contains("add");
	}

	@Test
	public void getCapShouldReturnAnXMLForACorrectVersion(){
		WMLS_GetCapResponse resp = this.witsmlServer.getCap("dataValue=1.3.1.1");
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
}
