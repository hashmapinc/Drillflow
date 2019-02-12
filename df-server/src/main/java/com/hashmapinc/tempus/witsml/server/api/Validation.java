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
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsCommonData;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsGeodeticModel;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLocalCRS;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLocation;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsProjectionx;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsReferencePoint;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsWellCRS;
import com.hashmapinc.tempus.WitsmlObjects.v1311.CsWellDatum;
import com.hashmapinc.tempus.WitsmlObjects.v1311.LengthMeasure;
import com.hashmapinc.tempus.WitsmlObjects.v1311.MeasuredDepthCoord;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell;
import com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore;
import com.hashmapinc.tempus.WitsmlObjects.v1311.TimeMeasure;
import com.hashmapinc.tempus.WitsmlObjects.v1311.WellElevationCoord;
import com.hashmapinc.tempus.WitsmlObjects.v1311.WellVerticalDepthCoord;
import com.hashmapinc.tempus.witsml.WitsmlException;
import com.hashmapinc.tempus.witsml.WitsmlObjectParser;
import com.hashmapinc.tempus.witsml.WitsmlUtil;
import com.hashmapinc.tempus.witsml.server.api.QueryValidation.ERRORCODE;
import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetFromStoreResponse;
import com.hashmapinc.tempus.witsml.server.api.pojo.Location;
import com.hashmapinc.tempus.witsml.server.api.pojo.WellLocation;

interface Validation extends Function<ValidateParam, ValidationResult> {

	static final Logger LOG = Logger.getLogger(Validation.class.getName());
	public static StoreImpl store = new StoreImpl();

	public static String uidExpression = "//*[@uid]";
	public static String uomExpression = "//*[@uom]";
	public static String WELL_XML_TAG = "well";
	public static String WELLS_XML_TAG = "wells";
	public static String LOG_XML_TAG = "logData";
	public static String uidAttribute = "uid";
	public static String uomAttribute = "uom";
	public static List<AbstractWitsmlObject> witsmlObjects = null;
	public static String version = null;

	static Validation error401() {
		return holds(param -> !checkWell(param.getXMLin()), ERRORCODE.ERROR_401.value());
	}

