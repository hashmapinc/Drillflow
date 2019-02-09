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

import java.nio.file.Paths;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;

import com.hashmapinc.tempus.witsml.server.api.pojo.WellParseCheck;

public class SAXParserTest {
	
	@Test
    public void testSAXParser() {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            WellHandler userhandler = new WellHandler();
            saxParser.parse(Paths.get("src/test/resources/well1311.xml").toFile(), userhandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@Test
	public void testWellParse() throws JAXBException
	{
		WellParseCheck well = new WellParseCheck();
		well.parseWell("src/test/resources/well1311.xml");
	}

}
