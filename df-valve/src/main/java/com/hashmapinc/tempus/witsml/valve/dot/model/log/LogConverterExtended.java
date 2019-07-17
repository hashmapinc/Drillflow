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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog;
import com.hashmapinc.tempus.witsml.ValveLogging;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.hashmapinc.tempus.witsml.valve.dot.DotDelegator;
import com.hashmapinc.tempus.witsml.valve.dot.client.DotClient;
import com.hashmapinc.tempus.witsml.valve.dot.client.UidUuidCache;
import com.hashmapinc.tempus.witsml.valve.dot.graphql.GraphQLQueryConverter;
import com.hashmapinc.tempus.witsml.valve.dot.graphql.GraphQLRespConverter;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.Channel;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ChannelSet;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONObject;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class LogConverterExtended extends com.hashmapinc.tempus.WitsmlObjects.Util.LogConverter {
    private static final Logger LOG = Logger.getLogger(DotDelegator.class.getName());

    /**
     * convertToChannelSet1311 takes in a v1.3.1.1 JSON string that was produced
     * client-side from SOAP WITSML XML & translates as necessary to adhere to DoT's
     * "Create a new ChannelSet" API.
     *
     * @return JSON String representing the conversion
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws ParseException
     * @throws DatatypeConfigurationException
     */

    public static ObjLog convertDotResponseToWitsml(String wellSearchEndpoint,String wellBoreSearchEndpoint,DotClient client, String username,
                                                    String password, String exchangeID,AbstractWitsmlObject witsmlObject,String channelSet,
                                                    List<Channel> channels,String channelsDepthResponse,Boolean getAllChannels,String indexType) throws JsonParseException,
            JsonMappingException, IOException, DatatypeConfigurationException, ParseException,ValveException, ValveAuthException, UnirestException {

        ObjLog log =null;

        List<ChannelSet> cs = ChannelSet.jsonToChannelSetList(channelSet);
        log = ChannelSet.to1411(cs.get(0));
        log.setUidWell(witsmlObject.getGrandParentUid());
        log.setUidWellbore(witsmlObject.getParentUid());
        log.setNameWell(getWellName( wellSearchEndpoint,client,  username,password,  exchangeID,witsmlObject));
        log.setNameWellbore(getWelBorelName( wellBoreSearchEndpoint,client,  username, password,  exchangeID,witsmlObject));

        //LogData requested or not
        if ((witsmlObject.getVersion().equals("1.4.1.1") && ((ObjLog)witsmlObject).getLogData() != null) ||
                (witsmlObject.getVersion().equals("1.3.1.1") && ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getLogData() != null) || getAllChannels){
            //if (((ObjLog)witsmlObject).getLogData().size() >0 || getAllChannels){
            if (channelsDepthResponse != null) {
                JSONObject logDataJsonObject = new JSONObject(channelsDepthResponse);
                List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogData> curves = new ArrayList<>();
                curves.add(DotLogDataHelper.convertTo1411FromDot(logDataJsonObject,indexType));
                log.setLogData(curves);
                List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> lcis = Channel.to1411WithLogData(channels,logDataJsonObject,cs.get(0));
                log.setLogCurveInfo(lcis);
            }
        }else{
            List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> lcis = Channel.to1411(channels,cs.get(0));
            log.setLogCurveInfo(lcis);
        }
        return log;
    }


    private static String getWellName(String wellSearchEndpoint,DotClient client, String username,
                                      String password, String exchangeID,AbstractWitsmlObject witsmlObject) throws ValveException, ValveAuthException, UnirestException {
        // REST call
        String wellName=null;
        String query;
        try {
            query = GraphQLQueryConverter.getWellNameQuery(witsmlObject);
            LOG.fine(ValveLogging.getLogMsg(
                    exchangeID,
                    System.lineSeparator() + "Graph QL Query: " + query,
                    witsmlObject)
            );
        } catch (Exception ex) {
            throw new ValveException(ex.getMessage());
        }

        // build request

        //String endpoint = this.getEndpoint("wellboresearch");
        HttpRequestWithBody request = Unirest.post(wellSearchEndpoint);
        request.header("Content-Type", "application/json");
        request.body(query);
        //LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObject));

        // get response
        HttpResponse<String> response = client.makeRequest(request, username, password, exchangeID);

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status || 400 == status) {
            // get the wellborename of the first wellbore in the response
            wellName = GraphQLRespConverter.getWellNameFromGraphqlResponse(new JSONObject(response.getBody()));

            // cache the wellbore uuid/uid
            //UidUuidCache.putInCache(wellName, witsmlObject.getParentUid(), witsmlObject.getGrandParentUid());

        }
        return wellName;
    }

    private static String getWelBorelName(String wellBoreSearchEndpoint,DotClient client, String username,
                                          String password, String exchangeID,AbstractWitsmlObject witsmlObject) throws ValveException, ValveAuthException, UnirestException {
        // REST call
        String wellboreName=null;
        String query;
        try {
            query = GraphQLQueryConverter.getWellboreNameQuery(witsmlObject);
            LOG.fine(ValveLogging.getLogMsg(
                    exchangeID,
                    System.lineSeparator() + "Graph QL Query: " + query,
                    witsmlObject)
            );
        } catch (Exception ex) {
            throw new ValveException(ex.getMessage());
        }

        // build request
        //String endpoint = this.getEndpoint("wellboresearch");
        HttpRequestWithBody request = Unirest.post(wellBoreSearchEndpoint);
        request.header("Content-Type", "application/json");
        request.body(query);
        //LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObject));

        // get response
        HttpResponse<String> response = client.makeRequest(request, username, password, exchangeID);

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status || 400 == status) {
            // get the wellborename of the first wellbore in the response
            wellboreName = GraphQLRespConverter.getWellboreNameFromGraphqlResponse(new JSONObject(response.getBody()));

            // cache the wellbore uuid/uid
            //UidUuidCache.putInCache(wellboreName, witsmlObject.getParentUid(), witsmlObject.getGrandParentUid());

        }
        return wellboreName;
    }

}
