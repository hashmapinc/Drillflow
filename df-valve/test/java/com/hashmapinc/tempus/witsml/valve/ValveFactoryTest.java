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

package com.hashmapinc.tempus.witsml.valve;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



public class ValveFactoryTest 
{
    @Autowired
    private ValveFactory valveFactory;
    @Test
    public void buildValveTestWithParameterMock(){
    	
    	@SuppressWarnings("static-access")
		Object testObject=valveFactory.buildValve("Mock");
    	assertThat(
    			testObject
            ).isNotNull();
    	
    }
    @Test
    public void buildValveTestWithParameterDoT(){
    	
    	@SuppressWarnings("static-access")
		Object testObject=valveFactory.buildValve("DoT");
    	assertThat(
    			testObject
            ).isNotNull();
    	
    }
    @Test
    public void buildValveTestWithParameterNull(){
    	
    	@SuppressWarnings("static-access")
		Object testObject=valveFactory.buildValve("Test");
    	assertThat(
    			testObject
            ).isNull();
    	
    }
	
}