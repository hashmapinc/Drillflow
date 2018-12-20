package com.hashmapinc.tempus.witsml.server.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

public class TestNew {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		// TODO Auto-generated method stub
		TestNew val = new TestNew();
		val.ValidateXML();
	}

	public void ValidateXML() throws FileNotFoundException, SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("well1411.xml").getFile());
		System.out.println("file is:-" + file.length());

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();

		Document doc = db.parse(new FileInputStream(file));

		XPathFactory factory = XPathFactory.newInstance();

		XPath xpath = factory.newXPath();

		String expression;

		NodeList nodeList;

		// 1. all elements where attribute 'key' equals 'mykey1'

		expression = "//*[@uom]";
		;

		nodeList = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);


		for (int i = 0; i < nodeList.getLength(); i++) {
			Element eElement = (Element) nodeList.item(i);

			System.out.print(eElement.getAttribute("uom")+ " ");

		}
	}

}
