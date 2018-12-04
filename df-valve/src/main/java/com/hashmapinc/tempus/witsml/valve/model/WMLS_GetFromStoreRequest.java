

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

package com.hashmapinc.tempus.witsml.valve.model;

import java.util.List;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;

public class WMLS_GetFromStoreRequest {
	private String clientVersion;
	private String XMLin;
	private List<AbstractWitsmlObject> witsmlObjects;

	public WMLS_GetFromStoreRequest() {
	}

	public WMLS_GetFromStoreRequest(String clientVerison, String XMLin, List<AbstractWitsmlObject> witsmlObjects) {
		this.clientVersion = clientVerison;
		this.XMLin = XMLin;
		this.witsmlObjects = witsmlObjects;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getXMLin() {
		return XMLin;
	}

	public void setXMLin(String xMLin) {
		XMLin = xMLin;
	}

	public List<AbstractWitsmlObject> getWitsmlObjects() {
		return witsmlObjects;
	}

	public void setWitsmlObjects(List<AbstractWitsmlObject> witsmlObjects) {
		this.witsmlObjects = witsmlObjects;
	}

}
