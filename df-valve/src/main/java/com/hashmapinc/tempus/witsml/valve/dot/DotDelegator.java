/**
 * Copyright © 2018-2018 Hashmap, Inc
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
import com.hashmapinc.tempus.witsml.ValveLogging;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class DotDelegator {
    private static final Logger LOG = Logger.getLogger(DotDelegator.class.getName());

    private final String URL;
    private final String WELL_PATH;
    private final String WB_PATH;
    private final String TRAJECTORY_PATH;
    private final String WELL_GQL_PATH;
    private final String WELLBORE_GQL_PATH;

    /**
     * Map based constructor
     *
     * @param config - map with field values
     */
    public DotDelegator(Map<String, String> config) {
        this.URL =             	config.get("baseurl");
        this.WELL_PATH =       	config.get("well.path");
        this.WB_PATH =         	config.get("wellbore.path");
        this.TRAJECTORY_PATH = 	config.get("trajectory.path");
        this.WELL_GQL_PATH =   	config.get("well.gql.path");
        this.WELLBORE_GQL_PATH =config.get("wellbore.gql.path") 	;
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
        // TODO: these should be injected in the DotDelegator constructor and not rely on a shared this.URL
        // get endpoint
        String endpoint;
        switch (objectType) { // TODO: add support for log and trajectory
            case "well":
            	endpoint = this.URL + this.WELL_PATH;
                break;
			case "wellsearch":
				endpoint = this.URL + this.WELL_GQL_PATH;
				break;
            case "wellbore":
                endpoint = this.URL + this.WB_PATH;
                break;
			case "wellboresearch":
				endpoint = this.URL + this.WELLBORE_GQL_PATH;
				break;
            case "trajectory":
                endpoint = this.URL + this.TRAJECTORY_PATH;
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
        String endpoint = this.getEndpoint(objectType) + uid; // add uid for delete call
 
        // create request
		HttpRequest request = Unirest.delete(endpoint).header("Content-Type", "application/json");
		ValveLogging valveLoggingRequest = new ValveLogging(exchangeID, logRequest(request), witsmlObj);
		LOG.info(valveLoggingRequest.toString());

		// add query string params
        if ("wellbore".equals(objectType)) {
			request.queryString("uidWell", witsmlObj.getParentUid()); // TODO: ensure parent uid exists?
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
			ValveLogging valveLoggingResponse = new ValveLogging(exchangeID,
					logResponse(response, "Successfully Deleted Object with UID :"+uid+"."), witsmlObj);
			LOG.info(valveLoggingResponse.toString());
		} else {
			ValveLogging valveLoggingResponse = new ValveLogging(exchangeID,
					logResponse(response, "Unable to delete"), witsmlObj);
			LOG.warning(valveLoggingResponse.toString());
            throw new ValveException("DELETE DoT REST call failed with status code: " + status);
        }
    }

    /**
     * updates the object in DoT
     *
     * @param witsmlObj - object to delete
     * @param username - auth username
     * @param password - auth password
	 * @param exchangeID - unique string for tracking which exchange called this method
     * @param client - DotClient to execute requests with
     */
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
		payload = removeEmpties(payload);

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
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) witsmlObj).getUidWell();
			} else {
				uidWell = ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory) witsmlObj).getUidWell();
			}
			request.queryString("uidWell", uidWell);
		}

		ValveLogging valveLoggingRequest = new ValveLogging(exchangeID, logRequest(request), witsmlObj);
		LOG.info(valveLoggingRequest.toString());

		// make the UPDATE call.
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status) {
			ValveLogging valveLoggingResponse = new ValveLogging(exchangeID,
				logResponse(response, "UPDATE for " + witsmlObj + " was successful"), witsmlObj);
			LOG.info(valveLoggingResponse.toString());
		} else {
			ValveLogging valveLoggingResponse = new ValveLogging(exchangeID,
				logResponse(response, "Received failure status code from DoT PUT"), witsmlObj);
			LOG.warning(valveLoggingResponse.toString());
			throw new ValveException(response.getBody());
		}
	}

	private String removeEmpties(String jsonQuery){
    	JSONObject object = new JSONObject(jsonQuery);
    	ArrayList<String> keysToRemove = new ArrayList<>();
    	for (Object key : object.keySet()){
    		if (Util.isEmpty(object.get(key.toString())))
    			keysToRemove.add(key.toString());
		}
    	for (String key : keysToRemove){
    		object.remove(key);
		}
    	return object.toString();
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

        // build the request
        HttpRequestWithBody request;
        if (null == uid || uid.isEmpty()){
            // create with POST and generate uid
            request = Unirest.post(endpoint);
        } else {
            // create with PUT using existing uid
            request = Unirest.put(endpoint + uid);
        }

        // add query string params
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
        }

        // add the header and payload
        request.header("Content-Type", "application/json");
        request.body(payload);

		ValveLogging valveLoggingRequest = new ValveLogging(exchangeID, logRequest(request), witsmlObj);
		LOG.info(valveLoggingRequest.toString());

        // get the request response.
        HttpResponse<String> response = client.makeRequest(request, username, password);

        // check response status
        int status = response.getStatus();
        if (201 == status || 200 == status) {
			ValveLogging valveLoggingResponse = new ValveLogging(exchangeID,
					logResponse(response, "Received successful status code from DoT create call"), witsmlObj);
			LOG.info(valveLoggingResponse.toString());
            return (null == uid || uid.isEmpty()) ? new JsonNode(response.getBody()).getObject().getString("uid") : uid;
        } else {
			ValveLogging valveLoggingResponse = new ValveLogging(exchangeID,
					logResponse(response, "Received " + status + " from DoT POST" + response.getBody()), witsmlObj);
			LOG.warning(valveLoggingResponse.toString());
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
		DotClient client
	) throws ValveException, ValveAuthException, UnirestException {
		String uid = witsmlObject.getUid();
		String objectType = witsmlObject.getObjectType();
		String endpoint = this.getEndpoint(objectType) + uid; // add uid for rest call
		String version = witsmlObject.getVersion();

		// build request
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
		ValveLogging valveLoggingRequest = new ValveLogging(exchangeID, logRequest(request), witsmlObject);
		LOG.info(valveLoggingRequest.toString());
		// get response
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status) {
			ValveLogging valveLoggingResponse = new ValveLogging(exchangeID,
					logResponse(response, "Successfully executed GET for query object=" + witsmlObject.toString()),
					witsmlObject);
			LOG.info(valveLoggingResponse.toString());
			// get an abstractWitsmlObject from merging the query and the result
			// JSON objects
			String jsonObj = witsmlObject.getJSONString("1.4.1.1");
			JSONObject queryJSON = new JSONObject(jsonObj);
			JSONObject responseJSON = new JsonNode(response.getBody()).getObject();
			return DotTranslator.translateQueryResponse(queryJSON, responseJSON, objectType);
		} else if (404 == status) {
			// handle not found. This is a valid response
			return null;
		} else {
			ValveLogging valveLoggingResponse = new ValveLogging(witsmlObject.getUid(),
					logResponse(response, "Unable to execute GET"), witsmlObject);
			LOG.warning(valveLoggingResponse.toString());
			throw new ValveException(response.getBody());
		}
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
	public ArrayList<AbstractWitsmlObject> executeGraphQL(
			AbstractWitsmlObject witsmlObject,
			String query,
			String username,
			String password,
			String exchangeID,
			DotClient client
	) throws ValveException, ValveAuthException, UnirestException, IOException {
		String objectType = witsmlObject.getObjectType();
		String endpoint = this.getEndpoint(objectType + "search");

		// build request
		HttpRequestWithBody request = Unirest.post(endpoint);
		request.header("Content-Type", "application/json");
		request.body(query);
		ValveLogging valveLoggingRequest = new ValveLogging(exchangeID, logRequest(request), witsmlObject);
		LOG.info(valveLoggingRequest.toString());
		// get response
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status || 400 == status) {
			ValveLogging valveLoggingResponse = new ValveLogging(exchangeID,
					logResponse(response, "Successfully executed POST for query object=" + witsmlObject.toString()),
					witsmlObject);
			LOG.info(valveLoggingResponse.toString());
			// get an abstractWitsmlObject from merging the query and the result
			// JSON objects
			GraphQLRespConverter converter = new GraphQLRespConverter();
			return converter.convert(response.getBody(), objectType);
		} else {
			ValveLogging valveLoggingResponse = new ValveLogging(witsmlObject.getUid(),
					logResponse(response, "Unable to execute POST"), witsmlObject);
			LOG.warning(valveLoggingResponse.toString());
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