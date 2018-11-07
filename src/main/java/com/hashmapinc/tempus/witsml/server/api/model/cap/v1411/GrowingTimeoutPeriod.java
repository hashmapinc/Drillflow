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
package com.hashmapinc.tempus.witsml.server.api.model.cap.v1411;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for growingTimeoutPeriod complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="growingTimeoutPeriod">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.witsml.org/api/141>abstractPositiveCount">
 *       &lt;attribute name="DDataObject" use="required" type="{http://www.witsml.org/api/141}str64" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "growingTimeoutPeriod", propOrder = {
    "value"
})
public class GrowingTimeoutPeriod {

    @XmlValue
    protected int value;
    @XmlAttribute(name = "dataObject", required = true)
    protected String dataObject;

    /**
     * The intended abstract supertype of all xsd:int types which must be positive.
     * 			This type should not be used directly except to derive another type.
     * 
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Gets the value of the DDataObject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataObject() {
        return dataObject;
    }

    /**
     * Sets the value of the DDataObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataObject(String value) {
        this.dataObject = value;
    }

}
