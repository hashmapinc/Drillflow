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
package com.hashmapinc.tempus.witsml.valve.dot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells;
import com.hashmapinc.tempus.WitsmlObjects.v1411.*;
import com.hashmapinc.tempus.witsml.valve.dot.client.DotClient;
import com.hashmapinc.tempus.witsml.valve.dot.client.UidUuidCache;
import com.hashmapinc.tempus.witsml.valve.dot.graphql.GraphQLQueryConverter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// TODO Test against this flow:
//          https://hashmapinc.slack.com/files/U717S2WPJ/FJP5VE0TS/logdata.postman_collection.json
public class DotDelegatorTest {
    private DotDelegator delegator;
    private DotClient client;
    private String url;
    private String trajectoryPath;
    private String graphQlWellPath;
    private String graphQlWellborePath;
    private String graphQlTrajectoryPath;

    private String logChannelsetPath;
    private String logChannelPath;
    // let init create this
    private HashMap<String, String> config = new HashMap<>();

    private GenericMeasure testGM = new GenericMeasure();
    private CsLogData csLogData = new CsLogData();
    private CsLogCurveInfo csLogCurveInfo = new CsLogCurveInfo();
    private List<CsLogCurveInfo> logCurveInfoList = new ArrayList<>();
    private List<CsLogData> dataList = new ArrayList<>();

    private static final String AB =
            "0123456789"
                    + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "abcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();
    private String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    @Before
    public void init() {
        // instantiate strings
        this.url = "test.com";
        this.trajectoryPath = "/trajectory/";
        this.graphQlWellPath = "/well/graphql/";
        this.graphQlWellborePath = "/wellbore/graphql/";
        this.graphQlTrajectoryPath = "/trajectory/graphql";
        //this.logChannelsetPath = "/channelSets/";
        //this.logChannelPath = "/channels/";

        // build config
        //config.put("baseurl", this.url);
        config.put("well.path", this.url + "/well/");
        config.put("wellbore.path", this.url + "/wellbore/");
        config.put("trajectory.path", this.url + "/trajectory/");
        config.put("well.gql.path", this.url + "/well/graphql/");
        config.put("wellbore.gql.path", this.url + "/wellbore/graphql/");
        config.put("trajectory.gql.path", this.url + "/trajectory/graphql");
        //config.put("log.channelset.path", this.url + logChannelsetPath);
        //config.put("log.channel.path", this.url + logChannelPath);

        // instantiate delegator
        this.delegator = new DotDelegator(config);

        // mock client
        this.client = mock(DotClient.class);
    }

    @Test
    public void shouldCreateAnUpdateInStoreQueryWithoutEmptyArrays() throws Exception{
        String query = TestUtilities.getResourceAsString("updateTest/WellCountryUpdate1311.xml");
        ObjWells wells = WitsmlMarshal.deserialize(query, ObjWells.class);
        ObjWell well = wells.getWell().get(0);
        String expectedJson = TestUtilities.getResourceAsString("updateTest/WellCountryUpdateResult1311.json");

        // get witsmlObj as json string for request payload
        String payload = well.getJSONString("1.4.1.1");
        payload = JsonUtil.removeEmpties(new JSONObject(payload));

        ObjectMapper om = new ObjectMapper();
        Map<String, Object> map1311 = (Map<String, Object>) (om.readValue(payload, Map.class));
        Map<String, Object> expectedMap = (Map<String, Object>) (om.readValue(expectedJson, Map.class));
        assertEquals(expectedMap, map1311);
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
    public void shouldFindUuidInCache() throws Exception {
        String testUid = "myUid";
        String parentUid = "myParentUid";
        String grandparentUid = "myGrandparentUid";
        String username = "Wclient@hashmapinc.com";
        String password = "Witsml2018.DrillFlow";
        String expectedUuid = "myUuid";

        UidUuidCache uidUuidCache = new UidUuidCache();
        uidUuidCache.putInCache(expectedUuid, testUid, parentUid, grandparentUid);
        AbstractWitsmlObject witsmlObject =
                new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog();
        // Only set those values that will set up the test...
        ((ObjLog) witsmlObject).setUid(testUid);
        ((ObjLog) witsmlObject).setUidWell(grandparentUid);
        ((ObjLog) witsmlObject).setUidWellbore(parentUid);

        DotDelegator dotDelegator = new DotDelegator(config);
        // client is mocked & config is mapped in init for this test class
        String actualUuid = dotDelegator.getUuid( witsmlObject,
                                                  testUid,
                                                  client,
                                                  username,
                                                  password );
        assertEquals(expectedUuid, actualUuid);
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

    @Test
    public void shouldFindWellsWithEmptyGraphQLQuery() throws Exception {
        String query = TestUtilities.getResourceAsString("well1311FullEmptyQuery.xml");
        String response = TestUtilities.getResourceAsString("WellGraphQLResponseFull.json");

        ObjWells queryObject = WitsmlMarshal.deserialize(query, ObjWells.class);
        AbstractWitsmlObject singleWell = queryObject.getWell().get(0);
        GraphQLQueryConverter converter = new GraphQLQueryConverter();
        String jsonQuery = converter.getQuery(singleWell);
        assertNotNull(jsonQuery);
        String endpoint = this.url + this.graphQlWellPath;
        HttpRequestWithBody req = Unirest.post(endpoint);
        req.header("Content-Type", "application/json");
        req.body(jsonQuery);

        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.getBody()).thenReturn(response);
        when(resp.getStatus()).thenReturn(200);

        when(this.client.makeRequest(argThat(someReq -> (
                someReq.getHttpMethod().name().equals(req.getHttpMethod().name()) &&
                        someReq.getUrl().equals(req.getUrl()) &&
                        someReq.getHeaders().containsKey("Content-Type")
        )), eq("goodUsername"), eq("goodPassword"))).thenReturn(resp);

        ArrayList<AbstractWitsmlObject> foundObjects = this.delegator.search(singleWell, "goodUsername", "goodPassword", "exchangeID", this.client, new HashMap<>());
        assertEquals(3, foundObjects.size());
    }
    
    @Test
    public void shouldDeleteTrajectory() throws Exception {
        // build object
        ObjTrajectory traj = new ObjTrajectory();
        traj.setUid("traj-a");
        traj.setUidWellbore("wellbore-a");
        traj.setUidWell("well-a");

        // build http request
        String endpoint = this.url + this.trajectoryPath + traj.getUid();
        HttpRequest req = Unirest.delete(endpoint);
        req.header("Content-Type", "application/json");
        req.queryString("uidWellbore", traj.getUidWellbore());
        req.queryString("uidWell", traj.getUidWell());

        // build http response mock
        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.getStatus()).thenReturn(204);

        // mock client behavior
        when(this.client.makeRequest(argThat(someReq -> (
            someReq.getHttpMethod().name().equals(req.getHttpMethod().name()) &&
                someReq.getUrl().equals(req.getUrl()) &&
                someReq.getHeaders().containsKey("Content-Type")
        )), eq("goodUsername"), eq("goodPassword"))).thenReturn(resp);

        // test
        this.delegator.deleteObject(traj, "goodUsername", "goodPassword", "exchangeID", this.client);

        // verify
        verify(this.client).makeRequest(argThat(someReq -> (
            someReq.getHttpMethod().name().equals(req.getHttpMethod().name()) &&
                someReq.getUrl().equals(req.getUrl()) &&
                someReq.getHeaders().containsKey("Content-Type")
        )), eq("goodUsername"), eq("goodPassword"));
    }

