package com.hashmapinc.tempus.witsml.server.api.pojo;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class WellParseCheck {
	
	public void parseWell(String filepath) throws JAXBException {
	  File file = new File(filepath);
	    //creating the JAXB context
	    JAXBContext jContext = JAXBContext.newInstance(Wells.class);
	    //creating the unmarshall object
	    Unmarshaller unmarshallerObj = jContext.createUnmarshaller();
	    //calling the unmarshall method
	    Wells wells=(Wells) unmarshallerObj.unmarshal(file);
	    //System.out.println(student.getName()+” “+student.getId()+” “+student.getSubject());
	    System.out.println(wells.getWell().getReferencePoint());
	    }

}
