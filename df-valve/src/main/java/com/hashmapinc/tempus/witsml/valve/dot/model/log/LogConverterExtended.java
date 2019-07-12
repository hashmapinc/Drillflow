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

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogData;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog;
import com.hashmapinc.tempus.witsml.ValveLogging;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.hashmapinc.tempus.witsml.valve.dot.DotDelegator;
import com.hashmapinc.tempus.witsml.valve.dot.client.DotClient;
import com.hashmapinc.tempus.witsml.valve.dot.graphql.GraphQLQueryConverter;
import com.hashmapinc.tempus.witsml.valve.dot.graphql.GraphQLRespConverter;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.Channel;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ChannelSet;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class LogConverterExtended extends com.hashmapinc.tempus.WitsmlObjects.Util.LogConverter {
    private static final Logger LOG = Logger.getLogger(DotDelegator.class.getName());

    /**
     * Converts a 2.0-ish DoT REST API response for a v1.4.1.1 Client
     *
     * @param wellSearchEndpoint        DoT API REST endpoint for well search
     * @param wellBoreSearchEndpoint    DoT API REST endpoint for wellBore search
     * @param client                    DoT client
     * @oaram username                  User name for authentication
     * @param password                  Password for authentication
     * @param exchangeID                Unique exchange ID
     * @param witsmlObject              The v1.4.1.1 Client's WITSML object (complete);
     *                                  It is a JSON String created from the ObjLog1411
     * @param channelSet                Channel Set as a v1.4.1.1 JSON String
     * @param channels                  Channels as a v1.4.1.1 JSON String
     * @oaran channelsDepthResponse     Channels Depth response as a String
     *
     * @return ObjLog                   ObjLog for a v1.4.1.1 Client
     *
     * @throws DatatypeConfigurationException
     * @throws ParseException
     * @throws ValveException
     * @throws ValveAuthException
     * @throws UnirestException
     */
    public static ObjLog convertDotResponseToWitsml1411( String wellSearchEndpoint,
                                                         String wellBoreSearchEndpoint,
                                                         DotClient client,
                                                         String username,
                                                         String password,
                                                         String exchangeID,
                                                         AbstractWitsmlObject witsmlObject,
                                                         String channelSet,
                                                         String channels,
                                                         String channelsDepthResponse)
                                                                throws DatatypeConfigurationException,
                                                                       ParseException,
                                                                       ValveException,
                                                                       ValveAuthException,
                                                                       UnirestException
    {
// TODO Enhance all code by checking if empty (for instance, any type of .get call).
        // ******************************* Channel Set ******************************* //
        // Get the Channel Set from a v1.4.1.1 JSON String to a Java List of Channel Sets.
        List<ChannelSet> cs = ChannelSet.jsonToChannelSetList(channelSet);
        // Map the Channel Set to v1.4.1.1 for a log object.
        ObjLog log = ChannelSet.to1411(cs.get(0));
        // Get the Well and Wellbore names using the DoT's search APIs.
        log.setNameWell(
                getWellName( wellSearchEndpoint, client, username,
                             password, exchangeID, witsmlObject ));
        log.setNameWellbore(
                getWellBoreName( wellBoreSearchEndpoint,client, username,
                                 password, exchangeID, witsmlObject ));

        // ******************************** Channels ********************************* //
        // Get a Java List of Channels from a JSON String of Channels.
        List<Channel> chans = Channel.jsonToChannelList(channels);
        List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> lcis =
                Channel.to1411( chans, cs.get(0) );
        log.setLogCurveInfo(lcis);

        // ********************************* Data *********************************** //
        JSONObject logDataJsonObject = new JSONObject(channelsDepthResponse);
        List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogData> curves =
                new ArrayList<>();

        curves.add( DotLogDataHelper.convertTo1411FromDot( logDataJsonObject,
                                                           log.getIndexType()) );
        log.setLogData(curves);
        return log;
    }

    /**
     * Converts a 2.0-ish DoT REST API response for a v1.3.1.1 Client
     *
     * @param wellSearchEndpoint        DoT API REST endpoint for well search
     * @param wellBoreSearchEndpoint    DoT API REST endpoint for wellBore search
     * @param client                    DoT client
     * @oaram username                  User name for authentication
     * @param password                  Password for authentication
     * @param exchangeID                Unique exchange ID
     * @param witsmlObject              The v1.3.1.1 Client's WITSML object (complete);
     *                                  It is a JSON String created from the ObjLog1311
     * @param channelSet                Channel Set as a v1.3.1.1 JSON String
     * @param channels                  Channels as a v1.3.1.1 JSON String
     * @oaran nonMergedJSONStringdata   <data/> contains mnemonics that need to be
     *                                  used for the mapping of Channels; this is
     *                                  non-merged <data/> as a JSON String
     *
     * @return ObjLog                   ObjLog for a v1.3.1.1 Client
     *
     * @throws DatatypeConfigurationException
     * @throws ParseException
     * @throws ValveException
     * @throws ValveAuthException
     * @throws UnirestException
     */
    public static com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog
                                convertDotResponseToWitsml1311(String wellSearchEndpoint,
                                                               String wellBoreSearchEndpoint,
                                                               DotClient client,
                                                               String username,
                                                               String password,
                                                               String exchangeID,
                                                               AbstractWitsmlObject witsmlObject,
                                                               String channelSet,
                                                               String channels,
                                                               String nonMergedJSONStringdata)
                                                                 throws DatatypeConfigurationException,
                                                                        ParseException,
                                                                        ValveException,
                                                                        ValveAuthException,
                                                                        UnirestException
    {
        // ******************************* Channel Set ******************************* //
        // Get the Channel Set from a v1.3.1.1 JSON String to a Java List of Channel Sets.
        List<ChannelSet> cs = ChannelSet.jsonToChannelSetList(channelSet);
        // Map the Channel Set to v1.3.1.1 for a log object.
        com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog log = ChannelSet.to1311(cs.get(0));
        // Get the Well and Wellbore names using the DoT's search APIs.
        log.setNameWell(
                getWellName( wellSearchEndpoint, client, username,
                             password, exchangeID, witsmlObject));
        log.setNameWellbore(
                getWellBoreName( wellBoreSearchEndpoint, client, username,
                                 password, exchangeID, witsmlObject));

        // ******************************** Channels ********************************* //
        // Get a Java list of lChannels from a JSON String of Channels.
        List<Channel> chans = Channel.jsonToChannelList(channels);

        // Card #454
        // The response is for a v1.3.1.1 Client; however, the response comes from a
        // 2.0-ish DoT API. Therefore, mappings for the lcis need to be performed
        // to convert the response. An extra parameter, mnemonicList, is needed from
        // <data/> in order to perform the mappings of the lcis within the log.
        JSONObject dataAsJson = new JSONObject(nonMergedJSONStringdata);
        String[] mnemonicList = DotLogDataHelper.returnMnemonicList1311FromDot(dataAsJson);
        // pass in the mnemonicList in order to calculate columnIndex
        List<com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo> lcis =
                Channel.to1311( chans, mnemonicList);

        log.setLogCurveInfo(lcis);

        // ********************************* Data *********************************** //
        JSONObject logDataJsonObject = new JSONObject();
        log.setLogData(DotLogDataHelper.convertTo1311FromDot(dataAsJson));

        return log;
    }

    /**
     * Finds the well's name using DoT's Well Search API.
     *
     * @param wellSearchEndpoint    DoT Well Search API Endpoint
     * @param client                DoT Client to use for the search
     * @param username              User name for authentication
     * @param password              Password for authentication
     * @param exchangeID            Unique exchange ID
     * @param witsmlObject          Original WITSML XML Object (for
     *                              association with the response)
     *
     * @return String               Found Well Name (or null if not found
     *                              or there was an error)
     *
     * @throws ValveException
     * @throws ValveAuthException
     * @throws UnirestException
     */
    private static String getWellName(String wellSearchEndpoint,
                                      DotClient client,
                                      String username,
                                      String password,
                                      String exchangeID,
                                      AbstractWitsmlObject witsmlObject)
                                                throws ValveException,
                                                       ValveAuthException,
                                                       UnirestException
    {

        String wellName = null; // if not able to get the name return null.
        String query;
        try {
            query = GraphQLQueryConverter.getWellNameQuery(witsmlObject);
            LOG.fine( ValveLogging.getLogMsg(
                                              exchangeID,
                                             System.lineSeparator()
                                                     + "Graph QL Query: " + query,
                                              witsmlObject)
            );
        } catch (Exception ex) {
            throw new ValveException(ex.getMessage());
        }

        // build request
        HttpRequestWithBody request = Unirest.post(wellSearchEndpoint);
        request.header("Content-Type", "application/json");
        request.body(query);
        //LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObject));

        // get response
        HttpResponse<String> response = client.makeRequest(request, username, password);

        int status = response.getStatus();
        // If there is an error, well name will be returned as NULL.
        // But if success, well's name will be returned.
        if ( 201 == status || 200 == status ) {
            // get the wellborename of the first wellbore in the response
            wellName = GraphQLRespConverter
                            .getWellNameFromGraphqlResponse(
                                    new JSONObject(response.getBody()));

            // cache the wellbore uuid/uid
            //UidUuidCache.putInCache(wellName, witsmlObject.getParentUid(),
            // witsmlObject.getGrandParentUid());

        }
        return wellName;
    }

    /**
     * Finds the well bore's name using DoT's Well Bore Search.
     *
     * @param wellBoreSearchEndpoint    DoT Well Bore Search Endpoint
     * @param client                    DoT Client to use for the search
     * @param username                  User name for authentication
     * @param password                  Password for authentication
     * @param exchangeID                Unique exchange ID
     * @param witsmlObject              Original WITSML XML Object (for
     *                                  association with the response)
     *
     * @return String               Found Well Name (or null if not found
     *                              or there was an error)
     *
     * @throws ValveException
     * @throws ValveAuthException
     * @throws UnirestException
     */
    private static String getWellBoreName(String wellBoreSearchEndpoint,
                                          DotClient client,
                                          String username,
                                          String password,
                                          String exchangeID,
                                          AbstractWitsmlObject witsmlObject)
                                                throws ValveException,
                                                       ValveAuthException,
                                                       UnirestException
    {
        String wellboreName = null; // if not able to get the name return null.
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
        HttpResponse<String> response = client.makeRequest(request, username, password);

        // check response status
        int status = response.getStatus();
        if ( 201 == status || 200 == status ) {
            // get the wellborename of the first wellbore in the response
            wellboreName = GraphQLRespConverter.getWellboreNameFromGraphqlResponse(new JSONObject(response.getBody()));

            // cache the wellbore uuid/uid
            //UidUuidCache.putInCache(wellboreName, witsmlObject.getParentUid(), witsmlObject.getGrandParentUid());

        }
        return wellboreName;
    }

}
