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

public class DotValveTest {
	private String username;
    private String password;
    private DotValve valve;

	@Before
	public void doSetup() {
		this.username = "admin";
        this.password = "12345";
        DotDelegator delegator = Mockito.mock(DotDelegator.class);
        DotClient client = Mockito.mock(DotClient.class);
		valve = new DotValve(client, delegator); // inject mocks into valve
	}

    @Test(expected = ValveAuthException.class)
	public void authVerifyException() throws ValveAuthException {
		String badPassword = this.password + "JUNK";
		valve.authenticate(username, badPassword);
	}
}








