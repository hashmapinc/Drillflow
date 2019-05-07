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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1411.IndexedObject;
import com.hashmapinc.tempus.witsml.valve.dot.TestUtilities;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ChannelSet;

import org.junit.Test;

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
}
