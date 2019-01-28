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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.ValveLogging;
import com.hashmapinc.tempus.witsml.valve.IValve;
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
import com.hashmapinc.tempus.witsml.valve.ValveException;

public class DotValve implements IValve {
	private static final Logger LOG = Logger.getLogger(DotValve.class.getName());
	private final DotClient CLIENT;
	private final DotDelegator DELEGATOR;

	/**
	 * Constructor that accepts a config map and builds the client and delegator
	 * fields
	 * 
	 * @param config
	 */
	public DotValve(Map<String, String> config) {
		String url = config.get("baseurl");
		String apikey = config.get("apikey");
		String tokenPath = config.get("token.path");

		this.CLIENT = new DotClient(url, apikey, tokenPath);
		this.DELEGATOR = new DotDelegator(config);

		LOG.info("Creating valve pointing to url: " + url);
	}

	/**
	 * Constructor for directly injecting a client and delegator
	 *
	 * @param client - dotClient for auth and request execution
	 * @pararm delegator - dotDelegator for executing valve methods
	 */
	public DotValve(DotClient client, DotDelegator delegator) {
		this.CLIENT = client;
		this.DELEGATOR = delegator;
	}

	/**
	 * Retrieve the name of the valve
	 * 
	 * @return The name of the valve
	 */
	@Override
	public String getName() {
		// DoT = Drillops Town
		return "DoT";
	}

	/**
	 * Retrieve the description of the valve
	 * 
	 * @return The description of the valve
	 */
	@Override
	public String getDescription() {
		// DoT = Drillops Town
		return "Valve for interaction with Drillops Town";
	}

	/**
	 * Gets the object based on the query from the WITSML STORE API
	 * 
	 * @param qc - QueryContext needed to execute the getObject querying
	 * @return The resultant object from the query in xml string format
	 */
	@Override
	@Async("asyncCustomTaskExecutor")
	public CompletableFuture<String> getObject(QueryContext qc) throws ValveException {
		String result = null;
		if (qc.WITSML_OBJECTS.size() == 1 && "".equals(qc.WITSML_OBJECTS.get(0).getUid())) {
			// its a search
			result = doSearch(qc);
		} else {
			// its a get
			result = getSingularObject(qc);
		}
		return CompletableFuture.completedFuture(result);
	}

	private String getSingularObject(QueryContext qc) throws ValveException {

		// handle each object
		ArrayList<AbstractWitsmlObject> queryResponses = new ArrayList<AbstractWitsmlObject>();
		try {
			for (AbstractWitsmlObject witsmlObject : qc.WITSML_OBJECTS) {
				AbstractWitsmlObject response = this.DELEGATOR.getObject(witsmlObject, qc.USERNAME, qc.PASSWORD,
						qc.EXCHANGE_ID, this.CLIENT);
				if (null != response)
					queryResponses.add(response);
			}
		} catch (Exception e) {
			LOG.warning("Exception in DotValve get object: " + e.getMessage());
			throw new ValveException(e.getMessage());
		}
		// return consolidated XML response in proper version
		return DotTranslator.consolidateObjectsToXML(queryResponses, qc.CLIENT_VERSION, qc.OBJECT_TYPE);
	}

	private String doSearch(QueryContext qc) throws ValveException {
		// handle each object
		ArrayList<AbstractWitsmlObject> queryResponses = new ArrayList<AbstractWitsmlObject>();
		if (qc.WITSML_OBJECTS.size() > 1) {
			LOG.info("Query received with more than one singular object, not supported");
			// return "";
		}
		GraphQLQueryConverter converter = new GraphQLQueryConverter();
		String query;
		try {
			query = converter.convertQuery(qc.WITSML_OBJECTS.get(0));
			ValveLogging log = new ValveLogging(qc.EXCHANGE_ID, System.lineSeparator() + "Graph QL Query: " + query,
					qc.WITSML_OBJECTS.get(0));
			LOG.fine(log.toString());
		} catch (IOException ex) {
			throw new ValveException(ex.getMessage());
		}
		try {
			queryResponses = this.DELEGATOR.executeGraphQL(qc.WITSML_OBJECTS.get(0), query, qc.USERNAME, qc.PASSWORD,
					qc.EXCHANGE_ID, this.CLIENT);
		} catch (Exception e) {
			LOG.warning("Exception in DotValve get object: " + e.getMessage());
			throw new ValveException(e.getMessage());
		}
		// return consolidated XML response in proper version
		return DotTranslator.consolidateObjectsToXML(queryResponses, qc.CLIENT_VERSION, qc.OBJECT_TYPE);
	}

