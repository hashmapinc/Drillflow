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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hashmapinc.tempus.witsml.server.api.model.WMLS_AddToStoreResponse;
import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetCapResponse;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreImplTests {

	@Autowired
	private StoreImpl storeImpl;

	@Test
	public void contextLoads() {
		assertThat(storeImpl).isNotNull();
	}

	@Test
	public void addToStoreShouldHandleBadInput() throws InterruptedException, ExecutionException {
<<<<<<< HEAD
		assertThat(this.storeImpl.addToStore("WMLtypeIn", "XMLin", "OptionsIn", "CapabilitiesIn").get().getResult())
=======
		assertThat(this.witsmlServer.addToStore("WMLtypeIn", "XMLin", "OptionsIn", "CapabilitiesIn").get().getResult())
>>>>>>> Spring Async using futures implentation
				.isEqualTo((short) -1);
	}

	@Test
	public void getVersionShouldReturnDefaultVersion() {
<<<<<<< HEAD
		assertThat(this.storeImpl.getVersion().getResult()).contains("1.3.1.1,1.4.1.1");
=======
		assertThat(this.witsmlServer.getVersion().getResult()).contains("1.3.1.1,1.4.1.1");
>>>>>>> Spring Async using futures implentation
	}

	@Test
	public void getBaseMsgShouldReturnATextualDescription() {
<<<<<<< HEAD
		assertThat(this.storeImpl.getBaseMsg((short) 412).getResult()).contains("add");
=======
		assertThat(this.witsmlServer.getBaseMsg((short) 412).getResult()).contains("add");
>>>>>>> Spring Async using futures implentation
	}

	@Test
	public void getBaseMsgShouldReturnATextualDescriptionForANegativeNumber() {
<<<<<<< HEAD
		assertThat(this.storeImpl.getBaseMsg((short) -412).getResult()).contains("add");
=======
		assertThat(this.witsmlServer.getBaseMsg((short) -412).getResult()).contains("add");
>>>>>>> Spring Async using futures implentation
	}

	@Test
	public void getCapShouldReturnAnXMLForACorrectVersion() {
<<<<<<< HEAD
		WMLS_GetCapResponse resp = this.storeImpl.getCap("dataValue=1.3.1.1");
=======
		WMLS_GetCapResponse resp = this.witsmlServer.getCap("dataValue=1.3.1.1");
>>>>>>> Spring Async using futures implentation
		assertThat(resp).isNotNull();
		assertThat(resp.getCapabilitiesOut()).contains("<name>");
		assertThat(resp.getResult()).isEqualTo((short) 1);
	}

	@Test
	public void getCapShouldReturn424ForAnIncorrectVersion() {
<<<<<<< HEAD
		WMLS_GetCapResponse resp = this.storeImpl.getCap("dataValue=7");
=======
		WMLS_GetCapResponse resp = this.witsmlServer.getCap("dataValue=7");
>>>>>>> Spring Async using futures implentation
		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short) -424);
		assertThat(resp.getCapabilitiesOut()).isNull();
	}

	@Test
	public void getCapShouldReturnTheCorrectErrorForAnEmptyValue() {
<<<<<<< HEAD
		WMLS_GetCapResponse resp = this.storeImpl.getCap("");
=======
		WMLS_GetCapResponse resp = this.witsmlServer.getCap("");
>>>>>>> Spring Async using futures implentation
		assertThat(resp).isNotNull();
		assertThat(resp.getResult()).isEqualTo((short) -424);
		assertThat(resp.getCapabilitiesOut()).isNull();
	}

	@Test
	public void testAsyncAnnotationForAddMethod() throws InterruptedException, ExecutionException {
<<<<<<< HEAD
		CompletableFuture<WMLS_AddToStoreResponse> future = this.storeImpl.addToStore("WMLtypeIn", "XMLin",
				"OptionsIn", "CapabilitiesIn");
		assertNotNull(future);
		assertEquals(-1, future.get().getResult());
=======
		CompletableFuture<WMLS_AddToStoreResponse> future = this.witsmlServer.addToStore("WMLtypeIn", "XMLin",
				"OptionsIn", "CapabilitiesIn");
		while (true) {
			System.out.println("Waiting for response from addToStore()...");
			if (future.isDone()) {
				System.out.println("Result from Async addToStore() -> " + future.get().getResult());
				break;
			}
			System.out.println("Continue execution...");
			Thread.sleep(1000);
		}

		System.out.println("Exit test case...");

>>>>>>> Spring Async using futures implentation
	}
}
