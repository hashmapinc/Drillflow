package com.hashmapinc.tempus.witsml.server.api;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestXMLValidate {

	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		TestXMLValidate val = new TestXMLValidate();
		val.ValidateXML();
	}
	
	public TestXMLValidate() {}
	
	public void ValidateXML() throws ParserConfigurationException, SAXException, IOException
	{

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("well1411.xml").getFile());
		System.out.println("file is:-"+file.length());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("well");
		
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);	
			System.out.println("Parsing for " + nNode.getNodeName() + " element");

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;
			
			if(eElement.getAttribute("uom")!=""||eElement.getAttribute("uom")!=null)
				System.out.println("uom is :" +eElement.getAttribute("uom"));

			System.out.println("UID is : " + eElement.getAttribute("uid"));
			NodeList well = doc.getElementsByTagName("wellDatum");
				for(int temp1 = 0; temp1<well.getLength(); temp1++)
					{
						Node wellNode = well.item(temp1);
						if(wellNode.getNodeType()==Node.ELEMENT_NODE)
						{
						Element wellElement=(Element) wellNode;
						System.out.println("child uid is :" +wellElement.getAttribute("uid"));
						}
					}
			NodeList wellLocation =doc.getElementsByTagName("wellLocation");	
				for(int temp2=0; temp2<wellLocation.getLength(); temp2++)
				{
					Node wellLocationNode = wellLocation.item(temp2);
					if(wellLocationNode.getNodeType()==Node.ELEMENT_NODE)
					{
						Element wellLocationElement=(Element) wellLocationNode;
						System.out.println("wellLocation :" +wellLocationElement.getAttribute("uid"));
					}
				}
			
			NodeList referencePoint =doc.getElementsByTagName("referencePoint");
				for(int temp3=0; temp3<referencePoint.getLength(); temp3++)
				{
					Node referencePointNode=referencePoint.item(temp3);
					if(referencePointNode.getNodeType()==Node.ELEMENT_NODE)
					{
						Element referencePointElement=(Element) referencePointNode;
						System.out.println("reference point :"+referencePointElement.getAttribute("uid"));
						NodeList location =doc.getElementsByTagName("location");
						for(int temp4=0; temp4<location.getLength(); temp4++)
						{
							Node locationNode=location.item(temp4);
							if(locationNode.getNodeType()==Node.ELEMENT_NODE)
							{
								Element locationElement=(Element) locationNode;
								System.out.println("location node is :" +locationElement.getAttribute("uid"));
							}
						}
					}
				}
				
				NodeList wellCRS =doc.getElementsByTagName("wellCRS");	
				for(int temp5=0; temp5<wellCRS.getLength(); temp5++)
				{
					Node wellCRSNode = wellCRS.item(temp5);
					if(wellCRSNode.getNodeType()==Node.ELEMENT_NODE)
					{
						Element wellCRSElement=(Element) wellCRSNode;
						if(!wellCRSElement.getAttribute("uid").equalsIgnoreCase(""))
						System.out.println("well CRS :" +wellCRSElement.getAttribute("uid"));
					}
				}
				
				
			
			}
		}
			
	}
	
}
