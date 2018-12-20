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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

public class QueryValidation {
	
	private static final Logger LOG = Logger.getLogger(QueryValidation.class.getName());

	/**
	 * This method validates input parameters for addToStore in StoreImpl, if the
	 * input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @param version
	 * @return an error code according to the specs after checking the parameters.
	 * @throws IOException
	 */
	public static Short validateAddToStore(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn,
			String version) throws IOException {
		LOG.info("Validating addToStore");

		WITSMLVERSION witsmlVersion = WITSMLVERSION.getType(version);

		switch (witsmlVersion) {
		case v1311:
			LOG.info("Going to validate witsml version 1.3.1.1");
			break;

		case v1411:
			LOG.info("Going to validate witsml version 1.4.1.1");
			return validateAddtoStoreVersion1411(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
		default:
			LOG.info("invalid witsml version");
			break;
		}

		return null;
	}

	private static Short validateAddtoStoreVersion1411(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn)
			throws IOException {
		Optional<Short> errorCode = null;
		// error checking for input parameters for version 1.4.1.1
		final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
		Validation validation = Validation.error407().and(Validation.error408()).and(Validation.error487()).and(Validation.error409()).and(Validation.error426()).and(Validation.error401()).and(Validation.error406()).and(Validation.error464()).and(Validation.error412()).and(Validation.error479()).and(Validation.error466()).and(Validation.error413()).and(Validation.error405()).and(Validation.error481()).and(Validation.error478()).and(Validation.error453()).and(Validation.error443()).and(Validation.error456()).and(Validation.error463()).and(Validation.error999());
		ValidationResult result = validation.apply(validateParam);

		errorCode = result.getReason();
		if (errorCode != null && errorCode.isPresent()) {
			return errorCode.get();
		}

		return null;
	}
	
	private static Short validateGetFromStoreVersion1411(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn)
			throws IOException {
		Optional<Short> errorCode = null;
		// error checking for input parameters for version 1.4.1.1
		final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
		Validation validation = Validation.error407().and(Validation.error408()).and(Validation.error409()).and(Validation.error410()).and(Validation.error426()).and(Validation.error425()).and(Validation.error475()).and(Validation.error476()).and(Validation.error466()).and(Validation.error402()).and(Validation.error479()).and(Validation.error427()).and(Validation.error428()).and(Validation.error477()).and(Validation.error460()).and(Validation.error461()).and(Validation.error462()).and(Validation.error429()).and(Validation.error482()).and(Validation.error999());
		ValidationResult result = validation.apply(validateParam);

		errorCode = result.getReason();
		if (errorCode != null && errorCode.isPresent()) {
			return errorCode.get();
		}

		return null;
	}
	
	private static Short validateUpdateInStoreVersion1411(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn)
			throws IOException {
		Optional<Short> errorCode = null;
		// error checking for input parameters for version 1.4.1.1
		final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
		Validation validation = Validation.error407().and(Validation.error408()).and(Validation.error409()).and(Validation.error426()).and(Validation.error479()).and(Validation.error466()).and(Validation.error433()).and(Validation.error464()).and(Validation.error415()).and(Validation.error444()).and(Validation.error401()).and(Validation.error484()).and(Validation.error445()).and(Validation.error464()).and(Validation.error453()).and(Validation.error443()).and(Validation.error446()).and(Validation.error456()).and(Validation.error463()).and(Validation.error480()).and(Validation.error436()).and(Validation.error434()).and(Validation.error449()).and(Validation.error451()).and(Validation.error452()).and(Validation.error999());
		ValidationResult result = validation.apply(validateParam);

		errorCode = result.getReason();
		if (errorCode != null && errorCode.isPresent()) {
			return errorCode.get();
		}

		return null;
	}
	
	private static Short validateDeleteInStoreVersion1411(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn)
			throws IOException {
		Optional<Short> errorCode = null;
		// error checking for input parameters for version 1.4.1.1
		final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
		Validation validation = Validation.error407().and(Validation.error408()).and(Validation.error433()).and(Validation.error414()).and(Validation.error472()).and(Validation.error466()).and(Validation.error415()).and(Validation.error444()).and(Validation.error416()).and(Validation.error417()).and(Validation.error418()).and(Validation.error419()).and(Validation.error420()).and(Validation.error437()).and(Validation.error999());
		ValidationResult result = validation.apply(validateParam);

		errorCode = result.getReason();
		if (errorCode != null && errorCode.isPresent()) {
			return errorCode.get();
		}

		return null;
	}

	/**
	 * This method validates input parameters for getFromStore in StoreImpl, if the
	 * input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @param version
	 * @return an error code according to the specs after checking the parameters.
	 * @throws IOException
	 */
	public static Short validateGetFromStore(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn,
			String version) throws IOException {
		LOG.info("validating input for getFromStore");
		WITSMLVERSION witsmlVersion = WITSMLVERSION.valueOf(version);

		switch (witsmlVersion) {
		case v1311:
			LOG.info("Going to validate witsml version 1.3.1.1");
			break;

		case v1411:
			LOG.info("Going to validate witsml version 1.4.1.1");
			validateGetFromStoreVersion1411(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
			break;

		default:
			LOG.info("invalid witsml version");
			break;
		}

		return null;
	}

	/**
	 * This method validates input parameters for updateInStore in StoreImpl, if the
	 * input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @param version
	 * @return an error code according to the specs after checking the parameters.
	 * @throws IOException
	 */
	public static Short validateUpdateInStore(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn,
			String version) throws IOException {
		LOG.info("validating input for updateInStore");
		WITSMLVERSION witsmlVersion = WITSMLVERSION.valueOf(version);

		switch (witsmlVersion) {
		case v1311:
			LOG.info("Going to validate witsml version 1.3.1.1");
			break;

		case v1411:
			LOG.info("Going to validate witsml version 1.4.1.1");
			validateUpdateInStoreVersion1411(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
			break;

		default:
			LOG.info("invalid witsml version");
			break;
		}

		return null;
	}

	/**
	 * This method validates input parameters for deleteFromStore in StoreImpl, if
	 * the input is not according to the specs it will return an error code.
	 * 
	 * @param WMLtypeIn
	 * @param XMLin
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @param version
	 * @return an error code according to the specs after checking the parameters.
	 * @throws IOException
	 */
	public static Short validateDeleteFromStore(String WMLtypeIn, String XMLin, String OptionsIn,
			String CapabilitiesIn, String version) throws IOException {
		LOG.info("validating input for deleteInStore");
		WITSMLVERSION witsmlVersion = WITSMLVERSION.valueOf(version);

		switch (witsmlVersion) {
		case v1311:
			LOG.info("Going to validate witsml version 1.3.1.1");
			break;

		case v1411:
			LOG.info("Going to validate witsml version 1.4.1.1");
			validateDeleteInStoreVersion1411(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
			break;

		default:
			LOG.info("invalid witsml version");
			break;
		}
		return null;
	}

	/**
	 * This method gets the errorMessage from the basemessages.properties file based
	 * on the errorCode
	 * 
	 * @param errorCode
	 * @return
	 * @throws IOException
	 */
	public static String getErrorMessage(Short errorCode) throws IOException {
		LOG.info("Checking the basemessage.properties file for errorMessages");
		Properties prop = new Properties();
		InputStream input = null;
		String basemessages = "resources/basemessages.properties";
		input = QueryValidation.class.getResourceAsStream(basemessages);
		if (input == null) {
			LOG.info("Error loading the basemessages.properties file");
		}
		prop.load(input);
		String errorM = "basemessages." + errorCode;
		LOG.info("The error Code is :" + errorM);
		return prop.getProperty(errorM);
	}

	enum WITSMLVERSION {
		v1311("1.3.1.1"), v1411("1.4.1.1");

		private String value;

		public static Map<String, WITSMLVERSION> typeMapping = new HashMap<String, QueryValidation.WITSMLVERSION>();
		static {
			typeMapping.put(v1311.name(), v1311);
			typeMapping.put(v1411.name(), v1411);
		}

		public static WITSMLVERSION getType(String typeName) {
			if (typeMapping.get(typeName) == null) {
				throw new RuntimeException(String.format("There is no Type mapping with name (%s)"));
			}
			return typeMapping.get(typeName);
		}

		private WITSMLVERSION(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}

	enum ERRORCODE {
		ERROR_401("-401"), ERROR_402("-402"), ERROR_403("-403"), ERROR_404("404"), ERROR_405("405"), ERROR_406("406"), ERROR_407("-407"), ERROR_408("-408"), ERROR_409("-409"), ERROR_410("-410"), ERROR_411("-411"), ERROR_412("-412"), ERROR_413("-413"), ERROR_414("-414"), ERROR_415("-415"), ERROR_416("-416"), ERROR_417("-417"), ERROR_418("-418"), ERROR_419("-419"), ERROR_420("-420"), ERROR_421("-421"), ERROR_422("-422"), ERROR_423("-423"), ERROR_424("-424"), ERROR_425("-425"), ERROR_426("-426"), ERROR_427("-427"), ERROR_428("-428"), ERROR_429("-429"), ERROR_430("-430"), ERROR_431("-431"), ERROR_432("-432"), ERROR_433("-433"), ERROR_434("-434"), ERROR_435("-435"), ERROR_436("-436"), ERROR_437("-437"), ERROR_438("-438"), ERROR_439("-439"), ERROR_440("-440"), ERROR_441("-441"), ERROR_442("-442"), ERROR_443("-443"), ERROR_444("-444"), ERROR_445("-445"), ERROR_446("-446"), ERROR_447("-447"), ERROR_448("-448"), ERROR_449("-449"), ERROR_450("-450"), ERROR_451("-451"), ERROR_452("-452"), ERROR_453("-453"), ERROR_454("-454"), ERROR_455("-455"), ERROR_456("-456"), ERROR_457("-457"), ERROR_458("-458"), ERROR_459("-459"), ERROR_460("-460"), ERROR_461("-461"), ERROR_462("-462"), ERROR_463("-463"), ERROR_464("-464"), ERROR_465("-465"), ERROR_466("-466"), ERROR_467("-467"), ERROR_468("-468"), ERROR_469("-469"), ERROR_470("-470"), ERROR_471("-471"), ERROR_472("-472"), ERROR_473("-473"), ERROR_474("-474"), ERROR_475("-475"), ERROR_476("-476"), ERROR_477("-477"), ERROR_478("-478"), ERROR_479("-479"), ERROR_480("-480"), ERROR_481("-481"), ERROR_482("-482"), ERROR_483("-483"), ERROR_484("-484"), ERROR_485("-485"), ERROR_486("-486"), ERROR_487("-487"), ERROR_999("-999");

		private String value;

		private ERRORCODE(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}
	}
}
