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
package com.hashmapinc.tempus.witsml.server.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class QueryValidation {
	private static String witsml_version_1311 = "1.3.1.1";
	private static String witsml_version_1411 = "1.4.1.1";

	private static final Logger LOG = Logger.getLogger(QueryValidation.class.getName());

	/**
	 * This method validates input parameters for addToStore in StoreImpl,
	 * if the input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @param version
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateAddToStore(
			String WMLtypeIn, 
			String XMLin, 
			String OptionsIn,
			String CapabilitiesIn, 
			String version) {
		LOG.info("validating input for addToStore");
		if(witsml_version_1311.equals(version))
		{
			//error checking for input parameters for version 1.3.1.1
		}
		else if(witsml_version_1411.equals(version))
		{
			//error checking for input parameters for version 1.4.1.1
		}
		else
		{
			LOG.info("invalid witsml version");
		}
		
		return null;
	}

		/**
	 * This method validates input parameters for getFromStore in StoreImpl,
	 * if the input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @param version
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateGetFromStore(
			String WMLtypeIn, 
			String XMLin, 
			String OptionsIn,
			String CapabilitiesIn, 
			String version) {
		LOG.info("validating input for getFromStore");
		if(witsml_version_1311.equals(version))
		{
			//error checking for input parameters for version 1.3.1.1
		}
		else if(witsml_version_1411.equals(version))
		{
			//error checking for input parameters for version 1.4.1.1
		}
		else
		{
			LOG.info("invalid witsml version");
		}
		return null;
	}

	/**
	 * This method validates input parameters for updateInStore in StoreImpl,
	 * if the input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @param version
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateUpdateInStore(
			String WMLtypeIn, 
			String XMLin, 
			String OptionsIn,
			String CapabilitiesIn, 
			String version) {
		LOG.info("validating input for updateInStore");
		if(witsml_version_1311.equals(version))
		{
			//error checking for input parameters for version 1.3.1.1
		}
		else if(witsml_version_1411.equals(version))
		{
			//error checking for input parameters for version 1.4.1.1
		}
		else
		{
			LOG.info("invalid witsml version");
		}
		return null;
	}

		/**
	 * This method validates input parameters for deleteFromStore in StoreImpl,
	 * if the input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @param version
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateDeleteFromStore(
			String WMLtypeIn, 
			String XMLin, 
			String OptionsIn,
			String CapabilitiesIn, 
			String version) {
		LOG.info("validating input for deleteInStore");
		if(witsml_version_1311.equals(version))
		{
			//error checking for input parameters for version 1.3.1.1
			//pass the errorCode in getErrorMessage after performing all checks.
					}
		else if(witsml_version_1411.equals(version))
		{
			//error checking for input parameters for version 1.4.1.1
		}
		else
		{
			LOG.info("invalid witsml version");
		}
		return null;
	}
	
	
	/**
	 * This method gets the errorMessage from the basemessages.properties file
	 * based on the errorCode
	 * @param errorCode
	 * @return
	 * @throws IOException
	 */
	public static String getErrorMessage(String errorCode) throws IOException
	{
		
		Properties prop = new Properties();
    	InputStream input = null;
    	String basemessages = "resources/basemessages.properties";
    	input = QueryValidation.class.getResourceAsStream(basemessages);
    	if(input==null)
    	{
    		LOG.info("Error loading the basemessages.properties file");
    	}
    	prop.load(input);
    	String errorMessage = prop.getProperty(errorCode);
		
		return errorMessage;
	}
}
