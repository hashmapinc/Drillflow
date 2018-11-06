package com.hashmapinc.tempus.witsml.server.api.model.cap;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DataObjectTest {

    @Test
    public void CreateDataObject(){
        DataObject dataObject = new DataObject();
        assertNotNull(dataObject);
    }

}
