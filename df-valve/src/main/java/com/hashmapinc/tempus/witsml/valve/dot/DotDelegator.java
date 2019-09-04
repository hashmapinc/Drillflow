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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.FluidsReportConverter;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReport;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ShortNameStruct;
import com.hashmapinc.tempus.WitsmlObjects.v20.Fluid;
import com.hashmapinc.tempus.witsml.ValveLogging;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.hashmapinc.tempus.witsml.valve.dot.client.DotClient;
import com.hashmapinc.tempus.witsml.valve.dot.client.UidUuidCache;
import com.hashmapinc.tempus.witsml.valve.dot.graphql.GraphQLQueryConverter;
import com.hashmapinc.tempus.witsml.valve.dot.graphql.GraphQLRespConverter;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.DotLogDataHelper;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.LogConverterExtended;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.Channel;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ChannelSet;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class DotDelegator {
	private static final Logger LOG = Logger.getLogger(DotDelegator.class.getName());

	private final String WELLBORE_OBJECT = "wellbore";
	private final String TRAJECTORY_OBJECT = "trajectory";
	private final String LOG_OBJECT = "log";
	private final String FLUIDSREPORT_OBJECT = "fluidsreport";
	private final String WELL_UID = "uidWell";
	private final String WELLBORE_UID = "uidWellbore";
	private final String CHANNELSET_UUID = "channelsetuuid";
	private final int	 CS_IDX_4_PAYLOADS=0;
	private final int	 CHANNELS_IDX_4_PAYLOADS=1;
	private final int	 DATA_IDX_4_PAYLOADS=2;

	private final String WELL_PATH;
	private final String WB_PATH;
	private final String TRAJECTORY_PATH;
	private final String WELL_GQL_PATH;
	private final String WELLBORE_GQL_PATH;
	private final String TRAJECTORY_GQL_PATH;
	private final String LOG_CHANNELSET_PATH;
	private final String LOG_CHANNEL_PATH;
	private final String LOG_CHANNELSET_UUID_PATH;
	private final String LOG_CHANNELS_PATH;
	private final String LOG_CHANNELS_DATA_PATH;
	private final String LOG_MNEMONIC_PATH;
	private final String LOG_DEPTHDATA_PATH;
	private final String LOG_TIMEDATA_PATH;
	private final String LOG_DEPTH_BOUNDARY_DATA_PATH;
	private final String LOG_TIME_BOUNDARY_DATA_PATH;
	private final String FLUIDS_REPORT_PATH;
	private final String FLUIDS_REPORT_IDENTITIES_PATH;

	/**
	 * Map based constructor
	 *
	 * @param config - map with field values
	 */
	public DotDelegator(Map<String, String> config) {
		this.WELL_PATH = config.get("well.path");
		this.WB_PATH = config.get("wellbore.path");
		this.TRAJECTORY_PATH = config.get("trajectory.path");
		this.WELL_GQL_PATH = config.get("well.gql.path");
		this.WELLBORE_GQL_PATH = config.get("wellbore.gql.path");
		this.TRAJECTORY_GQL_PATH = config.get("trajectory.gql.path");
		this.LOG_CHANNELSET_PATH = config.get("log.channelset.path");
		this.LOG_CHANNEL_PATH = config.get("log.channel.path");
		this.LOG_CHANNELSET_UUID_PATH = config.get("log.channelset.uuid.path");
		this.LOG_CHANNELS_PATH = config.get("log.channels.path");
		this.LOG_CHANNELS_DATA_PATH = config.get("log.channels.data.path");
		this.LOG_MNEMONIC_PATH = config.get("log.mnemonic.data.path");
		this.LOG_DEPTHDATA_PATH = config.get("log.channel.depthData.path");
		this.LOG_TIMEDATA_PATH = config.get("log.channel.timeData.path");
		this.LOG_DEPTH_BOUNDARY_DATA_PATH = config.get("log.channel.depthBoundaryData.path");
		this.LOG_TIME_BOUNDARY_DATA_PATH = config.get("log.channel.timeBoundaryData.path");
		this.FLUIDS_REPORT_PATH = config.get("fluids.report.path");
		this.FLUIDS_REPORT_IDENTITIES_PATH = config.get("fluids.report.identities");
	}

	/**
	 * returns the endpoint for each supported object type
	 *
	 * @param objectType - well, wellbore, trajectory, or log
	 * @return endpoint - String value to send requests to
	 * @throws ValveException
	 */
	private String getEndpoint(String objectType) throws ValveException {
		// get endpoint
		String endpoint;
		switch (objectType) {
			case "well":
				endpoint = this.WELL_PATH;
				break;
			case "wellsearch":
				endpoint = this.WELL_GQL_PATH;
				break;
			case "wellbore":
				endpoint = this.WB_PATH;
				break;
			case "wellboresearch":
				endpoint = this.WELLBORE_GQL_PATH;
				break;
			case "trajectory":
				endpoint = this.TRAJECTORY_PATH;
				break;
			case "trajectorysearch":
				endpoint = this.TRAJECTORY_GQL_PATH;
				break;
			case "log":
				endpoint = this.LOG_CHANNELSET_PATH;
				break;
			case "logChannel":
				endpoint = this.LOG_CHANNEL_PATH;
				break;
			case "channelsetuuid":
				endpoint = this.LOG_CHANNELSET_UUID_PATH;
				break;
			case "channels":
				endpoint = this.LOG_CHANNELS_PATH;
				break;
			case "channelData":
				endpoint = this.LOG_CHANNELS_DATA_PATH;
				break;
			case "logMenmonicPath":
				endpoint = this.LOG_MNEMONIC_PATH;
				break;
			case "logDepthPath":
				endpoint = this.LOG_DEPTHDATA_PATH;
				break;
			case "logTimePath":
				endpoint = this.LOG_TIMEDATA_PATH;
				break;
			case "logDepthBoundaryPath":
				endpoint = this.LOG_DEPTH_BOUNDARY_DATA_PATH;
				break;
			case "logTimeBoundaryPath":
				endpoint = this.LOG_TIME_BOUNDARY_DATA_PATH;
				break;
			case "fluidsreport":
				endpoint = this.FLUIDS_REPORT_PATH;
				break;
			case "fluidsreportidentities":
				endpoint = this.FLUIDS_REPORT_IDENTITIES_PATH;
				break;
			default:
				throw new ValveException("Unsupported object type<" + objectType + ">");
		}
		return endpoint;
	}

	/**
	 * deletes the object from DoT
	 *
	 * @param witsmlObj  - object to delete
	 * @param username   - auth username
	 * @param password   - auth password
	 * @param exchangeID - unique string for tracking which exchange called this
	 *                   method
	 * @param client     - DotClient to execute requests with
	 * Suggestions:
	 * - Break out the uid/uuid identity service for log into a seperate helper method
	 * - Fix the redundant initalization of the log request
	 */

	public void deleteObject(AbstractWitsmlObject witsmlObj, String username, String password, String exchangeID,
							 DotClient client) throws ValveException, UnirestException, ValveAuthException {
		String uid = witsmlObj.getUid(); // get uid for delete call
		String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
		String uuid = "";
		String endpoint = "";
		HttpRequest request = null;
		String logCurveInfoUid = "";
		String mnemonic = "";
		String channelsEndPoint;
		HttpRequest channelsRequest;
		HttpResponse<String> channelsResponse;
		int logCurveInfosize = 0;

		endpoint = this.getEndpoint(objectType) + uid; // add uid for rest call
		request = Unirest.delete(endpoint).header("Content-Type", "application/json");
		// add query string params
		if ("wellbore".equals(objectType)) {
			request = Unirest.delete(endpoint).header("Content-Type", "application/json");
			request.queryString("uidWell", witsmlObj.getParentUid());
		} else if ("trajectory".equals(objectType)) {
			request = Unirest.delete(endpoint).header("Content-Type", "application/json");
			request.queryString("uidWellbore", witsmlObj.getParentUid());
			String uidWell;
			if ("1.4.1.1".equals(witsmlObj.getVersion())) {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) witsmlObj).getUidWell();
			} else {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory) witsmlObj).getUidWell();
			}
			request.queryString("uidWell", uidWell);
		}
		// make the DELETE call.
		HttpResponse<String> response=null;
		if ("log".equals(objectType)) {
			if("1.4.1.1".equals(witsmlObj.getVersion())){
				logCurveInfosize = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObj).getLogCurveInfo().size();
			}else{
				logCurveInfosize = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj).getLogCurveInfo().size();
			}
			if (logCurveInfosize != 0) {
				logCurveInfoUid = ((ObjLog) witsmlObj).getLogCurveInfo().get(0).getUid();
				uuid = getUUID(uid,witsmlObj,client,username,password, exchangeID);
				if (uuid == null){
					throw new ValveException("", (short)-433);
				}
				// Build Request for Get Channels
				channelsEndPoint = this.getEndpoint("channels");
				channelsRequest = Unirest.get(channelsEndPoint);
				channelsRequest.header("accept", "application/json");
				channelsRequest.queryString("channelSetUuid", uuid);
				// get response
				channelsResponse = client.makeRequest(channelsRequest, username, password, exchangeID);
				List<Channel> channels = Channel.jsonToChannelList(channelsResponse.getBody());
				for (Channel c : channels) {
					try{
						if(logCurveInfoUid.equals(c.getUid())){
							mnemonic = c.getMnemonic();
							break;
						}
					} catch (Exception ex){
						continue;
					}
				}
				String logMnemonicEndpoint = this.getEndpoint("logMenmonicPath");
				//logMnemonicEndpoint = logMnemonicEndpoint + "/" + uuid;
				HttpRequest logMnemonicRequest = Unirest.delete(logMnemonicEndpoint).header("Content-Type",
						"application/json");
				logMnemonicRequest.queryString("channelSetUuid", uuid);
				logMnemonicRequest.queryString("mnemonic", mnemonic);
				HttpResponse<String> logMnemoniceResponse = client.makeRequest(logMnemonicRequest, username, password, exchangeID);
				int mnemonicDeleteStatus = logMnemoniceResponse.getStatus();
				if (204 != mnemonicDeleteStatus) {
					throw new ValveException("DELETE DoT REST call failed with status code: " + mnemonicDeleteStatus);
				}
			}else{
				uuid = getUUID(uid,witsmlObj,client,username,password, exchangeID);
				if (uuid == null){
					throw new ValveException("Not Found", (short)-433);
				}
				String logDeletEndpoint = this.getEndpoint(LOG_OBJECT);
				logDeletEndpoint = logDeletEndpoint + "/" + uuid;
				HttpRequest logDeleteRequest = Unirest.delete(logDeletEndpoint).header("Content-Type",
						"application/json");
				HttpResponse<String> logDeleteResponse = client.makeRequest(logDeleteRequest, username, password, exchangeID);
				int deleteStatus = logDeleteResponse.getStatus();
				if (204 != deleteStatus) {
					throw new ValveException("DELETE DoT REST call failed with status code: " + deleteStatus);
				}
			}

		}else if("fluidsreport".equals(objectType)){
			uuid = getUUIDFR(uid,witsmlObj,client,username,password, exchangeID);
			if (uuid == null){
				throw new ValveException("Not Found", (short)-433);
			}
			String fluidsReportDeletEndpoint = this.getEndpoint(FLUIDSREPORT_OBJECT);
			fluidsReportDeletEndpoint = fluidsReportDeletEndpoint + "/" + uuid;
			HttpRequest fluidsReportDeleteRequest = Unirest.delete(fluidsReportDeletEndpoint).header("Content-Type",
					"application/json");
			HttpResponse<String> fluidsReportDeleteResponse = client.makeRequest(fluidsReportDeleteRequest, username, password, exchangeID);
			int deleteStatus = fluidsReportDeleteResponse.getStatus();
			if (204 != deleteStatus) {
				throw new ValveException("DELETE DoT REST call failed with status code: " + deleteStatus);
			}
		}
		else {
			response = client.makeRequest(request, username, password, exchangeID);
			int status = response.getStatus();
			if (201 == status || 200 == status || 204 == status) {
				LOG.info(ValveLogging.getLogRespMsg(exchangeID, "Successfully Deleted Object with UID: " + uid, response));
			} else {
				LOG.warning(ValveLogging.getLogRespMsg(exchangeID, "Unable to delete Object with UID: " + uid, response));
				throw new ValveException("Unable to delete Object with UID: " + status);
			}

		}
	}

	public void performElementDelete(AbstractWitsmlObject witsmlObj, String username, String password,
									 String exchangeID, DotClient client) throws ValveException, ValveAuthException, UnirestException {
		// Throwing valve exception as this is currently not supported by DoT until the
		// Patch API is implemented
		// We dont want to delete the object because someone thought something was
		// implemented.
		String uid = witsmlObj.getUid(); // get uid for delete call
		String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
		String uuid = "";
		String endpoint = "";
		HttpRequestWithBody request = null;
		HttpResponse<String> response = null;

		// It is an object delete, so re-route there
		endpoint = this.getEndpoint(objectType) + uid; // add uid for delete call
		String payload = witsmlObj.getJSONString("1.4.1.1");

		// add query string params
		if ("wellbore".equals(objectType)) {
			request.queryString("uidWell", witsmlObj.getParentUid());
		} else if ("trajectory".equals(objectType)) {
			request.queryString("uidWellbore", witsmlObj.getParentUid());
			String uidWell;
			if ("1.4.1.1".equals(witsmlObj.getVersion())) {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) witsmlObj).getUidWell();
			} else {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory) witsmlObj).getUidWell();
			}
			request.queryString("uidWell", uidWell);
		}
		if ("log".equals(objectType)) {
			uuid = getUUID(uid,witsmlObj,client,username,password,exchangeID);
			if (uuid == null){
				throw new ValveException("Not Found", (short)-433);
			}
			String logElementDeletEndpoint = this.getEndpoint(LOG_OBJECT);
			logElementDeletEndpoint = logElementDeletEndpoint + "/" + uuid;
			request = Unirest.patch(logElementDeletEndpoint).header("Content-Type", "application/json");
			payload = JsonUtil.removeEmptyArrays(new JSONObject(payload));
			request.body(payload);
			response = client.makeRequest(request, username, password, exchangeID);

			int elementDeleteStatus = response.getStatus();
			if (201 != elementDeleteStatus && 200 != elementDeleteStatus && 204 != elementDeleteStatus) {
				throw new ValveException("DELETE DoT REST call failed with status code: " + elementDeleteStatus);
			}

		}else if("fluidsreport".equals(objectType)){
			uuid = getUUIDFR(uid,witsmlObj,client,username,password, exchangeID);
			if (uuid == null){
				throw new ValveException("Not Found", (short)-433);
			}
			String fluidsReportElementDeletEndpoint = this.getEndpoint(FLUIDSREPORT_OBJECT);
			fluidsReportElementDeletEndpoint = fluidsReportElementDeletEndpoint + "/" + uuid;
			request = Unirest.patch(fluidsReportElementDeletEndpoint).header("Content-Type", "application/json");
			payload = JsonUtil.removeEmptyArrays(new JSONObject(payload));
			request.body(payload);
			response = client.makeRequest(request, username, password, exchangeID);
			int elementDeleteStatus = response.getStatus();
			if (201 != elementDeleteStatus && 200 != elementDeleteStatus && 204 != elementDeleteStatus) {
				throw new ValveException("DELETE DoT REST call failed with status code: " + elementDeleteStatus);
			}
		} else {
			request = Unirest.patch(endpoint).header("Content-Type", "application/json");
			payload = JsonUtil.removeEmptyArrays(new JSONObject(payload));
			request.body(payload);

			response = client.makeRequest(request, username, password, exchangeID);
			int status = response.getStatus();
			if (201 == status || 200 == status || 204 == status) {
				LOG.info(ValveLogging.getLogRespMsg(exchangeID, "Successfully patched object", response));
			} else {
				LOG.warning(ValveLogging.getLogRespMsg(exchangeID, "Could not perform delete", response));
				throw new ValveException("PATCH DoT REST call failed with status code: " + status);
			}
		}
	}

	/**
	 * Updates an object.
	 *
	 * @param witsmlObj
	 * @param username
	 * @param password
	 * @param exchangeID
	 * @param client
	 *
	 * @throws ValveException
	 * @throws ValveAuthException
	 * @throws UnirestException
	 * @throws JsonProcessingException
	 */
	public void updateObject(AbstractWitsmlObject witsmlObj,
							 String username,
							 String password,
							 String exchangeID,
							 DotClient client)
			throws ValveException,
			ValveAuthException,
			UnirestException,
			JsonProcessingException
	{
		String objectType = witsmlObj.getObjectType();
		String version = witsmlObj.getVersion();

		/* ****************** Fluids Report Object ****************** */
		if ("fluidsReport".equalsIgnoreCase(objectType)) {
			updateFluidsReport(witsmlObj,
					username,
					password,
					exchangeID,
					client);
		} else {

			// get object as JSON payload string w/empties removed
			String payload;
			if ("1.4.1.1".equals(version)) {
				payload = witsmlObj.getJSONString("1.4.1.1");
			} else {
				payload = witsmlObj.getJSONString("1.3.1.1");
			}
			payload = JsonUtil.removeEmpties(new JSONObject(payload));

			/* *********************** Log Object *********************** */
			if (LOG_OBJECT.equals(objectType)) {
				updateLog(witsmlObj,
						username,
						password,
						exchangeID,
						client,
						payload);

			/* ********************* All Other Objects ******************* */
			} else {
				String uid = witsmlObj.getUid();
				String endpoint = this.getEndpoint(objectType) + uid;
				HttpRequestWithBody request = Unirest.put(endpoint);
				HttpResponse<String> response;
				request.header("Content-Type", "application/json");
				if (payload.length() > 0) {
					request.body(payload);

					// add query string params
					addQueryStringParams(objectType, request, witsmlObj);

					// make the UPDATE call
					response = client.makeRequest(request, username, password, exchangeID);

					// check response status
					int status = response.getStatus();
					if (201 == status || 200 == status) {
						LOG.info(ValveLogging.getLogRespMsg(exchangeID,
								"Successfully updated object with UID: "
										+ uid,
								response));
					} else {
						LOG.warning(ValveLogging.getLogRespMsg(exchangeID,
								"Failed to update object with UID: "
										+ uid,
								response));
						throw new ValveException(response.getBody());
					}
				}
			}
		}
	}


	/**
	 * This method (Patch) Patches a FluidsReport by UUID
	 *
	 * @param witsmlObj
	 * @param username
	 * @param password
	 * @param exchangeID
	 * @param client
	 *
	 * @return String representing uid
	 *
	 * @throws ValveException
	 * @throws ValveAuthException
	 * @throws UnirestException
	 */
	public String updateFluidsReport( AbstractWitsmlObject witsmlObj,
									  String username,
									  String password,
									  String exchangeID,
									  DotClient client )
			throws  ValveException,
			ValveAuthException,
			UnirestException,
			JsonProcessingException
	{
		String uid = witsmlObj.getUid();
		String version = witsmlObj.getVersion();
		HttpRequestWithBody request;
		String objectType = witsmlObj.getObjectType();
		String uuidFsR = this.getUUIDFR( uid,
				witsmlObj,
				client,
				username,
				password,
				exchangeID );

		if (uuidFsR == null) {
			throw new ValveException("Not Found", (short) -433);
		}

		//HashMap<String,String> requestParams;
		String endpoint;
		// DoT is always v2.0 for fluids report; convert upfront
		String payload;
		com.hashmapinc.tempus.WitsmlObjects.v20.FluidsReport fluidsReport;
		ObjectMapper om = new ObjectMapper();
		if ("1.4.1.1".equals(version)) {
			fluidsReport = FluidsReportConverter.convertTo20((ObjFluidsReport)witsmlObj);

			payload = om.writer().writeValueAsString(fluidsReport);
		} else if ("1.3.1.1".equals(version)) {
			fluidsReport = FluidsReportConverter.convertTo20(
					(com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReport)witsmlObj);
			payload = om.writer().writeValueAsString(fluidsReport);
		} else {
			payload = om.writer().writeValueAsString(witsmlObj);
		}
		payload = JsonUtil.removeEmpties(new JSONObject(payload));

		// **************************** Payload Rest Call **************************** //
		// PATCH is used to make partial changes to an existing resource.              //
		//                                                                             //
		// endpoint:                                                                   //
		//        .../fluidsreport/v1/witsml/fluidsReports/{uuid}                      //
		// *************************************************************************** //
		endpoint = this.getEndpoint( objectType );
		// parameters for url
		//requestParams = new HashMap<>();
		//requestParams.put( "uuid", uuidFsR );
		endpoint = endpoint + "/" + uuidFsR;

		request = Unirest.patch(endpoint);
		// call a central method to finish the REST set-up
		// and execute the REST call for Fluids Report payload
		HttpResponse<String> response = makeRESTCalls4Objects( request,
				payload,
				exchangeID,
				username,
				password,
				client );

		// check response status
		if(response == null){
			LOG.warning(ValveLogging.getLogMsg( exchangeID,
					"Received a null fluids report request," +
							" aborting fluids report create"));
			throw new ValveException("Missing mandatory fluids report payload", (short) -405);
		}

		int status = response.getStatus();
		// success for updating fluids report
		if (200 == status) {
			// TODO Once tested, change my other log code to uid.isBlank() instead of uid.isEmpty()
			return (null == uid || uid.isBlank()) ?
					new JsonNode(response.getBody())
							.getObject()
							.getString("uid") :
					uid;
		} else {
			// captures 400 (Bad Request), 401 (Unauthorized), 404 (Not Found) and 500 (Internal Server error)
			throw new ValveException(response.getBody());
		}
	}

	/**
	 * Updates a Log object
	 *
	 * @param witsmlObj
	 * @param username
	 * @param password
	 * @param exchangeID
	 * @param client
	 * @param payload
	 *
	 * @throws ValveException
	 * @throws ValveAuthException
	 * @throws UnirestException
	 */
	public void updateLog( AbstractWitsmlObject witsmlObj,
						   String username,
						   String password,
						   String exchangeID,
						   DotClient client,
						   String payload )
			throws  ValveException,
			ValveAuthException,
			UnirestException
	{
		String uid = witsmlObj.getUid();
		String objectType = witsmlObj.getObjectType();
		String version = witsmlObj.getVersion();
		ChannelSet cs;
		String channelSetEndpoint;
		String channelsEndpoint;
		String dataEndpoint;
		String uuid;
		HttpRequestWithBody channelSetRequest;
		HttpRequestWithBody channelsRequest;
		HttpRequestWithBody channelDataRequest;

		// a log will derive its payloads from "payload":
		//        channelSet, channels, and data
		String channelSetPayload;
		String channelPayload;
		String dataPayload;

		uuid = getUuid( witsmlObj,
				uid,
				client,
				username,
				password,
				exchangeID );
		if (uuid == null) {
			throw new ValveException("Not Found", (short) -433);
		}

		// TODO check if there is anything to update with Redis cache
		//    for the payload object with cache object

		// get up to three (3) payloads for log
		String[] payloads = getPayloads4Log( version,
				payload,
				witsmlObj );
		channelSetPayload = payloads[CS_IDX_4_PAYLOADS];
		channelPayload = payloads[CHANNELS_IDX_4_PAYLOADS];
		dataPayload = payloads[DATA_IDX_4_PAYLOADS];

		Channel idxChannel = payloadCheck( payloads,
				false,
				client,
				uuid,
				username,
				password,
				exchangeID );

		// if an index channel needed to be found and was,
		// now I must take the uom from that channel and stuff it
		// as a property into every other non-index channel within
		// the Item element as "uom".
		if (idxChannel != null && !"".equals(channelPayload)) {
			JSONArray channelPayloadAsJSON = new JSONArray(channelPayload);
			for (int n = 0; n < channelPayloadAsJSON.length(); n++) {
				JSONObject nonIdxChannel = channelPayloadAsJSON.getJSONObject(n);
				JSONArray indices = nonIdxChannel.getJSONArray("index");
				for (int i = 0; i < indices.length(); i++) {
					indices.getJSONObject(i).put("uom", idxChannel.getUom());
				}
			}
			channelPayload = channelPayloadAsJSON.toString();
		}

		if (channelSetPayload != null && channelSetPayload.length() > 0) {
			// ************************* CHANNELSET *************************
			// check if channelSet is in cache (not a granular search, but
			// against the channelSet in its entirely)
			// boolean bypass = false;channelPayloadAsJSON
			try {
				if ("1.4.1.1".equals(version)) {
					cs = ChannelSet
							.from1411((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObj);
					channelSetPayload = cs.toJson();
				} else {
					cs = ChannelSet
							.from1311((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj);
					channelSetPayload = cs.toJson();
				}
			} catch (JsonProcessingException ex) {
				LOG.warning(ValveLogging.getLogMsg(exchangeID,
						"Could not produce JSON payload for ChannelSet.",
						witsmlObj));
				throw new ValveException("Could not produce JSON payload for ChannelSet");
			}

			// TODO Check if CS was found in cache (if so, bypass update)
            /*
            if (ChannelSetCache.getCS(uuid, cs)) {

            }
            */

			// **************************** Payload Rest Call **************************** //
			// PATCH is used to make partial changes to an existing resource.              //
			//                                                                             //
			// endpoint:                                                                   //
			//        .../channelSets/{uuid}                                     		   //
			// *************************************************************************** //
			channelSetEndpoint = this.getEndpoint( objectType );
			channelSetEndpoint = channelSetEndpoint + "/{" + uuid + "}";
			channelSetRequest = Unirest.patch(channelSetEndpoint);
			// if any response other than success (status code 200, 201 or 202),
			// this method will throw an error (stopping the chain of REST calls)
			makeRESTCalls4Objects( channelSetRequest,
					channelSetPayload,
					exchangeID,
					username,
					password,
					client );
		}

		if (channelPayload!=null && !channelPayload.isEmpty()) {
			// ************************** CHANNELS **************************
			// .../witsml/channels/metadata?channelSetUuid={channelSetUuid}
			channelsEndpoint = this.getEndpoint("channels");
			channelsRequest = Unirest.post(channelsEndpoint);
			// add channelSetUuid={channelSetUuid} as a query parameter
			addQueryStringParams4Log(channelsRequest, uuid);
			// if any response other than success (status code 200 or 201),
			// this method will throw an error (stopping the chain of
			// REST calls)
			makeRESTCalls4Objects( channelsRequest,
					channelPayload,
					exchangeID,
					username,
					password,
					client );
		}

		// TODO Allow multiple data packets card #598
		if (dataPayload!=null && !dataPayload.isEmpty()) {
			// **************************** DATA *****************************
			// .../witsml/channels/data?channelSetUuid={channelSetUuid}
			dataEndpoint = this.getEndpoint("channelData");
			channelDataRequest = Unirest.post(dataEndpoint);
			// add channelSetUuid={channelSetUuid} as a query parameter
			addQueryStringParams4Log(channelDataRequest, uuid);
			// if any response other than success (status code 200 or 201),
			// this method will throw an error (stopping the chain of
			// REST calls)
			makeRESTCalls4Objects( channelDataRequest,
					dataPayload,
					exchangeID,
					username,
					password,
					client );
		}
	}

	/**
	 * Provides the uuid based upon uid.
	 *
	 * @param witsmlObj
	 * @param uid
	 * @param client
	 * @param username
	 * @param password
	 *
	 * @return String uuid
	 */
	public String getUuid( AbstractWitsmlObject witsmlObj,
						   String uid,
						   DotClient client,
						   String username,
						   String password,
						   String exchangeID)
			throws ValveException,
			UnirestException,
			ValveAuthException
	{
		String uuid;

		// see if the uuid is stored in the uid/uuid cache
		if (null != UidUuidCache.getUuid( witsmlObj.getUid(),
				witsmlObj.getParentUid(),
				witsmlObj.getGrandParentUid()) ) {
			uuid = UidUuidCache.getUuid( witsmlObj.getUid(),
					witsmlObj.getParentUid(),
					witsmlObj.getGrandParentUid()) ;
		} else {
			// make the call to get uuid, and put it in cache for next time
			uuid = getUUID(uid, witsmlObj, client, username, password, exchangeID);
			if (uuid == null)
				return null;
			UidUuidCache.putInCache( uuid,
					uid,
					witsmlObj.getParentUid(),
					witsmlObj.getGrandParentUid());
		}
		return uuid;
	}

	/**
	 * builds out the HttpRequestWithBody request with parameters
	 *
	 * @param objectType - well, wellbore, trajectory (log is not handled here)
	 * @param request -    request to build out with the parameters
	 * @param witsmlObj -  abstracted WITSML in JSON format
	 */
	private void addQueryStringParams(String objectType,
									  HttpRequestWithBody request,
									  AbstractWitsmlObject witsmlObj) {
		if (WELLBORE_OBJECT.equals(objectType)) {
			request.queryString(WELL_UID, witsmlObj.getParentUid());
		} else if (TRAJECTORY_OBJECT.equals(objectType)) {
			request.queryString(WELLBORE_UID, witsmlObj.getParentUid());
			String uidWell;
			if ("1.4.1.1".equals(witsmlObj.getVersion())) {
				// TODO: maybe replace with this -> uidWell = witsmlObj.getGrandParentUid();
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) witsmlObj).getUidWell();
			} else {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory) witsmlObj).getUidWell();
			}
			request.queryString(WELL_UID, uidWell);
		}
	}

	/**
	 * builds out the HttpRequestWithBody request with parameters for log only
	 *
	 * @param request - request to modify
	 * @param uuid - channelSet uuid
	 */
	private void addQueryStringParams4Log(HttpRequestWithBody request,
										  String uuid) {
		request.queryString("channelSetUuid", uuid);
	}

	/**
	 * Submits the object to the DoT rest API for creation
	 *
	 * @param witsmlObj  - AbstractWitsmlObject to create
	 * @param username   - auth username
	 * @param password   - auth password
	 * @param exchangeID - unique string for tracking which exchange called this
	 *                     method
	 * @param client     - DotClient to execute requests with
	 *
	 * @throws ValveException
	 * @throws ValveAuthException
	 * @throws UnirestException
	 *
	 * @return String uid of object successfully submited to DoT rest API
	 * 				       for creation
	 */
	public String createObject( AbstractWitsmlObject witsmlObj,
								String username,
								String password,
								String exchangeID,
								DotClient client )
			throws  ValveException,
			ValveAuthException,
			UnirestException,
			JsonProcessingException
	{
		String objectType = witsmlObj.getObjectType();
		// ****************************** LOG ****************************** //
		if ("log".equals(objectType)) {
			String response = createLogObject( witsmlObj,
					username,
					password,
					exchangeID,
					client );
			return response;
		}

		// ************************* FLUIDS REPORT ************************* //
		if ("fluidsReport".equalsIgnoreCase(objectType)){
			String response = createFluidsReportObject(
					witsmlObj,
					username,
					password,
					exchangeID,
					client );
			return response;
		}

		// *********************** ALL OTHER OBJECTS *********************** //
		String uid = witsmlObj.getUid();
		String endpoint = this.getEndpoint(objectType);

		// get object as payload string
		String payload = witsmlObj.getJSONString("1.4.1.1");

		// build the request
		HttpRequestWithBody request;
		if (null == uid || uid.isEmpty()) {
			// create with POST and generate uid
			request = Unirest.post(endpoint);
		} else {
			// create with PUT using existing uid
			request = Unirest.put(endpoint + uid);
		}

		// add remaining query string params
		if ("wellbore".equals(objectType)) {
			// TODO: error handle this?
			request.queryString("uidWell", witsmlObj.getParentUid());
		} else if ("trajectory".equals(objectType)) {
			request.queryString("uidWellbore", witsmlObj.getParentUid());
			request.queryString("uidWell", witsmlObj.getGrandParentUid());
		}

		// add the header and payload
		request.header("Content-Type", "application/json");
		request.body(payload);

		// get the request's response
		HttpResponse<String> response = client.makeRequest( request,
				username,
				password,
				exchangeID );
		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status) {
			return (null == uid || uid.isEmpty()) ?
					new JsonNode(response.getBody())
							.getObject()
							.getString("uid") :
					uid;
		} else {
			throw new ValveException(response.getBody());
		}
	}

	/**
	 * Create DoT object from Witsml request.
	 *
	 * @param witsmlObj   Correct object model for the type (e.g. ObjFluidsReport) and version
	 * @param username	  user name for authentication
	 * @param password	  password for authentication
	 * @param exchangeID  unique id for this transaction across the system
	 * @param client	  client
	 *
	 * @return String	  uid for created object
	 *
	 * @throws ValveException
	 * @throws ValveAuthException
	 * @throws UnirestException
	 */
	public String createFluidsReportObject ( AbstractWitsmlObject witsmlObj,
											 String username,
											 String password,
											 String exchangeID,
											 DotClient client )
			throws  ValveException,
			ValveAuthException,
			UnirestException,
			JsonProcessingException
	{
		HttpResponse<String> response;
		String objectType = witsmlObj.getObjectType();
		String version = witsmlObj.getVersion();

		// however, the uuid of parent Wellbore must be present
		String wellboreUuid = getParentWellboreUUID( witsmlObj,
				exchangeID,
				client,
				username,
				password );

		// TODO check if no wellboreUuid

		HashMap<String,String> requestParams;
		String endpoint;
		String payload;
		com.hashmapinc.tempus.WitsmlObjects.v20.FluidsReport fluidsReport;
		ObjectMapper om = new ObjectMapper();
		if ("1.4.1.1".equals(version)) {
			fluidsReport = FluidsReportConverter.convertTo20((ObjFluidsReport)witsmlObj);

			payload = om.writer().writeValueAsString(fluidsReport);
		} else if ("1.3.1.1".equals(version)) {
			fluidsReport = FluidsReportConverter.convertTo20(
					(com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReport)witsmlObj);
			payload = om.writer().writeValueAsString(fluidsReport);
		} else {
			// 2.0, so no conversion is required
			payload = om.writer().writeValueAsString(witsmlObj);
		}

		// **************************** Payload Rest Call **************************** //
		// POST is used to request that the origin server accept the entity enclosed   //
		// in the request as a NEW subordinate of the resource identified by the       //
		// Request-URI in the Request-Line.                                            //
		//                                                                             //
		// endpoint:                                                                   //
		//        .../fluidsreport/v1/witsml/fluidsReports?                 		   //
		//             uuidWellbore={uuidWellbore}[&uid]                               //
		// *************************************************************************** //
		endpoint = this.getEndpoint(objectType);
		// parameters for url
		requestParams = new HashMap<>();
		requestParams.put("uuidWellbore", wellboreUuid);

		// uid is an optional request parameter
		String uid = witsmlObj.getUid();
		if ( uid != null && !uid.isBlank() )
			requestParams.put("uid", uid);

		// call a central method to finish the REST set-up
		// and execute the REST call for Fluids Report payload
		response = performPost( payload,
				endpoint,
				requestParams,
				client,
				username,
				password,
				exchangeID );

		// check response status
		if(response == null){
			LOG.warning(ValveLogging.getLogMsg(exchangeID, "Received a null fluids report request," +
					" aborting fluids report create"));
			throw new ValveException("Missing mandatory fluids report payload", (short) -405);
		}

		int status = response.getStatus();
		// success for creating fluids report
		if (201 == status) {
			// TODO Once tested, change my other log code to uid.isBlank() instead of uid.ieEmpty()
			return (null == uid || uid.isBlank()) ? new JsonNode(response.getBody()).getObject().getString("uid") :
					uid;
		} else {
			// captures 400 (Bad Request), 401 (Unauthorized), 404 (Not Found) and 500 (Internal Server error)
			throw new ValveException(response.getBody());
		}
	}

	public String createLogObject ( AbstractWitsmlObject witsmlObj,
									String username,
									String password,
									String exchangeID,
									DotClient client )
			throws ValveException,
			ValveAuthException,
			UnirestException
	{
		HttpResponse<String> response;
		String objectType = witsmlObj.getObjectType();
		String uid = witsmlObj.getUid();
		String version = witsmlObj.getVersion();
		HashMap<String,String> requestParams;
		String endpoint;

		// get WITSML abstract object as 1.4.1.1 JSON string
		String payload = ("1.4.1.1".equals(version) ?
				witsmlObj.getJSONString("1.4.1.1") :
				witsmlObj.getJSONString("1.3.1.1") );

		// separate out from the payload the sub-payloads for
		//        ChannelSet (CS_IDX_4_PAYLOADS),
		//        Channels (CHANNELS_IDX_4_PAYLOADS),
		//        and Data (DATA_IDX_4_PAYLOADS)
		String[] allPayloads = getPayloads4Log( version,
				payload,
				witsmlObj );

		// all "Client should ..." checks will be performed in this method
		// this method will throw the correct valve exception
		// if the payload is non-conforming
		payloadCheck( allPayloads, true );

		// ********************************* ChannelSet ********************************* //
		// endpoint:
		//        .../channelSets?uid={uid}&uidWellbore={uidWellbore}&uidWell={uidWell}
		endpoint = this.getEndpoint(objectType.toLowerCase());
		// parameters for url
		requestParams = new HashMap<>();
		requestParams.put("uid", uid);
		requestParams.put("uidWellbore", witsmlObj.getParentUid());
		requestParams.put("uidWell", witsmlObj.getGrandParentUid());

		// call a central method to finish the REST set-up
		// and execute the rest call for ChannelSet
		response = performPost( allPayloads[CS_IDX_4_PAYLOADS],
				endpoint,
				requestParams,
				client,
				username,
				password,
				exchangeID );

		// check response status
		if(response == null){
			LOG.warning(ValveLogging.getLogMsg(exchangeID, "Received a null channel set, aborting log create"));
			throw new ValveException("Missing mandatory channel set", (short) -405);
		}
		int status = response.getStatus();
		if (409 == status) {
			LOG.info(ValveLogging.getLogRespMsg(exchangeID,
					"Log with uid " + uid + " already in store, aborting log create",
					response));
			throw new ValveException("Log already in store", (short) -405);
		}

		// success for adding channelSet is 201...
		if (201 == status) {
        	/*
         		// cache the channelSet - null pointer exception now
         		try {
            		ChannelSetCache.putInCache(getUuid(witsmlObj, uid, client, username, password),
                                 cs);
         		} catch (JsonProcessingException ex) {
            		// not being able to cache should not stop the workflow -- log it & continue
            		LOG.severe(ValveLogging.getLogMsg(exchangeID,
                  		"JSON Processing Exception trying to cache a ChannelSet "
                        + ex.getMessage(),
                  	witsmlObj));
         		}
			*/
			// ********************************** Channels ********************************** //
			// add channels to an existing ChannelSet
			if (!(allPayloads[CHANNELS_IDX_4_PAYLOADS].isEmpty())) {

				// build the channels Request...
				// endpoint: .../channels/metadata?channelSetUuid={channelSetUuid}
				endpoint = this.getEndpoint("channels");
				// get the uuid for the channelSet just created from the response
				String uuid4CS = new JsonNode(response.getBody())
						.getObject()
						.getString("uuid");

				requestParams = new HashMap<>();
				requestParams.put("channelSetUuid", uuid4CS);
				// call a central method to finish the REST set-up
				// and execute the rest call for ChannelSet
				response = performPost( allPayloads[CHANNELS_IDX_4_PAYLOADS],
						endpoint,
						requestParams,
						client,
						username,
						password,
						exchangeID );

				// check response status
				status = response.getStatus();
				if (200 == status) {
					//LOG.info(ValveLogging.getLogRespMsg(exchangeID, "Successfully added channel metadata to the ChannelSet in DoT with uuid " + uuid4CS, response));
					// TODO: cache the channels

					// ************************************ Data ************************************ //
					// .../channels/data?channelSetUuid={channelSetUuid}
					endpoint = this.getEndpoint("channelData");
					requestParams = new HashMap<>();
					requestParams.put("channelSetUuid", uuid4CS);

					response = performPost( allPayloads[DATA_IDX_4_PAYLOADS],
							endpoint,
							requestParams,
							client,
							username,
							password,
							exchangeID );
					// check response status
					if(response != null){
						status = response.getStatus();
						// actually this requires a 200...
						if (200 != status) {
							throw new ValveException("Failed to add channel data to the ChannelSet");
						}
					}
				}

			}
			return (null == uid || uid.isEmpty()) ? new JsonNode(response.getBody()).getObject().getString("uid") :
					uid;
		} else {
			//LOG.info(ValveLogging.getLogRespMsg(exchangeID, "Failed to add ChannelSet to the DoT", response));
			throw new ValveException(response.getBody());
		}
	}

	/**
	 * Before any REST calls are performed, validate that the payloads provided
	 * by the Client are valid.
	 *
	 * This method supports the DoT business rules for payloads.
	 *
	 * @param allPayloads
	 * @param thisIsAdd		boolean flag for detecting Add (=true) vs Update (=false)
	 */
	private void payloadCheck( String[] allPayloads, boolean thisIsAdd )
			throws ValveException,
			ValveAuthException,
			UnirestException
	{
		// for AddToStore, I toss the returned Index Channel (it is already present
		// in the payload)
		payloadCheck(allPayloads, thisIsAdd,
				null, null, null, null, null);
	}

	// overload this method for Update, that requires more parameters
	private Channel payloadCheck( String[] allPayloads,
								  boolean thisIsAdd,
								  DotClient client,
								  String uuid,
								  String username,
								  String password,
								  String exchangeID )
			throws ValveException,
			ValveAuthException,
			UnirestException
	{
		String CSErrorMsg = "Client must provide valid payload: " +
				"channel set (log header with name) is missing.";
		String CHErrorMsg = "Client must provide valid payload: " +
				"channels is missing (so unit of measure cannot be obtained).";
		String CH_NoIndexChannelErrorMsg = "Client must provide valid payload: " +
				"no index channel matching <indexCurve/> was found.";

		// if there is no channel set payload, fail this request
		// (both Add and Update must specify Channel Set)
		if ( allPayloads[CS_IDX_4_PAYLOADS].equals("") &&
				thisIsAdd ) {
			LOG.warning( CSErrorMsg );
			// Throw a mandatory write schema item is missing
			throw new ValveException( CSErrorMsg, (short)-484 );
		}

		// if there are no channels on an add, then fail this transaction
		if (  allPayloads[CHANNELS_IDX_4_PAYLOADS].equals("") && thisIsAdd ) {
			LOG.warning(CHErrorMsg);
			// Client must always specify the unit for all measure data
			// on an Add
			throw new ValveException(CSErrorMsg, (short) -453);
		}

		String mnemonicForIdxChannel = findIdxChannelIdentity(allPayloads,
				thisIsAdd,
				CH_NoIndexChannelErrorMsg);

		// first check the query's channels list for the index channel
		Channel idxChannel = null;
		Iterator iterator;

		if ( !"".equals(allPayloads[CHANNELS_IDX_4_PAYLOADS]) ) {
			List<Channel> channelsList =
					Channel.jsonToChannelList(allPayloads[CHANNELS_IDX_4_PAYLOADS]);
			iterator = channelsList.iterator();
			while (iterator.hasNext()) {
				Channel currentCH = (Channel) iterator.next();
				if (currentCH.getMnemonic().equals(mnemonicForIdxChannel)) {
					idxChannel = currentCH;
					break;
				}
			}
		}
		// if the index channel is not within the query's channel list
		// (or, in the case of UpdateInStore, there is no channel list)
		if (idxChannel == null) {
			// ... and this is AddToStore ...
			if (thisIsAdd) {
				// if index is not present, there is no way to find the
				// channel index; this is why it is required; throw the error
				// that a mandatory write schema is missing
				LOG.warning(CH_NoIndexChannelErrorMsg);
				throw new ValveException(CH_NoIndexChannelErrorMsg, (short) -484);
			} else {
				// for an update, the final check for channel index will be
				// after a GET for channel metadata; this is the only way
				// remaining to add an index LogCurveInfo (LCI) to the LCI list

				// *********************** Channel Metadata *********************** //
				// endpoint:
				//        .../channels/metadata?channelSetUuid={channelSetUuid}
				String endpoint = this.getEndpoint("channels");
				HttpRequest request = Unirest.get(endpoint);
				request.header("accept", "application/json");
				request.queryString("channelSetUuid", uuid);
				HttpResponse<String> response;
				response = client.makeRequest(request, username, password, exchangeID);
				List<Channel> channels = Channel.jsonToChannelList(response.getBody());
				iterator = channels.iterator();
				while (iterator.hasNext()) {
					Channel currentCH = (Channel) iterator.next();
					if ( currentCH.getMnemonic().equals(mnemonicForIdxChannel) ) {
						idxChannel = currentCH;
						break;
					}
				}
			}
		}
		return idxChannel;
	}

	/**
	 * Identify the Index Channel by mnemonic.
	 *
	 * @param allPayloads
	 * @param thisIsAdd
	 * @param CH_NoIndexChannelErrorMsg
	 *
	 * @return String Mnemonic identity for the Index Channel
	 * 				           OR
	 * 				  Empty String (if no Mnemonic identity)
	 * @throws ValveException
	 */
	private String findIdxChannelIdentity( String[] allPayloads,
										   boolean thisIsAdd,
										   String CH_NoIndexChannelErrorMsg )
			throws ValveException
	{
		JSONArray indexArray;
		String mnemonicForIdxChannel = "";
		// first try to find the index channel's identity (mnemonic)
		// in the query's Channel Set
		if (!allPayloads[CS_IDX_4_PAYLOADS].isEmpty()) {
			JSONObject channelSet =
					new JSONObject(allPayloads[CS_IDX_4_PAYLOADS]);
			if (channelSet.has("index") && channelSet.get("index") != null) {
				indexArray = channelSet.getJSONArray("index");
				// even though index is an array, always use the 1st element
				if (indexArray.getJSONObject(0).has("mnemonic")) {
					mnemonicForIdxChannel = indexArray
							.getJSONObject(0).getString("mnemonic");
				}
			} else {
				if (thisIsAdd) {
					// if index is not present in Add, there is no way to find the
					// channel index; this is why it is required; throw the error
					// that a mandatory write schema is missing
					throw new ValveException(CH_NoIndexChannelErrorMsg, (short) -484);
				}
			}
		}
		return mnemonicForIdxChannel;
	}

	/**
	 * Perform a POST REST call.
	 *
	 * @param payload
	 * @param endpoint
	 * @param requestParams
	 * @param client
	 * @param username
	 * @param password
	 * @param exchangeID
	 *
	 * @return response to the REST call -OR- null if there is no payload
	 *
	 * @throws ValveAuthException
	 * @throws UnirestException
	 * @throws ValveException
	 */
	public HttpResponse<String> performPost( String payload,
											 String endpoint,
											 HashMap<String,String> requestParams,
											 DotClient client,
											 String username,
											 String password,
											 String exchangeID )
			throws ValveAuthException,
			UnirestException,
			ValveException
	{

		// it is not necessary to perform any work if there is
		// no payload
		if (payload.isBlank()) {
			return null;
		}

		HttpRequestWithBody request = Unirest.post(endpoint);
		request.header("Content-Type", "application/json");
		//request.header("Accept", "application/json");
		request.body(payload);
		// place the request parameters, if any, into the request
		if (!requestParams.isEmpty()) {
			requestParams.forEach(
					(key, value) -> { request.queryString(key, value); }
			);
		}

		// return the response
		return client.makeRequest(request, username, password, exchangeID);

	}

	/**
	 * creates an array of payloads:
	 *        (1) creates the POJOs for channelSet & channels and then uses them to return
	 *            array entries for their respective payloads based on the POJOs
	 *        (2) for the data payload, it simply converts it accordingly for the version
	 *            and then also returns that payload in the array
	 *
	 * @param version  either 1.4.1.1 or 1.3.1.1
	 * @param payload  JSON String with empties removed (important since witsmlObj
	 *                      may contain null) & correct for the version
	 * @param witsmlObj WITSML XML in JSON format
	 *
	 * @return String  array representing the payloads (channelSet, channels, & data);
	 *                 empty payloads are represented by the empty String
	 *
	 * @throws ValveException
	 */
	public String[] getPayloads4Log(String version,
									String payload,
									AbstractWitsmlObject witsmlObj)
			throws ValveException
	{
		String[] payloads = new String[3];
		JSONObject payloadJSON = new JSONObject(payload);

		try {
			// ****************************************** CHANNEL SET ******************************************
			// even if there is no "name" element provided by the Client,
			// Drillflow sometimes provides a "name" equal to the String "null"
			if ( payloadJSON.has("uid") &&
					payloadJSON.getString("uid") != null &&
					payloadJSON.getString("uid").length() > 0) {
				switch (version) {
					case "1.3.1.1":
						payloads[CS_IDX_4_PAYLOADS] = ChannelSet.from1311(
								(com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj).toJson();
						break;
					case "1.4.1.1":
						payloads[CS_IDX_4_PAYLOADS] = ChannelSet.from1411(
								(ObjLog) witsmlObj).toJson();
						break;
					default:
						payloads[CS_IDX_4_PAYLOADS] = "";
						break;
				}
			} else {
				payloads[CS_IDX_4_PAYLOADS] = "";
			}

			// ******************************************* CHANNELS ********************************************

			if (payloadJSON.has("logCurveInfo") &&
					payloadJSON.getJSONArray("logCurveInfo") != null &&
					payloadJSON.getJSONArray("logCurveInfo").length() > 0) {
				switch (version) {
					case "1.3.1.1":
						payloads[CHANNELS_IDX_4_PAYLOADS] = Channel.channelListToJson(
								Channel.from1311((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj));
						break;
					case "1.4.1.1":
						payloads[CHANNELS_IDX_4_PAYLOADS] = Channel.channelListToJson(
								Channel.from1411((ObjLog) witsmlObj));
						break;
					default:
						payloads[CHANNELS_IDX_4_PAYLOADS] = "[]";
						break;
				}
			} else {
				payloads[CHANNELS_IDX_4_PAYLOADS] = "[]";
			}

			// catch the case where logCurveInfo is all null elements,
			// resulting in payloads[CHANNELS_IDX_4_PAYLOADS] = []
			JSONArray channelsJSON = new JSONArray(payloads[CHANNELS_IDX_4_PAYLOADS]);
			if (channelsJSON.length() == 0 ) {
				payloads[CHANNELS_IDX_4_PAYLOADS] = "";
			}

			// ********************************************* DATA **********************************************
			if ( payloadJSON.has("logData") ) {
				if ( !JsonUtil.isEmpty(payloadJSON.get("logData"))) {
					switch (version) {
						case "1.3.1.1":
							if (payloadJSON.getJSONObject("logData").length() > 0) {
								payloads[DATA_IDX_4_PAYLOADS] = DotLogDataHelper.convertDataToDotFrom1311(
										(com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj);
							}
							break;
						case "1.4.1.1":
							if (payloadJSON.getJSONArray("logData").length() > 0) {
								payloads[DATA_IDX_4_PAYLOADS] = DotLogDataHelper.convertDataToDotFrom1411(
										(ObjLog) witsmlObj);
							}
							break;
						default:
							payloads[DATA_IDX_4_PAYLOADS] = "";
							break;
					}
				} else {
					payloads[DATA_IDX_4_PAYLOADS] = "";
				}
			} else {
				payloads[DATA_IDX_4_PAYLOADS] = "";
			}

		} catch ( JsonProcessingException ex ) {
			throw new ValveException("Error converting Log to ChannelSet/Channels/Data: " +
					ex.getMessage() );
		}
		return payloads;
	}

	/**
	 * Submits the query to the DoT rest API for object GETing
	 *
	 * @param witsmlObject - AbstractWitsmlObject to get
	 * @param username     - auth username
	 * @param password     - auth password
	 * @param exchangeID   - unique string for tracking which exchange called this
	 *                     method
	 * @param client       - DotClient to execute requests with
	 * @param optionsIn
	 *
	 * @return get results AbstractWitsmlObject OR null if version not implemented yet
	 */
	public AbstractWitsmlObject getObject( AbstractWitsmlObject witsmlObject,
										   String username,
										   String password,
										   String exchangeID,
										   DotClient client,
										   Map<String, String> optionsIn )
			throws  ValveException,
			ValveAuthException,
			UnirestException,
			DatatypeConfigurationException,
			IOException
	{
		String uid = witsmlObject.getUid();
		String version = witsmlObject.getVersion();
		String objectType = witsmlObject.getObjectType();
		String uuid;
		String finalResponse;

		// ********************************* LOG ********************************* //
		if ("log".equals(objectType)) {
			boolean shouldGetData = false;
			boolean getAllChannels = false;

			if ( optionsIn.containsKey("returnElements") &&
					optionsIn.get("returnElements").equals("all") )
			{
				shouldGetData = true;
				getAllChannels = true;

			} else {
				// Log only supports 1.3.1.1 OR 1.4.1.1
				if ( version.equals("1.3.1.1") ) {
					if ( ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)
							witsmlObject).getLogData() != null ) {
						shouldGetData = true;
					}
				} else {
					if ( ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog)
							witsmlObject).getLogData() != null ) {
						shouldGetData = true;
					}
				}
			}

			uuid = getUUID( uid,
					witsmlObject,
					client,
					username,
					password,
					exchangeID );

			if (uuid == null)
				return null;

			finalResponse =  getFromStoreRestCalls( witsmlObject,
					client,
					uuid,
					username,
					password,
					exchangeID,
					shouldGetData,
					getAllChannels );

			/*
			TODO This is the fix for logs, but it still needs work.
			int status = finalResponse.getStatus();
			if (201 == status || 200 == status) {
				if ( version.equals("1.4.1.1") ) {
					return DotTranslator.createFsRQueryResponse( finalResponse.getBody(), witsmlObject );
				} else if ( version.equals("1.3.1.1") )
					return DotTranslator.createFsRQueryResponse( finalResponse.getBody(), witsmlObject);
				else
					// version 2.0, so no transaction is required; however, still need to go from JSON to
					// an AbstractWitsmlObject
					return WitsmlMarshal.deserializeFromJSON(finalResponse.getBody(),
							com.hashmapinc.tempus.WitsmlObjects.v20.FluidsReport.class);
			} else if (404 == status) {
				// handle not found. This is a valid response
				return null;
			} else {
				throw new ValveException(finalResponse.getBody());
			}
			*/

			if ( version.equals("1.4.1.1") )
				return DotTranslator.translateQueryResponse( witsmlObject,
						finalResponse,
						optionsIn );
			else if ( version.equals("1.3.1.1") )
				return DotTranslator.get1311WitsmlObject( witsmlObject );
			else
				return null;

		// **************************** FLUIDS REPORT **************************** //
		} else if("fluidsreport".equals(objectType)) {

			String fluidsReportEndpoint;
			HttpRequest fluidsReportRequest;
			HttpResponse<String> fluidsReportResponse;

			uuid = getUUIDFR( uid,
					witsmlObject,
					client,
					username,
					password,
					exchangeID );

			if (uuid == null)
				return null;

			fluidsReportEndpoint = this.getEndpoint(FLUIDSREPORT_OBJECT);
			fluidsReportEndpoint = fluidsReportEndpoint + "/" + uuid;
			fluidsReportRequest = Unirest.get(fluidsReportEndpoint);
			fluidsReportRequest.header("accept", "application/json");

			fluidsReportResponse = client.makeRequest( fluidsReportRequest,
					username,
					password,
					exchangeID);

			int status = fluidsReportResponse.getStatus();
			if (201 == status || 200 == status) {
				if ( version.equals("1.4.1.1") ) {
					return DotTranslator.createFsRQueryResponse( fluidsReportResponse.getBody(), witsmlObject );
				} else if ( version.equals("1.3.1.1") )
					return DotTranslator.createFsRQueryResponse( fluidsReportResponse.getBody(), witsmlObject);
				else
					// version 2.0, so no transaction is required; however, still need to go from JSON to
					// an AbstractWitsmlObject
					return WitsmlMarshal.deserializeFromJSON(fluidsReportResponse.getBody(),
							com.hashmapinc.tempus.WitsmlObjects.v20.FluidsReport.class);
			} else if (404 == status) {
				// handle not found. This is a valid response
				return null;
			} else {
				throw new ValveException(fluidsReportResponse.getBody());
			}
		}

		// **************************** ALL OTHER OBJECTS **************************** //
		String endpoint = this.getEndpoint(objectType) + uid;
		HttpRequest request = Unirest.get(endpoint);
		request.header("accept", "application/json");

		if ("wellbore".equals(objectType)) {
			request.queryString("uidWell", witsmlObject.getParentUid()); // TODO: check the parent uid exists?
		} else if ("trajectory".equals(objectType)) {
			request.queryString("uidWellbore", witsmlObject.getParentUid());
			String uidWell;
			if ("1.4.1.1".equals(version)) {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) witsmlObject).getUidWell();
			} else {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory) witsmlObject).getUidWell();
			}
			request.queryString("uidWell", uidWell);
		}
		// get response
		HttpResponse<String> response = client.makeRequest(request, username, password, exchangeID);
		int status = response.getStatus();
		if (201 == status || 200 == status) {
			return DotTranslator.translateQueryResponse(witsmlObject, response.getBody(), optionsIn);
		} else if (404 == status) {
			// handle not found. This is a valid response
			return null;
		} else {
			throw new ValveException(response.getBody());
		}
	}

	/**
	 * All other rest calls to verify channelSetMetadata,ChannelSet,Channels,logData
	 *
	 * @param witsmlObject - AbstractWitsmlObject to get
	 * @param client
	 * @param uuid
	 * @param username     - auth username
	 * @param password     - auth password
	 * @param exchangeID   - unique string for tracking which exchange called this
	 *                     method
	 * @param getData
	 * @param getAllChannels
	 *
	 * @throws ValveException
	 * @throws ValveAuthException
	 * @throws UnirestException
	 *
	 * @returns String
	 */
	private String getFromStoreRestCalls( AbstractWitsmlObject witsmlObject,
										  DotClient client,
										  String uuid,
										  String username,
										  String password,
										  String exchangeID,
										  boolean getData,
										  boolean getAllChannels )
			throws ValveException,
			ValveAuthException,
			UnirestException
	{

		String channelsetmetadataEndpoint;
		HttpResponse<String> channelsetmetadataResponse;
		HttpRequest channelsetmetadataRequest;
		String channelsetuuidEndpoint;
		HttpRequest channelsetuuidRequest;
		HttpResponse<String> allChannelSet;
		String channelsEndPoint;
		HttpRequest channelsRequest;
		HttpResponse<String> channelsResponse;
		String channelsDepthEndPoint;
		HttpRequestWithBody channelsDepthRequest;
		HttpResponse<String> channelsDepthResponse=null;
		ObjLog finalResponse = null;
		String data ="";
		String indexType="";

		// get object as payload string
		String payload;
		if ("1.4.1.1".equals(witsmlObject.getVersion())) {
			payload = witsmlObject.getJSONString("1.4.1.1");
		} else {
			payload = witsmlObject.getJSONString("1.3.1.1");
		}
		payload = JsonUtil.removeEmpties(new JSONObject(payload));

		// Build Request for Get ChannelSet Metadata
		channelsetmetadataEndpoint = this.getEndpoint(LOG_OBJECT);
		channelsetmetadataEndpoint = channelsetmetadataEndpoint + "/" + uuid;
		channelsetmetadataRequest = Unirest.get(channelsetmetadataEndpoint);
		channelsetmetadataRequest.header("accept", "application/json");
		// get response
		channelsetmetadataResponse = client.makeRequest(channelsetmetadataRequest, username, password, exchangeID);
		// Build Request for Get All ChannelSet
		channelsetuuidEndpoint = this.getEndpoint("log");
		channelsetuuidRequest = Unirest.get(channelsetuuidEndpoint);
		channelsetuuidRequest.header("accept", "application/json");
		channelsetuuidRequest.queryString("containerId", uuid);
		// get response
		allChannelSet = client.makeRequest(channelsetuuidRequest, username, password, exchangeID);
		List<ChannelSet> cs = ChannelSet.jsonToChannelSetList(allChannelSet.getBody());
		for (ChannelSet channelSet : cs) {
			try{
				if (channelSet.getTimeDepth().toLowerCase().contains("depth")) {
					indexType = "depth";
				}else{
					indexType = "time";
				}
			} catch (Exception ex){
				continue;
			}
		}
		// Build Request for Get Channels
		channelsEndPoint = this.getEndpoint("channels");
		channelsRequest = Unirest.get(channelsEndPoint);
		channelsRequest.header("accept", "application/json");
		channelsRequest.queryString("channelSetUuid", uuid);

		channelsResponse = client.makeRequest(channelsRequest, username, password, exchangeID);
		List<Channel> channels = Channel.jsonToChannelList(channelsResponse.getBody());

		String startIndex = null;
		String endIndex = null;

		if ("1.3.1.1".equals(witsmlObject.getVersion())){
			if (((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getStartDateTimeIndex() != null){
				startIndex = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getStartDateTimeIndex();
			} else if (((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getStartIndex() != null &&
					((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getStartIndex().getValue() != null){
				startIndex = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getStartIndex().getValue().toString();
			}
			if (((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getEndDateTimeIndex() != null){
				endIndex = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getEndDateTimeIndex();
			} else if (((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getEndIndex() != null&&
					((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getEndIndex().getValue() != null){
				endIndex = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getEndIndex().getValue().toString();
			}
		} else if ("1.4.1.1".equals(witsmlObject.getVersion())) {
			if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog)witsmlObject).getStartDateTimeIndex() != null){
				startIndex = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog)witsmlObject).getStartDateTimeIndex();
			} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog)witsmlObject).getStartIndex() != null &&
					((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog)witsmlObject).getStartIndex().getValue() != null){
				startIndex = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog)witsmlObject).getStartIndex().getValue().toString();
			}
			if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog)witsmlObject).getEndDateTimeIndex() != null){
				endIndex = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog)witsmlObject).getEndDateTimeIndex();
			} else if (((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog)witsmlObject).getEndIndex() != null &&
					((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog)witsmlObject).getEndIndex().getValue() != null){
				endIndex = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog)witsmlObject).getEndIndex().getValue().toString();
			}
		}

		if (!getAllChannels)
			channels = filterChannelsBasedOnRequest(channels, witsmlObject,cs.get(0));
		// Build Request for Get Channels Depth
		String channelData = null;
		if (getData) {
			if(getAllChannels || channels != null) {
				if (indexType.equals("depth")) {
					JSONObject payloadJSON = new JSONObject(payload);
					if (((witsmlObject.getVersion().equals("1.4.1.1") && ((ObjLog)witsmlObject).getLogData() != null) || getAllChannels) ||
							(witsmlObject.getVersion().equals("1.3.1.1") && ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getLogData() != null)){
						//if (((ObjLog) witsmlObject).getLogData().size() > 0 || getAllChannels) {
						String sortDesc = "true";
						data = DotLogDataHelper.convertChannelDepthDataToDotFrom(channels, uuid, sortDesc, startIndex, endIndex);
						// create with POST
						channelsDepthEndPoint = this.getEndpoint("logDepthPath");
						channelsDepthRequest = Unirest.post(channelsDepthEndPoint);
						channelsDepthRequest.header("Content-Type", "application/json");
						channelsDepthRequest.body(data);
						// get the request response.
						channelsDepthResponse = client.makeRequest(channelsDepthRequest, username, password, exchangeID);
						channelData = channelsDepthResponse.getBody();
					}
				} else {
					JSONObject payloadJSON = new JSONObject(payload);
					if (((witsmlObject.getVersion().equals("1.4.1.1") && ((ObjLog)witsmlObject).getLogData() != null) || getAllChannels) ||
							(witsmlObject.getVersion().equals("1.3.1.1") && ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObject).getLogData() != null)){
						//if (((ObjLog)witsmlObject).getLogData().size() > 0 || getAllChannels) {
						String sortDesc = "true";
						data = DotLogDataHelper.convertChannelDepthDataToDotFrom(channels, uuid, sortDesc, startIndex, endIndex);
						// create with POST
						channelsDepthEndPoint = this.getEndpoint("logTimePath");
						channelsDepthRequest = Unirest.post(channelsDepthEndPoint);
						channelsDepthRequest.header("Content-Type", "application/json");
						channelsDepthRequest.body(data);
						// get the request response.
						channelsDepthResponse = client.makeRequest(channelsDepthRequest, username, password, exchangeID);
						channelData = channelsDepthResponse.getBody();
					}
				}
			}

		}

		if (201 == channelsetmetadataResponse.getStatus() || 200 == channelsetmetadataResponse.getStatus()
				|| 200 == allChannelSet.getStatus() || 201 == allChannelSet.getStatus()
				|| 200 == channelsResponse.getStatus() || 201 == channelsResponse.getStatus()) {
			try {
				String wellSearchEndpoint = this.getEndpoint("wellsearch");
				String wellBoreSearchEndpoint = this.getEndpoint("wellboresearch");
				finalResponse = LogConverterExtended.convertDotResponseToWitsml
						(wellSearchEndpoint,wellBoreSearchEndpoint,client,username,password,exchangeID, witsmlObject,allChannelSet.getBody(),
								channels,channelData,getAllChannels,indexType, getData);
			} catch (Exception e) {
				throw new ValveException("Could not convert DOT log response to JSON " + e.getMessage());
			}
		} else {
			// throw new ValveException(channelsResponse.getBody());
			return null;
		}
		if (finalResponse != null)
			return finalResponse.getJSONString("1.4.1.1");
		else
			return null;
	}

	private List<Channel> filterChannelsBasedOnRequest(List<Channel> allChannels,
													   AbstractWitsmlObject requestObject,
													   ChannelSet channelSet)
			throws ValveException
	{
		List<Channel> requestedChannels = new ArrayList<>();

		if (allChannels.size() == 0){
			return null;
		}

		if ("1.3.1.1".equals(requestObject.getVersion())) {
			List<CsLogCurveInfo> infos =
					((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) requestObject).getLogCurveInfo();

			//if nothing was requested
			if (infos.size() == 0)
				return null;

			if (infos.size() == 1 && infos.get(0).getMnemonic().isEmpty()){
				return allChannels;
			} else {
				//extract index
				String indexMnem = allChannels.get(0).getIndex().get(0).getMnemonic();
				boolean alreadyRequested = false;
				for (CsLogCurveInfo lci : infos){
					if (lci.getMnemonic().equals(indexMnem)){
						alreadyRequested = true;
					}
				}
				if (!alreadyRequested){
					//create index request
					CsLogCurveInfo lci = new CsLogCurveInfo();
					lci.setMnemonic(indexMnem);
					infos.add(lci);
				}
			}
		} else if ("1.4.1.1".equals(requestObject.getVersion())){
			List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> infos =
					((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) requestObject).getLogCurveInfo();

			//if nothing was requested
			if (infos.size() == 0)
				return null;

			if (infos.size() == 1 && infos.get(0).getMnemonic().getValue().isEmpty()){
				return allChannels;
			}else {
				//extract index
				String indexMnem = allChannels.get(0).getIndex().get(0).getMnemonic();
				boolean alreadyRequested = false;
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo lci : infos){
					if (lci.getMnemonic().getValue().equals(indexMnem)){
						alreadyRequested = true;
					}
				}
				if (!alreadyRequested){
					//create index request
					com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo lci
							= new com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo();
					ShortNameStruct sns = new ShortNameStruct();
					sns.setValue(indexMnem);
					lci.setMnemonic(sns);
					infos.add(lci);
				}
			}
		} else {
			throw new ValveException("Unsupported WITSML version for Log");
		}

		for (Channel currentChannel : allChannels){
			if ("1.3.1.1".equals(requestObject.getVersion())){
				for (CsLogCurveInfo lci : ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) requestObject).getLogCurveInfo()){
					if (lci.getMnemonic().equals(currentChannel.getMnemonic())){
						if (currentChannel.getIndex().get(0).getIndexType().toLowerCase().contains("time")){
							if (lci.getMaxDateTimeIndex() != null)
								currentChannel.setEndIndex(lci.getMaxDateTimeIndex());
							else
								currentChannel.setEndIndex(null);
							if (lci.getMinDateTimeIndex() != null)
								currentChannel.setStartIndex(lci.getMaxDateTimeIndex());
							else
								currentChannel.setStartIndex(null);
						} else {
							if (lci.getMaxIndex() != null && lci.getMaxIndex().getValue() != null)
								currentChannel.setEndIndex(lci.getMaxIndex().getValue().toString());
							else
								currentChannel.setEndIndex(null);
							if (lci.getMinIndex() != null && lci.getMinIndex().getValue() != null)
								currentChannel.setStartIndex(lci.getMinIndex().getValue().toString());
							else
								currentChannel.setEndIndex(null);
						}
						requestedChannels.add(currentChannel);

					}
				}

			} else if ("1.4.1.1".equals(requestObject.getVersion())){
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo lci : ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) requestObject).getLogCurveInfo()){
					if (lci.getMnemonic().getValue().equals(currentChannel.getMnemonic())){

						if (currentChannel.getStartIndex() != null){
							currentChannel.setStartIndex(currentChannel.getStartIndex());
							currentChannel.setMnemonic(lci.getMnemonic().getValue());
						}else{
							currentChannel.setStartIndex(channelSet.getStartIndex());
							currentChannel.setMnemonic(lci.getMnemonic().getValue());
						}

						if (currentChannel.getEndIndex() != null){
							currentChannel.setEndIndex(currentChannel.getEndIndex());
							currentChannel.setMnemonic(lci.getMnemonic().getValue());
						}else{
							currentChannel.setEndIndex(channelSet.getEndIndex());
							currentChannel.setMnemonic(lci.getMnemonic().getValue());
						}

						requestedChannels.add(currentChannel);
					}
				}
			} else{
				throw new ValveException("Unknown WITSML version for log");
			}
		}
		return requestedChannels;
	}

	/**
	 * Submits a search query to the DoT rest API for object GETing
	 *
	 * @param witsmlObject - AbstractWitsmlObject to get
	 * @param username - auth username
	 * @param password - auth password
	 * @param exchangeID - unique string for tracking which exchange called this method
	 * @param client - DotClient to execute requests with
	 * @param optionsIn
	 *
	 * @return get results AbstractWitsmlObject
	 */
	public ArrayList<AbstractWitsmlObject> search(
			AbstractWitsmlObject witsmlObject,
			String username,
			String password,
			String exchangeID,
			DotClient client,
			Map<String, String> optionsIn
	) throws ValveException, ValveAuthException, UnirestException, IOException, DatatypeConfigurationException, JAXBException {
		String objectType = witsmlObject.getObjectType();

		if (objectType.equals("log"))
			return performLogSearch(witsmlObject, username, password, exchangeID, client, optionsIn);

		String endpoint = this.getEndpoint(objectType + "search");

		String query;
		try {
			//TODO: Shift to does not = well || wellbore
			if ("trajectory".equals(objectType)) {
				String wellboreUuid = getParentWellboreUUID(witsmlObject, exchangeID, client, username, password);
				if (wellboreUuid == null){
					return null;
				}
				query = GraphQLQueryConverter.getQuery(witsmlObject, wellboreUuid);
			} else {
				query = GraphQLQueryConverter.getQuery(witsmlObject);
			}
		} catch (Exception ex){
			throw new ValveException(ex.getMessage());
		}

		// build request
		HttpRequestWithBody request = Unirest.post(endpoint);
		request.header("Content-Type", "application/json");
		request.body(query);

		// get response
		HttpResponse<String> response = client.makeRequest(request, username, password, exchangeID);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status || 400 == status) {

			// get matching objects from search as list of abstract witsml objects
			ArrayList<AbstractWitsmlObject> wmlResponses = GraphQLRespConverter.convert(response.getBody(), objectType);

			// translate responses
			if (wmlResponses == null || wmlResponses.isEmpty())
				return null; // this is valid. No matches found

			if ("trajectory".equals(objectType)) {
				for (AbstractWitsmlObject wmlResponse: wmlResponses) {
					ObjTrajectory trajResp = (ObjTrajectory) wmlResponse;
					trajResp.setUidWell(witsmlObject.getGrandParentUid());
					trajResp.setUidWellbore(witsmlObject.getParentUid());
				}
			}

			ArrayList<AbstractWitsmlObject> results = new ArrayList<>();
			for (AbstractWitsmlObject wmlResponse: wmlResponses) {
				results.add(
						DotTranslator.translateQueryResponse(
								witsmlObject,
								wmlResponse.getJSONString("1.4.1.1"),
								optionsIn
						)
				);
			}

			return results;

		} else {
			throw new ValveException(response.getBody());
		}
	}

	/**
	 *
	 * @param witsmlObject
	 * @param username
	 * @param password
	 * @param exchangeID
	 * @param client
	 * @param optionsIn
	 *
	 * @return ArrayList<AbstractWitsmlObject><
	 *
	 * @throws ValveAuthException
	 * @throws UnirestException
	 * @throws ValveException
	 */
	private ArrayList<AbstractWitsmlObject> performLogSearch(AbstractWitsmlObject witsmlObject,
															 String username,
															 String password,
															 String exchangeID,
															 DotClient client,
															 Map<String, String> optionsIn)
			throws ValveAuthException, UnirestException, ValveException, DatatypeConfigurationException, JAXBException
	{
		String containerID = getParentWellboreUUID(witsmlObject, exchangeID, client, username, password);
		if (containerID == null){
			return null;
		}
		String logSearchEndpoint = this.getEndpoint(LOG_OBJECT);
		HttpRequest logSearchRequest = Unirest.get(logSearchEndpoint);
		logSearchRequest.queryString("containerId", containerID);
		HttpResponse<String> logSearchResponse = client.makeRequest(logSearchRequest, username, password, exchangeID);

		if (logSearchResponse.getStatus() != 200)
			throw new ValveException("Error searching for logs in container " + containerID);

		JSONArray foundChannelSets = new JSONArray(logSearchResponse.getBody());

		if (foundChannelSets.length() == 0)
			return null;

		List<String> foundUuids = new ArrayList<>();
		for (int i = 0; i < foundChannelSets.length(); i++){
			JSONObject currentCS = (JSONObject)foundChannelSets.get(i);
			foundUuids.add(currentCS.get("uuid").toString());
		}

		boolean getAllChannels = false;
		if (optionsIn.containsKey("returnElements") && optionsIn.get("returnElements").equals("all")) {
			getAllChannels = true;
		}

		ArrayList<AbstractWitsmlObject> logs = new ArrayList<>();
		for (String uuid : foundUuids){
			// Note never get data for a log search
			String fullLog =  getFromStoreRestCalls(witsmlObject,client,uuid,username,password,exchangeID, false, getAllChannels);
			DotTranslator.translateQueryResponse(witsmlObject, fullLog, optionsIn);
			logs.add(DotTranslator.translateQueryResponse(witsmlObject, fullLog, optionsIn));
		}
		return logs;

	}



	/**
	 *
	 * @param wmlObject - trajectory or log child of the wellbore in question
	 * @param exchangeID - string ID for logging
	 * @param client - DotClient instance to use for sending API requests
	 * @param username - string username for basic client auth
	 * @param password - string password for basic client auth
	 *
	 * @return - string Uuid of the wellbore parent of wmlObject
	 *
	 * @throws ValveException
	 * @throws ValveAuthException
	 * @throws UnirestException
	 */
	private String getParentWellboreUUID(
			AbstractWitsmlObject wmlObject,
			String exchangeID,
			DotClient client,
			String username,
			String password )
			throws ValveException, ValveAuthException, UnirestException
	{
		// validate wmlObject
		String objectType = wmlObject.getObjectType();
		if ( !( "log".equals(objectType) ||
				"trajectory".equals(objectType) ||
				"fluidsreport".equals(objectType)))
			throw new ValveException("object type <" + objectType + "> does not have a parent wellbore");

		// see if the uuid is stored in the uid/uuid cache
		if (null != UidUuidCache.getUuid(wmlObject.getParentUid(), wmlObject.getGrandParentUid()))
			return UidUuidCache.getUuid(wmlObject.getParentUid(), wmlObject.getGrandParentUid());


		// wellbore uuid not found in cache. Proceed to fetch it
		String query;
		try {
			query = GraphQLQueryConverter.getWellboreAndWellUuidQuery(wmlObject);
		} catch (Exception ex) {
			throw new ValveException(ex.getMessage());
		}

		// build request
		String endpoint = this.getEndpoint( "wellboresearch");
		HttpRequestWithBody request = Unirest.post(endpoint);
		request.header("Content-Type", "application/json");
		request.body(query);

		// get response
		HttpResponse<String> response = client.makeRequest(request, username, password, exchangeID);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status) {

			// get the UUID of the first wellbore in the response
			String wellboreUUID = GraphQLRespConverter.getWellboreUuidFromGraphqlResponse(new JSONObject(response.getBody()));
			if (wellboreUUID == null)
				return null;
			// cache the wellbore uuid/uid
			UidUuidCache.putInCache(wellboreUUID, wmlObject.getParentUid(), wmlObject.getGrandParentUid());

			return wellboreUUID;

		} else {
			throw new ValveException(response.getBody());
		}
	}

	/**
	 * Requests uuid associated with the uid & client
	 *
	 * @param uid
	 * @param witsmlObj
	 * @param client
	 * @param username
	 * @param password
	 * @param exchangeID
	 *
	 * @return String with uuid
	 */
	private String getUUID( String uid,
							AbstractWitsmlObject witsmlObj,
							DotClient client,
							String username,
							String password,
							String exchangeID)
			throws ValveException,
			UnirestException,
			ValveAuthException
	{

		String uidWellbore;
		String uidWellLog;
		String uuid = "";
		String endpoint = "";
		HttpRequest logRequest = null;

		endpoint = this.getEndpoint("channelsetuuid");
		logRequest = Unirest.get(endpoint);
		logRequest.header("accept", "application/json");
		if ("1.4.1.1".equals(witsmlObj.getVersion())) {
			uidWellbore = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObj).getUidWellbore();
			uidWellLog = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObj).getUidWell();
		} else {
			uidWellbore = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj).getUidWellbore();
			uidWellLog = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj).getUidWell();
		}
		logRequest.queryString("uid", uid);
		logRequest.queryString("uidWellbore", uidWellbore);
		logRequest.queryString("uidWell", uidWellLog);
		HttpResponse<String> response;
		response = client.makeRequest(logRequest, username, password, exchangeID);
		if (response.getBody().isEmpty()) {
			throw new ValveException("No log found.");
		}
		JSONObject responseJson = new JSONObject(response.getBody());
		if (!responseJson.has("uuid"))
			return null;
		uuid = responseJson.getString("uuid");
		return uuid;
	}

	/**
	 * Requests uuid associated with the uid & client
	 *
	 * @param uid
	 * @param witsmlObj
	 * @param client
	 * @param username
	 * @param password
	 * @param exchangeID
	 *
	 * @return String uuid NULL if a UUID can be obtained;
	 * 					   otherwise, it is the UUID
	 */
	private String getUUIDFR( String uid,
							  AbstractWitsmlObject witsmlObj,
							  DotClient client,
							  String username,
							  String password,
							  String exchangeID)
			throws ValveException,
			UnirestException,
			ValveAuthException
	{

		String uidWellbore;
		String uidWellfr;
		String uuid = "";
		String endpoint = "";
		HttpRequest logRequest = null;

		endpoint = this.getEndpoint("fluidsreportidentities");
		logRequest = Unirest.get(endpoint);
		logRequest.header("accept", "application/json");
		if ("1.4.1.1".equals(witsmlObj.getVersion())) {
			uidWellbore = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReport) witsmlObj).getUidWellbore();
			uidWellfr = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReport) witsmlObj).getUidWell();
		} else {
			uidWellbore = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReport) witsmlObj).getUidWellbore();
			uidWellfr = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReport) witsmlObj).getUidWell();
		}
		logRequest.queryString("uid", uid);
		logRequest.queryString("uidWellbore", uidWellbore);
		logRequest.queryString("uidWell", uidWellfr);
		HttpResponse<String> response;
		response = client.makeRequest(logRequest, username, password, exchangeID);
		if (response.getBody().isEmpty() || response.getStatus() == 404) {
			//throw new ValveException("No fluids report data found.");
			return null;
		}
		JSONObject responseJson = new JSONObject(response.getBody());
		if (!responseJson.has("uuid"))
			return null;
		uuid = responseJson.getString("uuid");
		return uuid;
	}

	/**
	 * Makes all PATCH REST calls to the DoT API for Add/Update.
	 *
	 * @param request
	 * @param payload
	 * @param exchangeID
	 * @param username
	 * @param password
	 * @param client
	 *
	 * @return HttpResponse<String></String>
	 *
	 * @throws ValveException
	 * @throws UnirestException
	 * @throws ValveAuthException
	 */
	private HttpResponse<String> makeRESTCalls4Objects(
			HttpRequestWithBody request,
			String payload,
			String exchangeID,
			String username,
			String password,
			DotClient client )
			throws ValveException,
			UnirestException,
			ValveAuthException
	{
		HttpResponse<String> response;
		request.header( "Content-Type", "application/json" );
		request.body(payload);

		response = client.makeRequest(
				request,
				username,
				password,
				exchangeID );
		int status = response.getStatus();
		if (201 != status && 200 != status && 202 != status) {
			throw new ValveException(response.getBody());
		}
		return response;
	}

}