	static Validation error402() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_402.value());
	}

	static Validation error403() {
		return holds(param -> !checkNameSpace(param.getXMLin()), ERRORCODE.ERROR_403.value());
	}

	static Validation error404() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_404.value());
	}

	static Validation error405() {
		return holds(param -> !checkExistingUID(param.getWMLtypeIn(), param.getXMLin(), param.getOptionsIn(),
				param.getCapabilitiesIn()), ERRORCODE.ERROR_405.value());
	}

	static Validation error406() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_406.value());
	}

	static Validation error407() {
		return holds(param -> !checkWMLTypeEmpty(param.getWMLtypeIn()), ERRORCODE.ERROR_407.value());
	}

	static Validation error408() {
		return holds(param -> !checkXMLEmpty(param.getXMLin()), ERRORCODE.ERROR_408.value());
	}

	static Validation error409() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_409.value());
	}

	static Validation error410() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_410.value());
	}

	static Validation error411() {
		return holds(param -> !checkOptionsForEncoding(param.getOptionsIn()), ERRORCODE.ERROR_411.value());
	}

	static Validation error412() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_412.value());
	}

	static Validation error413() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_413.value());
	}

	// checks if the user have the delete rights
	static Validation error414() {
		return holds(param -> false, ERRORCODE.ERROR_414.value());
	}

	static Validation error415() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_415.value());
	}

	static Validation error416() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_416.value());
	}

	static Validation error417() {
		return holds(param -> !checkNotNullUOM(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_417.value());
	}

	static Validation error418() {
		return holds(param -> !checkUniqueUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_418.value());
	}

	static Validation error419() {
		return holds(param -> !checkNodeValue(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_419.value());
	}

	static Validation error420() {
		return holds(param -> !checkNodeValue(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_420.value());
	}

	// checks for return after delete call
	static Validation error421() {
		return holds(param -> true, ERRORCODE.ERROR_421.value());
	}

	// checks for GetBaseMessage input
	static Validation error422() {
		return holds(param -> true, ERRORCODE.ERROR_422.value());
	}

	// checks for GetCap
	static Validation error423() {
		return holds(param -> true, ERRORCODE.ERROR_423.value());
	}

	// checks for data version of OptionsIn for GetCap
	static Validation error424() {
		return holds(param -> !checkDataVerison(param.getCapabilitiesIn()), ERRORCODE.ERROR_424.value());
	}

	static Validation error425() {
		return holds(param -> !checkOptionsInHeader(param.getOptionsIn()), ERRORCODE.ERROR_425.value());
	}

	static Validation error429() {
		return holds(param -> !checkLogData(param.getXMLin()), ERRORCODE.ERROR_429.value());
	}

	static Validation error432() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_432.value());
	}

	static Validation error433() {
		return holds(param -> !checkExistingUID(param.getWMLtypeIn(), param.getXMLin(), param.getOptionsIn(),
				param.getCapabilitiesIn()), ERRORCODE.ERROR_433.value());
	}

	static Validation error434() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_434.value());
	}

	static Validation error437() {
		return holds(param -> !checkMnemonicListUnique(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_437.value());
	}

	static Validation error438() {
		return holds(param -> checkMnemonicListUnique(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_438.value());
	}

	static Validation error439() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_439.value());
	}

	static Validation error444() {
		return holds(param -> !checkWellforDelete(param.getWMLtypeIn(), param.getXMLin(), param.getOptionsIn(),
				param.getCapabilitiesIn()), ERRORCODE.ERROR_444.value());
	}

	static Validation error445() {
		return holds(param -> !checkNodeValue(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_445.value());
	}

	static Validation error447() {
		return holds(param -> !checkUniqueUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_447.value());
	}

	static Validation error448() {
		return holds(param -> !checkNotNullUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_448.value());
	}

	static Validation error449() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_449.value());
	}

	static Validation error450() {
		return holds(param -> !checkMnemonicListUnique(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_450.value());
	}

	static Validation error453() {
		return holds(param -> !checkNotNullUOM(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_453.value());
	}

	static Validation error459() {
		return holds(param -> !checkMnemonicForSpecialCharacters(param.getXMLin()), ERRORCODE.ERROR_459.value());
	}

	static Validation error461() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_461.value());
	}

	static Validation error462() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_462.value());
	}

	static Validation error463() {
		return holds(param -> !checkUniqueUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_463.value());
	}

	static Validation error464() {
		return holds(param -> !checkUniqueUid(param.getXMLin(), param.getWMLtypeIn()), ERRORCODE.ERROR_464.value());
	}

	static Validation error468() {
		return holds(param -> !checkSchemaVersion(param.getXMLin()), ERRORCODE.ERROR_468.value());
	}

	static Validation error469() {
		return holds(param -> !validateSchemaCheck(param.getXMLin()), ERRORCODE.ERROR_469.value());
	}

	static Validation error475() {
		return holds(param -> !checkWell(param.getXMLin()), ERRORCODE.ERROR_475.value());
	}

	static Validation error481() {
		return holds(param -> !checkExistingUID(param.getWMLtypeIn(), param.getXMLin(), param.getOptionsIn(),
				param.getCapabilitiesIn()), ERRORCODE.ERROR_481.value());
	}

	static Validation error482() {
		return holds(param -> !checkMnemonicListUnique(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_482.value());
	}

	static Validation error483() {
		return holds(param -> !validateSchemaCheck(param.getXMLin()), ERRORCODE.ERROR_483.value());
	}

	static Validation error486() {
		return holds(param -> !checkIfXMLEqualsWMLObj(param.getXMLin(), param.getWMLtypeIn()),
				ERRORCODE.ERROR_486.value());
	}

	static Validation error999() {
		// This error code is thrown if none of the custom error codes conditions are
		// met and this is the base error referring unknown base exception
		return holds(param -> false, ERRORCODE.ERROR_999.value());
	}

	static Validation holds(Predicate<ValidateParam> p, String message) {
		return param -> p.test(param) ? valid() : invalid(message);
	}

	static ValidationResult invalid(String message) {
		return new Invalid(Short.valueOf(message));

	}

	static ValidationResult valid() {
		return ValidationSupport.valid();
	}

	default Validation and(Validation other) {
		return user -> {
			final ValidationResult result = this.apply(user);
			return result.isValid() ? other.apply(user) : result;
		};
	}

	/**
	 * This method parse the XML Document
	 * 
	 * @param XMLin
	 * @return XML Document object
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void WmlType(String WMLTypein) {
		String type = WMLTypein.toString();
		System.out.println("The type is :" + type);
	}

	public static Document getXMLDocument(String XMLin) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(XMLin));
		Document doc = builder.parse(is);
		doc.getDocumentElement().normalize();

		return doc;
	}

	/**
	 * This method validates the XMLin against the schemaLocation.
	 * 
	 * @param xmlFileUrl
	 * @param schemaFileUrl
	 * @return true if the XMLin is validated
	 */
	static boolean schemaValidate(String xmlFileUrl, String schemaFileUrl) {
		Objects.requireNonNull(xmlFileUrl);
		Objects.requireNonNull(schemaFileUrl);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(new URL(schemaFileUrl));

			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(xmlFileUrl));
			return true;
		} catch (SAXException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method checks for empty WMLType
	 * 
	 * @param WMLType
	 * @return true if empty else false
	 */
	static boolean checkWMLTypeEmpty(String WMLType) {
		boolean result = false;
		LOG.info("The WMLType in is : " + WMLType);
		if (WMLType.trim().isEmpty()) {
			result = true;
		}
		return result;
	}

	/**
	 * This method checks for empty WMLType
	 * 
	 * @param WMLType
	 * @return true if empty else false
	 */
	static boolean checkDataVerison(String OptionsIn) {
		boolean result = false;
		if (OptionsIn.trim().isEmpty()) {
			result = true;
		}
		return result;
	}

	/**
	 * This method checks for XML empty
	 * 
	 * @param XMLin
	 * @return true if empty else false
	 */
	static boolean checkXMLEmpty(String XMLin) {
		boolean result = false;
		if (XMLin.trim().isEmpty()) {
			result = true;
		}
		return result;
	}

	/**
	 * This method checks for XML empty
	 * 
	 * @param XMLin
	 * @return true if empty else false
	 */
	static boolean checkCapabilitiesEmpty(String Capabilities) {
		boolean result = false;
		if (Capabilities.trim().isEmpty()) {
			result = true;
		}
		return result;
	}

	/**
	 * This method check if XML Object equals WML Object
	 * 
	 * @param XMLin
	 * @param WMLType
	 * @return true if equal else false
	 */
	static boolean checkIfXMLEqualsWMLObj(String XMLin, String WMLType) {
		boolean result = false;
		if (XMLin.equals(WMLType)) {
			result = true;
		}
		return result;
	}

	/**
	 * This method checks for well tag for deleteFromStore
	 * 
	 * @param XMLin
	 * @return true if available else false
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */

	static boolean checkWellforDelete(String WMLtypeIn, String QueryIn, String OptionsIn, String CapabilitiesIn) {
		boolean result = false;
		WMLS_GetFromStoreResponse resp = new WMLS_GetFromStoreResponse();
		try {
			resp = store.getFromStore(WMLtypeIn, QueryIn, OptionsIn, CapabilitiesIn);
			if (resp.getResult() == 1) {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks for multiple well tag in XMLin
	 * 
	 * @param XMLin
	 * @return true if exists else false
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkWell(String XMLin) {
		boolean result = false;

		try {
			if (!XMLin.contains("wells")) {
				result = true;
			}

		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}
		return result;
	}

	/**
	 * This method validates the XMlin schema
	 * 
	 * @param XMLin
	 * @return true if schema doesn't match
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean validateSchemaCheck(String XMLin) {
		boolean result = false;
		Document doc;
		try {
			doc = getXMLDocument(XMLin);
			NodeList nodeList = doc.getElementsByTagName("wells");
			Element eElement = (Element) nodeList;
			String schemaLocation = eElement.getAttribute("xsi:schemaLocation");
			if (!schemaValidate(XMLin, schemaLocation)) {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks checks for the XMl version as supported by the server.
	 * 
	 * @param XMLin
	 * @return true if they do not match.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkSchemaVersion(String XMLin) {
		boolean result = false;
		try {
			String version = WitsmlUtil.getVersionFromXML(XMLin);
			if (version != "1.3.1.1" || version != "1.4.1.1") {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}

		return result;
	}

	/**
	 * This method checks NameSpace for XMLin
	 * 
	 * @param XMLin
	 * @return true if check fails
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkNameSpace(String XMLin) {
		boolean result = false;
		try {
			if (!XMLin.contains("xmlns")) {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks for logData tag in XMLin
	 * 
	 * @param XMLin
	 * @return true if exists else false
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkLogData(String XMLin) {
		boolean result = false;
		try {

			if (!XMLin.contains(LOG_XML_TAG)) {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks for UID to be null.
	 * 
	 * @param XMLin
	 * @return true if uid is null else false
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkNotNullUid(String XMLin, String WMLTypein) {
		boolean result = false;
		List<AbstractWitsmlObject> witsmlObjects;
		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			witsmlObjects = WitsmlObjectParser.parse(WMLTypein, XMLin, version);
			switch (WMLTypein) {
			case "log":
				result = checkNotNullUidForDiffVersionLog(witsmlObjects);
			case "trajectory":
				//result = checkNotNullUidForDiffVersionTraj(witsmlObjects);
			case "well":
				result = checkNotNullUidForDiffVersionWell(witsmlObjects);
			case "wellbore":
				//result = checkNotNullUidForDiffVersionWellbore(witsmlObjects);
			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is " + e.getMessage());
		}
		return result;
	}

	static boolean checkNotNullUidForDiffVersionLog(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjLog) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}
				List<CsLogCurveInfo> logCurveInfos = ((ObjLog) abstractWitsmlObject).getLogCurveInfo();
				for (CsLogCurveInfo csLogCurveInfo : logCurveInfos) {
					if (csLogCurveInfo.getUid() == null
							|| (csLogCurveInfo.getUid() != null && csLogCurveInfo.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
				

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> logCurveInfos = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) abstractWitsmlObject)
						.getLogCurveInfo();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo csLogCurveInfo : logCurveInfos) {
					if (csLogCurveInfo.getUid() == null
							|| (csLogCurveInfo.getUid() != null && csLogCurveInfo.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
			}
		}

		return result;
	}
	
	static boolean checkNotNullUidForDiffVersionWell(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWell) {
				LOG.info("checking well object ");
				ObjWell objWell1311 = (ObjWell) abstractWitsmlObject;
				if (objWell1311.getUid() == null
						|| (objWell1311.getUid() != null && objWell1311.getUid().isEmpty())) {
					result=true;
					break;
				}
				List<CsReferencePoint> wellRefrenceinfo = objWell1311.getReferencePoint();
				for (CsReferencePoint refrencePoint : wellRefrenceinfo) {
					if (refrencePoint.getUid() == null
							|| (refrencePoint.getUid() != null && refrencePoint.getUid().isEmpty())) {
						result=true;
						break;
					}
				}
				List<CsWellDatum> wellDatum = objWell1311.getWellDatum();
				for (CsWellDatum datum : wellDatum) {
					if(datum.getUid()==null||(datum.getUid()!=null && datum.getUid().isEmpty())) {
						result=true;
						break;
					}
				}
				List<CsLocation> wellLocation = objWell1311.getWellLocation();
				for (CsLocation location : wellLocation) {
					if(location.getUid()==null||(location.getUid()!=null && location.getUid().isEmpty())) {
						result=true;
						break;
					}
				}
				List<CsWellCRS> wellCRS = objWell1311.getWellCRS();
				for (CsWellCRS crs : wellCRS) {
					if(crs.getUid()==null||(crs.getUid()!=null && crs.getUid().isEmpty())) {
						result=true;
						break;
					}
				}
				

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint> wellRefrenceinfo = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getReferencePoint();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint refrencePoint : wellRefrenceinfo) {
					if (refrencePoint.getUid() == null
							|| (refrencePoint.getUid() != null && refrencePoint.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum> wellDatum = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getWellDatum();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum datum : wellDatum) {
					if(datum.getUid()==null||(datum.getUid()!=null && datum.getUid().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation> wellLocation = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellLocation();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation location : wellLocation) {
					if(location.getUid()==null||(location.getUid()!=null && location.getUid().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS> wellCRS = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellCRS();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS crs : wellCRS) {
					if(crs.getUid()==null||(crs.getUid()!=null && crs.getUid().isEmpty())) {
						result=true;
						break;
					}
				}
			}
		}

		return result;
	}

	
	/**
	 * This method checks for the Node Value to be empty or blank.
	 * 
	 * @param XMLin
	 * @return true is empty node value is found.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkNodeValue(String XMLin, String WMLTypein) {
		boolean result = false;
		List<AbstractWitsmlObject> witsmlObjects;
		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			witsmlObjects = WitsmlObjectParser.parse(WMLTypein, XMLin, version);
			switch (WMLTypein) {
			case "log":
				//result = checkNotNullUidForDiffVersionLog(witsmlObjects);
			case "trajectory":
				//result = checkNotNullUidForDiffVersionTraj(witsmlObjects);
			case "well":
				result = checkNotNullNodeForDiffVersionWell(witsmlObjects);
			case "wellbore":
				result = checkNotNullNodeForDiffVersionWellbore(witsmlObjects);
			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is the " + e.getMessage());
		}
		return result;
	}
	

	static boolean checkNotNullNodeForDiffVersionWell(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWell) {
				LOG.info("checking well object ");
				ObjWell objWell1311 = (ObjWell) abstractWitsmlObject;
				if (objWell1311.getUid() == null
						|| (objWell1311.getUid() != null && objWell1311.getUid().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getName()==null||(objWell1311.getName() != null && objWell1311.getName().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getNameLegal()==null||(objWell1311.getNameLegal() != null && objWell1311.getNameLegal().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getNumLicense()==null||(objWell1311.getNumLicense() != null && objWell1311.getNumLicense().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getNumGovt()==null||(objWell1311.getNumGovt() != null && objWell1311.getNumGovt().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getField()==null||(objWell1311.getField() != null && objWell1311.getField().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getCountry()==null||(objWell1311.getCountry() != null && objWell1311.getCountry().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getRegion()==null||(objWell1311.getRegion() != null && objWell1311.getRegion().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getDistrict()==null||(objWell1311.getDistrict() != null && objWell1311.getDistrict().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getBlock()==null||(objWell1311.getBlock() != null && objWell1311.getBlock().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getTimeZone()==null||(objWell1311.getTimeZone() != null && objWell1311.getTimeZone().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getOperator()==null||(objWell1311.getOperator() != null && objWell1311.getOperator().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getOperatorDiv()==null||(objWell1311.getOperatorDiv() != null && objWell1311.getOperatorDiv().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getPcInterest()==null||(objWell1311.getPcInterest() != null && objWell1311.getPcInterest().toString().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getNumAPI()==null||(objWell1311.getNumAPI() != null && objWell1311.getNumAPI().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getStatusWell()==null||(objWell1311.getStatusWell() != null && objWell1311.getStatusWell().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getPurposeWell()==null||(objWell1311.getPurposeWell() != null && objWell1311.getPurposeWell().isEmpty())) {					
						result=true;
					break;
				}
				else if(objWell1311.getDTimSpud()==null||(objWell1311.getDTimSpud() != null && objWell1311.getDTimSpud().isEmpty())) {
					result=true;
					break;
				}
				else if(objWell1311.getDTimPa()==null||(objWell1311.getDTimPa() != null && objWell1311.getDTimPa().isEmpty())) {					
						result=true;
					break;
				}
				else if(objWell1311.getWellheadElevation()==null||(objWell1311.getWellheadElevation() != null && objWell1311.getWellheadElevation().toString().isEmpty())) {					
					result=true;
				break;
				}
				else if(objWell1311.getGroundElevation()==null||(objWell1311.getGroundElevation() != null && objWell1311.getGroundElevation().toString().isEmpty())) {					
					result=true;
				break;
				}
				else if(objWell1311.getWaterDepth()==null||(objWell1311.getWaterDepth() != null && objWell1311.getWaterDepth().toString().isEmpty())) {					
					result=true;
				break;
				}
				List<CsWellDatum> wellDatum = objWell1311.getWellDatum();
				for(CsWellDatum datum : wellDatum) {
					if(datum.getName()==null||(datum.getName()!=null&&datum.getName().isEmpty())) {
						result=true;
						break;
					}
					if(datum.getCode()==null||(datum.getCode()!=null&&datum.getCode().toString().isEmpty())) {
						result=true;
						break;
					}
					if(datum.getElevation()==null||(datum.getElevation()!=null&&datum.getElevation().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<CsLocation> wellLocation = objWell1311.getWellLocation();
				for(CsLocation location : wellLocation) {
					if(location.getWellCRS()==null||(location.getWellCRS()!=null&&location.getWellCRS().toString().isEmpty())) {
						result=true;
						break;
					}
					if(location.getEasting()==null||(location.getEasting()!=null&&location.getEasting().toString().isEmpty())) {
						result=true;
						break;
					}
					if(location.getNorthing()==null||(location.getNorthing()!=null&&location.getNorthing().toString().isEmpty())) {
						result=true;
						break;
					}
					if(location.getDescription()==null||(location.getDescription()!=null&&location.getDescription().isEmpty())) {
						result=true;
						break;
					}
				}
				List<CsReferencePoint> referencePoint = objWell1311.getReferencePoint();
				for(CsReferencePoint reference : referencePoint) {
					if(reference.getName()==null||(reference.getName()!=null&&reference.getName().isEmpty())) {
						result=true;
						break;
					}
					if(reference.getType()==null||(reference.getType()!=null&&reference.getType().isEmpty())) {
						result=true;
						break;
					}
					if(reference.getElevation()==null||(reference.getElevation()!=null&&reference.getElevation().toString().isEmpty())) {
						result=true;
						break;
					}
					if(reference.getMeasuredDepth()==null||(reference.getMeasuredDepth()!=null&&reference.getMeasuredDepth().toString().isEmpty())) {
						result=true;
						break;
					}
					List<CsLocation> location = objWell1311.getWellLocation();
					for(CsLocation loc : location) {
						if(loc.getWellCRS()==null||(loc.getWellCRS()!=null&&loc.getWellCRS().toString().isEmpty())) {
							result=true;
							break;
						}
						if(loc.getEasting()==null||(loc.getEasting()!=null&&loc.getEasting().toString().isEmpty())) {
							result=true;
							break;
						}
						if(loc.getNorthing()==null||(loc.getNorthing()!=null&&loc.getNorthing().toString().isEmpty())) {
							result=true;
							break;
						}
						if(loc.getLocalX()==null||(loc.getLocalX()!=null&&loc.getLocalX().toString().isEmpty())) {
							result=true;
							break;
						}
						if(loc.getLocalY()==null||(loc.getLocalY()!=null&&loc.getLocalY().toString().isEmpty())) {
							result=true;
							break;
						}
						if(loc.getDescription()==null||(loc.getDescription()!=null&&loc.getDescription().toString().isEmpty())) {
							result=true;
							break;
						}
						if(loc.getLongitude()==null||(loc.getLongitude()!=null&&loc.getLongitude().toString().isEmpty())) {
							result=true;
							break;
						}
						if(loc.getLatitude()==null||(loc.getLatitude()!=null&&loc.getLatitude().toString().isEmpty())) {
							result=true;
							break;
						}
					}
					
				}
				List<CsWellCRS> wellCRS = objWell1311.getWellCRS();
				for(CsWellCRS crs : wellCRS) {
					if(crs.getName()==null||(crs.getName()!=null&&crs.getName().isEmpty())) {
						result=true;
						break;
					}
					List<CsGeodeticModel> geographic = (List<CsGeodeticModel>) crs.getGeographic();
					for(CsGeodeticModel geo : geographic) {
						if(geo.getNameCRS()==null||(geo.getNameCRS()!=null&&geo.getNameCRS().toString().isEmpty())) {
							result=true;
							break;
						}
						if(geo.getGeodeticDatumCode()==null||(geo.getGeodeticDatumCode()!=null&&geo.getGeodeticDatumCode().toString().isEmpty())) {
							result=true;
							break;
						}
						if(geo.getXTranslation()==null||(geo.getXTranslation()!=null&&geo.getXTranslation().toString().isEmpty())) {
							result=true;
							break;
						}
						if(geo.getYTranslation()==null||(geo.getYTranslation()!=null&&geo.getYTranslation().toString().isEmpty())) {
							result=true;
							break;
						}
						if(geo.getZTranslation()==null||(geo.getZTranslation()!=null&&geo.getZTranslation().toString().isEmpty())) {
							result=true;
							break;
						}
						if(geo.getXRotation()==null||(geo.getXRotation()!=null&&geo.getXRotation().toString().isEmpty())) {
							result=true;
							break;
						}
						if(geo.getYRotation()==null||(geo.getYRotation()!=null&&geo.getYRotation().toString().isEmpty())) {
							result=true;
							break;
						}
						if(geo.getZRotation()==null||(geo.getZRotation()!=null&&geo.getZRotation().toString().isEmpty())) {
							result=true;
							break;
						}
						if(geo.getScaleFactor()==null||(geo.getScaleFactor()!=null&&geo.getScaleFactor().toString().isEmpty())) {
							result=true;
							break;
						}
						if(geo.getEllipsoidCode()==null||(geo.getEllipsoidCode()!=null&&geo.getEllipsoidCode().toString().isEmpty())) {
							result=true;
							break;
						}
						if(geo.getEllipsoidSemiMajorAxis()==null||(geo.getEllipsoidSemiMajorAxis()!=null&&geo.getEllipsoidSemiMajorAxis().toString().isEmpty())) {
							result=true;
							break;
						}
						if(geo.getEllipsoidInverseFlattening()==null||(geo.getEllipsoidInverseFlattening()!=null&&geo.getEllipsoidInverseFlattening().toString().isEmpty())) {
							result=true;
							break;
						}
					}
					List<CsProjectionx> mapProjection = (List<CsProjectionx>) crs.getMapProjection();
					for(CsProjectionx projection : mapProjection) {
						if(projection.getNameCRS()==null||projection.getNameCRS()!=null&&projection.getNameCRS().toString().isEmpty()) {
							result=true;
							break;
						}
						if(projection.getProjectionCode()==null||projection.getProjectionCode()!=null&&projection.getProjectionCode().toString().isEmpty()) {
							result=true;
							break;
						}
						if(projection.getProjectedFrom()==null||projection.getProjectedFrom()!=null&&projection.getProjectedFrom().toString().isEmpty()) {
							result=true;
							break;
						}
						if(projection.getZone()==null||projection.getZone()!=null&&projection.getZone().toString().isEmpty()) {
							result=true;
							break;
						}
					}
					
					List<CsLocalCRS> localCRS = (List<CsLocalCRS>) crs.getLocalCRS();
					for(CsLocalCRS local : localCRS) {
						if(local.getYAxisAzimuth()==null||local.getYAxisAzimuth()!=null&&local.getYAxisAzimuth().toString().isEmpty()) {
							result=true;
							break;
						}
						if(local.getYAxisDescription()==null||local.getYAxisDescription()!=null&&local.getYAxisDescription().toString().isEmpty()) {
							result=true;
							break;
						}
						if(local.isUsesWellAsOrigin()==null||local.isUsesWellAsOrigin()!=null&&local.isUsesWellAsOrigin().toString().isEmpty()) {
							result=true;
							break;
						}
						if(local.isXRotationCounterClockwise()==null||local.isXRotationCounterClockwise()!=null&&local.isXRotationCounterClockwise().toString().isEmpty()) {
							result=true;
							break;
						}
					}
				}
				List<CsCommonData> commonData = (List<CsCommonData>) objWell1311.getCommonData();
				for(CsCommonData data : commonData) {
					if(data.getDTimCreation()==null||data.getDTimCreation()!=null&&data.getDTimCreation().toString().isEmpty()) {
						result=true;
						break;
					}
					if(data.getDTimLastChange()==null||data.getDTimLastChange()!=null&&data.getDTimLastChange().toString().isEmpty()) {
						result=true;
						break;
					}
					if(data.getItemState()==null||data.getItemState()!=null&&data.getItemState().toString().isEmpty()) {
						result=true;
						break;
					}
					if(data.getComments()==null||data.getComments()!=null&&data.getComments().isEmpty()) {
						result=true;
						break;
					}
				}
							

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint> wellRefrenceinfo = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getReferencePoint();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsReferencePoint refrencePoint : wellRefrenceinfo) {
					if (refrencePoint.getUid() == null
							|| (refrencePoint.getUid() != null && refrencePoint.getUid().isEmpty())) {
						result = true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum> wellDatum = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getWellDatum();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellDatum datum : wellDatum) {
					if(datum.getUid()==null||(datum.getUid()!=null && datum.getUid().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation> wellLocation = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellLocation();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLocation location : wellLocation) {
					if(location.getUid()==null||(location.getUid()!=null && location.getUid().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS> wellCRS = ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellCRS();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsWellCRS crs : wellCRS) {
					if(crs.getUid()==null||(crs.getUid()!=null && crs.getUid().isEmpty())) {
						result=true;
						break;
					}
				}
			}
		}

		return result;
	}
	
	static boolean checkNotNullNodeForDiffVersionWellbore(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWellbore) {
				LOG.info("checking wellBore object ");
				ObjWellbore objWellbore1311 = (ObjWellbore) abstractWitsmlObject;
				
				List<MeasuredDepthCoord> mdCurrent = (List<MeasuredDepthCoord>) objWellbore1311.getMdCurrent();
				for (MeasuredDepthCoord current : mdCurrent) {
					if (current.getUom() == null
							|| (current.getUom() != null && current.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<WellVerticalDepthCoord> tvdCurrent = (List<WellVerticalDepthCoord>) objWellbore1311.getTvdCurrent();
				for (WellVerticalDepthCoord vdCurrent : tvdCurrent) {
					if(vdCurrent.getUom()==null||(vdCurrent.getUom()!=null && vdCurrent.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<MeasuredDepthCoord> mdKickoff = (List<MeasuredDepthCoord>) objWellbore1311.getMdKickoff();
				for (MeasuredDepthCoord kickOff : mdKickoff) {
					if (kickOff.getUom() == null
							|| (kickOff.getUom() != null && kickOff.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<WellVerticalDepthCoord> tvdKickoff = (List<WellVerticalDepthCoord>) objWellbore1311.getTvdKickoff();
				for (WellVerticalDepthCoord vdKickOff : tvdKickoff) {
					if(vdKickOff.getUom()==null||(vdKickOff.getUom()!=null && vdKickOff.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<MeasuredDepthCoord> mdPlanned = (List<MeasuredDepthCoord>) objWellbore1311.getMdPlanned();
				for (MeasuredDepthCoord planned : mdPlanned) {
					if (planned.getUom() == null
							|| (planned.getUom() != null && planned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<WellVerticalDepthCoord> tvdPlanned = (List<WellVerticalDepthCoord>) objWellbore1311.getTvdPlanned();
				for (WellVerticalDepthCoord vdPlanned : tvdPlanned) {
					if(vdPlanned.getUom()==null||(vdPlanned.getUom()!=null && vdPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<MeasuredDepthCoord> mdSubSeaPlanned = (List<MeasuredDepthCoord>) objWellbore1311.getMdSubSeaPlanned();
				for (MeasuredDepthCoord seaPlanned : mdSubSeaPlanned) {
					if (seaPlanned.getUom() == null
							|| (seaPlanned.getUom() != null && seaPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<WellVerticalDepthCoord> tvdSubSeaPlanned = (List<WellVerticalDepthCoord>) objWellbore1311.getTvdSubSeaPlanned();
				for (WellVerticalDepthCoord tvSeaPlanned : tvdSubSeaPlanned) {
					if(tvSeaPlanned.getUom()==null||(tvSeaPlanned.getUom()!=null && tvSeaPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<TimeMeasure> dayTarget = (List<TimeMeasure>) objWellbore1311.getDayTarget();
				for (TimeMeasure target : dayTarget) {
					if(target.getUom()==null||(target.getUom()!=null && target.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) {
				
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord> mdCurrent = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord>) ((ObjWellbore) abstractWitsmlObject).getMdCurrent();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord current : mdCurrent) {
					if (current.getUom() == null
							|| (current.getUom() != null && current.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord> tvdCurrent = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord>) ((ObjWellbore) abstractWitsmlObject).getTvdCurrent();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord vdCurrent : tvdCurrent) {
					if(vdCurrent.getUom()==null||(vdCurrent.getUom()!=null && vdCurrent.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord> mdKickoff = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getMdKickoff();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord kickOff : mdKickoff) {
					if (kickOff.getUom() == null
							|| (kickOff.getUom() != null && kickOff.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord> tvdKickoff = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getTvdKickoff();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord vdKickOff : tvdKickoff) {
					if(vdKickOff.getUom()==null||(vdKickOff.getUom()!=null && vdKickOff.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord> mdPlanned = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getMdPlanned();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord planned : mdPlanned) {
					if (planned.getUom() == null
							|| (planned.getUom() != null && planned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord> tvdPlanned = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getTvdPlanned();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord vdPlanned : tvdPlanned) {
					if(vdPlanned.getUom()==null||(vdPlanned.getUom()!=null && vdPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord> mdSubSeaPlanned = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getMdSubSeaPlanned();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord seaPlanned : mdSubSeaPlanned) {
					if (seaPlanned.getUom() == null
							|| (seaPlanned.getUom() != null && seaPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord> tvdSubSeaPlanned = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getTvdSubSeaPlanned();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvSeaPlanned : tvdSubSeaPlanned) {
					if(tvSeaPlanned.getUom()==null||(tvSeaPlanned.getUom()!=null && tvSeaPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.TimeMeasure> dayTarget = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.TimeMeasure>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getDayTarget();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.TimeMeasure target : dayTarget) {
					if(target.getUom()==null||(target.getUom()!=null && target.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				
			}
		}

		return result;
	}
	


	/**
	 * This method checks for unique UID in XMLin
	 * 
	 * @param XMLin
	 * @return true if unique else false
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkUniqueUid(String XMLin, String WMLTypein) {
		boolean result = false;
		Document doc;
		try {
			doc = getXMLDocument(XMLin);
			NodeList nodeList = getNodeListForExpression(doc, uidExpression);
			Set<String> uids = new HashSet<String>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element eElement = (Element) nodeList.item(i);
				String uid = eElement.getAttribute(uidAttribute);
				LOG.info("the uid is : " + uid);
				if (uids.contains(uid)) {
					result = true;
					break;
				} else {
					uids.add(uid);
				}
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}

		return result;
	}

	/**
	 * This method checks for existing UID's with getFromStore
	 * 
	 * @param WMLtypeIn
	 * @param QueryIn
	 * @param OptionsIn
	 * @param CapabilitiesIn
	 * @return true if exists else false
	 * @throws Exception
	 */
	static boolean checkExistingUID(String WMLtypeIn, String QueryIn, String OptionsIn, String CapabilitiesIn) {
		boolean result = false;
		WMLS_GetFromStoreResponse resp = new WMLS_GetFromStoreResponse();
		try {
			resp = store.getFromStore(WMLtypeIn, QueryIn, OptionsIn, CapabilitiesIn);
			if (resp.getResult() == 1) {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks for UOM attribute to be null.
	 * 
	 * @param XMLin
	 * @return true if null else false
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkNotNullUOM(String XMLin, String WMLTypein) {
		boolean result = false;
		List<AbstractWitsmlObject> witsmlObjects;
		String version;
		try {
			version = WitsmlUtil.getVersionFromXML(XMLin);
			LOG.info("the version is: " + version);
			witsmlObjects = WitsmlObjectParser.parse(WMLTypein, XMLin, version);
			switch (WMLTypein) {
			case "log":
				//result = checkNotNullUidForDiffVersionLog(witsmlObjects);
			case "trajectory":
				//result = checkNotNullUidForDiffVersionTraj(witsmlObjects);
			case "well":
				result = checkNotNullUOMForDiffVersionWell(witsmlObjects);
			case "wellbore":
				result = checkNotNullUOMForDiffVersionWellBore(witsmlObjects);
			default:
				throw new WitsmlException("unsupported witsml object type: " + WMLTypein);
			}
		} catch (Exception e) {
			LOG.warning("the error is the " + e.getMessage());
		}
		return result;
	}
	
	static boolean checkNotNullUOMForDiffVersionWell(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWell) {
				LOG.info("checking well object ");
				ObjWell objWell1311 = (ObjWell) abstractWitsmlObject;
				
				List<WellElevationCoord> wellHeadElevation = (List<WellElevationCoord>) objWell1311.getWellheadElevation();
				for (WellElevationCoord headElevation : wellHeadElevation) {
					if (headElevation.getUom() == null
							|| (headElevation.getUom() != null && headElevation.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<WellElevationCoord> groundElevation = (List<WellElevationCoord>) objWell1311.getGroundElevation();
				for (WellElevationCoord elevation : groundElevation) {
					if(elevation.getUom()==null||(elevation.getUom()!=null && elevation.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<LengthMeasure> waterDepth = (List<LengthMeasure>) objWell1311.getWaterDepth();
				for (LengthMeasure depth : waterDepth) {
					if(depth.getUom()==null||(depth.getUom()!=null && depth.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) {
				if (abstractWitsmlObject.getUid() == null
						|| (abstractWitsmlObject.getUid() != null && abstractWitsmlObject.getUid().isEmpty())) {
					result = true;
					break;
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord> wellHeadElevation = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWellheadElevation();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord headElevation : wellHeadElevation) {
					if (headElevation.getUom() == null
							|| (headElevation.getUom() != null && headElevation.getUom().toString().isEmpty())) {
						result = true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord> groundElevation = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject).getGroundElevation();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.WellElevationCoord elevation : groundElevation) {
					if(elevation.getUom()==null||(elevation.getUom()!=null && elevation.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure> waterDepth = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) abstractWitsmlObject)
						.getWaterDepth();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure depth : waterDepth) {
					if(depth.getUom()==null||(depth.getUom()!=null && depth.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				
			}
		}

		return result;
	}
	
	static boolean checkNotNullUOMForDiffVersionWellBore(List<AbstractWitsmlObject> witsmlObjects) {

		boolean result = false;

		for (AbstractWitsmlObject abstractWitsmlObject : witsmlObjects) {
			if (abstractWitsmlObject instanceof ObjWellbore) {
				LOG.info("checking wellBore object ");
				ObjWellbore objWellbore1311 = (ObjWellbore) abstractWitsmlObject;
				
				List<MeasuredDepthCoord> mdCurrent = (List<MeasuredDepthCoord>) objWellbore1311.getMdCurrent();
				for (MeasuredDepthCoord current : mdCurrent) {
					if (current.getUom() == null
							|| (current.getUom() != null && current.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<WellVerticalDepthCoord> tvdCurrent = (List<WellVerticalDepthCoord>) objWellbore1311.getTvdCurrent();
				for (WellVerticalDepthCoord vdCurrent : tvdCurrent) {
					if(vdCurrent.getUom()==null||(vdCurrent.getUom()!=null && vdCurrent.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<MeasuredDepthCoord> mdKickoff = (List<MeasuredDepthCoord>) objWellbore1311.getMdKickoff();
				for (MeasuredDepthCoord kickOff : mdKickoff) {
					if (kickOff.getUom() == null
							|| (kickOff.getUom() != null && kickOff.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<WellVerticalDepthCoord> tvdKickoff = (List<WellVerticalDepthCoord>) objWellbore1311.getTvdKickoff();
				for (WellVerticalDepthCoord vdKickOff : tvdKickoff) {
					if(vdKickOff.getUom()==null||(vdKickOff.getUom()!=null && vdKickOff.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<MeasuredDepthCoord> mdPlanned = (List<MeasuredDepthCoord>) objWellbore1311.getMdPlanned();
				for (MeasuredDepthCoord planned : mdPlanned) {
					if (planned.getUom() == null
							|| (planned.getUom() != null && planned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<WellVerticalDepthCoord> tvdPlanned = (List<WellVerticalDepthCoord>) objWellbore1311.getTvdPlanned();
				for (WellVerticalDepthCoord vdPlanned : tvdPlanned) {
					if(vdPlanned.getUom()==null||(vdPlanned.getUom()!=null && vdPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<MeasuredDepthCoord> mdSubSeaPlanned = (List<MeasuredDepthCoord>) objWellbore1311.getMdSubSeaPlanned();
				for (MeasuredDepthCoord seaPlanned : mdSubSeaPlanned) {
					if (seaPlanned.getUom() == null
							|| (seaPlanned.getUom() != null && seaPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<WellVerticalDepthCoord> tvdSubSeaPlanned = (List<WellVerticalDepthCoord>) objWellbore1311.getTvdSubSeaPlanned();
				for (WellVerticalDepthCoord tvSeaPlanned : tvdSubSeaPlanned) {
					if(tvSeaPlanned.getUom()==null||(tvSeaPlanned.getUom()!=null && tvSeaPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<TimeMeasure> dayTarget = (List<TimeMeasure>) objWellbore1311.getDayTarget();
				for (TimeMeasure target : dayTarget) {
					if(target.getUom()==null||(target.getUom()!=null && target.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				

			} else if (abstractWitsmlObject instanceof com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) {
				
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord> mdCurrent = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord>) ((ObjWellbore) abstractWitsmlObject).getMdCurrent();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord current : mdCurrent) {
					if (current.getUom() == null
							|| (current.getUom() != null && current.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord> tvdCurrent = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord>) ((ObjWellbore) abstractWitsmlObject).getTvdCurrent();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord vdCurrent : tvdCurrent) {
					if(vdCurrent.getUom()==null||(vdCurrent.getUom()!=null && vdCurrent.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord> mdKickoff = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getMdKickoff();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord kickOff : mdKickoff) {
					if (kickOff.getUom() == null
							|| (kickOff.getUom() != null && kickOff.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord> tvdKickoff = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getTvdKickoff();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord vdKickOff : tvdKickoff) {
					if(vdKickOff.getUom()==null||(vdKickOff.getUom()!=null && vdKickOff.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord> mdPlanned = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getMdPlanned();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord planned : mdPlanned) {
					if (planned.getUom() == null
							|| (planned.getUom() != null && planned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord> tvdPlanned = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getTvdPlanned();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord vdPlanned : tvdPlanned) {
					if(vdPlanned.getUom()==null||(vdPlanned.getUom()!=null && vdPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord> mdSubSeaPlanned = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getMdSubSeaPlanned();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord seaPlanned : mdSubSeaPlanned) {
					if (seaPlanned.getUom() == null
							|| (seaPlanned.getUom() != null && seaPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord> tvdSubSeaPlanned = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getTvdSubSeaPlanned();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.WellVerticalDepthCoord tvSeaPlanned : tvdSubSeaPlanned) {
					if(tvSeaPlanned.getUom()==null||(tvSeaPlanned.getUom()!=null && tvSeaPlanned.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				List<com.hashmapinc.tempus.WitsmlObjects.v1411.TimeMeasure> dayTarget = (List<com.hashmapinc.tempus.WitsmlObjects.v1411.TimeMeasure>) ((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) abstractWitsmlObject).getDayTarget();
				for (com.hashmapinc.tempus.WitsmlObjects.v1411.TimeMeasure target : dayTarget) {
					if(target.getUom()==null||(target.getUom()!=null && target.getUom().toString().isEmpty())) {
						result=true;
						break;
					}
				}
				
			}
		}

		return result;
	}
	

	/**
	 * This methods checks for mnemonic list for empty values.
	 * 
	 * @param XMLin
	 * @return true if mnemonic list id empty.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkMnemonicListNotEmpty(String XMLin, String WMLTypein) {
		boolean result = false;
		Document doc;
		try {
			doc = getXMLDocument(XMLin);
			NodeList nodeList = getNodeListForExpression(doc, "mnemonicList");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Element eElement = (Element) nodeList.item(i);
				if (eElement.getNodeValue().isEmpty()) {
					result = true;
					break;
				}
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks for mnemonic list to be unique.
	 * 
	 * @param XMLin
	 * @return true if not unique.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkMnemonicListUnique(String XMLin, String WMLTypin) {
		boolean result = false;
		Document doc;
		try {
			doc = getXMLDocument(XMLin);
			NodeList nodeList = getNodeListForExpression(doc, "mnemonicList");

			Set<String> mnemonic = new HashSet<String>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element eElement = (Element) nodeList.item(i);
				String mnemonicValue = eElement.getNodeValue();
				if (mnemonic.contains(mnemonicValue)) {
					result = true;
					break;
				} else {
					mnemonic.add(mnemonicValue);
				}
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}

		return result;
	}

	/**
	 * This method checks for special characters in mnemonic list
	 * 
	 * @param XMLin
	 * @return true if special character is found.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	static boolean checkMnemonicForSpecialCharacters(String XMLin) {
		boolean result = false;

		try {

			String regex = "',><&//\\";

			if (!XMLin.matches(regex)) {
				result = true;
			}
		} catch (Exception e) {
			LOG.warning(e.getMessage());
		}
		return result;
	}

	/**
	 * This method checks for Encoding in OptionsIn
	 * 
	 * @param OptionsIn
	 * @return true if special character is found.
	 * 
	 */
	static boolean checkOptionsForEncoding(String OptionsIn) {
		boolean result = false;
		String regex = ";";
		if (!OptionsIn.matches(regex)) {
			result = true;
		}
		return result;
	}

	/**
	 * This method checks for header in OptionsIn
	 * 
	 * @param OptionsIn
	 * @return true if nested objects are not found
	 * 
	 */
	static boolean checkOptionsInHeader(String OptionsIn) {
		boolean result = false;
		String regex = ";";
		if (!OptionsIn.matches(regex)) {
			result = true;
		}
		return result;
	}

	static Validation checkErrorForAddtoStoreVersion1411() {
		return error407().and(error408()).and(error409()).and(error401()).and(error406()).and(error464())
				.and(error412()).and(error413()).and(error405()).and(error481()).and(error453()).and(error463())
				.and(error999());
	}

	static Validation checkErrorForAddtoStoreVersion1311() {
		return error407().and(error408()).and(error409()).and(error401()).and(error406()).and(error464())
				.and(error412()).and(error413()).and(error405()).and(error481()).and(error453()).and(error463())
				.and(error999());
	}

	static Validation checkErrorForGetFromStoreVersion1411() {
		return error407().and(error408()).and(error409()).and(error410()).and(error425()).and(error475())
				.and(error402()).and(error461()).and(error462()).and(error429()).and(error482()).and(error999());
	}

	static Validation checkErrorForGetFromStoreVersion1311() {
		return error407().and(error408()).and(error409()).and(error410()).and(error425()).and(error475())
				.and(error402()).and(error461()).and(error462()).and(error429()).and(error482()).and(error999());
	}

	static Validation checkErrorForUpdateInStoreVersion1411() {
		return error407().and(error408()).and(error409()).and(error433()).and(error464()).and(error415())
				.and(error444()).and(error401()).and(error445()).and(error464()).and(error453())
				.and(error463()).and(error434()).and(error449()).and(error999());
	}

	static Validation checkErrorForUpdateInStoreVersion1311() {
		return error407().and(error408()).and(error409()).and(error433()).and(error464()).and(error415())
				.and(error444()).and(error401()).and(error445()).and(error464()).and(error453())
				.and(error463()).and(error434()).and(error449()).and(error999());
	}

	static Validation checkErrorForDeleteInStoreVersion1411() {
		return error407().and(error408()).and(error433()).and(error414()).and(error415()).and(error444())
				.and(error416()).and(error417()).and(error418()).and(error419()).and(error420()).and(error437())
				.and(error999());
	}

	static Validation checkErrorForDeleteInStoreVersion1311() {
		return error407().and(error408()).and(error433()).and(error414()).and(error415()).and(error444())
				.and(error416()).and(error417()).and(error418()).and(error419()).and(error420()).and(error437())
				.and(error999());
	}
}
