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

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore;
import com.hashmapinc.tempus.WitsmlObjects.v1411.CsCommonData;
import com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DotTranslatorTest {

    @Test(expected = ValveException.class)
    public void consolidateObjectsToXMLFailTest() throws ValveException {

        // build well list
        ArrayList<AbstractWitsmlObject> witsmlObjects = new ArrayList<>();
        ObjWell well = new ObjWell();
        well.setName("well-1");
        well.setUid("well-1");
        witsmlObjects.add(well);

        String clientVersion = "InvalidVersion";
        String objectType = "well";

        DotTranslator.consolidateObjectsToXML(witsmlObjects, clientVersion, objectType);
    }

    @Test
    public void consolidateObjectsToXMLWellTest() throws ValveException {

        // build well list
        ArrayList<AbstractWitsmlObject> witsmlObjects = new ArrayList<>();
        ObjWell well = new ObjWell();
        well.setName("well-1");
        well.setUid("well-1");
        witsmlObjects.add(well);

        String clientVersion = "1.3.1.1";
        String objectType = "well";

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><wells version=\"1.3.1.1\" xmlns=\"http://www.witsml.org/schemas/131\"><well uid=\"well-1\"><name>well-1</name></well></wells>";

        String actual = DotTranslator.consolidateObjectsToXML(witsmlObjects, clientVersion, objectType);

        assertEquals(expected, actual);

        witsmlObjects = new ArrayList<>();
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell well1411 = new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell();
        well1411.setName("well-1");
        well1411.setUid("well-1");
        witsmlObjects.add(well1411);

        clientVersion = "1.4.1.1";
        objectType = "well";

        expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><wells version=\"1.4.1.1\" xmlns=\"http://www.witsml.org/schemas/1series\" xmlns:ns2=\"http://www.energistics.org/schemas/abstract\"><well uid=\"well-1\"><name>well-1</name></well></wells>";

        actual = DotTranslator.consolidateObjectsToXML(witsmlObjects, clientVersion, objectType);

        assertEquals(expected, actual);
    }

    @Test
    public void stationLocationOnlyTrajectoryTest()
            throws JAXBException, IOException, ValveException, DatatypeConfigurationException {
        // Load the strings from the test resources
        String dotResponse = TestUtilities.getResourceAsString("trajectory1411.xml");
        String soapQuery = TestUtilities.getResourceAsString("trajectoryGraphql/FullTrajectoryQuery1411.xml");

        // Get the Query object
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys trajQuery =
                WitsmlMarshal.deserialize(soapQuery, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys.class);
        AbstractWitsmlObject trajQuerySingular = trajQuery.getTrajectory().get(0);

        // Create the "DOT Response Object"...which is just a 1411 object (meaning deserialize the string from earlier)
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys respTrajs =
                WitsmlMarshal.deserialize(dotResponse, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys.class);

        // Get the singluar object
        AbstractWitsmlObject respTraj = respTrajs.getTrajectory().get(0);

        // Create the JSON to simulate the response from DoT
        String respJson = respTraj.getJSONString("1.4.1.1");

        // Setup the options in for the test
        Map<String,String> optionsIn = new HashMap<>();
        optionsIn.put("returnElements", "station-location-only");

        // Do the translation
        AbstractWitsmlObject resp = DotTranslator.translateQueryResponse(trajQuerySingular, respJson, optionsIn);

        // Cast it to the concrete traj object as the response...since the query was 1411 the result of the
        // translation should be 1411
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory resultTraj =
                (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory)resp;

        assertNull(resultTraj.getServiceCompany());
        assertNull(resultTraj.getGridCorUsed());
        assertNull(resultTraj.getAziRef());
        assertNull(resultTraj.getMagDeclUsed());
        assertNull(resultTraj.getAziVertSect());
        assertNotNull(resultTraj.getName());
        assertNotNull(resultTraj.getUid());
        assertNotNull(resultTraj.getNameWell());
        assertNotNull(resultTraj.getUidWell());
        assertNotNull(resultTraj.getNameWellbore());
        assertNotNull(resultTraj.getUidWellbore());
        assertNotNull(resultTraj.getTrajectoryStation());
        assertEquals(1, resultTraj.getTrajectoryStation().size());
    }

    @Test
    public void headerOnlyTrajectoryTests()
            throws JAXBException, IOException, ValveException, DatatypeConfigurationException {
        // Load the strings from the test resources
        String dotResponse = TestUtilities.getResourceAsString("trajectory1411.xml");
        String soapQuery = TestUtilities.getResourceAsString("trajectoryGraphql/FullTrajectoryQuery1411.xml");

        // Get the Query object
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys trajQuery =
                WitsmlMarshal.deserialize(soapQuery, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys.class);
        AbstractWitsmlObject trajQuerySingular = trajQuery.getTrajectory().get(0);

        // Create the "DOT Response Object"...which is just a 1411 object (meaning deserialize the string from earlier)
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys respTrajs =
                WitsmlMarshal.deserialize(dotResponse, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys.class);

        // Get the singluar object
        AbstractWitsmlObject respTraj = respTrajs.getTrajectory().get(0);

        // Create the JSON to simulate the response from DoT
        String respJson = respTraj.getJSONString("1.4.1.1");

        // Setup the options in for the test
        Map<String,String> optionsIn = new HashMap<>();
        optionsIn.put("returnElements", "header-only");

        // Do the translation
        AbstractWitsmlObject resp = DotTranslator.translateQueryResponse(trajQuerySingular, respJson, optionsIn);

        // Cast it to the concrete traj object as the response...since the query was 1411 the result of the
        // translation should be 1411
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory resultTraj =
                (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory)resp;

        assertEquals(0, resultTraj.getTrajectoryStation().size());
    }

    @Test
    public void consolidateObjectsToXMLWellBoreTest() throws ValveException {

        // build well list
        ArrayList<AbstractWitsmlObject> witsmlObjects = new ArrayList<>();
        ObjWellbore well = new ObjWellbore();
        well.setName("well-1");
        well.setUid("well-1");
        witsmlObjects.add(well);

        String clientVersion = "1.3.1.1";
        String objectType = "wellbore";

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><wellbores version=\"1.3.1.1\" xmlns=\"http://www.witsml.org/schemas/131\"><wellbore uid=\"well-1\"><name>well-1</name></wellbore></wellbores>";

        String actual = DotTranslator.consolidateObjectsToXML(witsmlObjects, clientVersion, objectType);

        assertEquals(expected, actual);

        witsmlObjects = new ArrayList<>();
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore well1411 = new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore();
        well1411.setName("well-1");
        well1411.setUid("well-1");
        witsmlObjects.add(well1411);

        clientVersion = "1.4.1.1";
        objectType = "wellbore";

        expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><wellbores version=\"1.4.1.1\" xmlns=\"http://www.witsml.org/schemas/1series\" xmlns:ns2=\"http://www.energistics.org/schemas/abstract\"><wellbore uid=\"well-1\"><name>well-1</name></wellbore></wellbores>";

        actual = DotTranslator.consolidateObjectsToXML(witsmlObjects, clientVersion, objectType);

        System.out.println(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void consolidateObjectsToXMLTrajectoryTest() throws ValveException {

        // build well list
        ArrayList<AbstractWitsmlObject> witsmlObjects = new ArrayList<>();
        ObjTrajectory trajectory = new ObjTrajectory();
        trajectory.setName("trajectory-1");
        trajectory.setUid("trajectory-1");
        witsmlObjects.add(trajectory);

        String clientVersion = "1.3.1.1";
        String objectType = "trajectory";

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><trajectorys version=\"1.3.1.1\" xmlns=\"http://www.witsml.org/schemas/131\"><trajectory uid=\"trajectory-1\"><name>trajectory-1</name></trajectory></trajectorys>";

        String actual = DotTranslator.consolidateObjectsToXML(witsmlObjects, clientVersion, objectType);

        assertEquals(expected, actual);

        witsmlObjects = new ArrayList<>();
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory traj1411 = new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory();
        traj1411.setName("trajectory-1");
        traj1411.setUid("trajectory-1");
        witsmlObjects.add(traj1411);

        clientVersion = "1.4.1.1";
        objectType = "trajectory";

        expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><trajectorys version=\"1.4.1.1\" xmlns=\"http://www.witsml.org/schemas/1series\" xmlns:ns2=\"http://www.energistics.org/schemas/abstract\"><trajectory uid=\"trajectory-1\"><name>trajectory-1</name></trajectory></trajectorys>";

        actual = DotTranslator.consolidateObjectsToXML(witsmlObjects, clientVersion, objectType);

        System.out.println(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void translateQueryResponseTest() throws Exception {
        AbstractWitsmlObject wmlObject = WitsmlMarshal.deserializeFromJSON(
                "{\"country\":\"\",\"numLicense\":\"\",\"groundElevation\":{\"datum\":null,\"uom\":null,\"value\":null},\"commonData\":{\"privateGroupOnly\":null,\"comments\":\"\",\"acquisitionTimeZone\":[],\"dTimLastChange\":null,\"extensionAny\":null,\"defaultDatum\":{\"value\":\"\",\"uidRef\":\"\"},\"itemState\":null,\"sourceName\":null,\"extensionNameValue\":[],\"serviceCategory\":null,\"dTimCreation\":null},\"county\":\"\",\"timeZone\":\"\",\"waterDepth\":{\"uom\":\"\",\"value\":null},\"numAPI\":\"\",\"operator\":\"\",\"pcInterest\":{\"uom\":\"\",\"value\":null},\"referencePoint\":[{\"elevation\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"uid\":\"\",\"measuredDepth\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"name\":\"\",\"location\":[{\"uid\":\"\",\"easting\":{\"uom\":\"\",\"value\":null},\"wellCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"latitude\":{\"uom\":\"\",\"value\":null},\"localY\":{\"uom\":\"\",\"value\":null},\"description\":\"\",\"localX\":{\"uom\":\"\",\"value\":null},\"extensionNameValue\":[],\"northing\":{\"uom\":\"\",\"value\":null},\"longitude\":{\"uom\":\"\",\"value\":null}}],\"type\":\"\",\"extensionNameValue\":[]}],\"wellLocation\":[{\"uid\":\"\",\"easting\":{\"uom\":\"\",\"value\":null},\"wellCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"description\":\"\",\"extensionNameValue\":[],\"northing\":{\"uom\":\"\",\"value\":null}}],\"uid\":\"randy\",\"wellheadElevation\":{\"datum\":null,\"uom\":null,\"value\":null},\"field\":\"\",\"wellCRS\":[{\"uid\":\"\",\"localCRS\":{\"yAxisAzimuth\":{\"uom\":\"\",\"northDirection\":null,\"value\":null}},\"mapProjectionCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"geodeticCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"name\":\"\",\"description\":\"\",\"extensionNameValue\":[]}],\"nameLegal\":\"\",\"district\":\"\",\"numGovt\":\"\",\"block\":\"\",\"state\":\"\",\"region\":\"\",\"operatorDiv\":\"\",\"wellDatum\":[{\"elevation\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"uid\":\"\",\"datumName\":{\"namingSystem\":\"\",\"code\":\"\",\"value\":\"\"},\"kind\":[],\"name\":\"\",\"extensionNameValue\":[]}]}",
                com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class);

        String responseString = "{\"country\":\"US\",\"dTimLicense\":\"2001-05-15T13:20:00+00:00\",\"numLicense\":\"Company License Number\",\"county\":\"Montgomery\",\"waterDepth\":{\"uom\":\"ft\",\"value\":520},\"operator\":\"Operating Company\",\"pcInterest\":{\"uom\":\"%\",\"value\":65},\"dTimPa\":\"2001-07-15T15:30:00+00:00\",\"uid\":\"randy\",\"nameLegal\":\"Company Legal Name\",\"block\":\"Block Name\",\"state\":\"TX\",\"operatorDiv\":\"Division Name\",\"groundElevation\":{\"uom\":\"FT\",\"value\":250},\"commonData\":{\"comments\":\"These are the comments associated with the Well data object.\",\"dTimLastChange\":\"2019-01-30T14:09:27.268843+00:00\",\"acquisitionTimeZone\":[],\"defaultDatum\":{\"value\":\"Kelly Bushing\",\"uidRef\":\"KB\"},\"itemState\":\"PLAN\",\"extensionNameValue\":[],\"dTimCreation\":\"2019-01-24T16:59:38.88059+00:00\"},\"timeZone\":\"-06:00\",\"statusWell\":\"DRILLING\",\"purposeWell\":\"EXPLORATION\",\"numAPI\":\"123-543-987AZ\",\"referencePoint\":[{\"uid\":\"SRP1\",\"name\":\"Slot Bay Centre\",\"location\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425366.47},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623781.95}},{\"uid\":\"loc-2\",\"wellCRS\":{\"value\":\"WellOneWSP\",\"uidRef\":\"localWell1\"},\"localY\":{\"uom\":\"m\",\"value\":-3.74},\"description\":\"Location of the Site Reference Point with respect to the well surface point\",\"localX\":{\"uom\":\"m\",\"value\":12.63},\"extensionNameValue\":[]}],\"type\":\"Site Reference Point\",\"extensionNameValue\":[]},{\"elevation\":{\"datum\":\"SL\",\"uom\":\"FT\",\"value\":-118.4},\"uid\":\"WRP2\",\"measuredDepth\":{\"datum\":\"KB\",\"uom\":\"FT\",\"value\":173.09},\"name\":\"Sea Bed\",\"location\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425353.84},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623785.69}},{\"uid\":\"loc-2\",\"wellCRS\":{\"value\":\"ED50\",\"uidRef\":\"geog1\"},\"latitude\":{\"uom\":\"dega\",\"value\":59.743844},\"extensionNameValue\":[],\"longitude\":{\"uom\":\"dega\",\"value\":1.67198083}}],\"type\":\"Well Reference Point\",\"extensionNameValue\":[]}],\"wellLocation\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425353.84},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"description\":\"Location of well surface point in projected system.\",\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623785.69}}],\"wellheadElevation\":{\"uom\":\"FT\",\"value\":500},\"field\":\"Big Field\",\"dTimSpud\":\"2001-05-31T08:15:00+00:00\",\"wellCRS\":[{\"uid\":\"geog1\",\"geodeticCRS\":{\"value\":\"4230\",\"uidRef\":\"4230\"},\"name\":\"ED50\",\"description\":\"ED50 system with EPSG code 4230.\",\"extensionNameValue\":[]},{\"uid\":\"proj1\",\"mapProjectionCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"23031\"},\"name\":\"ED50 \\/ UTM Zone 31N\",\"extensionNameValue\":[]},{\"uid\":\"localWell1\",\"localCRS\":{\"usesWellAsOrigin\":true,\"xRotationCounterClockwise\":false,\"yAxisAzimuth\":{\"uom\":\"dega\",\"northDirection\":\"GRID_NORTH\",\"value\":0}},\"name\":\"WellOneWSP\",\"extensionNameValue\":[]}],\"district\":\"District Name\",\"name\":\"6507\\/7-A-42\",\"numGovt\":\"Govt-Number\",\"region\":\"Region Name\",\"wellDatum\":[{\"elevation\":{\"datum\":\"SL\",\"uom\":\"FT\",\"value\":78.5},\"uid\":\"KB\",\"code\":\"KB\",\"kind\":[],\"name\":\"Kelly Bushing\",\"extensionNameValue\":[]},{\"uid\":\"SL\",\"code\":\"SL\",\"datumName\":{\"namingSystem\":\"EPSG\",\"code\":\"5106\",\"value\":\"Caspian Sea\"},\"kind\":[],\"name\":\"Sea Level\",\"extensionNameValue\":[]}]}";


        // get query response
        AbstractWitsmlObject abstractWitsmlObject = DotTranslator.translateQueryResponse(wmlObject, responseString, new HashMap<>());

        assertTrue(abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell);

        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell objWell = (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject;

        assertEquals("Block Name", objWell.getBlock());
        assertTrue(objWell.getCommonData() instanceof CsCommonData);
        assertTrue(objWell.getGroundElevation() instanceof WellElevationCoord);
        assertEquals("Montgomery", objWell.getCounty());
        assertEquals("123-543-987AZ", objWell.getNumAPI());
    }

    @Test
    public void translateQueryResponseTestIdOnly() throws Exception{
        AbstractWitsmlObject wmlObject = WitsmlMarshal.deserializeFromJSON(
                "{\"country\":\"\",\"groundElevation\":{\"datum\":null,\"uom\":null,\"value\":null},\"commonData\":{\"privateGroupOnly\":null,\"comments\":\"\",\"acquisitionTimeZone\":[],\"dTimLastChange\":null,\"extensionAny\":null,\"defaultDatum\":{\"value\":\"\",\"uidRef\":\"\"},\"itemState\":null,\"sourceName\":null,\"extensionNameValue\":[],\"serviceCategory\":null,\"dTimCreation\":null},\"county\":\"\",\"timeZone\":\"\",\"waterDepth\":{\"uom\":\"\",\"value\":null},\"numAPI\":\"\",\"operator\":\"\",\"pcInterest\":{\"uom\":\"\",\"value\":null},\"referencePoint\":[{\"elevation\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"uid\":\"\",\"measuredDepth\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"name\":\"\",\"location\":[{\"uid\":\"\",\"easting\":{\"uom\":\"\",\"value\":null},\"wellCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"latitude\":{\"uom\":\"\",\"value\":null},\"localY\":{\"uom\":\"\",\"value\":null},\"description\":\"\",\"localX\":{\"uom\":\"\",\"value\":null},\"extensionNameValue\":[],\"northing\":{\"uom\":\"\",\"value\":null},\"longitude\":{\"uom\":\"\",\"value\":null}}],\"type\":\"\",\"extensionNameValue\":[]}],\"wellLocation\":[{\"uid\":\"\",\"easting\":{\"uom\":\"\",\"value\":null},\"wellCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"description\":\"\",\"extensionNameValue\":[],\"northing\":{\"uom\":\"\",\"value\":null}}],\"uid\":\"randy\",\"wellheadElevation\":{\"datum\":null,\"uom\":null,\"value\":null},\"field\":\"\",\"wellCRS\":[{\"uid\":\"\",\"localCRS\":{\"yAxisAzimuth\":{\"uom\":\"\",\"northDirection\":null,\"value\":null}},\"mapProjectionCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"geodeticCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"name\":\"\",\"description\":\"\",\"extensionNameValue\":[]}],\"nameLegal\":\"\",\"district\":\"\",\"numGovt\":\"\",\"block\":\"\",\"state\":\"\",\"region\":\"\",\"operatorDiv\":\"\",\"wellDatum\":[{\"elevation\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"uid\":\"\",\"datumName\":{\"namingSystem\":\"\",\"code\":\"\",\"value\":\"\"},\"kind\":[],\"name\":\"\",\"extensionNameValue\":[]}]}",
                com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class);
        
        String responseString = "{\"name\":\"BIG Well 0116\",\"nameLegal\":\"Company Legal Name\",\"numLicense\":\"Company License Number\",\"numGovt\":\"Govt-Number\",\"field\":\"Big Field\",\"country\":\"US\",\"state\":\"TX\",\"county\":\"Montgomery\",\"region\":\"Region Name\",\"district\":\"District Name\",\"block\":\"Block Name\",\"timeZone\":\"-06:00\",\"operator\":\"Operating Company\",\"operatorDiv\":\"Division Name\",\"pcInterest\":{\"value\":65,\"uom\":\"%\"},\"numAPI\":\"123-543-987AZ\",\"statusWell\":\"DRILLING\",\"purposeWell\":\"EXPLORATION\",\"wellheadElevation\":{\"value\":500,\"uom\":\"FT\",\"datum\":null},\"wellDatum\":[{\"name\":\"Kelly Bushing\",\"code\":\"KB\",\"kind\":[],\"elevation\":{\"value\":78.5,\"uom\":\"FT\",\"datum\":\"SL\"},\"extensionNameValue\":[],\"uid\":\"KB\"},{\"name\":\"Sea Level\",\"code\":\"SL\",\"datumName\":{\"value\":\"Caspian Sea\",\"namingSystem\":\"EPSG\",\"code\":\"5106\"},\"kind\":[],\"extensionNameValue\":[],\"uid\":\"SL\"}],\"groundElevation\":{\"value\":250,\"uom\":\"FT\",\"datum\":null},\"waterDepth\":{\"value\":520,\"uom\":\"ft\"},\"wellLocation\":[{\"wellCRS\":{\"value\":\"ED50 / UTM Zone 31N\",\"uidRef\":\"proj1\"},\"easting\":{\"value\":425353.84,\"uom\":\"m\"},\"northing\":{\"value\":6623785.69,\"uom\":\"m\"},\"description\":\"Location of well surface point in projected system.\",\"extensionNameValue\":[],\"uid\":\"loc-1\"}],\"referencePoint\":[{\"name\":\"Slot Bay Centre\",\"type\":\"Site Reference Point\",\"location\":[{\"wellCRS\":{\"value\":\"ED50 / UTM Zone 31N\",\"uidRef\":\"proj1\"},\"easting\":{\"value\":425366.47,\"uom\":\"m\"},\"northing\":{\"value\":6623781.95,\"uom\":\"m\"},\"extensionNameValue\":[],\"uid\":\"loc-1\"},{\"wellCRS\":{\"value\":\"WellOneWSP\",\"uidRef\":\"localWell1\"},\"localX\":{\"value\":12.63,\"uom\":\"m\"},\"localY\":{\"value\":-3.74,\"uom\":\"m\"},\"description\":\"Location of the Site Reference Point with respect to the well surface point\",\"extensionNameValue\":[],\"uid\":\"loc-2\"}],\"extensionNameValue\":[],\"uid\":\"SRP1\"},{\"name\":\"Sea Bed\",\"type\":\"Well Reference Point\",\"elevation\":{\"value\":-118.4,\"uom\":\"FT\",\"datum\":\"SL\"},\"measuredDepth\":{\"value\":173.09,\"uom\":\"FT\",\"datum\":\"KB\"},\"location\":[{\"wellCRS\":{\"value\":\"ED50 / UTM Zone 31N\",\"uidRef\":\"proj1\"},\"easting\":{\"value\":425353.84,\"uom\":\"m\"},\"northing\":{\"value\":6623785.69,\"uom\":\"m\"},\"extensionNameValue\":[],\"uid\":\"loc-1\"},{\"wellCRS\":{\"value\":\"ED50\",\"uidRef\":\"geog1\"},\"latitude\":{\"value\":59.743844,\"uom\":\"dega\"},\"longitude\":{\"value\":1.67198083,\"uom\":\"dega\"},\"extensionNameValue\":[],\"uid\":\"loc-2\"}],\"extensionNameValue\":[],\"uid\":\"WRP2\"}],\"wellCRS\":[{\"name\":\"ED50\",\"geodeticCRS\":{\"value\":\"4230\",\"uidRef\":\"4230\"},\"description\":\"ED50 system with EPSG code 4230.\",\"extensionNameValue\":[],\"uid\":\"geog1\"},{\"name\":\"ED50 / UTM Zone 31N\",\"mapProjectionCRS\":{\"value\":\"ED50 / UTM Zone 31N\",\"uidRef\":\"23031\"},\"extensionNameValue\":[],\"uid\":\"proj1\"},{\"name\":\"WellOneWSP\",\"localCRS\":{\"yAxisAzimuth\":{\"value\":0,\"uom\":\"dega\",\"northDirection\":\"GRID_NORTH\"}},\"extensionNameValue\":[],\"uid\":\"localWell1\"}],\"uid\":\"W-0116\"}";


        Map<String, String> optionsIn = new HashMap<>();

        optionsIn.put("returnElements", "id-only");

        AbstractWitsmlObject abstractWitsmlObject = DotTranslator.translateQueryResponse(wmlObject, responseString, optionsIn);

        assertTrue(abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell);

        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell objWell = (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject;

        assertEquals("123-543-987AZ", objWell.getNumAPI());
        assertEquals("Govt-Number", objWell.getNumGovt());
        assertEquals("BIG Well 0116", objWell.getName());
    }

    @Test
    public void translateQueryResponseTestWithReturnElementsAll() throws Exception {
        AbstractWitsmlObject wmlObject = WitsmlMarshal.deserializeFromJSON(
                "{\"country\":\"\",\"groundElevation\":{\"datum\":null,\"uom\":null,\"value\":null},\"commonData\":{\"privateGroupOnly\":null,\"comments\":\"\",\"acquisitionTimeZone\":[],\"dTimLastChange\":null,\"extensionAny\":null,\"defaultDatum\":{\"value\":\"\",\"uidRef\":\"\"},\"itemState\":null,\"sourceName\":null,\"extensionNameValue\":[],\"serviceCategory\":null,\"dTimCreation\":null},\"county\":\"\",\"timeZone\":\"\",\"waterDepth\":{\"uom\":\"\",\"value\":null},\"numAPI\":\"\",\"operator\":\"\",\"pcInterest\":{\"uom\":\"\",\"value\":null},\"referencePoint\":[{\"elevation\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"uid\":\"\",\"measuredDepth\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"name\":\"\",\"location\":[{\"uid\":\"\",\"easting\":{\"uom\":\"\",\"value\":null},\"wellCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"latitude\":{\"uom\":\"\",\"value\":null},\"localY\":{\"uom\":\"\",\"value\":null},\"description\":\"\",\"localX\":{\"uom\":\"\",\"value\":null},\"extensionNameValue\":[],\"northing\":{\"uom\":\"\",\"value\":null},\"longitude\":{\"uom\":\"\",\"value\":null}}],\"type\":\"\",\"extensionNameValue\":[]}],\"wellLocation\":[{\"uid\":\"\",\"easting\":{\"uom\":\"\",\"value\":null},\"wellCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"description\":\"\",\"extensionNameValue\":[],\"northing\":{\"uom\":\"\",\"value\":null}}],\"uid\":\"randy\",\"wellheadElevation\":{\"datum\":null,\"uom\":null,\"value\":null},\"field\":\"\",\"wellCRS\":[{\"uid\":\"\",\"localCRS\":{\"yAxisAzimuth\":{\"uom\":\"\",\"northDirection\":null,\"value\":null}},\"mapProjectionCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"geodeticCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"name\":\"\",\"description\":\"\",\"extensionNameValue\":[]}],\"nameLegal\":\"\",\"district\":\"\",\"numGovt\":\"\",\"block\":\"\",\"state\":\"\",\"region\":\"\",\"operatorDiv\":\"\",\"wellDatum\":[{\"elevation\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"uid\":\"\",\"datumName\":{\"namingSystem\":\"\",\"code\":\"\",\"value\":\"\"},\"kind\":[],\"name\":\"\",\"extensionNameValue\":[]}]}",
                com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class);

        String responseString = "{\"country\":\"US\",\"dTimLicense\":\"2001-05-15T13:20:00+00:00\",\"numLicense\":\"Company License Number\",\"county\":\"Montgomery\",\"waterDepth\":{\"uom\":\"ft\",\"value\":520},\"operator\":\"Operating Company\",\"pcInterest\":{\"uom\":\"%\",\"value\":65},\"dTimPa\":\"2001-07-15T15:30:00+00:00\",\"uid\":\"randy\",\"nameLegal\":\"Company Legal Name\",\"block\":\"Block Name\",\"state\":\"TX\",\"operatorDiv\":\"Division Name\",\"groundElevation\":{\"uom\":\"FT\",\"value\":250},\"commonData\":{\"comments\":\"These are the comments associated with the Well data object.\",\"dTimLastChange\":\"2019-01-30T14:09:27.268843+00:00\",\"acquisitionTimeZone\":[],\"defaultDatum\":{\"value\":\"Kelly Bushing\",\"uidRef\":\"KB\"},\"itemState\":\"PLAN\",\"extensionNameValue\":[],\"dTimCreation\":\"2019-01-24T16:59:38.88059+00:00\"},\"timeZone\":\"-06:00\",\"statusWell\":\"DRILLING\",\"purposeWell\":\"EXPLORATION\",\"numAPI\":\"123-543-987AZ\",\"referencePoint\":[{\"uid\":\"SRP1\",\"name\":\"Slot Bay Centre\",\"location\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425366.47},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623781.95}},{\"uid\":\"loc-2\",\"wellCRS\":{\"value\":\"WellOneWSP\",\"uidRef\":\"localWell1\"},\"localY\":{\"uom\":\"m\",\"value\":-3.74},\"description\":\"Location of the Site Reference Point with respect to the well surface point\",\"localX\":{\"uom\":\"m\",\"value\":12.63},\"extensionNameValue\":[]}],\"type\":\"Site Reference Point\",\"extensionNameValue\":[]},{\"elevation\":{\"datum\":\"SL\",\"uom\":\"FT\",\"value\":-118.4},\"uid\":\"WRP2\",\"measuredDepth\":{\"datum\":\"KB\",\"uom\":\"FT\",\"value\":173.09},\"name\":\"Sea Bed\",\"location\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425353.84},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623785.69}},{\"uid\":\"loc-2\",\"wellCRS\":{\"value\":\"ED50\",\"uidRef\":\"geog1\"},\"latitude\":{\"uom\":\"dega\",\"value\":59.743844},\"extensionNameValue\":[],\"longitude\":{\"uom\":\"dega\",\"value\":1.67198083}}],\"type\":\"Well Reference Point\",\"extensionNameValue\":[]}],\"wellLocation\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425353.84},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"description\":\"Location of well surface point in projected system.\",\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623785.69}}],\"wellheadElevation\":{\"uom\":\"FT\",\"value\":500},\"field\":\"Big Field\",\"dTimSpud\":\"2001-05-31T08:15:00+00:00\",\"wellCRS\":[{\"uid\":\"geog1\",\"geodeticCRS\":{\"value\":\"4230\",\"uidRef\":\"4230\"},\"name\":\"ED50\",\"description\":\"ED50 system with EPSG code 4230.\",\"extensionNameValue\":[]},{\"uid\":\"proj1\",\"mapProjectionCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"23031\"},\"name\":\"ED50 \\/ UTM Zone 31N\",\"extensionNameValue\":[]},{\"uid\":\"localWell1\",\"localCRS\":{\"usesWellAsOrigin\":true,\"xRotationCounterClockwise\":false,\"yAxisAzimuth\":{\"uom\":\"dega\",\"northDirection\":\"GRID_NORTH\",\"value\":0}},\"name\":\"WellOneWSP\",\"extensionNameValue\":[]}],\"district\":\"District Name\",\"name\":\"6507\\/7-A-42\",\"numGovt\":\"Govt-Number\",\"region\":\"Region Name\",\"wellDatum\":[{\"elevation\":{\"datum\":\"SL\",\"uom\":\"FT\",\"value\":78.5},\"uid\":\"KB\",\"code\":\"KB\",\"kind\":[],\"name\":\"Kelly Bushing\",\"extensionNameValue\":[]},{\"uid\":\"SL\",\"code\":\"SL\",\"datumName\":{\"namingSystem\":\"EPSG\",\"code\":\"5106\",\"value\":\"Caspian Sea\"},\"kind\":[],\"name\":\"Sea Level\",\"extensionNameValue\":[]}]}";

        Map<String, String> optionsIn = new HashMap<>();

        optionsIn.put("returnElements", "all");

        // get query response
        AbstractWitsmlObject abstractWitsmlObject = DotTranslator.translateQueryResponse(wmlObject, responseString, optionsIn);

        assertTrue(abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell);

        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell objWell = (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject;

        assertEquals("Company License Number", objWell.getNumLicense());
    }


    @Test
    public void translateQueryResponseTestWithReturnElementsIdOnly() throws Exception {
        AbstractWitsmlObject wmlObject = WitsmlMarshal.deserializeFromJSON(
                "{\"country\":\"\",\"groundElevation\":{\"datum\":null,\"uom\":null,\"value\":null},\"commonData\":{\"privateGroupOnly\":null,\"comments\":\"\",\"acquisitionTimeZone\":[],\"dTimLastChange\":null,\"extensionAny\":null,\"defaultDatum\":{\"value\":\"\",\"uidRef\":\"\"},\"itemState\":null,\"sourceName\":null,\"extensionNameValue\":[],\"serviceCategory\":null,\"dTimCreation\":null},\"county\":\"\",\"timeZone\":\"\",\"waterDepth\":{\"uom\":\"\",\"value\":null},\"numAPI\":\"\",\"operator\":\"\",\"pcInterest\":{\"uom\":\"\",\"value\":null},\"referencePoint\":[{\"elevation\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"uid\":\"\",\"measuredDepth\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"name\":\"\",\"location\":[{\"uid\":\"\",\"easting\":{\"uom\":\"\",\"value\":null},\"wellCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"latitude\":{\"uom\":\"\",\"value\":null},\"localY\":{\"uom\":\"\",\"value\":null},\"description\":\"\",\"localX\":{\"uom\":\"\",\"value\":null},\"extensionNameValue\":[],\"northing\":{\"uom\":\"\",\"value\":null},\"longitude\":{\"uom\":\"\",\"value\":null}}],\"type\":\"\",\"extensionNameValue\":[]}],\"wellLocation\":[{\"uid\":\"\",\"easting\":{\"uom\":\"\",\"value\":null},\"wellCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"description\":\"\",\"extensionNameValue\":[],\"northing\":{\"uom\":\"\",\"value\":null}}],\"uid\":\"randy\",\"wellheadElevation\":{\"datum\":null,\"uom\":null,\"value\":null},\"field\":\"\",\"wellCRS\":[{\"uid\":\"\",\"localCRS\":{\"yAxisAzimuth\":{\"uom\":\"\",\"northDirection\":null,\"value\":null}},\"mapProjectionCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"geodeticCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"name\":\"\",\"description\":\"\",\"extensionNameValue\":[]}],\"nameLegal\":\"\",\"district\":\"\",\"numGovt\":\"\",\"block\":\"\",\"state\":\"\",\"region\":\"\",\"operatorDiv\":\"\",\"wellDatum\":[{\"elevation\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"uid\":\"\",\"datumName\":{\"namingSystem\":\"\",\"code\":\"\",\"value\":\"\"},\"kind\":[],\"name\":\"\",\"extensionNameValue\":[]}]}",
                com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class);

        String responseString = "{\"country\":\"US\",\"dTimLicense\":\"2001-05-15T13:20:00+00:00\",\"numLicense\":\"Company License Number\",\"county\":\"Montgomery\",\"waterDepth\":{\"uom\":\"ft\",\"value\":520},\"operator\":\"Operating Company\",\"pcInterest\":{\"uom\":\"%\",\"value\":65},\"dTimPa\":\"2001-07-15T15:30:00+00:00\",\"uid\":\"randy\",\"nameLegal\":\"Company Legal Name\",\"block\":\"Block Name\",\"state\":\"TX\",\"operatorDiv\":\"Division Name\",\"groundElevation\":{\"uom\":\"FT\",\"value\":250},\"commonData\":{\"comments\":\"These are the comments associated with the Well data object.\",\"dTimLastChange\":\"2019-01-30T14:09:27.268843+00:00\",\"acquisitionTimeZone\":[],\"defaultDatum\":{\"value\":\"Kelly Bushing\",\"uidRef\":\"KB\"},\"itemState\":\"PLAN\",\"extensionNameValue\":[],\"dTimCreation\":\"2019-01-24T16:59:38.88059+00:00\"},\"timeZone\":\"-06:00\",\"statusWell\":\"DRILLING\",\"purposeWell\":\"EXPLORATION\",\"numAPI\":\"123-543-987AZ\",\"referencePoint\":[{\"uid\":\"SRP1\",\"name\":\"Slot Bay Centre\",\"location\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425366.47},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623781.95}},{\"uid\":\"loc-2\",\"wellCRS\":{\"value\":\"WellOneWSP\",\"uidRef\":\"localWell1\"},\"localY\":{\"uom\":\"m\",\"value\":-3.74},\"description\":\"Location of the Site Reference Point with respect to the well surface point\",\"localX\":{\"uom\":\"m\",\"value\":12.63},\"extensionNameValue\":[]}],\"type\":\"Site Reference Point\",\"extensionNameValue\":[]},{\"elevation\":{\"datum\":\"SL\",\"uom\":\"FT\",\"value\":-118.4},\"uid\":\"WRP2\",\"measuredDepth\":{\"datum\":\"KB\",\"uom\":\"FT\",\"value\":173.09},\"name\":\"Sea Bed\",\"location\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425353.84},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623785.69}},{\"uid\":\"loc-2\",\"wellCRS\":{\"value\":\"ED50\",\"uidRef\":\"geog1\"},\"latitude\":{\"uom\":\"dega\",\"value\":59.743844},\"extensionNameValue\":[],\"longitude\":{\"uom\":\"dega\",\"value\":1.67198083}}],\"type\":\"Well Reference Point\",\"extensionNameValue\":[]}],\"wellLocation\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425353.84},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"description\":\"Location of well surface point in projected system.\",\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623785.69}}],\"wellheadElevation\":{\"uom\":\"FT\",\"value\":500},\"field\":\"Big Field\",\"dTimSpud\":\"2001-05-31T08:15:00+00:00\",\"wellCRS\":[{\"uid\":\"geog1\",\"geodeticCRS\":{\"value\":\"4230\",\"uidRef\":\"4230\"},\"name\":\"ED50\",\"description\":\"ED50 system with EPSG code 4230.\",\"extensionNameValue\":[]},{\"uid\":\"proj1\",\"mapProjectionCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"23031\"},\"name\":\"ED50 \\/ UTM Zone 31N\",\"extensionNameValue\":[]},{\"uid\":\"localWell1\",\"localCRS\":{\"usesWellAsOrigin\":true,\"xRotationCounterClockwise\":false,\"yAxisAzimuth\":{\"uom\":\"dega\",\"northDirection\":\"GRID_NORTH\",\"value\":0}},\"name\":\"WellOneWSP\",\"extensionNameValue\":[]}],\"district\":\"District Name\",\"name\":\"6507\\/7-A-42\",\"numGovt\":\"Govt-Number\",\"region\":\"Region Name\",\"wellDatum\":[{\"elevation\":{\"datum\":\"SL\",\"uom\":\"FT\",\"value\":78.5},\"uid\":\"KB\",\"code\":\"KB\",\"kind\":[],\"name\":\"Kelly Bushing\",\"extensionNameValue\":[]},{\"uid\":\"SL\",\"code\":\"SL\",\"datumName\":{\"namingSystem\":\"EPSG\",\"code\":\"5106\",\"value\":\"Caspian Sea\"},\"kind\":[],\"name\":\"Sea Level\",\"extensionNameValue\":[]}]}";

        Map<String, String> optionsIn = new HashMap<>();
        // change this to id-only
        optionsIn.put("returnElements", "id-only");

        // get query response
        AbstractWitsmlObject abstractWitsmlObject = DotTranslator.translateQueryResponse(wmlObject, responseString, optionsIn);

        assertTrue(abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell);

        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell objWell = (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject;

        assertEquals(null, objWell.getNumLicense());
    }


    @Test
    public void translateQueryResponseTestWithSubSelect() throws Exception {
        AbstractWitsmlObject wmlObject = WitsmlMarshal.deserializeFromJSON(
                "{\"country\":\"\",\"groundElevation\":{\"datum\":null,\"uom\":null,\"value\":null},\"commonData\":{\"privateGroupOnly\":null,\"comments\":\"\",\"acquisitionTimeZone\":[],\"dTimLastChange\":null,\"extensionAny\":null,\"defaultDatum\":{\"value\":\"\",\"uidRef\":\"\"},\"itemState\":null,\"sourceName\":null,\"extensionNameValue\":[],\"serviceCategory\":null,\"dTimCreation\":null},\"county\":\"\",\"timeZone\":\"\",\"waterDepth\":{\"uom\":\"\",\"value\":null},\"numAPI\":\"\",\"operator\":\"\",\"pcInterest\":{\"uom\":\"\",\"value\":null},\"referencePoint\":[{\"elevation\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"uid\":\"\",\"measuredDepth\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"name\":\"\",\"location\":[{\"uid\":\"\",\"easting\":{\"uom\":\"\",\"value\":null},\"wellCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"latitude\":{\"uom\":\"\",\"value\":null},\"localY\":{\"uom\":\"\",\"value\":null},\"description\":\"\",\"localX\":{\"uom\":\"\",\"value\":null},\"extensionNameValue\":[],\"northing\":{\"uom\":\"\",\"value\":null},\"longitude\":{\"uom\":\"\",\"value\":null}}],\"type\":\"\",\"extensionNameValue\":[]}],\"wellLocation\":[{\"uid\":\"\",\"easting\":{\"uom\":\"\",\"value\":null},\"wellCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"description\":\"\",\"extensionNameValue\":[],\"northing\":{\"uom\":\"\",\"value\":null}}],\"uid\":\"randy\",\"wellheadElevation\":{\"datum\":null,\"uom\":null,\"value\":null},\"field\":\"\",\"wellCRS\":[{\"uid\":\"\",\"localCRS\":{\"yAxisAzimuth\":{\"uom\":\"\",\"northDirection\":null,\"value\":null}},\"mapProjectionCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"geodeticCRS\":{\"value\":\"\",\"uidRef\":\"\"},\"name\":\"\",\"description\":\"\",\"extensionNameValue\":[]}],\"nameLegal\":\"\",\"district\":\"\",\"numGovt\":\"\",\"block\":\"\",\"state\":\"\",\"region\":\"\",\"operatorDiv\":\"\",\"wellDatum\":[{\"elevation\":{\"datum\":\"\",\"uom\":null,\"value\":null},\"uid\":\"\",\"datumName\":{\"namingSystem\":\"\",\"code\":\"\",\"value\":\"\"},\"kind\":[],\"name\":\"\",\"extensionNameValue\":[]}]}",
                com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class);

        String responseString = "{\"country\":\"US\",\"dTimLicense\":\"2001-05-15T13:20:00+00:00\",\"numLicense\":\"Company License Number\",\"county\":\"Montgomery\",\"waterDepth\":{\"uom\":\"ft\",\"value\":520},\"operator\":\"Operating Company\",\"pcInterest\":{\"uom\":\"%\",\"value\":65},\"dTimPa\":\"2001-07-15T15:30:00+00:00\",\"uid\":\"randy\",\"nameLegal\":\"Company Legal Name\",\"block\":\"Block Name\",\"state\":\"TX\",\"operatorDiv\":\"Division Name\",\"groundElevation\":{\"uom\":\"FT\",\"value\":250},\"commonData\":{\"comments\":\"These are the comments associated with the Well data object.\",\"dTimLastChange\":\"2019-01-30T14:09:27.268843+00:00\",\"acquisitionTimeZone\":[],\"defaultDatum\":{\"value\":\"Kelly Bushing\",\"uidRef\":\"KB\"},\"itemState\":\"PLAN\",\"extensionNameValue\":[],\"dTimCreation\":\"2019-01-24T16:59:38.88059+00:00\"},\"timeZone\":\"-06:00\",\"statusWell\":\"DRILLING\",\"purposeWell\":\"EXPLORATION\",\"numAPI\":\"123-543-987AZ\",\"referencePoint\":[{\"uid\":\"SRP1\",\"name\":\"Slot Bay Centre\",\"location\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425366.47},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623781.95}},{\"uid\":\"loc-2\",\"wellCRS\":{\"value\":\"WellOneWSP\",\"uidRef\":\"localWell1\"},\"localY\":{\"uom\":\"m\",\"value\":-3.74},\"description\":\"Location of the Site Reference Point with respect to the well surface point\",\"localX\":{\"uom\":\"m\",\"value\":12.63},\"extensionNameValue\":[]}],\"type\":\"Site Reference Point\",\"extensionNameValue\":[]},{\"elevation\":{\"datum\":\"SL\",\"uom\":\"FT\",\"value\":-118.4},\"uid\":\"WRP2\",\"measuredDepth\":{\"datum\":\"KB\",\"uom\":\"FT\",\"value\":173.09},\"name\":\"Sea Bed\",\"location\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425353.84},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623785.69}},{\"uid\":\"loc-2\",\"wellCRS\":{\"value\":\"ED50\",\"uidRef\":\"geog1\"},\"latitude\":{\"uom\":\"dega\",\"value\":59.743844},\"extensionNameValue\":[],\"longitude\":{\"uom\":\"dega\",\"value\":1.67198083}}],\"type\":\"Well Reference Point\",\"extensionNameValue\":[]}],\"wellLocation\":[{\"uid\":\"loc-1\",\"easting\":{\"uom\":\"m\",\"value\":425353.84},\"wellCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"proj1\"},\"description\":\"Location of well surface point in projected system.\",\"extensionNameValue\":[],\"northing\":{\"uom\":\"m\",\"value\":6623785.69}}],\"wellheadElevation\":{\"uom\":\"FT\",\"value\":500},\"field\":\"Big Field\",\"dTimSpud\":\"2001-05-31T08:15:00+00:00\",\"wellCRS\":[{\"uid\":\"geog1\",\"geodeticCRS\":{\"value\":\"4230\",\"uidRef\":\"4230\"},\"name\":\"ED50\",\"description\":\"ED50 system with EPSG code 4230.\",\"extensionNameValue\":[]},{\"uid\":\"proj1\",\"mapProjectionCRS\":{\"value\":\"ED50 \\/ UTM Zone 31N\",\"uidRef\":\"23031\"},\"name\":\"ED50 \\/ UTM Zone 31N\",\"extensionNameValue\":[]},{\"uid\":\"localWell1\",\"localCRS\":{\"usesWellAsOrigin\":true,\"xRotationCounterClockwise\":false,\"yAxisAzimuth\":{\"uom\":\"dega\",\"northDirection\":\"GRID_NORTH\",\"value\":0}},\"name\":\"WellOneWSP\",\"extensionNameValue\":[]}],\"district\":\"District Name\",\"name\":\"6507\\/7-A-42\",\"numGovt\":\"Govt-Number\",\"region\":\"Region Name\",\"wellDatum\":[{\"elevation\":{\"datum\":\"SL\",\"uom\":\"FT\",\"value\":78.5},\"uid\":\"KB\",\"code\":\"KB\",\"kind\":[],\"name\":\"Kelly Bushing\",\"extensionNameValue\":[]},{\"uid\":\"SL\",\"code\":\"SL\",\"datumName\":{\"namingSystem\":\"EPSG\",\"code\":\"5106\",\"value\":\"Caspian Sea\"},\"kind\":[],\"name\":\"Sea Level\",\"extensionNameValue\":[]}]}";

        Map<String, String> optionsIn = new HashMap<>();
        optionsIn.put("returnElements", "requested");

        // get query response
        AbstractWitsmlObject abstractWitsmlObject = DotTranslator.translateQueryResponse(wmlObject, responseString, optionsIn);

        assertTrue(abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell);

        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell objWell = (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject;

        assertEquals(null, objWell.getNumLicense());
    }
}
