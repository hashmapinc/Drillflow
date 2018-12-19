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

public final class ValidateParam {

	private String WMLtypeIn;
	private String XMLin;
	private String OptionsIn;
	private String CapabilitiesIn;

	public ValidateParam(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn) {
		this.WMLtypeIn = WMLtypeIn;
		this.XMLin = XMLin;
		this.OptionsIn = OptionsIn;
		this.CapabilitiesIn = CapabilitiesIn;
	}

	public String getWMLtypeIn() {
		return WMLtypeIn;
	}

	public void setWMLtypeIn(String wMLtypeIn) {
		WMLtypeIn = wMLtypeIn;
	}

	public String getXMLin() {
		return XMLin;
	}

	public void setXMLin(String xMLin) {
		XMLin = xMLin;
	}

	public String getOptionsIn() {
		return OptionsIn;
	}

	public void setOptionsIn(String optionsIn) {
		OptionsIn = optionsIn;
	}

	public String getCapabilitiesIn() {
		return CapabilitiesIn;
	}

	public void setCapabilitiesIn(String capabilitiesIn) {
		CapabilitiesIn = capabilitiesIn;
	}
	
	
}
