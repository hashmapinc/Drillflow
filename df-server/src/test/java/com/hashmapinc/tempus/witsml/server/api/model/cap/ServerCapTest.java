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
package com.hashmapinc.tempus.witsml.server.api.model.cap;

import org.junit.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class ServerCapTest {

    @Test
    public void createServerCapObject(){
        ServerCap sc = new ServerCap();
        assertNotNull(sc);
    }

    @Test
    public void generateDefault1311Object() throws JAXBException {
        ServerCap sc = new ServerCap();
        String obj = sc.getWitsmlObject("1.3.1.1");
        assertNotNull(obj);
        assertNotEquals(obj, "");
        assertThat(obj, containsString("1.3.1.1"));
    }

    @Test
    public void generateDefault1411Object() throws JAXBException {
        ServerCap sc = new ServerCap();
        String obj = sc.getWitsmlObject("1.4.1.1");
        assertNotNull(obj);
        assertNotEquals(obj, "");
        assertThat(obj, containsString("1.4.1.1"));
    }

    @Test
    public void addGrowingTimeout1411() throws JAXBException {
        ServerCap sc = new ServerCap();
        sc.addGrowingTimeoutPeriod("log", 1000);
        String obj = sc.getWitsmlObject("1.4.1.1");
        assertNotNull(obj);
        assertNotEquals(obj, "");
        assertThat(obj, containsString("<growingTimeoutPeriod dataObject=\"log\">1000</growingTimeoutPeriod>"));
    }

    @Test
    public void removeGrowingTimeout1411() throws JAXBException {
        ServerCap sc = new ServerCap();
        sc.addGrowingTimeoutPeriod("log", 1000);
        String obj = sc.getWitsmlObject("1.4.1.1");
        assertNotNull(obj);
        assertNotEquals(obj, "");
        assertThat(obj, containsString("<growingTimeoutPeriod dataObject=\"log\">1000</growingTimeoutPeriod>"));
        sc.removeGrowingTimeoutPeriod("log");
        String obj2 = sc.getWitsmlObject("1.4.1.1");
        assertThat(obj2, not(containsString("<growingTimeoutPeriod dataObject=\"log\">1000</growingTimeoutPeriod>")));
    }

    @Test
    public void addGrowingTimeoutEnsureNotAppear1311() throws JAXBException {
        ServerCap sc = new ServerCap();
        sc.addGrowingTimeoutPeriod("log", 1000);
        String obj = sc.getWitsmlObject("1.3.1.1");
        assertNotNull(obj);
        assertNotEquals(obj, "");
        assertThat(obj, not(containsString("<growingTimeoutPeriod dataObject=\"log\">1000</growingTimeoutPeriod>")));
    }
}
