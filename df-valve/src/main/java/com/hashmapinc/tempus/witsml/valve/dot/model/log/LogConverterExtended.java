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

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.Channel;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ChannelSet;

public class LogConverterExtended extends com.hashmapinc.tempus.WitsmlObjects.Util.LogConverter {
    /**
     * convertToChannelSet1311 takes in a v1.3.1.1 JSON string that was produced
     * client-side from SOAP WITSML XML & translates as necessary to adhere to DoT's
     * "Create a new ChannelSet" API.
     * 
     * @param witsmlObj Represents the client's WITSML object (complete) It is a
     *                  JSON String created from the ObjLog1311
     * @return JSON String representing the conversion
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws ParseException
     * @throws DatatypeConfigurationException
     */

    public static ObjLog convertDotResponseToWitsml(String channelSet, String channels) throws JsonParseException,
        JsonMappingException, IOException, DatatypeConfigurationException, ParseException {
        List<ChannelSet> cs = ChannelSet.jsonToChannelSetList(channelSet);
        ObjLog log = ChannelSet.to1411(cs.get(0));
        List<Channel> chans = Channel.jsonToChannelList(channels);
        List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> lcis = Channel.to1411(chans);
        log.setLogCurveInfo(lcis);
        return log;
    }
}