    @Test
    public void shouldUpdateTrajectory1311() throws Exception {
        //=====================================================================
        // 1.3.1.1
        //=====================================================================
        // build object
        ObjTrajectory traj1311 = new ObjTrajectory();
        traj1311.setUid("traj1311");
        traj1311.setName("traj1311");
        traj1311.setUidWellbore("wellbore1311");
        traj1311.setUidWell("well1311");

        // build http request
        String endpoint = this.url + this.trajectoryPath + traj1311.getUid();
        HttpRequest req1311 = Unirest.put(endpoint);
        req1311.header("Content-Type", "application/json");
        req1311.queryString("uidWellbore", traj1311.getUidWellbore());
        req1311.queryString("uidWell", traj1311.getUidWell());

        // build http response mock
        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.getStatus()).thenReturn(200);

        // mock client behavior
        when(this.client.makeRequest(argThat(someReq -> (
            someReq.getHttpMethod().name().equals(req1311.getHttpMethod().name()) &&
            someReq.getUrl().equals(req1311.getUrl()) &&
            someReq.getHeaders().containsKey("Content-Type")
        )), eq("goodUsername"), eq("goodPassword"))).thenReturn(resp);

        // test
        this.delegator.updateObject(traj1311, "goodUsername", "goodPassword", "exchangeID", this.client);

        // verify
        verify(this.client).makeRequest(argThat(someReq -> (
            someReq.getHttpMethod().name().equals(req1311.getHttpMethod().name()) &&
            someReq.getUrl().equals(req1311.getUrl()) &&
            someReq.getHeaders().containsKey("Content-Type")
        )), eq("goodUsername"), eq("goodPassword"));
        //=====================================================================
    }

    @Test
    public void shouldUpdateTrajectory1411() throws Exception {
        //=====================================================================
        // 1.4.1.1
        //=====================================================================
        // build object
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory traj1411 = new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory();
        traj1411.setUid("traj1411");
        traj1411.setName("traj1411");
        traj1411.setUidWellbore("wellbore1411");
        traj1411.setUidWell("well1411");

        // build http request
        String endpoint = this.url + this.trajectoryPath + traj1411.getUid();
        HttpRequest req1411 = Unirest.put(endpoint);
        req1411.header("Content-Type", "application/json");
        req1411.queryString("uidWellbore", traj1411.getUidWellbore());
        req1411.queryString("uidWell", traj1411.getUidWell());

        // build http response mock
        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.getStatus()).thenReturn(200);

        // mock client behavior
        when(this.client.makeRequest(argThat(someReq -> (
            someReq.getHttpMethod().name().equals(req1411.getHttpMethod().name()) &&
            someReq.getUrl().equals(req1411.getUrl()) &&
            someReq.getHeaders().containsKey("Content-Type")
        )), eq("goodUsername"), eq("goodPassword"))).thenReturn(resp);

        // test
        this.delegator.updateObject(traj1411, "goodUsername", "goodPassword", "exchangeID", this.client);

        // verify
        verify(this.client).makeRequest(argThat(someReq -> (
            someReq.getHttpMethod().name().equals(req1411.getHttpMethod().name()) &&
            someReq.getUrl().equals(req1411.getUrl()) &&
            someReq.getHeaders().containsKey("Content-Type")
        )), eq("goodUsername"), eq("goodPassword"));
        //=====================================================================
    }
}
