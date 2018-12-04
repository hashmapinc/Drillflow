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
package com.hashmapinc.tempus.witsml.server.api.model;

import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell;

public class WMLS_WellObjectToObj {
	
	private ObjWell well;
	private String witsml_version;
	private String witsml_type;
	
	public WMLS_WellObjectToObj () {}
	
	public WMLS_WellObjectToObj(ObjWell well, String witsml_version, String witsml_type)
	{
		this.well = well;
		this.witsml_type = witsml_type;
		this.witsml_version = witsml_version;
		
				
	}

	public ObjWell getWell() {
		return well;
	}

	public void setWell(ObjWell well) {
		this.well = well;
	}

	public String getWitsml_version() {
		return witsml_version;
	}

	public void setWitsml_version(String witsml_version) {
		this.witsml_version = witsml_version;
	}

	public String getWitsml_type() {
		return witsml_type;
	}

	public void setWitsml_type(String witsml_type) {
		this.witsml_type = witsml_type;
	}
	

}
