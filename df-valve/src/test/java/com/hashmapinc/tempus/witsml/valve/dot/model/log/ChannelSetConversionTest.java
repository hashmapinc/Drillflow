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
package com.hashmapinc.tempus.witsml.valve.dot.model.log;

import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.witsml.valve.dot.TestUtilities;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.Channel;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ChannelSet;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChannelSetConversionTest {
    @Test
    public void shouldCovertChannelSetFrom1411() throws JAXBException, IOException {
        String logXml = TestUtilities.getResourceAsString("log1411.xml");
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs logs = 
            WitsmlMarshal.deserialize(logXml, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs.class);
        
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog log = logs.getLog().get(0);
        ChannelSet channelSet = ChannelSet.from1411(log);
        assertEquals(log.getName(), channelSet.getCitation().getTitle());
        assertEquals(log.getServiceCompany(), channelSet.getLoggingCompanyName());
        assertEquals(log.getRunNumber(), channelSet.getRunNumber());
        assertEquals(log.getCreationDate().toString(), channelSet.getCitation().getCreation());
        assertEquals(log.getDescription(), channelSet.getCitation().getDescription());
        //assertEquals(log.getIndexType(), channelSet.getTimeDepth());
        assertEquals(log.getStartIndex().getValue().toString(), channelSet.getStartIndex());
        assertEquals(log.getEndIndex().getValue().toString(), channelSet.getEndIndex());
        assertEquals(log.getStepIncrement().getUom(), channelSet.getStepIncrement().getUom());
        assertEquals(log.getStepIncrement().getValue().toString(), channelSet.getStepIncrement().getValue());
        assertEquals(log.getNullValue(), channelSet.getNullValue());
        for (int i = 0; i < log.getLogParam().size(); i++){
            assertEquals(log.getLogParam().get(i).getDescription(),
                    channelSet.getLogParam().get(i).getDescription());
            assertEquals(log.getLogParam().get(i).getIndex(),
                    channelSet.getLogParam().get(i).getIndex());
            assertEquals(log.getLogParam().get(i).getName(),
                    channelSet.getLogParam().get(i).getName());
            assertEquals(log.getLogParam().get(i).getUom(),
                    channelSet.getLogParam().get(i).getUom());
            assertEquals(log.getLogParam().get(i).getUid(),
                    channelSet.getLogParam().get(i).getUid());
            assertEquals(log.getLogParam().get(i).getValue(),
                    channelSet.getLogParam().get(i).getValue());
        }
        assertEquals(log.getCommonData().getItemState(), channelSet.getCommonData().getItemState());
        assertEquals(log.getCommonData().getComments(), channelSet.getCommonData().getComments());
    }

   /* @Test
    public void shouldCovertChannelSetToJson() throws JAXBException, IOException {
        String logXml = TestUtilities.getResourceAsString("log1411.xml");
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs logs =
                WitsmlMarshal.deserialize(logXml, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs.class);

        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog log = logs.getLog().get(0);
        ChannelSet channelSet = ChannelSet.from1411(log);
        String jsonChannelSet = channelSet.toJson();
        assertEquals("{\n" +
                "  \"citation\" : {\n" +
                "    \"title\" : \"L001\",\n" +
                "    \"creation\" : \"2001-06-18T13:20:00.000Z\",\n" +
                "    \"description\" : \"Drilling Data Log\"\n" +
                "  },\n" +
                "  \"index\" : [ {\n" +
                "    \"indexType\" : \"Depth\",\n" +
                "    \"uom\" : \"m\",\n" +
                "    \"direction\" : \"increasing\",\n" +
                "    \"mnemonic\" : \"Mdepth\"\n" +
                "  } ],\n" +
                "  \"stepIncrement\" : {\n" +
                "    \"uom\" : \"m\",\n" +
                "    \"value\" : \"0.0\"\n" +
                "  },\n" +
                "  \"logParam\" : [ {\n" +
                "    \"index\" : \"1\",\n" +
                "    \"name\" : \"MRES\",\n" +
                "    \"uom\" : \"ohm.m\",\n" +
                "    \"description\" : \"Mud Resistivity\",\n" +
                "    \"uid\" : \"lp-1\",\n" +
                "    \"value\" : \"1.25\"\n" +
                "  }, {\n" +
                "    \"index\" : \"2\",\n" +
                "    \"name\" : \"BDIA\",\n" +
                "    \"uom\" : \"in\",\n" +
                "    \"description\" : \"Bit Diameter\",\n" +
                "    \"uid\" : \"lp-2\",\n" +
                "    \"value\" : \"12.25\"\n" +
                "  } ],\n" +
                "  \"nullValue\" : \"-999.25\",\n" +
                "  \"timeDepth\" : \"Depth\",\n" +
                "  \"runNumber\" : \"12\",\n" +
                "  \"startIndex\" : \"499.0\",\n" +
                "  \"endIndex\" : \"509.01\",\n" +
                "  \"loggingCompanyName\" : \"Baker Hughes INTEQ\",\n" +
                "  \"commonData\" : {\n" +
                "    \"itemState\" : \"plan\",\n" +
                "    \"comments\" : \"These are the comments associated with the log object.\",\n" +
                "    \"acquisitionTimeZone\" : [ ]\n" +
                "  }\n" +
                "}", jsonChannelSet);
    }*/

    @Test
    public void shouldConvertChannelsFrom1411() throws JAXBException, IOException {
        String logXml = TestUtilities.getResourceAsString("log1411.xml");
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs logs =
                WitsmlMarshal.deserialize(logXml, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs.class);

        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog log = logs.getLog().get(0);
        List<Channel> channels = Channel.from1411(log);
        assertEquals(21, channels.size());
    }

    @Test
    public void shouldConvertChannelsListToJson() throws JAXBException, IOException {
        String logXml = TestUtilities.getResourceAsString("log1411.xml");
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs logs =
                WitsmlMarshal.deserialize(logXml, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs.class);

        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog log = logs.getLog().get(0);
        List<Channel> channels = Channel.from1411(log);
        assertEquals(21, channels.size());
        String jsonChannelList = Channel.channelListToJson(channels);
        assertNotNull(jsonChannelList);
    }

}