	/**
	 * Creates an object
	 * 
	 * @param qc - query context to use for query execution
	 * @return the UID of the newly created object
	 */
	@Override
	@Async("asyncCustomTaskExecutor")
	public CompletableFuture<String> createObject(QueryContext qc) throws ValveException {
		// create each object
		ArrayList<String> uids = new ArrayList<>();
		LOG.info("Async create object");

		try {
			for (AbstractWitsmlObject witsmlObject : qc.WITSML_OBJECTS) {
				uids.add(this.DELEGATOR.createObject(witsmlObject, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID,
						this.CLIENT));
			}

		} catch (Exception e) {
			LOG.warning("Exception in DotValve create object: " + e.getMessage());
			throw new ValveException(e.getMessage());
		}
		return CompletableFuture.completedFuture(StringUtils.join(uids, ','));
	}

	/**
	 * Deletes an object
	 * 
	 * @param qc - QueryContext with information needed to delete object
	 * @return
	 */
	@Override
	@Async("asyncCustomTaskExecutor")
	public CompletableFuture<Boolean> deleteObject(QueryContext qc) throws ValveException {
		// delete each object with 1 retry for bad token errors
		boolean result = false;
		try {
			for (AbstractWitsmlObject witsmlObject : qc.WITSML_OBJECTS) {
				this.DELEGATOR.deleteObject(witsmlObject, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.CLIENT);
			}
			result = true;
		} catch (Exception e) {
			LOG.warning("Got exception in DotValve delete object: " + e.getMessage());
			throw new ValveException(e.getMessage());
		}
		return CompletableFuture.completedFuture(result);
	}

	/**
	 * Updates an already existing object
	 * 
	 * @param qc - QueryContext needed to execute the deleteObject querying
	 */
	@Override
	@Async("asyncCustomTaskExecutor")
	public CompletableFuture<Boolean> updateObject(QueryContext qc) throws ValveException {
		// update each object with 1 retry for bad tokens
		boolean result = false;
		try {
			for (AbstractWitsmlObject witsmlObject : qc.WITSML_OBJECTS) {
				this.DELEGATOR.updateObject(witsmlObject, qc.USERNAME, qc.PASSWORD, qc.EXCHANGE_ID, this.CLIENT);
			}
			result = true;
		} catch (Exception e) {
			LOG.warning("Exception in DotValve update object: " + e.getMessage());
			throw new ValveException(e.getMessage());
		}
		return CompletableFuture.completedFuture(result);
	}

	/**
	 * Authenticates with the DotAuth class
	 * 
	 * @param username The user name to authenticate with
	 * @param password The password to authenticate with
	 * @throws ValveAuthException
	 */
	@Override
	public void authenticate(String username, String password) throws ValveAuthException {
		this.CLIENT.getJWT(username, password);
	}

	/**
	 * Return a map of FUNCTION_NAME->LIST_OF_SUPPORTED_OBJECTS
	 * 
	 * @return capabilities - map of FUNCTION_NAME->LIST_OF_SUPPORTED_OBJECTS
	 */
	public Map<String, AbstractWitsmlObject[]> getCap() {
		// define capabilities map
		Map<String, AbstractWitsmlObject[]> cap = new HashMap<>();

		// array of supported functions
		String[] funcs = { "WMLS_AddToStore", "WMLS_GetFromStore", "WMLS_DeleteFromStore", "WMLS_UpdateInStore" };

		// supported objects for each function
		AbstractWitsmlObject well = new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell(); // 1311 is arbitrary
		AbstractWitsmlObject wellbore = new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore(); // 1311 is
																										// arbitrary
		AbstractWitsmlObject trajectory = new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory(); // 1311 is
																											// arbitrary
		AbstractWitsmlObject[][] supportedObjects = { { well, wellbore, trajectory }, // ADD TO STORE OBJECTS
				{ well, wellbore }, // GET FROM STORE OBJECTS
				{ well, wellbore, trajectory }, // DELETE FROM STORE OBJECTS
				{ well, wellbore, trajectory }, // UPDATE IN STORE OBJECTS
		};

		// populate cap
		for (int i = 0; i < funcs.length; i++) {
			cap.put(funcs[i], supportedObjects[i]);
		}

		return cap;
	}
}