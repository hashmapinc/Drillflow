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

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class DotDelegatorTest {
    private DotDelegator delegator;
    private DotClient client;
    private String url;
    private String trajectoryPath;

    @Before
    public void init() {
        // instantiate strings
        this.url = "test.com";
        this.trajectoryPath = "/trajectory/";

        // build config
        HashMap<String, String> config = new HashMap<>();
        config.put("baseurl", this.url);
        config.put("well.path", "/well/");
        config.put("wellbore.path", "/wellbore/");
        config.put("trajectory.path", this.trajectoryPath);

        // instantiate delegator
        this.delegator = new DotDelegator(config);

        // mock client
        this.client = mock(DotClient.class);
    }

    @Test
    public void shouldCreateTrajectoryWithUid() throws Exception {
        // build object
        ObjTrajectory traj = new ObjTrajectory();
        traj.setUid("traj-a");
        traj.setName("traj-a");
        traj.setUidWellbore("wellbore-a");
        traj.setUidWell("well-a");

        // get payload
        String payload = ((AbstractWitsmlObject) traj).getJSONString("1.4.1.1");

        // build http request
        String endpoint = this.url + this.trajectoryPath + traj.getUid();
        HttpRequestWithBody req = Unirest.put(endpoint);
        req.header("Content-Type", "application/json");
        req.body(payload);
        req.queryString("uidWellbore", traj.getUidWellbore());
        req.queryString("uidWell", traj.getUidWell());

        // build http response mock
        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.getBody()).thenReturn("{\"uid\": \"" + traj.getUid() + "\"}");
        when(resp.getStatus()).thenReturn(200);

        // mock client behavior
        when(this.client.makeRequest(argThat(someReq -> (
            someReq.getHttpMethod().name().equals(req.getHttpMethod().name()) &&
            someReq.getUrl().equals(req.getUrl()) &&
            someReq.getHeaders().containsKey("Content-Type")
        )), eq("goodUsername"), eq("goodPassword"))).thenReturn(resp);

        // test
        String actualUid = this.delegator.createObject(traj, "goodUsername", "goodPassword", "exchangeID", this.client);
        String expectedUid = traj.getUid();
        assertEquals(expectedUid, actualUid);
    }

    @Test
    public void shouldCreateTrajectoryWithoutUid() throws Exception {
        // build object
        ObjTrajectory traj = new ObjTrajectory();
        traj.setUid("");
        traj.setName("traj-a");
        traj.setUidWell("well-a");
        traj.setUidWellbore("wellbore-a");

        // get payload
        String payload = ((AbstractWitsmlObject) traj).getJSONString("1.4.1.1");

        // build http request
        String endpoint = this.url + this.trajectoryPath;
        HttpRequestWithBody req = Unirest.post(endpoint);
        req.header("Content-Type", "application/json");
        req.body(payload);
        req.queryString("uidWellbore", traj.getUidWellbore());
        req.queryString("uidWell", traj.getUidWell());

        // build http response mock
        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.getBody()).thenReturn("{\"uid\": \"traj-a\"}");
        when(resp.getStatus()).thenReturn(200);

        // mock client behavior
        when(this.client.makeRequest(argThat(someReq -> (
            someReq.getHttpMethod().name().equals(req.getHttpMethod().name()) &&
            someReq.getUrl().equals(req.getUrl()) &&
            someReq.getHeaders().containsKey("Content-Type")
        )), eq("goodUsername"), eq("goodPassword"))).thenReturn(resp);

        // test
        String actualUid = this.delegator.createObject(traj, "goodUsername", "goodPassword", "exchangeID", this.client);
        String expectedUid = "traj-a";
        assertEquals(expectedUid, actualUid);
    }
}
