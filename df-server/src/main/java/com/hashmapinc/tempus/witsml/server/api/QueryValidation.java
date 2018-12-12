package com.hashmapinc.tempus.witsml.server.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author vijay
 *
 */

public class QueryValidation {
	private static String witsml_version_1311 = "1.3.1.1";
	private static String witsml_version_1411 = "1.4.1.1";

	private static final Logger LOG = Logger.getLogger(QueryValidation.class.getName());

	/**
	 * This method validates input parameters for addToStore in StoreImpl for 1.3,
	 * if the input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateAddToStore(String WMLtypeIn, String XMLin, String OptionsIn,
			String CapabilitiesIn, String version) {
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
	 * This method validates input parameters for getFromStore in StoreImpl for 1.3,
	 * if the input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateGetFromStore(String WMLtypeIn, String XMLin, String OptionsIn,
			String CapabilitiesIn, String version) {
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
	 * This method validates input parameters for updateInStore in StoreImpl for
	 * 1.3, if the input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateUpdateInStore(String WMLtypeIn, String XMLin, String OptionsIn,
			String CapabilitiesIn, String version) {
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
	 * This method validates input parameters for deleteInStore in StoreImpl for
	 * 1.3, if the input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateDeleteInStore(String WMLtypeIn, String XMLin, String OptionsIn,
			String CapabilitiesIn, String version) {
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
