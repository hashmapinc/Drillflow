package com.hashmapinc.tempus.witsml.server.api.model.cap;

import org.junit.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class ServerCapTest {

    @Test
    public void CreateServerCapObject(){
        ServerCap sc = new ServerCap();
        assertNotNull(sc);
    }

    @Test
    public void GenerateDefault1311Object() throws JAXBException {
        ServerCap sc = new ServerCap();
        String obj = sc.getWitsmlObject("1.3.1.1");
        assertNotNull(obj);
        assertNotEquals(obj, "");
        assertThat(obj, containsString("1.3.1.1"));
    }

    @Test
    public void GenerateDefault1411Object() throws JAXBException {
        ServerCap sc = new ServerCap();
        String obj = sc.getWitsmlObject("1.4.1.1");
        assertNotNull(obj);
        assertNotEquals(obj, "");
        assertThat(obj, containsString("1.4.1.1"));
    }

    @Test
    public void AddGrowingTimeout1411() throws JAXBException {
        ServerCap sc = new ServerCap();
        sc.addGrowingTimeoutPeriod("log", 1000);
        String obj = sc.getWitsmlObject("1.4.1.1");
        assertNotNull(obj);
        assertNotEquals(obj, "");
        assertThat(obj, containsString("<growingTimeoutPeriod dataObject=\"log\">1000</growingTimeoutPeriod>"));
    }

    @Test
    public void RemoveGrowingTimeout1411() throws JAXBException {
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
    public void AddGrowingTimeoutEnsureNotAppear1311() throws JAXBException {
        ServerCap sc = new ServerCap();
        sc.addGrowingTimeoutPeriod("log", 1000);
        String obj = sc.getWitsmlObject("1.3.1.1");
        assertNotNull(obj);
        assertNotEquals(obj, "");
        assertThat(obj, not(containsString("<growingTimeoutPeriod dataObject=\"log\">1000</growingTimeoutPeriod>")));
    }
}
