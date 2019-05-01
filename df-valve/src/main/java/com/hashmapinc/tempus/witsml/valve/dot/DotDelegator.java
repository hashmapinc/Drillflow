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
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory;
import com.hashmapinc.tempus.witsml.ValveLogging;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.hashmapinc.tempus.witsml.valve.dot.client.DotClient;
import com.hashmapinc.tempus.witsml.valve.dot.client.UidUuidCache;
import com.hashmapinc.tempus.witsml.valve.dot.graphql.GraphQLQueryConverter;
import com.hashmapinc.tempus.witsml.valve.dot.graphql.GraphQLRespConverter;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.LogConverterExtended;
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
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class DotDelegator {
	private static final Logger LOG = Logger.getLogger(DotDelegator.class.getName());

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
	/**
	 * Map based constructor
	 *
	 * @param config - map with field values
	 */
	public DotDelegator(Map<String, String> config) {
		this.WELL_PATH =                    config.get("well.path");
		this.WB_PATH =                      config.get("wellbore.path");
		this.TRAJECTORY_PATH =              config.get("trajectory.path");
		this.WELL_GQL_PATH =                config.get("well.gql.path");
		this.WELLBORE_GQL_PATH =         config.get("wellbore.gql.path");
		this.TRAJECTORY_GQL_PATH =           config.get("trajectory.gql.path");
		this.LOG_PATH =                   config.get("log.channelset.path");
		this.LOG_CHANNEL_PATH =       config.get("log.channel.path");
		this.LOG_CHANNELSET_METADATA =    config.get("log.channelset.metadata.path");
		this.LOG_CHANNELSET_UUID =       config.get("log.channelset.uuid.path");
		this.LOG_CHANNELS =                config.get("log.channels.path");
	}

	/**
	 * returns the endpoint for each supported object type
	 * @param objectType - well, wellbore, trajectory, or log
	 * @return endpoint - String value to send requests to
	 * @throws ValveException
	 */
	private String getEndpoint(
			String objectType
	) throws ValveException {
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
				endpoint = this.LOG_PATH;
				break;
			case "logChannel":
				endpoint = this.LOG_CHANNEL_PATH;
				break;
			case "channelsetmetadata":
				endpoint = this.LOG_CHANNELSET_METADATA;
				break;
			case "channelsetuuid":
				endpoint = this.LOG_CHANNELSET_UUID;
				break;
			case "channels":
				endpoint = this.LOG_CHANNELS;
				break;
			default:
				throw new ValveException("Unsupported object type<" + objectType + ">");
		}
		return endpoint;
	}

	/**
	 * deletes the object from DoT
	 *
	 * @param witsmlObj - object to delete
	 * @param username - auth username
	 * @param password - auth password
	 * @param exchangeID - unique string for tracking which exchange called this method
	 * @param client - DotClient to execute requests with
	 */
	public void deleteObject(
			AbstractWitsmlObject witsmlObj,
			String username,
			String password,
			String exchangeID,
			DotClient client
	) throws ValveException, UnirestException, ValveAuthException {
		String uid = witsmlObj.getUid(); // get uid for delete call
		String objectType = witsmlObj.getObjectType(); // get obj type for exception handling

		// It is an object delete, so re-route there
		String endpoint = this.getEndpoint(objectType) + uid; // add uid for delete call

		// create request
		HttpRequest request = Unirest.delete(endpoint).header("Content-Type", "application/json");
		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObj));

		// add query string params
		if ("wellbore".equals(objectType)) {
			request.queryString("uidWell", witsmlObj.getParentUid());
		} else if ("trajectory".equals(objectType)){
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
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status || 204 == status) {
			LOG.info(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "Successfully Deleted Object with UID :"+uid+"."),
					witsmlObj)
			);
		} else {
			LOG.warning(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "Unable to delete"),
					witsmlObj)
			);
			throw new ValveException("DELETE DoT REST call failed with status code: " + status);
		}
	}

	public void performElementDelete(
			AbstractWitsmlObject witsmlObj,
			String username,
			String password,
			String exchangeID,
			DotClient client
	) throws ValveException, ValveAuthException, UnirestException {
		// Throwing valve exception as this is currently not supported by DoT until the Patch API is implemented
		// We dont want to delete the object because someone thought something was implemented.
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
		} else if ("trajectory".equals(objectType)){
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
			LOG.info(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "Successfully Patched Object with UID :"+uid+"."),
					witsmlObj)
			);
		} else {
			LOG.warning(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "Unable to patch"),
					witsmlObj)
			);
			throw new ValveException("PATCH DoT REST call failed with status code: " + status);
		}
	}

	public void updateObject(
			AbstractWitsmlObject witsmlObj,
			String username,
			String password,
			String exchangeID,
			DotClient client
	) throws ValveException, ValveAuthException, UnirestException {
		String uid = witsmlObj.getUid();
		String objectType = witsmlObj.getObjectType();
		String endpoint = this.getEndpoint(objectType) + uid;

		// get witsmlObj as json string for request payload
		String payload = witsmlObj.getJSONString("1.4.1.1");
		payload = JsonUtil.removeEmpties(new JSONObject(payload));

		// build the request
		HttpRequestWithBody request = Unirest.put(endpoint);
		request.header("Content-Type", "application/json");
		request.body(payload);

		// add query string params
		if ("wellbore".equals(objectType)) {
			request.queryString("uidWell", witsmlObj.getParentUid());
		} else if ("trajectory".equals(objectType)) {
			request.queryString("uidWellbore", witsmlObj.getParentUid());
			String uidWell;
			if ("1.4.1.1".equals(witsmlObj.getVersion())) {
				// TODO: maybe replace with this -> uidWell = witsmlObj.getGrandParentUid();
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) witsmlObj).getUidWell();
			} else {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory) witsmlObj).getUidWell();
			}
			request.queryString("uidWell", uidWell);
		}

		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObj));

		// make the UPDATE call.
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status) {
			LOG.info(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "UPDATE for " + witsmlObj + " was successful"),
					witsmlObj
			));
		} else {
			LOG.warning(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "Received failure status code from DoT PUT"),
					witsmlObj
			));
			throw new ValveException(response.getBody());
		}
	}

	/**
	 * Submits the object to the DoT rest API for creation
	 *
	 * @param witsmlObj - AbstractWitsmlObject to create
	 * @param username - auth username
	 * @param password - auth password
	 * @param exchangeID - unique string for tracking which exchange called this method
	 * @param client - DotClient to execute requests with
	 * @return
	 */
	public String createObject(
			AbstractWitsmlObject witsmlObj,
			String username,
			String password,
			String exchangeID,
			DotClient client
	) throws ValveException, ValveAuthException, UnirestException {
		String objectType = witsmlObj.getObjectType(); // get obj type for exception handling
		String uid = witsmlObj.getUid();
		String endpoint = this.getEndpoint(objectType);
		String version = witsmlObj.getVersion();

		// get object as payload string
		String payload = witsmlObj.getJSONString("1.4.1.1");

		// a log will derive its payload for creating a ChannelSet from "payload"
		// & then use "payload" again to update the ChannelSet with Log Curve information (Channel)
		JSONObject objLog;
		String channelSetPayload="";
		String channelPayload="";

		// build the requests (log requires two HttpRequests
		HttpRequestWithBody request;
		HttpRequestWithBody channelsRequest;
		if (null == uid || uid.isEmpty() || "log".equals(objectType)){
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
		} else if ( "log".equals(objectType) ) {
			request.queryString("uid", uid);
			request.queryString("uidWellbore", witsmlObj.getParentUid());
			String uidWell;
			if ("1.4.1.1".equals(version)) {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObj).getUidWell();
			} else {
				// TODO work with v1.3.1.1
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) witsmlObj).getUidWell();
			}
			request.queryString("uidWell", uidWell);
		}

		// add the header and payload
		request.header("Content-Type", "application/json");

		if ("log".equals(objectType)) {
			// create the payload for create ChannelSet
			LogConverterExtended logConverter = new LogConverterExtended();
			if ("1.4.1.1".equals(version)) {
				// TODO Fix this
				objLog = null;
				objLog = logConverter.convertToChannelSet1411(
						(com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObj);
				if (objLog.has("logCurveInfo")) {
					channelPayload = objLog.getJSONArray("logCurveInfo").toString();
					objLog.remove("logCurveInfo");
				}
				channelSetPayload = objLog.toString();

			} else {
				channelSetPayload =
						logConverter.convertToChannelSet1311(witsmlObj.getJSONString("1.3.1.1"));
			}
			request.body(channelSetPayload);
		} else {

			request.body(payload);
		}

		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObj));

		// get the request response.
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status) {
			LOG.info(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "Received successful status code from DoT create call"),
					witsmlObj
			));

			// add channels to an existing ChannelSet
			if ("log".equals(objectType) && !(channelPayload.isEmpty())) {

				// build the request...
				endpoint = this.getEndpoint(objectType+"Channel");
				endpoint = endpoint + "/metadata";
				// get the uuid for the channelSet just created from the response
				int startIndexUUID = response.getBody().indexOf("uuid")
						+ "uuid".length() + 3;
				String sub = response.getBody().substring(startIndexUUID);
				int lengthOfUUID = sub.indexOf('"');
				String uuid4CS = response.getBody().substring(
						startIndexUUID,
						startIndexUUID+lengthOfUUID
				);

				// create with POST
				channelsRequest = Unirest.post(endpoint);
				// provide the ChannelSet's UUID as a query parameter
				channelsRequest.queryString("channelSetUuid", uuid4CS);
				// add the header and payload
				channelsRequest.header("Content-Type", "application/json");
				// TODO Figure out if I need to add more data to channelPayload? such as - citation?
				channelsRequest.body(channelPayload);

				LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(channelsRequest), witsmlObj));

				// get the request response.
				response = client.makeRequest(channelsRequest, username, password);
				// check response status
				status = response.getStatus();
				if (201 == status || 200 == status) {
					LOG.info(ValveLogging.getLogMsg(
							exchangeID,
							logResponse(response, "Received successful status code from DoT create call"),
							witsmlObj
					));
				}

			}

			// TODO I should probably return something else? yes....this needs to concatenate responses for BOTH REST calls
			//      And what if there are no channels -- still create the channelSet (I think "yes")
			//      But what if there is a failure creating channels -- should I delete the channelSet (I think "yes")
			return (null == uid || uid.isEmpty()) ? new JsonNode(response.getBody()).getObject().getString("uid") : uid;
		} else {
			LOG.warning(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "Received " + status + " from DoT POST" + response.getBody()),
					witsmlObj
			));
			throw new ValveException(response.getBody());
		}
	}

	/**
	 * Submits the query to the DoT rest API for object GETing
	 *
	 * @param witsmlObject - AbstractWitsmlObject to get
	 * @param username - auth username
	 * @param password - auth password
	 * @param exchangeID - unique string for tracking which exchange called this method
	 * @param client - DotClient to execute requests with
	 * @return get results AbstractWitsmlObject
	 */
	public AbstractWitsmlObject getObject(
			AbstractWitsmlObject witsmlObject,
			String username,
			String password,
			String exchangeID,
			DotClient client,
			Map<String,String> optionsIn
	) throws ValveException, ValveAuthException, UnirestException ,JAXBException{
		String uid = witsmlObject.getUid();
		String uidWellbore ;
		String uidWellLog ;
		String objectType = witsmlObject.getObjectType();
		String endpoint="";
		String uuid="";
		String finalResponse=null;

		if ("log".equals(objectType)){
			endpoint = this.getEndpoint("channelsetuuid");
		}else{
			endpoint = this.getEndpoint(objectType) + uid; // add uid for rest call
		}
		String version = witsmlObject.getVersion();
		// build request
		HttpRequest request = Unirest.get(endpoint);
		request.header("accept", "application/json");
		if ("wellbore".equals(objectType)) {
			request.queryString("uidWell", witsmlObject.getParentUid()); // TODO: check the parent uid exists?
		} if ("log".equals(objectType)) { // code added to handle log object
			uidWellbore = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObject).getUidWellbore();
			uidWellLog = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) witsmlObject).getUidWell();
			request.queryString("uid", uid);
			request.queryString("uidWellbore", uidWellbore);
			request.queryString("uidWell", uidWellLog);
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
		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObject));
		// get response
		HttpResponse<String> response = client.makeRequest(request, username, password);
		if ("log".equals(objectType)) {
			JSONObject responseJson = new JSONObject(response.getBody());
			uuid = responseJson.getString("uuid");
		}
		// check response status
		int status = response.getStatus();
		//HttpResponse<String> finalResponse = client.makeRequest(request, username, password);
		if (201 == status || 200 == status) {
			// Code logic added to handle log ChannelSet Metadata/Get Channels/get All Channels
			if ("log".equals(objectType)) {
				// TODO Are we to only return channels OR channel set + channels data?
				finalResponse = getRestCalls(witsmlObject,client,uuid,username,password,exchangeID);
				// log requires transformation of the response
			}
			LOG.info(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "Successfully executed GET for query object=" + witsmlObject.toString()),
					witsmlObject
			));
			//
			return DotTranslator.translateQueryResponse(witsmlObject, finalResponse, optionsIn);
		} else if (404 == status) {
			// handle not found. This is a valid response
			return null;
		} else {
			LOG.warning(ValveLogging.getLogMsg(
					witsmlObject.getUid(),
					logResponse(response, "Unable to execute GET"),
					witsmlObject
			));
			throw new ValveException(response.getBody());
		}
	}

	/**
	 * All other rest calls to verify channelSetMetadata,ChannelSet,Channels
	 *
	 * @param witsmlObject - AbstractWitsmlObject to get
	 * @param username - auth username
	 * @param password - auth password
	 * @param exchangeID - unique string for tracking which exchange called this method
	 * @param client - DotClient to execute requests with
	 */

	private String getRestCalls(AbstractWitsmlObject witsmlObject,DotClient client,String uuid,String username, String password, String exchangeID) throws ValveException, ValveAuthException, UnirestException, JAXBException {

		String channelsetmetadataEndpoint;
		HttpResponse<String> channelsetmetadataResponse;
		HttpRequest channelsetmetadataRequest;
		String channelsetuuidEndpoint;
		HttpRequest channelsetuuidRequest;
		HttpResponse<String> allChannelSet;
		String channelsEndPoint;
		HttpRequest channelsRequest;
		HttpResponse<String> channelsResponse;
		String finalResponse;

		//Build Request for Get ChannelSet Metadata
		channelsetmetadataEndpoint = this.getEndpoint("channelsetmetadata");
		channelsetmetadataRequest = Unirest.get(channelsetmetadataEndpoint);
		channelsetmetadataRequest.header("accept", "application/json");
		channelsetmetadataRequest.queryString("uuid", uuid);
		// get response
		channelsetmetadataResponse = client.makeRequest(channelsetmetadataRequest, username, password);
		//Build Request for Get All ChannelSet
		channelsetuuidEndpoint = this.getEndpoint("log");
		channelsetuuidRequest = Unirest.get(channelsetuuidEndpoint);
		channelsetuuidRequest.header("accept", "application/json");
		channelsetuuidRequest.queryString("containerId", uuid);
		// get response
		allChannelSet = client.makeRequest(channelsetuuidRequest, username, password);
		//Build Request for Get Channels
		channelsEndPoint = this.getEndpoint("channels");
		channelsRequest = Unirest.get(channelsEndPoint);
		channelsRequest.header("accept", "application/json");
		channelsRequest.queryString("channelSetUuid", uuid);
		// get response
		channelsResponse = client.makeRequest(channelsRequest, username, password);
		if (201 == channelsetmetadataResponse.getStatus() || 200 == channelsetmetadataResponse.getStatus() || 201 == allChannelSet.getStatus() || 201 == allChannelSet.getStatus() || 201 == channelsResponse.getStatus() || 201 == channelsResponse.getStatus()) {

			// call conversion  method with allChannelSet and channelsResponse
			JSONArray channelSetArray = new JSONArray(allChannelSet.getBody());
			JSONArray channelsArray = new JSONArray(channelsResponse.getBody());
			LogConverterExtended logConverterExtended = new LogConverterExtended();

			finalResponse = logConverterExtended.convertTo1411(channelSetArray, channelsArray);
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
		// Create a JSON String that contains the ChannelSet + Channels data
		// First, create a JSONObject that encompasses the CahnnelSet & the
		// Channels JSONArray.
		return finalResponse;
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
	 * Generates logging for Http Request
	 * @param request
	 * @return
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
	 * Generates logging for Http Response
	 * @param response
	 * @param customResponseMessage
	 * @return
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
}