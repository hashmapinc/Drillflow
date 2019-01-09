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
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;

import com.hashmapinc.tempus.witsml.server.api.QueryValidation.ERRORCODE;
import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetFromStoreResponse;

interface Validation extends Function<ValidateParam, ValidationResult> {

	static final Logger LOG = Logger.getLogger(Validation.class.getName());
	 public static StoreImpl store = new StoreImpl();

	    public static String uidExpression = "//*[@uid]";
	    public static String uomExpression = "//*[@uom]";
	    public static String WELL_XML_TAG = "well";
	    public static String LOG_XML_TAG = "logData";
	    public static String uidAttribute = "uid";
	    public static String uomAttribute = "uom";

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
		return holds(param -> !checkNotNullUid(param.getXMLin()), ERRORCODE.ERROR_406.value());
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
	
	//checks if the user have the delete rights
	static Validation error414() {
		return holds(param -> false, ERRORCODE.ERROR_414.value());
	}

	static Validation error415() {
		return holds(param -> !checkNotNullUid(param.getXMLin()), ERRORCODE.ERROR_415.value());
	}

	static Validation error416() {
		return holds(param -> !checkNotNullUid(param.getXMLin()), ERRORCODE.ERROR_416.value());
	}

	static Validation error417() {
		return holds(param -> !checkNotNullUOM(param.getXMLin()), ERRORCODE.ERROR_417.value());
	}

	static Validation error418() {
		return holds(param -> !checkUniqueUid(param.getXMLin()), ERRORCODE.ERROR_418.value());
	}

	static Validation error419() {
		return holds(param -> !checkNodeValue(param.getXMLin()), ERRORCODE.ERROR_419.value());
	}

	static Validation error420() {
		return holds(param -> !checkNodeValue(param.getXMLin()), ERRORCODE.ERROR_420.value());
	}
	
	//checks for return after delete call
	static Validation error421() {
		return holds(param -> true, ERRORCODE.ERROR_421.value());
	}
	
	//checks for GetBaseMessage input
	static Validation error422() {
		return holds(param -> true, ERRORCODE.ERROR_422.value());
	}
	
	//checks for GetCap 
	static Validation error423() {
		return holds(param -> true, ERRORCODE.ERROR_423.value());
	}
	
