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
