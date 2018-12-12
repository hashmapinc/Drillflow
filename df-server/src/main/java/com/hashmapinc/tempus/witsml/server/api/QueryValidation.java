package com.hashmapinc.tempus.witsml.server.api;

import java.util.logging.Logger;

/**
 * @author java
 *
 */
/**
 * @author java
 *
 */
public class QueryValidation {
	
	private static final Logger LOG = Logger.getLogger(QueryValidation.class.getName());
	
	
	/**
	 * This method validates input parameters for addToStore in StoreImpl for 1.3,
	 * if the input is not according to the specs it will return an error code.
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateAddToStore1311(
		String WMLtypeIn, 
		String XMLin, 
		String OptionsIn, 
		String CapabilitiesIn
		) {
		LOG.info("validating input for addToStore v 1311");
		return null;
	}

	/**
	 * This method validates input parameters for addToStore in StoreImpl for 1.4,
	 * if the input is not according to the specs it will return an error code.
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateAddToStore1411(
			String WMLtypeIn, 
			String XMLin, 
			String OptionsIn, 
			String CapabilitiesIn
			) {
		LOG.info("validating input for addToStore v 1411");
		return null;
	}

	/**
	 * This method validates input parameters for getFromStore in StoreImpl for 1.3,
	 * if the input is not according to the specs it will return an error code.
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateGetFromStore1311(
			String WMLtypeIn, 
			String XMLin, 
			String OptionsIn, 
			String CapabilitiesIn
			) {
		LOG.info("validating input for getFromStore v 1311");
		return null;
	}
	
	/**
	 * This method validates input parameters for getFromStore in StoreImpl for 1.4,
	 * if the input is not according to the specs it will return an error code.
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateGetFromStore1411(
			String WMLtypeIn, 
			String XMLin, 
			String OptionsIn, 
			String CapabilitiesIn
			) {
		LOG.info("validating input for getFromStore v 1411");
		return null;
	}
	
	/**
	 * This method validates input parameters for updateInStore in StoreImpl for 1.3,
	 * if the input is not according to the specs it will return an error code.
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateUpdateInStore1311(
			String WMLtypeIn, 
			String XMLin, 
			String OptionsIn, 
			String CapabilitiesIn
			) {
		LOG.info("validating input for updateInStore v 1311");
		return null;
	}
	
	/**
	 * This method validates input parameters for updateInStore in StoreImpl for 1.4,
	 * if the input is not according to the specs it will return an error code.
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateUpdateInStore1411(
			String WMLtypeIn, 
			String XMLin, 
			String OptionsIn, 
			String CapabilitiesIn
			) {
		LOG.info("validating input for updateInStore v 1411");
		return null;
	}
	
	
	/**
	 * This method validates input parameters for deleteInStore in StoreImpl for 1.3,
	 * if the input is not according to the specs it will return an error code.
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateDeleteInStore1311(
			String WMLtypeIn, 
			String XMLin, 
			String OptionsIn, 
			String CapabilitiesIn
			) {
		LOG.info("validating input for deleteInStore v 1411");
		return null;
	}
	
	
	/**
	 * This method validates input parameters for deleteInStore in StoreImpl for 1.4,
	 * if the input is not according to the specs it will return an error code.
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return an error code according to the specs after checking the parameters.
	 */
	public static String validateDeleteInStore1411(
			String WMLtypeIn, 
			String XMLin, 
			String OptionsIn, 
			String CapabilitiesIn
			) {
		LOG.info("validating input for deleteInStore v 1411");
		return null;
	}

}
