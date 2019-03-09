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

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.server.AsyncAppConstants;
import com.hashmapinc.tempus.witsml.server.WitsmlServerApplication;
import com.hashmapinc.tempus.witsml.valve.dot.client.DotClient;
import com.hashmapinc.tempus.witsml.valve.dot.DotDelegator;
import com.hashmapinc.tempus.witsml.valve.dot.DotValve;

public class AsyncTest {

	private DotClient mockClient;
	private DotDelegator mockDelegator;
	private DotValve valve;

	@Before
	public void doSetup() {
		this.mockClient = mock(DotClient.class);
		this.mockDelegator = mock(DotDelegator.class);
		this.valve = new DotValve(this.mockClient, this.mockDelegator); // inject mocks into valve
	}

	@Configuration
	@Import(WitsmlServerApplication.class)
	static class ContextConfiguration {
		@Bean("asyncCustomTaskExecutor")
		public TaskExecutor getAsyncExecutor() {
			ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
			executor.setCorePoolSize(AsyncAppConstants.CORE_POOL_SIZE);
			executor.setMaxPoolSize(AsyncAppConstants.MAX_POOL_SIZE);
			executor.setQueueCapacity(AsyncAppConstants.QUEUE_CAPACITY);
			executor.setKeepAliveSeconds(AsyncAppConstants.KEEP_ALIVE_TIME_IN_SEC);
			executor.setWaitForTasksToCompleteOnShutdown(true);
			executor.setThreadNamePrefix(AsyncAppConstants.ASYNC_THREAD_NAME);
			executor.initialize();
			return executor;
		}
	}

	@Test
	public void createObjectInAsync() throws Exception {
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
			CompletableFuture<String> actual1 = this.valve.createObject(qc);
			CompletableFuture<String> actual2 = this.valve.createObject(qc);
			CompletableFuture<String> actual3 = this.valve.createObject(qc);
			assertEquals(expected, actual1.get());
			assertEquals(expected, actual2.get());
			assertEquals(expected, actual3.get());
		}

}