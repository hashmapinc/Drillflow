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
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory;
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
import org.json.JSONObject;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DotDelegator {
	private static final Logger LOG = Logger.getLogger(DotDelegator.class.getName());

	private final String WELLBORE_OBJECT = "wellbore";
	private final String TRAJECTORY_OBJECT = "trajectory";
	private final String LOG_OBJECT = "log";
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
	private final String LOG_PATH;
	private final String LOG_CHANNEL_PATH;
	private final String LOG_CHANNELSET_METADATA;
	private final String LOG_CHANNELSET_UUID;
	private final String LOG_CHANNELS;
	private final String LOG_CHANNEL_DATA;
	private final String LOG_MNEMONIC_PATH;
	private final String LOG_DEPTH_PATH;
	private final String LOG_TIME_PATH;


	private final int CREATE_CS_LOG = 1;
	private final int CREATE_CHANNELS_LOG = 2;
	private final int CREATE_DATA_LOG = 3;

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
		this.LOG_PATH = config.get("log.channelset.path");
		this.LOG_CHANNEL_PATH = config.get("log.channel.path");
		this.LOG_CHANNELSET_METADATA = config.get("log.channelset.metadata.path");
		this.LOG_CHANNELSET_UUID = config.get("log.channelset.uuid.path");
		this.LOG_CHANNELS = config.get("log.channels.path");
		this.LOG_CHANNEL_DATA = config.get("log.channels.data.path");
		this.LOG_MNEMONIC_PATH = config.get("log.mnemonic.data.path");
		this.LOG_DEPTH_PATH = config.get("log.channel.depthData.path");
		this.LOG_TIME_PATH = config.get("log.channel.timeData.path");

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
		case LOG_OBJECT:
			endpoint = this.LOG_PATH;
			break;
		case "logChannel":
			endpoint = this.LOG_CHANNEL_PATH;
			break;
		case "channelsetmetadata":
			endpoint = this.LOG_CHANNELSET_METADATA;
			break;
		case CHANNELSET_UUID:
			endpoint = this.LOG_CHANNELSET_UUID;
			break;
		case "channels":
			endpoint = this.LOG_CHANNELS;
			break;
		case "channelData":
			endpoint = this.LOG_CHANNEL_DATA;
			break;
		case "logMenmonicPath":
			endpoint = this.LOG_MNEMONIC_PATH;
			break;
		case "logDepthPath":
				endpoint = this.LOG_DEPTH_PATH;
				break;
		case "logTimePath":
				endpoint = this.LOG_TIME_PATH;
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
				uuid = getUUID(uid,witsmlObj,client,username,password);
				if (uuid == null){
					throw new ValveException("Not Found", (short)-433);
				}
				// Build Request for Get Channels
				channelsEndPoint = this.getEndpoint("channels");
				channelsRequest = Unirest.get(channelsEndPoint);
				channelsRequest.header("accept", "application/json");
				channelsRequest.queryString("channelSetUuid", uuid);
				// get response
				channelsResponse = client.makeRequest(channelsRequest, username, password);
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
				HttpResponse<String> logMnemoniceResponse = client.makeRequest(logMnemonicRequest, username, password);
				int mnemonicDeleteStatus = logMnemoniceResponse.getStatus();
				if (204 == mnemonicDeleteStatus) {
					LOG.info(ValveLogging.getLogMsg(exchangeID,
							logResponse(logMnemoniceResponse, "Successfully Element Deleted Object with UID :" + uid + "."), witsmlObj));
				} else {
					LOG.warning(
							ValveLogging.getLogMsg(exchangeID, logResponse(response, "Unable to delete"), witsmlObj));
					throw new ValveException("DELETE DoT REST call failed with status code: " + mnemonicDeleteStatus);
				}
			}else{
				uuid = getUUID(uid,witsmlObj,client,username,password);
				String logDeletEndpoint = this.getEndpoint("channelsetmetadata");
				logDeletEndpoint = logDeletEndpoint + "/" + uuid;
				HttpRequest logDeleteRequest = Unirest.delete(logDeletEndpoint).header("Content-Type",
						"application/json");
				HttpResponse<String> logDeleteResponse = client.makeRequest(logDeleteRequest, username, password);
				int deleteStatus = logDeleteResponse.getStatus();
				if (204 == deleteStatus) {
					LOG.info(ValveLogging.getLogMsg(exchangeID,
							logResponse(logDeleteResponse, "Successfully Deleted Object with UID :" + uid + "."), witsmlObj));
				} else {
					LOG.warning(
							ValveLogging.getLogMsg(exchangeID, logResponse(response, "Unable to delete"), witsmlObj));
					throw new ValveException("DELETE DoT REST call failed with status code: " + deleteStatus);
				}
			}

		} else {
			response = client.makeRequest(request, username, password);
			int status = response.getStatus();
			if (201 == status || 200 == status || 204 == status) {
				LOG.info(ValveLogging.getLogMsg(exchangeID,
						logResponse(response, "Successfully Deleted Object with UID :" + uid + "."), witsmlObj));
			} else {
				LOG.warning(ValveLogging.getLogMsg(exchangeID, logResponse(response, "Unable to delete"), witsmlObj));
				throw new ValveException("DELETE DoT REST call failed with status code: " + status);
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
				uuid = getUUID(uid,witsmlObj,client,username,password);
				if (uuid == null){
					throw new ValveException("Not Found", (short)-433);
				}
				String logElementDeletEndpoint = this.getEndpoint("channelsetmetadata");
				logElementDeletEndpoint = logElementDeletEndpoint + "/" + uuid;
				request = Unirest.patch(logElementDeletEndpoint).header("Content-Type", "application/json");
				payload = JsonUtil.removeEmptyArrays(new JSONObject(payload));
				request.body(payload);
				LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObj));
				response = client.makeRequest(request, username, password);
				int elementDeleteStatus = response.getStatus();
				if (201 == elementDeleteStatus || 200 == elementDeleteStatus || 204 == elementDeleteStatus) {
					LOG.info(ValveLogging.getLogMsg(exchangeID,
							logResponse(response, "Successfully Element Deleted Object with UID :" + uid + "."), witsmlObj));
				} else {
					LOG.warning(
							ValveLogging.getLogMsg(exchangeID, logResponse(response, "Unable to delete"), witsmlObj));
					throw new ValveException("DELETE DoT REST call failed with status code: " + elementDeleteStatus);
				}

		} else {
				request = Unirest.patch(endpoint).header("Content-Type", "application/json");
				payload = JsonUtil.removeEmptyArrays(new JSONObject(payload));
				request.body(payload);
				LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObj));
				response = client.makeRequest(request, username, password);
				int status = response.getStatus();
				if (201 == status || 200 == status || 204 == status) {
					LOG.info(ValveLogging.getLogMsg(exchangeID,
							logResponse(response, "Successfully Patched Object with UID :" + uid + "."), witsmlObj));
				} else {
					LOG.warning(ValveLogging.getLogMsg(exchangeID, logResponse(response, "Unable to delete"), witsmlObj));
					throw new ValveException("PATCH DoT REST call failed with status code: " + status);
				}
		}
	}

	public void updateObject(AbstractWitsmlObject witsmlObj,
							 String username,
							 String password,
							 String exchangeID,
							 DotClient client)
											throws ValveException,
												   ValveAuthException,
												   UnirestException
	{
		String uid = witsmlObj.getUid();
		String objectType = witsmlObj.getObjectType();
		String version = witsmlObj.getVersion();
		ChannelSet cs;

		// get object as payload string
		String payload;
		if ("1.4.1.1".equals(version)) {
			payload = witsmlObj.getJSONString("1.4.1.1");
		} else {
			payload = witsmlObj.getJSONString("1.3.1.1");
		}
		payload = JsonUtil.removeEmpties(new JSONObject(payload));

		// build the request(s) separating out log from the other types
		if (LOG_OBJECT.equals(objectType)) {
			String channelSetEndpoint;
			String channelsEndpoint;
			String dataEndpoint;
			String uuid;
			HttpRequestWithBody channelSetRequest;
			HttpRequestWithBody channelsRequest;
			HttpRequestWithBody channelDataRequest;

			// a log will derive its payloads from "payload":
			// 		channelSet, channels, and data
			String channelSetPayload;
			String channelPayload;
			String dataPayload;

			// TODO Redis -- caching Wrapper for Traj -- to prepare for caching
			//               went looking, but couldn't find this -- where is it?
			uuid = getUuid(witsmlObj, uid, client, username, password);
			if (uuid == null){
				throw new ValveException("Not Found", (short)-433);
			}

			// TODO check if there is anything to update with Redis cache
			//		for the payload object with cache object

			// get up to three (3) payloads for log
			String[] payloads = getPayloads4Log(version, payload, witsmlObj);
			channelSetPayload = payloads[CS_IDX_4_PAYLOADS];
			channelPayload = payloads[CHANNELS_IDX_4_PAYLOADS];
			dataPayload = payloads[DATA_IDX_4_PAYLOADS];

			if (channelSetPayload!=null && !channelSetPayload.isEmpty()) {
				// ************************* CHANNELSET *************************
				// check if channelSet is in cache (not a granular search, but
				// against the channelSet in its entirely)
				// boolean bypass = false;
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
				// .../witsml/channelSets/{uuid}
				channelSetEndpoint = this.getEndpoint(objectType);
				channelSetEndpoint = channelSetEndpoint + "/{" + uuid + "}";
				channelSetRequest = Unirest.patch(channelSetEndpoint);
				// if any response other than success (status code 200 or 201),
				// this method will throw an error (stopping the chain of
				// REST calls)
				makeRequests4Log( channelSetRequest,
								  channelSetPayload,
								  witsmlObj,
								  exchangeID,
								  username,
								  password,
								  client );
			}

			// TODO need to catch channelPayload = "[]"--check that it does now
			// TODO what about nulls within the payloads?
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
				makeRequests4Log( channelsRequest,
								  channelPayload,
								  witsmlObj,
								  exchangeID,
								  username,
								  password,
								  client );
			}

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
				makeRequests4Log( channelDataRequest,
								  dataPayload,
								  witsmlObj,
								  exchangeID,
								  username,
								  password,
								  client );
			}

		// all other (non-log) types
		} else {
			String endpoint = this.getEndpoint(objectType) + uid;
			HttpRequestWithBody request = Unirest.put(endpoint);
			HttpResponse<String> response;
			request.header("Content-Type", "application/json");
			if (payload.length() > 0) {
				request.body(payload);

				// add query string params
				addQueryStringParams(objectType, request, witsmlObj);

				LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObj));

				// make the UPDATE call
				response = client.makeRequest(request, username, password);

				// check response status
				int status = response.getStatus();
				if (201 == status || 200 == status) {
					LOG.info(ValveLogging.getLogMsg(exchangeID,
							logResponse( response,
									"UPDATE for " + witsmlObj + " was successful"),
							witsmlObj) );
				} else {
					LOG.warning(ValveLogging.getLogMsg(exchangeID,
							logResponse( response,
									"Received failure status code from DoT PUT"),
							witsmlObj) );
					throw new ValveException(response.getBody());
				}
			}
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
						   String password )
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
			uuid = getUUID(uid, witsmlObj, client, username, password);
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
	 *                   method
	 * @param client     - DotClient to execute requests with
	 *
	 * @throws ValveException
	 * @throws ValveAuthException
	 * @throws UnirestException
	 *
	 * @return String uid of object successfully submited to DoT rest API
	 * 				       for creation
	 * 
	 * Suggestions:
	 * -- Rename getrestcalls to something more log specific
	 * -- make getrestcalls async
	 */
	public String createObject( AbstractWitsmlObject witsmlObj,
								String username,
								String password,
								String exchangeID,
								DotClient client)
													throws ValveException,
														   ValveAuthException,
														   UnirestException
	{
		String objectType = witsmlObj.getObjectType();

		if ("log".equals(objectType)) {
			// log's testability is sufficiently complex that it will be handled differently
			// than the other objects
			String response = createLogObject(witsmlObj, username, password, exchangeID, client);
			return response;
		}

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
			request.queryString("uidWell", witsmlObj.getParentUid()); // TODO: error handle this?
		} else if ("trajectory".equals(objectType)) {
			request.queryString("uidWellbore", witsmlObj.getParentUid());
			request.queryString("uidWell", witsmlObj.getGrandParentUid());
		}

		// add the header and payload
		request.header("Content-Type", "application/json");
		request.body(payload);

		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObj));

		// get the request's response
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status) {
			LOG.info(ValveLogging.getLogMsg( exchangeID,
									         logResponse(response,
													     "Received successful " +
																 "status code from DoT create call"),
											 witsmlObj) );
			return (null == uid || uid.isEmpty()) ?
					new JsonNode(response.getBody()).getObject().getString("uid") :
					uid;
		} else {
			LOG.warning(ValveLogging.getLogMsg(exchangeID,
					logResponse( response,
							    "Received " + status +
										" from DoT POST" + response.getBody()),
								 witsmlObj));
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

		// get WITSML abstract object as JSON string
		String payload = ("1.4.1.1".equals(version) ? witsmlObj.getJSONString("1.4.1.1") :
													  witsmlObj.getJSONString("1.3.1.1") );

		// separate out from the payload the sub-payloads for
		// 		ChannelSet (CS_IDX_4_PAYLOADS),
		// 		Channels (CHANNELS_IDX_4_PAYLOADS),
		// 		and Data (DATA_IDX_4_PAYLOADS)
		String[] allPayloads = getPayloads4Log( version,
												payload,
												witsmlObj );

		// set-up for channel set creation..
		// ... endpoint url: /channelSets
		endpoint = this.getEndpoint(objectType);
		// ... parameters for url
		requestParams = new HashMap<>();
		requestParams.put("uid", uid);
		requestParams.put("uidWellbore", witsmlObj.getParentUid());
		requestParams.put("uidWell", witsmlObj.getGrandParentUid());


		// call a central method to finish the REST set-up
		// and execute the rest call for ChannelSet
		response = performRestCall( CREATE_CS_LOG,
								    allPayloads[CS_IDX_4_PAYLOADS],
									endpoint,
									requestParams,
									client,
									username,
									password,
									witsmlObj,
									exchangeID );

		// check response status; nullpointer during mocking....why?
		int status = response.getStatus();
		if (409 == status) {
			LOG.info( ValveLogging.getLogMsg( exchangeID,
										      logResponse( response,
													      "Log already in store" ),
											  witsmlObj) );
			throw new ValveException("Log already in store", (short) -405);
		}

		// actually a 201...
		// TODO Verify that it is acceptable to allow 200 for testing purposes...check traj stuff
		if (201 == status || 200 == status) {
			LOG.info(ValveLogging.getLogMsg(exchangeID,
					logResponse(response,
								"Received successful "
									+ "status code from DoT create call"),
					witsmlObj));
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
			// add channels to an existing ChannelSet
			if (!(allPayloads[CHANNELS_IDX_4_PAYLOADS].isEmpty())) {

				// build the channelSetRequest...
				// endpoint: .../channels/metadata?channelSetUuid={channelSetUuid}
				endpoint = this.getEndpoint(objectType + "Channel");
				endpoint = endpoint + "/metadata";
				// get the uuid for the channelSet just created from the response
				String uuid4CS = new JsonNode(response.getBody())
																 .getObject()
																 .getString("uuid");

				requestParams = new HashMap<>();
				requestParams.put("channelSetUuid", uuid4CS);
				// call a central method to finish the REST set-up
				// and execute the rest call for ChannelSet
				response = performRestCall( CREATE_CHANNELS_LOG,
											allPayloads[CHANNELS_IDX_4_PAYLOADS],
											endpoint,
											requestParams,
											client,
											username,
											password,
											witsmlObj,
											exchangeID );

				// check response status
				status = response.getStatus();
				if (200 == status) {
					LOG.info(ValveLogging.getLogMsg( exchangeID,
														logResponse(response,
																   "Received successful " +
																		   "status code from DoT create call"),
																    witsmlObj));
					// TODO: cache the channels

					// .../channels/data?channelSetUuid={channelSetUuid}
					endpoint = this.getEndpoint("channelData");
					// Use same requestParams as for the previous call (channelSetUuid={channelSetUuid})
					// call a central method to finish the REST set-up
					// and execute the rest call for ChannelSet
					response = performRestCall( CREATE_DATA_LOG,
											    allPayloads[DATA_IDX_4_PAYLOADS],
												endpoint,
												requestParams,
												client,
												username,
												password,
												witsmlObj,
												exchangeID );
					// check response status
					status = response.getStatus();
					// actually this requires a 200...
					if (200 == status) {
						LOG.info(ValveLogging.getLogMsg( exchangeID,
														 logResponse(response,
											   						"Received successful "
																			+ "status code from DoT add data call"),
																	 witsmlObj));
					} else {
						LOG.warning(ValveLogging.getLogMsg(exchangeID,
								logResponse(response, "Received " + status + " from DoT POST" + response.getBody()), witsmlObj));
						throw new ValveException(response.getBody());
					}
				}

			}
			return (null == uid || uid.isEmpty()) ? new JsonNode(response.getBody()).getObject().getString("uid") :
												    uid;
		} else {
			LOG.warning(ValveLogging.getLogMsg(exchangeID,
					logResponse( response,
							 	"Received " + status +
										" from DoT POST" + response.getBody()),
								 witsmlObj));
			throw new ValveException(response.getBody());
		}
	}

	/**
	 * Perform a REST call.
	 *
	 * @param payload
	 * @param endpoint
	 * @param requestParams
	 * @param client
	 * @param username
	 * @param password
	 * @return response to the REST call
	 * @throws ValveAuthException
	 * @throws UnirestException
	 * @throws ValveException
	 */
	public HttpResponse<String> performRestCall( int createWhichPartOfLog,
												 String payload,
												 String endpoint,
												 HashMap<String,String> requestParams,
												 DotClient client,
												 String username,
												 String password,
												 AbstractWitsmlObject witsmlObj,
												 String exchangeID )
														throws ValveAuthException,
															   UnirestException,
															   ValveException {

		// create with POST and generate uid
		// CS endpoint:    .../channelSets?uid={uid}&uidWellbore={uidWellbore}&uidWell={uidWell}
		// Channels endpt: .../channels/metadata?channelSetUuid={channelSetUuid}
		// Data endpoint:  .../channels/data?channelSetUuid={channelSetUuid}
		HttpRequestWithBody request = Unirest.post(endpoint);
		request.header("Content-Type", "application/json");
		request.body(payload);
		// place the request parameters, if any, into the request
		if (!requestParams.isEmpty()) {
			requestParams.forEach(
				(key, value) -> { request.queryString(key, value); }
			);
		}

		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObj));

		// return the response
		HttpResponse<String> response = client.makeRequest(request, username, password);
		return response;

	}

	/**
	 * creates an array of payloads:
	 * 		(1) creates the POJOs for channelSet & channels and then uses them to return
	 * 		    array entries for their respective payloads based on the POJOs
	 * 		(2) for the data payload, it simply converts it accordingly for the version
	 * 	    	and then also returns that payload in the array
	 *
	 * @param version	either 1.4.1.1 or 1.3.1.1
	 * @param payload	JSON String with empties removed (important since witsmlObj
	 *                      may contain null) & correct for the version
	 * @param witsmlObj WITSML XML in JSON format
	 *
	 * @return String 	array representing the payloads (channelSet, channels, & data)
	 *
	 * @throws ValveException
	 */
	public String[] getPayloads4Log(String version,
									String payload,
									AbstractWitsmlObject witsmlObj)
												throws ValveException {
		String[] payloads = new String[3];
		JSONObject payloadJSON = new JSONObject(payload);

		try {
			// ****************************************** CHANNEL SET ******************************************
			if (payload.contains("name")) {
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
			if (payloadJSON.has("logCurveInfo")) {
				switch (version) {
					case "1.3.1.1":
						payloads[CHANNELS_IDX_4_PAYLOADS] = Channel.channelListToJson(
								Channel.from1311((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj));
						break;
					case "1.4.1.1":
						payloads[CHANNELS_IDX_4_PAYLOADS] = Channel.channelListToJson(
								Channel.from1411((ObjLog)witsmlObj));
						break;
					default:
						payloads[CHANNELS_IDX_4_PAYLOADS] = "";
						break;
				}
			} else {
				payloads[CHANNELS_IDX_4_PAYLOADS] = "";
			}

			// ********************************************* DATA **********************************************
			if (payload.contains("logData")) {
				switch (version) {
					case "1.3.1.1":
						payloads[DATA_IDX_4_PAYLOADS] = DotLogDataHelper.convertDataToDotFrom1311(
								(com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj);
						break;
					case "1.4.1.1":
						payloads[DATA_IDX_4_PAYLOADS] = DotLogDataHelper.convertDataToDotFrom1411(
								(ObjLog) witsmlObj);
						break;
					default:
						payloads[DATA_IDX_4_PAYLOADS] = "";
						break;
				}
			} else {
				payloads[DATA_IDX_4_PAYLOADS] = "";
			}
		} catch (JsonProcessingException e) {
			throw new ValveException("Error converting Log to ChannelSet/Channels/Data");
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
	 * @return get results AbstractWitsmlObject
	 */
	public AbstractWitsmlObject getObject(AbstractWitsmlObject witsmlObject, String username, String password,
										  String exchangeID, DotClient client, Map<String, String> optionsIn)
			throws ValveException, ValveAuthException, UnirestException, JAXBException {
		String uid = witsmlObject.getUid();
		String objectType = witsmlObject.getObjectType();
		String endpoint = "";
		String uuid = "";
		String finalResponse = null;

		endpoint = this.getEndpoint(objectType) + uid; // add uid for rest call
		String version = witsmlObject.getVersion();
		// build request
		HttpRequest request = Unirest.get(endpoint);
		request.header("accept", "application/json");
		if ("wellbore".equals(objectType)) {
			request.queryString("uidWell", witsmlObject.getParentUid()); // TODO: check the parent uid exists?
		}else if ("trajectory".equals(objectType)) {
			request.queryString("uidWellbore", witsmlObject.getParentUid());
			String uidWell;
			if ("1.4.1.1".equals(version)) {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) witsmlObject).getUidWell();
			} else {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory) witsmlObject).getUidWell();
			}
			request.queryString("uidWell", uidWell);
		}
		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObject));


		if ("log".equals(objectType)) {
			uuid = getUUID(uid,witsmlObject,client,username,password);
			if (uuid == null)
				return null;
			finalResponse =  getFromStoreRestCalls(witsmlObject,client,uuid,username,password,exchangeID);
			return DotTranslator.translateQueryResponse(witsmlObject, finalResponse, optionsIn);

		}else{
			// get response
			HttpResponse<String> response = client.makeRequest(request, username, password);
			int status = response.getStatus();
			if (201 == status || 200 == status) {
				return DotTranslator.translateQueryResponse(witsmlObject, response.getBody(), optionsIn);
			} else if (404 == status) {
				// handle not found. This is a valid response
				return null;
			} else {
				LOG.warning(ValveLogging.getLogMsg(witsmlObject.getUid(), logResponse(response, "Unable to execute GET"),
						witsmlObject));
				throw new ValveException(response.getBody());
			}
		}
	}

	/**
	 * All other rest calls to verify channelSetMetadata,ChannelSet,Channels,logData
	 *
	 * @param witsmlObject - AbstractWitsmlObject to get
	 * @param username     - auth username
	 * @param password     - auth password
	 * @param exchangeID   - unique string for tracking which exchange called this
	 *                     method
	 * @param client       - DotClient to execute requests with
	 */

	private String getFromStoreRestCalls(AbstractWitsmlObject witsmlObject, DotClient client, String uuid, String username,
										 String password, String exchangeID)
			throws ValveException, ValveAuthException, UnirestException, JAXBException {

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
		HttpResponse<String> channelsDepthResponse;
		ObjLog finalResponse = null;
		String data ="";
		String payload="";
		String indexType="";
		String channelPayload="";

		// Build Request for Get ChannelSet Metadata
		channelsetmetadataEndpoint = this.getEndpoint("channelsetmetadata");
		channelsetmetadataEndpoint = channelsetmetadataEndpoint + "/" + uuid;
		channelsetmetadataRequest = Unirest.get(channelsetmetadataEndpoint);
		channelsetmetadataRequest.header("accept", "application/json");
		// get response
		channelsetmetadataResponse = client.makeRequest(channelsetmetadataRequest, username, password);
		// Build Request for Get All ChannelSet
		channelsetuuidEndpoint = this.getEndpoint("log");
		channelsetuuidRequest = Unirest.get(channelsetuuidEndpoint);
		channelsetuuidRequest.header("accept", "application/json");
		channelsetuuidRequest.queryString("containerId", uuid);
		// get response
		allChannelSet = client.makeRequest(channelsetuuidRequest, username, password);

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
		// get response
		channelsResponse = client.makeRequest(channelsRequest, username, password);

		List<Channel> channels = Channel.jsonToChannelList(channelsResponse.getBody());
		// Build Request for Get Channels Depth
		if(indexType.equals("depth")){
			String sortDesc = "true";
			data = DotLogDataHelper.convertChannelDepthDataToDotFrom(channels,uuid,sortDesc);
			// create with POST
			channelsDepthEndPoint = this.getEndpoint("logDepthPath");
			channelsDepthRequest = Unirest.post(channelsDepthEndPoint);
			channelsDepthRequest.header("Content-Type", "application/json");
			channelsDepthRequest.body(data);
			// get the request response.
			channelsDepthResponse = client.makeRequest(channelsDepthRequest, username, password);
		}else{
			String sortDesc = "true";
			data = DotLogDataHelper.convertChannelDepthDataToDotFrom(channels,uuid,sortDesc);
			// create with POST
			channelsDepthEndPoint = this.getEndpoint("logTimePath");
			channelsDepthRequest = Unirest.post(channelsDepthEndPoint);
			channelsDepthRequest.header("Content-Type", "application/json");
			channelsDepthRequest.body(data);
			// get the request response.
			channelsDepthResponse = client.makeRequest(channelsDepthRequest, username, password);
		}

		if (201 == channelsetmetadataResponse.getStatus() || 200 == channelsetmetadataResponse.getStatus()
				|| 200 == allChannelSet.getStatus() || 201 == allChannelSet.getStatus()
				|| 200 == channelsResponse.getStatus() || 201 == channelsResponse.getStatus()) {
			try {
				String wellSearchEndpoint = this.getEndpoint("wellsearch");
				String wellBoreSearchEndpoint = this.getEndpoint("wellboresearch");
				finalResponse = LogConverterExtended.convertDotResponseToWitsml(wellSearchEndpoint,wellBoreSearchEndpoint,client,username,password,exchangeID, witsmlObject,allChannelSet.getBody(),
						channelsResponse.getBody(),channelsDepthResponse.getBody());
			} catch (Exception e) {
				LOG.info(ValveLogging.getLogMsg(
						exchangeID,
						logResponse(channelsResponse, "Error converting dot response for log: " + e.getMessage()),
						witsmlObject));
			}
			LOG.info(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(channelsResponse, "Successfully executed GET for query object=" + witsmlObject.toString()),
					witsmlObject
			));
		} else {
			LOG.warning(ValveLogging.getLogMsg(
					witsmlObject.getUid(),
					logResponse(channelsResponse, "Unable to execute GET"),
					witsmlObject
			));
			throw new ValveException(channelsResponse.getBody());
		}
		if (finalResponse != null)
			return finalResponse.getJSONString("1.4.1.1");
		else
			return null;
	}

	/**
	 * Submits a search query to the DoT rest API for object GETing
	 *
	 * @param witsmlObject - AbstractWitsmlObject to get
	 * @param username - auth username
	 * @param password - auth password
	 * @param exchangeID - unique string for tracking which exchange called this method
	 * @param client - DotClient to execute requests with
	 * @return get results AbstractWitsmlObject
	 */
	public ArrayList<AbstractWitsmlObject> search(
			AbstractWitsmlObject witsmlObject,
			String username,
			String password,
			String exchangeID,
			DotClient client,
			Map<String, String> optionsIn
	) throws ValveException, ValveAuthException, UnirestException, IOException, DatatypeConfigurationException {
		String objectType = witsmlObject.getObjectType();
		String endpoint = this.getEndpoint(objectType + "search");

		String query;
		try {
			//TODO: Shift to does not = well || wellbore
			if ("trajectory".equals(objectType)) {
				String wellboreUuid = getParentWellboreUUID(witsmlObject, exchangeID, client, username, password);
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
		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObject));

		// get response
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status || 400 == status) {
			LOG.info(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "Successfully executed POST for query object=" + witsmlObject.toString()),
					witsmlObject
			));

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
			LOG.warning(ValveLogging.getLogMsg(
					witsmlObject.getUid(),
					logResponse(response, "Unable to execute POST"),
					witsmlObject
			));
			throw new ValveException(response.getBody());
		}
	}

	/**
	 *
	 * @param wmlObject - trajectory or log child of the wellbore in question
	 * @param exchangeID - string ID for logging
	 * @param client - DotClient instance to use for sending API requests
	 * @param username - string username for basic client auth
	 * @param password - string password for basic client auth
	 * @return - string Uuid of the wellbore parent of wmlObject
	 * @throws ValveException
	 * @throws ValveAuthException
	 * @throws UnirestException
	 */
	private String getParentWellboreUUID(
			AbstractWitsmlObject wmlObject,
			String exchangeID,
			DotClient client,
			String username,
			String password
	) throws ValveException, ValveAuthException, UnirestException {
		// validate wmlObject
		String objectType = wmlObject.getObjectType();
		if (!("log".equals(objectType) || "trajectory".equals(objectType)))
			throw new ValveException("object type <" + objectType + "> does not have a parent wellbore");

		// see if the uuid is stored in the uid/uuid cache
		if (null != UidUuidCache.getUuid(wmlObject.getParentUid(), wmlObject.getGrandParentUid()))
			return UidUuidCache.getUuid(wmlObject.getParentUid(), wmlObject.getGrandParentUid());


		// wellbore uuid not found in cache. Proceed to fetch it
		String query;
		try {
			query = GraphQLQueryConverter.getWellboreAndWellUuidQuery(wmlObject);
			LOG.fine(ValveLogging.getLogMsg(
					exchangeID,
					System.lineSeparator() + "Graph QL Query: " + query,
					wmlObject)
			);
		} catch (Exception ex) {
			throw new ValveException(ex.getMessage());
		}

		// build request
		String endpoint = this.getEndpoint( "wellboresearch");
		HttpRequestWithBody request = Unirest.post(endpoint);
		request.header("Content-Type", "application/json");
		request.body(query);
		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), wmlObject));

		// get response
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status || 400 == status) {
			LOG.info(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "Successfully executed POST for query object=" + wmlObject.toString()),
					wmlObject
			));

			// get the UUID of the first wellbore in the response
			String wellboreUUID = GraphQLRespConverter.getWellboreUuidFromGraphqlResponse(new JSONObject(response.getBody()));

			// cache the wellbore uuid/uid
			UidUuidCache.putInCache(wellboreUUID, wmlObject.getParentUid(), wmlObject.getGrandParentUid());

			return wellboreUUID;

		} else {
			LOG.warning(ValveLogging.getLogMsg(
					wmlObject.getUid(),
					logResponse(response, "Unable to execute POST"),
					wmlObject
			));
			throw new ValveException(response.getBody());
		}
	}

	/**
	 * Builds logging for Http Request
	 *
	 * @param request - HttpRequest
	 * @return String - built request for logging
	 */
	private String logRequest(HttpRequest request) {
		StringBuilder requestString = new StringBuilder();
		requestString.append("===========================request begin================================================" + System.lineSeparator());
		requestString.append("URI         : " + request.getUrl() + System.lineSeparator());
		requestString.append("Method      : " + request.getHttpMethod() + System.lineSeparator());
		requestString.append("Headers     : " + request.getHeaders() + System.lineSeparator());
		requestString.append("==========================request end================================================" + System.lineSeparator());
		return String.valueOf(requestString);
	}

	/**
	 * Builds the final response string to include status code, status text,
	 * headers, response message (customized), & response body in a uniform format
	 *
	 * @param response - the response to finalize
	 * @param customResponseMessage - customized portion of the response
	 * @return String - built response for logging
	 */
	private String logResponse(HttpResponse<String> response, String customResponseMessage) {
		StringBuilder responseString = new StringBuilder();
		responseString.append("============================response begin==========================================" + System.lineSeparator());
		responseString.append("Status code  : " + response.getStatus() + System.lineSeparator());
		responseString.append("Status text  : " + response.getStatusText() + System.lineSeparator());
		responseString.append("Headers      : " + response.getHeaders() + System.lineSeparator());
		responseString.append("Response Message      : " + customResponseMessage + System.lineSeparator());
		responseString.append("Response body: " + response.getBody() + System.lineSeparator());
		responseString.append("============================response end==========================================" + System.lineSeparator());
		return String.valueOf(responseString);
	}

	/**
	 * Requests uuid associated with the uid & client
	 * @param uid
	 * @param witsmlObj
	 * @param client
	 * @param username
	 * @param password
	 * @return uuid
	 */
	private String getUUID( String uid,
							AbstractWitsmlObject witsmlObj,
							DotClient client,
							String username,
							String password )
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
		response = client.makeRequest(logRequest, username, password);
		if (response.getBody().isEmpty()) {
			throw new ValveException("No log found.");
		}
		JSONObject responseJson = new JSONObject(response.getBody());
		if (!responseJson.has("uuid"))
			return null;
		uuid = responseJson.getString("uuid");
		return uuid;
	}

	private void makeRequests4Log( HttpRequestWithBody request,
								   String payload,
								   AbstractWitsmlObject witsmlObj,
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

		LOG.info(ValveLogging.getLogMsg( exchangeID,
										 logRequest(request),
										 witsmlObj) );
		response = client.makeRequest( request,
									   username,
									   password );
		// check response status (202 is the only valid response for data)
		int status = response.getStatus();
		if (201 == status || 200 == status || 202 == status) {
			LOG.info(ValveLogging.getLogMsg( exchangeID,
					logResponse(response, "UPDATE for " + witsmlObj + " was successful"),
					witsmlObj));
		} else {
			LOG.warning(ValveLogging.getLogMsg(exchangeID,
					logResponse(response, "Received failure status code from DoT PUT"),
					witsmlObj));
			throw new ValveException(response.getBody());
		}
	}
}