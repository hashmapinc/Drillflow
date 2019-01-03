package com.hashmapinc.tempus.witsml.server.api;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetFromStoreResponse;

public class ValidationCheck {

    public static StoreImpl store = new StoreImpl();

    public static String uidExpression = "//*[@uid]";
    public static String uomExpression = "//*[@uom]";
    public static String WELL_XML_TAG = "well";
    public static String LOG_XML_TAG = "logData";
    public static String uidAttribute = "uid";
    public static String uomAttribute = "uom";

    /**
     * Get XML Document
     * 
     * @param XMLin
     * @return XML Document object
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private static Document getXMLDocument(String XMLin)
            throws SAXException, IOException, ParserConfigurationException {
        ClassLoader classLoader = ValidationCheck.class.getClassLoader();
        File file = new File(classLoader.getResource("well1411.xml").getFile());

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    /**
     * Check if WML type empty
     * 
     * @param WMLType
     * @return true if empty else false
     */
    public static boolean checkWMLTypeEmpty(String WMLType) {
        boolean result = false;
        if (WMLType.trim().isEmpty()) {
            result = true;
        }
        return result;
    }

    /**
     * Check if XML empty
     * 
     * @param XMLin
     * @return true if empty else false
     */
    public static boolean checkXMLEmpty(String XMLin) {
        boolean result = false;
        if (XMLin.trim().isEmpty()) {
            result = true;
        }
        return result;
    }

    /**
     * Check if XML Object equals WML Object
     * 
     * @param XMLin
     * @param WMLType
     * @return true if equal else false
     */
    public static boolean checkIfXMLEqualsWMLObj(String XMLin, String WMLType) {
        boolean result = false;
        if (XMLin.equals(WMLType)) {
            result = true;
        }
        return result;
    }

    /**
     * Check well tag in XML document if available
     * 
     * @param XMLin
     * @return true if available else false
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static boolean checkWellforDelete(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        NodeList nodeList = doc.getElementsByTagName(WELL_XML_TAG);
        if (nodeList.getLength() > 1) {
            result = true;
            }
        return result;
    }

    /**
     * Check well tag in XML document
     * 
     * @param XMLin
     * @return true if exists else false
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static boolean checkWell(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        NodeList nodeList = doc.getElementsByTagName(WELL_XML_TAG);
        if (nodeList.getLength() < 2) {
            result = true;
        }
        return result;
    }
    
    public static boolean checkSchemaVersion(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        NodeList nodeList = doc.getElementsByTagName("wells");
        Element eElement = (Element) nodeList;
        if (eElement.getAttribute("Version")!="1.3.1.1"||eElement.getAttribute("Version")!="1.4.1.1") {
            result = true;
        }
        return result;
    }
    
    public static boolean checkNameSpace(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        NodeList nodeList = doc.getElementsByTagName("wells");
        Element eElement = (Element) nodeList;
        if (eElement.getAttribute("xmlns").isBlank()||eElement.getAttribute("xmlns").isEmpty()) {
            result = true;
        }
        return result;
    }
    /**
     * Check well tag in XML document
     * 
     * @param XMLin
     * @return true if exists else false
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static boolean checkLogData(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        NodeList nodeList = doc.getElementsByTagName(LOG_XML_TAG);
        if (nodeList.getLength() < 2) {
            result = true;
        }
        return result;
    }

    /**
     * Check if uid is null
     * 
     * @param XMLin
     * @return true if uid is null else false
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static boolean checkNotNullUid(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
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
        return result;
    }
    
     
    public static boolean checkNodeValue(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        XPathFactory factory = XPathFactory.newInstance();

        XPath xpath = factory.newXPath();
        NodeList nodeList = (NodeList) xpath.evaluate("*", doc, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element eElement = (Element) nodeList.item(i);
            if (eElement.getNodeValue().isEmpty()||eElement.getNodeValue().isBlank()) {
                result = true;
            }
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
    private static NodeList getNodeListForExpression(Document doc, String expression) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        NodeList nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        return nodeList;
    }

    /**
     * Check unique uid
     * 
     * @param XMLin
     * @return true if unique else false
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static boolean checkUniqueUid(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
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
        return result;
    }

    /**
     * Check existing UID
     * 
     * @param WMLtypeIn
     * @param QueryIn
     * @param OptionsIn
     * @param CapabilitiesIn
     * @return true if exists else false
     * @throws Exception
     */
    public static boolean checkExistingUID(String WMLtypeIn, String QueryIn, String OptionsIn, String CapabilitiesIn)
            throws Exception {
        boolean result = false;
        WMLS_GetFromStoreResponse resp = new WMLS_GetFromStoreResponse();
        resp = store.getFromStore(WMLtypeIn, QueryIn, OptionsIn, CapabilitiesIn);
        if (resp.getResult() == 1) {
            result = true;
        }
        return result;
    }

    /**
     * Check if uom attribute is null
     * 
     * @param XMLin
     * @return true if null else false
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static boolean checkNotNullUOM(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        NodeList nodeList = getNodeListForExpression(doc, uomExpression);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element eElement = (Element) nodeList.item(i);
            if (eElement.getAttribute(uomAttribute).equalsIgnoreCase("")
                    && eElement.getAttribute(uomAttribute).equalsIgnoreCase(null)) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    public static boolean checkUomNodeValue(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        NodeList nodeList = getNodeListForExpression(doc, uomExpression);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element eElement = (Element) nodeList.item(i);
            if (eElement.getNodeValue().isEmpty()||eElement.getNodeValue().isBlank()) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    public static boolean checkMnemonicListNotEmpty(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        NodeList nodeList = getNodeListForExpression(doc, "mnemonicList");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element eElement = (Element) nodeList.item(i);
            if (eElement.getNodeValue().isEmpty()||eElement.getNodeValue().isBlank()) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    public static boolean checkMnemonicListUnique(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
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
        return result;
    }
    
    public static boolean checkUnitList(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        NodeList nodeList = getNodeListForExpression(doc, "unitList");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element eElement = (Element) nodeList.item(i);
            if (eElement.getNodeValue().isEmpty()||eElement.getNodeValue().isBlank()) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static boolean checkMnemonicForSpecialCharacters(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        NodeList nodeList = getNodeListForExpression(doc, "mnemonicList");
        String regex = "',><&//\\";
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element eElement = (Element) nodeList.item(i);
            if (eElement.getNodeValue().matches(regex)) {
                result = true;
                break;
            }
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
    public static boolean checkUOMWithWitsml(String XMLin)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        boolean result = false;
        Document doc = getXMLDocument(XMLin);
        NodeList nodeList = getNodeListForExpression(doc, uomExpression);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element eElement = (Element) nodeList.item(i);
            String uom = eElement.getAttribute(uomAttribute);
            // check uom with witsml unit
        }
        return result;
    }
}
