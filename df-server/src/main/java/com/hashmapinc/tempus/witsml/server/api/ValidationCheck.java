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

	private StoreImpl store;

	public NodeList parseXML(String XMLin) throws SAXException, IOException, ParserConfigurationException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("well1411.xml").getFile());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("well");
		return nList;
	}
	
	public boolean checkWMLTypeEmpty(String WMLType) {
		if(WMLType.trim().isEmpty())
		{
			return true;
		}
		return false;
	}
	
	public boolean checkXMLEmpty(String XMLin) {
		if(XMLin.trim().isEmpty())
		{
			return true;
		}
		return false;
	}
	
	public boolean checkXMLwMLObject(String XMLin, String WMLType)
	{
		if(XMLin.equals(WMLType))
		{
			return true;
		}
		return false;
	}
	
	public boolean checkWellforDelete(String XMLin)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("well1411.xml").getFile());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName("well");
		if(nodeList.getLength()>1)
		{
			return true;
		}
		return false;
	}
	
	public boolean checkWell(String XMLin)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("well1411.xml").getFile());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nodeList = doc.getElementsByTagName("well");
		if(nodeList.getLength()<2)
		{
			return true;
		}
		return false;
	}
	
	public boolean checkNotNullUid(String XMLin)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("well1411.xml").getFile());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		XPathFactory factory = XPathFactory.newInstance();

		XPath xpath = factory.newXPath();

		String expression = "//*[@uid]";
		NodeList nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element eElement = (Element) nodeList.item(i);
			if (eElement.getAttribute("uid").equalsIgnoreCase("")
					&& eElement.getAttribute("uid").equalsIgnoreCase(null)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkUniqueUid(String XMLin)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("well1411.xml").getFile());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		XPathFactory factory = XPathFactory.newInstance();
		Set<String> uids = new HashSet<String>();
		XPath xpath = factory.newXPath();

		String expression = "//*[@uid]";
		NodeList nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element eElement = (Element) nodeList.item(i);
			String uid = eElement.getAttribute("uid");
			if (uids.contains(uid)) {
				return true;
			} else {
				uids.add(uid);
			}
		}
		return false;
	}

	public boolean checkExistingUID(String WMLtypeIn, String QueryIn, String OptionsIn, String CapabilitiesIn)
			throws Exception {
		WMLS_GetFromStoreResponse resp = new WMLS_GetFromStoreResponse();
		resp = store.getFromStore(WMLtypeIn, QueryIn, OptionsIn, CapabilitiesIn);
		if (resp.getResult() == 1) {
			return true;
		}
		return false;
	}

	public boolean checkNotNullUOM(String XMLin)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("well1411.xml").getFile());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		XPathFactory factory = XPathFactory.newInstance();

		XPath xpath = factory.newXPath();

		String expression = "//*[@uom]";
		NodeList nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element eElement = (Element) nodeList.item(i);
			if (eElement.getAttribute("uom").equalsIgnoreCase("")
					&& eElement.getAttribute("uom").equalsIgnoreCase(null)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkUOMWithWitsml(String XMLin)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("well1411.xml").getFile());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();

		String expression = "//*[@uom]";
		NodeList nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element eElement = (Element) nodeList.item(i);
			String uom = eElement.getAttribute("uom");
			// check uom with witsml unit
		}
		return false;
	}

}
