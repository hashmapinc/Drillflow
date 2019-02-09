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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Well {
	
	
	private List<ReferencePoint> referencePoint;
	
    private WellLocation wellLocation;
    
	
    private List<WellDatum> wellDatum;

	public List<ReferencePoint> getReferencePoint() {
		return referencePoint;
	}
	
	@XmlElement
	public void setReferencePoint(List<ReferencePoint> referencePoint) {
		this.referencePoint = referencePoint;
	}

	public WellLocation getWellLocation() {
		return wellLocation;
	}
	
	@XmlElement
	public void setWellLocation(WellLocation wellLocation) {
		this.wellLocation = wellLocation;
	}

	public List<WellDatum> getWellDatum() {
		return wellDatum;
	}
	
	@XmlElement
	public void setWellDatum(List<WellDatum> wellDatum) {
		this.wellDatum = wellDatum;
	}
	
}
