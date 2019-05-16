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

		endpoint = this.getEndpoint(objectType) + uid; // add uid for rest call
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
			//response = client.makeRequest(logRequest, username, password);
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

		// It is an object delete, so re-route there
		String endpoint = this.getEndpoint(objectType) + uid; // add uid for delete call
		String payload = witsmlObj.getJSONString("1.4.1.1");
		// create request
		HttpRequestWithBody request = Unirest.patch(endpoint).header("Content-Type", "application/json");
		payload = JsonUtil.removeEmptyArrays(new JSONObject(payload));
		request.body(payload);
		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObj));

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

		// make the PATCH call.
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status || 204 == status) {
			LOG.info(ValveLogging.getLogMsg(exchangeID,
					logResponse(response, "Successfully Patched Object with UID :" + uid + "."), witsmlObj));
		} else {
			LOG.warning(ValveLogging.getLogMsg(exchangeID, logResponse(response, "Unable to patch"), witsmlObj));
			throw new ValveException("PATCH DoT REST call failed with status code: " + status);
		}
	}

    public void updateObject(AbstractWitsmlObject witsmlObj,
                             String username,
                             String password,
                             String exchangeID,
                             DotClient client)
            		throws ValveException, ValveAuthException, UnirestException {
        String uid = witsmlObj.getUid();
        String objectType = witsmlObj.getObjectType();
        String version = witsmlObj.getVersion();
        String endpoint;
        String channelsEndpoint;
        String dataEndpoint;
        String uuid;

        // get object as payload string
        String payload;
        if ("1.4.1.1".equals(version)) {
            payload = witsmlObj.getJSONString("1.4.1.1");
        } else {
            payload = witsmlObj.getJSONString("1.3.1.1");
        }
        payload = JsonUtil.removeEmpties(new JSONObject(payload));

        // a log will derive its payloads from "payload":
        // channelSet, channels, and data (if v1.3.1.1)
        // TODO if this is right, circle back to check if createObject must follow suit
        JSONObject objLog;
        String channelSetPayload = "";
        String channelPayload = "";
        String dataPayload = "";                // not v1.4.1.1

        // build the requests (log potentially requires three HttpRequests)
        HttpRequestWithBody request;
        HttpRequestWithBody channelsRequest;
        HttpRequestWithBody channelDataRequest;

        HttpResponse<String> response = null;

        // build the request
        if (LOG_OBJECT.equals(objectType)) {
			// TODO Redis -- caching Wrapper for Traj -- to prepare for caching
			//               went looking, but couldn't find this -- where is it?
			// see if the uuid is stored in the uid/uuid cache
			if (null != UidUuidCache.getUuid(witsmlObj.getParentUid(),
					witsmlObj.getGrandParentUid())) {
				// TODO still need to test this path
				uuid = UidUuidCache.getUuid(witsmlObj.getParentUid(),
						witsmlObj.getGrandParentUid());
			} else {
				// make the call to get uuid, and put it in cache for next time
				uuid = getUUID(uid, witsmlObj, client, username, password);
				UidUuidCache.putInCache(
						uuid,
						uid,
						witsmlObj.getParentUid(),
						witsmlObj.getGrandParentUid());
			}
			// TODO check if there is anything to update with Redis cache

            // get up to three (3) payloads for log
            String[] payloads = getPayloads4LogUsingPayloadAndWitsmlObj(version, payload, witsmlObj);
            channelSetPayload = payloads[CS_IDX_4_PAYLOADS];
            channelPayload = payloads[CHANNELS_IDX_4_PAYLOADS];
            dataPayload = payloads[DATA_IDX_4_PAYLOADS];
            // first handle any channelSet update --
			if (channelSetPayload!=null && !channelSetPayload.isEmpty()) {
                // ************************* CHANNELSET *************************
                // .../witsml/channelSets/{uuid}
                endpoint = this.getEndpoint(objectType);
                endpoint = endpoint + "/{" + uuid + "}";
                request = Unirest.patch(endpoint);
                makeRequests4Log(request, channelSetPayload, objectType, witsmlObj,
                        exchangeID, username, password, client, response);
            }
            // second handle any channels update --
			// TODO need to catch channelPayload = "[]"
			// TODO what about nulls within the payloads?
			if (channelPayload!=null && !channelPayload.isEmpty()) {
                // ************************** CHANNELS **************************
                // .../witsml/channels/metadata?channelSetUuid={channelSetUuid}
                channelsEndpoint = this.getEndpoint("channels");
				channelsRequest = Unirest.post(channelsEndpoint);
				// add channelSetUuid={channelSetUuid} as a query parameter
				addQueryStringParams4Log(channelsRequest, uuid);
				makeRequests4Log(channelsRequest, channelPayload, objectType, witsmlObj,
						exchangeID, username, password, client, response);
            }
            // finally handle any data update...
            if (dataPayload!=null && !dataPayload.isEmpty()) {
                // **************************** DATA *****************************
                // .../witsml/channels/data?channelSetUuid={channelSetUuid}
                dataEndpoint = this.getEndpoint("channelData");
				channelDataRequest = Unirest.post(dataEndpoint);
				// add channelSetUuid={channelSetUuid} as a query parameter
				addQueryStringParams4Log(channelDataRequest, uuid);
                makeRequests4Log(channelDataRequest, dataPayload, objectType, witsmlObj,
                        exchangeID, username, password, client, response);
            }
        } else {
            endpoint = this.getEndpoint(objectType) + uid;
            request = Unirest.put(endpoint);
            // TODO reorganize this code...put the common stuff into a callable method
            //      and separate log from the rest of it
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
	 * @return
	 * 
	 * Suggestions:
	 * -- Rename getrestcalls to something more log specific
	 * -- make getrestcalls async
	 */
	public String createObject(AbstractWitsmlObject witsmlObj, String username, String password, String exchangeID,
			DotClient client) throws ValveException, ValveAuthException, UnirestException {
		String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
		String uid = witsmlObj.getUid();
		String endpoint = this.getEndpoint(objectType);
		String version = witsmlObj.getVersion();

		// get object as payload string
		String payload = witsmlObj.getJSONString("1.4.1.1");

		// a log will derive its payload for creating a ChannelSet from "payload"
		// & then use "payload" again to update the ChannelSet with Log Curve
		// information (Channel)
		JSONObject objLog;
		String channelSetPayload = "";
		String channelPayload = "";
		String data ="";

		// build the requests (log requires two HttpRequests
		HttpRequestWithBody request;
		HttpRequestWithBody channelsRequest;
		HttpRequestWithBody channelData;
		if (null == uid || uid.isEmpty() || "log".equals(objectType)) {
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
			String uidWell;
			if ("1.4.1.1".equals(version)) {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) witsmlObj).getUidWell();
			} else {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory) witsmlObj).getUidWell();
			}
			request.queryString("uidWell", uidWell);
		} else if ("log".equals(objectType)) {
			request.queryString("uid", uid);
			request.queryString("uidWellbore", witsmlObj.getParentUid());
			String uidWell;
			if ("1.4.1.1".equals(version)) {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObj).getUidWell();
			} else {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj).getUidWell();
			}
			request.queryString("uidWell", uidWell);
		}

		// add the header and payload
		request.header("Content-Type", "application/json");

		if ("log".equals(objectType)) {
			// pick up all of the payloads
            String[] payloads = getPayloads4LogUsingPayloadAndWitsmlObj(version, payload, witsmlObj);
			channelSetPayload = payloads[CS_IDX_4_PAYLOADS];
			channelPayload = payloads[CHANNELS_IDX_4_PAYLOADS];
			// TODO FIX THIS!!!!
			if (!"1.4.1.1".equals(version)) {
				data = payloads[DATA_IDX_4_PAYLOADS];
			}
			// make the first rest call with channelSet payload
			request.body(channelSetPayload);
		} else {

			request.body(payload);
		}

		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObj));

		// get the request response
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (409 == status) {
			LOG.info(ValveLogging.getLogMsg(exchangeID, logResponse(response, "Log already in store"), witsmlObj));
			throw new ValveException("Log already in store", (short) -405);
		}

		if (201 == status || 200 == status) {
			LOG.info(ValveLogging.getLogMsg(exchangeID,
					logResponse(response, "Received successful status code from DoT create call"), witsmlObj));

			// add channels to an existing ChannelSet
			if ("log".equals(objectType) && !(channelPayload.isEmpty())) {

				// build the request...
				endpoint = this.getEndpoint(objectType + "Channel");
				endpoint = endpoint + "/metadata";

				// get the uuid for the channelSet just created from the response
				String uuid4CS = new JsonNode(response.getBody()).getObject().getString("uuid");

				// create with POST
				channelsRequest = Unirest.post(endpoint);
				channelsRequest.queryString("channelSetUuid", uuid4CS);
				channelsRequest.header("Content-Type", "application/json");
				channelsRequest.body(channelPayload);

				LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(channelsRequest), witsmlObj));

				// get the request response.
				response = client.makeRequest(channelsRequest, username, password);
				// check response status
				status = response.getStatus();
				if (201 == status || 200 == status) {
					LOG.info(ValveLogging.getLogMsg(exchangeID,
							logResponse(response, "Received successful status code from DoT create call"), witsmlObj));
					String chDataEndpoint = endpoint = this.getEndpoint(objectType + "Channel");
					channelData = Unirest.post(chDataEndpoint);
					channelData.queryString("channelSetUuid", uuid4CS);
					channelData.header("Content-Type", "application/json");
					channelData.body(data);
					LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(channelsRequest), witsmlObj));

					// get the request response.
					response = client.makeRequest(channelData, username, password);
					if (201 == status || 200 == status) {
						LOG.info(ValveLogging.getLogMsg(exchangeID,
								logResponse(response, "Received successful status code from DoT add data call"), witsmlObj));
					} else {
						LOG.warning(ValveLogging.getLogMsg(exchangeID,
							logResponse(response, "Received " + status + " from DoT POST" + response.getBody()), witsmlObj));
							throw new ValveException(response.getBody());
					}
				}
			}
			return (null == uid || uid.isEmpty()) ? new JsonNode(response.getBody()).getObject().getString("uid") : uid;
		} else {
			LOG.warning(ValveLogging.getLogMsg(exchangeID,
					logResponse(response, "Received " + status + " from DoT POST" + response.getBody()), witsmlObj));
			throw new ValveException(response.getBody());
		}
	}

	/**
	 * creates the POJOs for channelSet & channels and then uses the POJOs (plus data
	 * if v1.3.1.1) to return an array of the payloads based on the POJOs (and data
	 * if v1.3.1.1)
	 *
	 * @param version	either 1.4.1.1 or 1.3.1.1
	 * @param payload	JSON String with empties removed (important since witsmlObj
	 *                      may contain null) & correct for the version
	 * @param witsmlObj abstract WITSML XML in JSON format
	 *
	 * @return String array representing the payloads (channelSet, channels, & if
	 * 					1.3.1.1 also data)
	 *
	 * @throws ValveException
	 */
	public String[] getPayloads4LogUsingPayloadAndWitsmlObj(String version,
															String payload,
															AbstractWitsmlObject witsmlObj)
															throws ValveException {
		String channelSetPayload;
		String channelPayload;
		String dataPayload;
		String[] payloads;
		JSONObject payloadJSON = new JSONObject(payload);
		try {
			if ("1.4.1.1".equals(version)) {
				payloads = new String[3];
				// if there is a channelSet, then get the payload
				if (payload.contains("name")) {
					channelSetPayload = ChannelSet
							.from1411((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObj)
							.toJson();
					payloads[CS_IDX_4_PAYLOADS] = channelSetPayload;
				} else {
					payloads[CS_IDX_4_PAYLOADS] = "";
				}
				if (payloadJSON.has("logCurveInfo")) {
					List<Channel> chanList = Channel
												.from1411((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObj);
					channelPayload = Channel.channelListToJson(chanList);
					payloads[CHANNELS_IDX_4_PAYLOADS] = channelPayload;
				} else {
					payloads[CHANNELS_IDX_4_PAYLOADS] = "";
				}
				if (payload.contains("logData")) {
                    dataPayload = DotLogDataHelper.convertDataToDotFrom1411((ObjLog)witsmlObj);
                    payloads[DATA_IDX_4_PAYLOADS] = dataPayload;
                }
			} else {
				payloads = new String[3];
				if (payload.contains("name")) {
					channelSetPayload = ChannelSet
							.from1311((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj)
							.toJson();
					payloads[CS_IDX_4_PAYLOADS] = channelSetPayload;
				} else {
					payloads[CS_IDX_4_PAYLOADS] = "";
				}
				if (payloadJSON.has("logCurveInfo")) {
					List<Channel> chanList = Channel
							.from1311((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj);
					channelPayload = Channel.channelListToJson(chanList);
					payloads[CHANNELS_IDX_4_PAYLOADS] = channelPayload;
				} else {
					payloads[CHANNELS_IDX_4_PAYLOADS] = "";
				}
                if (payload.contains("logData")) {
                    dataPayload = DotLogDataHelper
                            .convertDataToDotFrom1311((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog)witsmlObj);
                    payloads[DATA_IDX_4_PAYLOADS] = dataPayload;
                }
			}
		} catch (JsonProcessingException e) {
			throw new ValveException("Error converting Log to ChannelSet");
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
	 * All other rest calls to verify channelSetMetadata,ChannelSet,Channels
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
		ObjLog finalResponse = null;

		// Build Request for Get ChannelSet Metadata
		channelsetmetadataEndpoint = this.getEndpoint("channelsetmetadata");
		channelsetmetadataRequest = Unirest.get(channelsetmetadataEndpoint);
		channelsetmetadataRequest.header("accept", "application/json");
		channelsetmetadataRequest.queryString("uuid", uuid);
		// get response
		channelsetmetadataResponse = client.makeRequest(channelsetmetadataRequest, username, password);
		// Build Request for Get All ChannelSet
		channelsetuuidEndpoint = this.getEndpoint("log");
		channelsetuuidRequest = Unirest.get(channelsetuuidEndpoint);
		channelsetuuidRequest.header("accept", "application/json");
		channelsetuuidRequest.queryString("containerId", uuid);
		// get response
		allChannelSet = client.makeRequest(channelsetuuidRequest, username, password);
		// Build Request for Get Channels
		channelsEndPoint = this.getEndpoint("channels");
		channelsRequest = Unirest.get(channelsEndPoint);
		channelsRequest.header("accept", "application/json");
		channelsRequest.queryString("channelSetUuid", uuid);
		// get response
		channelsResponse = client.makeRequest(channelsRequest, username, password);
		if (201 == channelsetmetadataResponse.getStatus() || 200 == channelsetmetadataResponse.getStatus()
				|| 200 == allChannelSet.getStatus() || 201 == allChannelSet.getStatus()
				|| 200 == channelsResponse.getStatus() || 201 == channelsResponse.getStatus()) {
			try {
				String wellSearchEndpoint = this.getEndpoint("wellsearch");
				String wellBoreSearchEndpoint = this.getEndpoint("wellboresearch");
				finalResponse = LogConverterExtended.convertDotResponseToWitsml(wellSearchEndpoint,wellBoreSearchEndpoint,client,username,password,exchangeID, witsmlObject,allChannelSet.getBody(),
						channelsResponse.getBody());
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
					logResponse(response,
							"Successfully executed POST for query object=" +
							wmlObject.toString()),
					wmlObject
			));

			// get the UUID of the first wellbore in the response
			String wellboreUUID = GraphQLRespConverter
							.getWellboreUuidFromGraphqlResponse(new JSONObject(response.getBody()));

			// cache the wellbore uuid/uid
			UidUuidCache.putInCache(
					wellboreUUID,
					wmlObject.getParentUid(),
					wmlObject.getGrandParentUid());

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
     * Builds logging for a request in a uniform format
     *
     * @param request
     * @return built request for logging
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
     * @param response              - the response to finalize
     * @param customResponseMessage - customized portion of the response
     * @return built response for logging
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
     *
     * @param uid
     * @param witsmlObj
     * @param client
     * @param username
     * @param password
     * @return uuid
     */
    private String getUUID(String uid,
                           AbstractWitsmlObject witsmlObj,
                           DotClient client,
                           String username,
                           String password)
            throws ValveException,
            UnirestException,
            ValveAuthException {

        String uidWellbore;
        String uidWellLog;
        String uuid;
        String endpoint;

        // set up the request with the correct endpoint, header, & query parameters
        HttpRequest request;
        endpoint = this.getEndpoint(CHANNELSET_UUID);
        request = Unirest.get(endpoint);
        request.header("accept", "application/json");
        if ("1.4.1.1".equals(witsmlObj.getVersion())) {
            uidWellbore = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObj).getUidWellbore();
            uidWellLog = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObj).getUidWell();
        } else {
            uidWellbore = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj).getUidWellbore();
            uidWellLog = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj).getUidWell();
        }
        request.queryString("uid", uid);
        request.queryString("uidWellbore", uidWellbore);
        request.queryString("uidWell", uidWellLog);

        // make the request
        HttpResponse<String> response;
        response = client.makeRequest(request, username, password);
        if (response.getBody().isEmpty()) {
            throw new ValveException("No log found for request: " + witsmlObj.getObjectType());
        }

        // obtain the uuid from the response
        JSONObject responseJson = new JSONObject(response.getBody());
        uuid = responseJson.getString("uuid");

        // return the uuid associated with the uid &
        // (uidWellbore & uidWell) from the witsmlObj
        return uuid;
    }


    private void makeRequests4Log(HttpRequestWithBody request,
                                  String payload,
                                  String objectType,
                                  AbstractWitsmlObject witsmlObj,
                                  String exchangeID,
                                  String username,
                                  String password,
                                  DotClient client,
                                  HttpResponse<String> response)
            throws ValveException,
            UnirestException,
            ValveAuthException {
        request.header("Content-Type", "application/json");
        if (payload.length() > 0) {
            request.body(payload);

            LOG.info(ValveLogging.getLogMsg(
                    exchangeID,
                    logRequest(request), witsmlObj));

            // make the UPDATE call.
            response = client.makeRequest(request, username, password);

            // check response status
            int status = response.getStatus();
            if (201 == status || 200 == status) {
                LOG.info(ValveLogging.getLogMsg(
                        exchangeID,
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

}