	//checks for data version of OptionsIn for GetCap
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
		return holds(param -> !checkNotNullUid(param.getXMLin()), ERRORCODE.ERROR_432.value());
	}

	static Validation error433() {
		return holds(param -> !checkExistingUID(param.getWMLtypeIn(), param.getXMLin(), param.getOptionsIn(),
				param.getCapabilitiesIn()), ERRORCODE.ERROR_433.value());
	}

	static Validation error434() {
		return holds(param -> !checkNotNullUid(param.getXMLin()), ERRORCODE.ERROR_434.value());
	}

	static Validation error437() {
		return holds(param -> !checkMnemonicListUnique(param.getXMLin()), ERRORCODE.ERROR_437.value());
	}

	static Validation error438() {
		return holds(param -> checkMnemonicListUnique(param.getXMLin()), ERRORCODE.ERROR_438.value());
	}

	static Validation error439() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin()), ERRORCODE.ERROR_439.value());
	}

	static Validation error444() {
		return holds(param -> !checkWellforDelete(param.getXMLin()), ERRORCODE.ERROR_444.value());
	}

	static Validation error445() {
		return holds(param -> !checkNodeValue(param.getXMLin()), ERRORCODE.ERROR_445.value());
	}

	static Validation error446() {
		return holds(param -> !checkUomNodeValue(param.getXMLin()), ERRORCODE.ERROR_446.value());
	}

	static Validation error447() {
		return holds(param -> !checkUniqueUid(param.getXMLin()), ERRORCODE.ERROR_447.value());
	}

	static Validation error448() {
		return holds(param -> !checkNotNullUid(param.getXMLin()), ERRORCODE.ERROR_448.value());
	}

	static Validation error449() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin()), ERRORCODE.ERROR_449.value());
	}

	static Validation error450() {
		return holds(param -> !checkMnemonicListUnique(param.getXMLin()), ERRORCODE.ERROR_450.value());
	}

	static Validation error451() {
		return holds(param -> !checkUnitList(param.getXMLin()), ERRORCODE.ERROR_451.value());
	}

	static Validation error453() {
		return holds(param -> !checkNotNullUOM(param.getXMLin()), ERRORCODE.ERROR_453.value());
	}

	static Validation error459() {
		return holds(param -> !checkMnemonicForSpecialCharacters(param.getXMLin()), ERRORCODE.ERROR_459.value());
	}

	static Validation error461() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin()), ERRORCODE.ERROR_461.value());
	}

	static Validation error462() {
		return holds(param -> !checkMnemonicListNotEmpty(param.getXMLin()), ERRORCODE.ERROR_462.value());
	}

	static Validation error463() {
		return holds(param -> !checkUniqueUid(param.getXMLin()), ERRORCODE.ERROR_463.value());
	}

	static Validation error464() {
		return holds(param -> !checkUniqueUid(param.getXMLin()), ERRORCODE.ERROR_464.value());
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
		return holds(param -> !checkExistingUID(param.getWMLtypeIn(), param.getXMLin(), param.getOptionsIn(), param.getCapabilitiesIn()), ERRORCODE.ERROR_481.value());
	}

	static Validation error482() {
		return holds(param -> !checkMnemonicListUnique(param.getXMLin()), ERRORCODE.ERROR_482.value());
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
    static Document getXMLDocument(String XMLin) throws SAXException, IOException, ParserConfigurationException {

    	URL url = new URL(XMLin);
        URLConnection urlConnection = url.openConnection();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(urlConnection.getInputStream());
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
    static boolean checkWellforDelete(String XMLin) {
        boolean result = false;
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            NodeList nodeList = doc.getElementsByTagName(WELL_XML_TAG);
            if (nodeList.getLength() > 1) {
                result = true;
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
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
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            NodeList nodeList = doc.getElementsByTagName(WELL_XML_TAG);
            if (nodeList.getLength() < 2) {
                result = true;
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
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
        } catch (SAXException | IOException | ParserConfigurationException e) {
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
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            NodeList nodeList = doc.getElementsByTagName("wells");
            Element eElement = (Element) nodeList;
            if (eElement.getAttribute("Version") != "1.3.1.1" || eElement.getAttribute("Version") != "1.4.1.1") {
                result = true;
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
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
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            NodeList nodeList = doc.getElementsByTagName("wells");
            Element eElement = (Element) nodeList;
            if (eElement.getAttribute("xmlns").isEmpty()) {
                result = true;
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
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
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            NodeList nodeList = doc.getElementsByTagName(LOG_XML_TAG);
            if (nodeList.getLength() < 2) {
                result = true;
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
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
    static boolean checkNotNullUid(String XMLin) {
        boolean result = false;
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            XPathFactory factory = XPathFactory.newInstance();

            XPath xpath = factory.newXPath();
            NodeList nodeList = (NodeList) xpath.evaluate(uidExpression, doc, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element eElement = (Element) nodeList.item(i);
                if (eElement.getAttribute(uidAttribute).equalsIgnoreCase("")
                        && eElement.getAttribute(uidAttribute).equalsIgnoreCase(null)) {
                    result = true;
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            LOG.warning(e.getMessage());
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
    static boolean checkNodeValue(String XMLin) {
        boolean result = false;
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            XPathFactory factory = XPathFactory.newInstance();

            XPath xpath = factory.newXPath();
            NodeList nodeList = (NodeList) xpath.evaluate("*", doc, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element eElement = (Element) nodeList.item(i);
                if (eElement.getNodeValue().isEmpty()) {
                    result = true;
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            LOG.warning(e.getMessage());
        }
        return result;
    }

    /**
     * Get node list for given document and expression
     * 
     * @param doc
     * @param expression
     * @return nodelist
     * @throws XPathExpressionException
     */
    static NodeList getNodeListForExpression(Document doc, String expression) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        NodeList nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        return nodeList;
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
    static boolean checkUniqueUid(String XMLin) {
        boolean result = false;
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            NodeList nodeList = getNodeListForExpression(doc, uidExpression);
            Set<String> uids = new HashSet<String>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element eElement = (Element) nodeList.item(i);
                String uid = eElement.getAttribute(uidAttribute);
                if (uids.contains(uid)) {
                    result = true;
                    break;
                } else {
                    uids.add(uid);
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
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
    static boolean checkNotNullUOM(String XMLin) {
        boolean result = false;
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            NodeList nodeList = getNodeListForExpression(doc, uomExpression);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element eElement = (Element) nodeList.item(i);
                if (eElement.getAttribute(uomAttribute).equalsIgnoreCase("")
                        && eElement.getAttribute(uomAttribute).equalsIgnoreCase(null)) {
                    result = true;
                    break;
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            LOG.warning(e.getMessage());
        }
        return result;
    }

    /**
     * This method checks for UOM node value in XMLin.
     * 
     * @param XMLin
     * @return true if null value exists.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    static boolean checkUomNodeValue(String XMLin) {
        boolean result = false;
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            NodeList nodeList = getNodeListForExpression(doc, uomExpression);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element eElement = (Element) nodeList.item(i);
                if (eElement.getNodeValue().isEmpty()) {
                    result = true;
                    break;
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            LOG.warning(e.getMessage());
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
    static boolean checkMnemonicListNotEmpty(String XMLin) {
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
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
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
    static boolean checkMnemonicListUnique(String XMLin) {
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
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            LOG.warning(e.getMessage());
        }

        return result;
    }

    /**
     * This method checks for unitList to be empty
     * 
     * @param XMLin
     * @return true if unitList is empty.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    static boolean checkUnitList(String XMLin) {
        boolean result = false;
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            NodeList nodeList = getNodeListForExpression(doc, "unitList");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element eElement = (Element) nodeList.item(i);
                if (eElement.getNodeValue().isEmpty()) {
                    result = true;
                    break;
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
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
        Document doc;
        try {
            doc = getXMLDocument(XMLin);
            NodeList nodeList = getNodeListForExpression(doc, "mnemonicList");
            String regex = "',><&//\\";
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element eElement = (Element) nodeList.item(i);
                if (eElement.getNodeValue().matches(regex)) {
                    result = true;
                    break;
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
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
  
   
    /**
     * Check uom attribute with witsml
     * 
     * @param XMLin
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    static boolean checkErrorCode(String XMLin) {
        boolean result = false;
        try {
                Document  doc = getXMLDocument(XMLin);
                NodeList nodeList = getNodeListForExpression(doc, "mnemonicList");
                String regex = "',><&//\\";
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element eElement = (Element) nodeList.item(i);
                    if (eElement.getNodeValue().matches(regex)) {
                        result = true;
                        break;
                    }
                }
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            LOG.warning(e.getMessage());
        }

        return result;
    }

    static Validation checkErrorForAddtoStoreVersion1411() {
        return error407().and(error408()).and(error409()).and(error401())
                .and(error406()).and(error464()).and(error412()).and(error413())
                .and(error405()).and(error481()).and(error453())
                .and(error463()).and(error999());
    }

    static Validation checkErrorForAddtoStoreVersion1311() {
        return error407().and(error408()).and(error409()).and(error401())
                .and(error406()).and(error464()).and(error412()).and(error413())
                .and(error405()).and(error481()).and(error453())
                .and(error463()).and(error999());
    }

    static Validation checkErrorForGetFromStoreVersion1411() {
        return error407().and(error408()).and(error409()).and(error410()).and(error425())
                .and(error475()).and(error402())
               .and(error461()).and(error462()).and(error429())
                .and(error482()).and(error999());
    }

    static Validation checkErrorForGetFromStoreVersion1311() {
    	 return error407().and(error408()).and(error409()).and(error410()).and(error425())
                 .and(error475()).and(error402())
                .and(error461()).and(error462()).and(error429())
                 .and(error482()).and(error999());
    }

    static Validation checkErrorForUpdateInStoreVersion1411() {
        return error407().and(error408()).and(error409())
                .and(error433()).and(error464()).and(error415()).and(error444()).and(error401())
                .and(error445()).and(error464()).and(error453()).and(error446())
                .and(error463()).and(error434()).and(error449()).and(error451())
                .and(error999());
    }

    static Validation checkErrorForUpdateInStoreVersion1311() {
    	 return error407().and(error408()).and(error409())
                 .and(error433()).and(error464()).and(error415()).and(error444()).and(error401())
                 .and(error445()).and(error464()).and(error453()).and(error446())
                 .and(error463()).and(error434()).and(error449()).and(error451())
                 .and(error999());
    }

    static Validation checkErrorForDeleteInStoreVersion1411() {
        return error407().and(error408()).and(error433()).and(error414())
                .and(error415()).and(error444()).and(error416()).and(error417()).and(error418()).and(error419())
                .and(error420()).and(error437()).and(error999());
    }

    static Validation checkErrorForDeleteInStoreVersion1311() {
        return error407().and(error408()).and(error433()).and(error414())
                .and(error415()).and(error444()).and(error416()).and(error417()).and(error418()).and(error419())
                .and(error420()).and(error437()).and(error999());
    }
}
