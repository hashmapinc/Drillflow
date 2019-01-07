package com.hashmapinc.tempus.witsml.valve.dot;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory;
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
        traj.setUidWell("well-a");
        traj.setUidWellbore("wellbore-a");

        // get payload
        String payload = ((AbstractWitsmlObject) traj).getJSONString("1.4.1.1");

        // build http request
        String endpoint = this.url + this.trajectoryPath + traj.getUid();
        HttpRequestWithBody req = Unirest.put(endpoint);
        req.header("Content-Type", "application/json");
        req.body(payload);
        req.queryString("uidWell", traj.getUidWell());
        req.queryString("uidWellbore", traj.getUidWellbore());

        // build http response mock
        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.getBody()).thenReturn("{\"uid\": \"" + traj.getUid() + "\"}");
        when(resp.getStatus()).thenReturn(200);

        // mock client behavior
        when(
            this.client.makeRequest(argThat(someReq -> {
                return (
                    someReq.getHttpMethod().name().equals(req.getHttpMethod().name()) &&
                    someReq.getBody().toString().equals(payload) &&
                    someReq.getUrl().equals(endpoint) &&
                    someReq.getHeaders().containsKey("Content-Type")
                );
            }), eq("goodUsername"), eq("goodPassword"))
        ).thenReturn(resp);

        // test
        String actualUid = this.delegator.createObject(traj, "goodUsername", "goodPassword", this.client);
        String expectedUid = traj.getUid();
        assertEquals(expectedUid, actualUid);

        // verify
        verify(this.client).makeRequest(any(HttpRequestWithBody.class), eq("goodUsername"), eq("goodPassword"));
        verifyNoMoreInteractions(this.client);
        verify(resp).getStatus();
        verify(resp).getBody();
        verifyNoMoreInteractions();
    }

    @Test
    public void shouldCreateTrajectoryWithoutUid() throws Exception {
        // build object
        ObjTrajectory traj = new ObjTrajectory();
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
        req.queryString("uidWell", traj.getUidWell());
        req.queryString("uidWellbore", traj.getUidWellbore());

        // build http response mock
        HttpResponse<String> resp = mock(HttpResponse.class);
        when(resp.getBody()).thenReturn("{\"uid\": \"traj-a\"}");
        when(resp.getStatus()).thenReturn(200);

        // mock client behavior
        when(
            this.client.makeRequest(argThat(someReq -> {
                return (
                    someReq.getHttpMethod().name().equals(req.getHttpMethod().name()) &&
                    someReq.getBody().toString().equals(payload) &&
                    someReq.getUrl().equals(endpoint) &&
                    someReq.getHeaders().containsKey("Content-Type")
                );
            }), eq("goodUsername"), eq("goodPassword"))
        ).thenReturn(resp);

        // test
        String actualUid = this.delegator.createObject(traj, "goodUsername", "goodPassword", this.client);
        String expectedUid = "traj-a";
        assertEquals(expectedUid, actualUid);

        // verify
        verify(this.client).makeRequest(any(HttpRequestWithBody.class), eq("goodUsername"), eq("goodPassword"));
        verifyNoMoreInteractions(this.client);
        verify(resp).getStatus();
        verify(resp).getBody();
        verifyNoMoreInteractions();
    }
}
