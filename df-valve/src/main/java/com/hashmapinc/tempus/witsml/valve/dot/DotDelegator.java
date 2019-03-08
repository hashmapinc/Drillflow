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
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONObject;

import javax.xml.datatype.DatatypeConfigurationException;
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
	private final String TRAJECTORY_GQL_PATH;

    /**
     * Map based constructor
     *
     * @param config - map with field values
     */
    public DotDelegator(Map<String, String> config) {
        this.URL =             		config.get("baseurl");
        this.WELL_PATH =       		config.get("well.path");
        this.WB_PATH =         		config.get("wellbore.path");
        this.TRAJECTORY_PATH = 		config.get("trajectory.path");
        this.WELL_GQL_PATH =   		config.get("well.gql.path");
        this.WELLBORE_GQL_PATH = 	config.get("wellbore.gql.path");
		this.TRAJECTORY_GQL_PATH = 	config.get("trajectory.gql.path");
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
        switch (objectType) {
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
			case "trajectorysearch":
				endpoint = this.URL + this.TRAJECTORY_GQL_PATH;
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

		LOG.info(ValveLogging.getLogMsg(exchangeID, logRequest(request), witsmlObject));

		// get response
		HttpResponse<String> response = client.makeRequest(request, username, password);

		// check response status
		int status = response.getStatus();
		if (201 == status || 200 == status) {
			LOG.info(ValveLogging.getLogMsg(
					exchangeID,
					logResponse(response, "Successfully executed GET for query object=" + witsmlObject.toString()),
					witsmlObject
			));

			// translate the query response
			return DotTranslator.translateQueryResponse(witsmlObject, response.getBody(), optionsIn);
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
				String wellboreUuid = getWellboreUUID(witsmlObject, exchangeID, client, username, password);
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

		// build query
		String query;
		try {
			query = GraphQLQueryConverter.getUidUUIDMappingQuery(wmlObject);
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

			// get matching objects from search as list of abstract witsml objects
			return GraphQLRespConverter.getUUid(new JSONObject(response.getBody()));

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