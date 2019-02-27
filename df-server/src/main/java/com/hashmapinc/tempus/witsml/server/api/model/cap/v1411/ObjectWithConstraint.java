/**
 * Copyright © 2018-2019 Hashmap, Inc
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
 * <p>Java class for objectWithConstraint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="objectWithConstraint">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.witsml.org/api/141>abstractString64">
 *       &lt;attribute name="maxDataNodes" type="{http://www.witsml.org/api/141}positiveCount" />
 *       &lt;attribute name="maxDataPoints" type="{http://www.witsml.org/api/141}positiveCount" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectWithConstraint", propOrder = {
    "value"
})
public class ObjectWithConstraint {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "maxDataNodes")
    protected Integer maxDataNodes;
    @XmlAttribute(name = "maxDataPoints")
    protected Integer maxDataPoints;

    /**
     * The intended abstract supertype of all 64 character string types.
     * 			This type should not be used directly except to derive another type.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the maxDataNodes property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxDataNodes() {
        return maxDataNodes;
    }

    /**
     * Sets the value of the maxDataNodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer } must be greater than 0
     *     
     */
    public void setMaxDataNodes(Integer value) {
        // Check to avoid setting < 0 in the xml
        if (value <= 0)
            return;
        this.maxDataNodes = value;
    }

    /**
     * Gets the value of the maxDataPoints property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxDataPoints() {
        return maxDataPoints;
    }

    /**
     * Sets the value of the maxDataPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer } must be greater than 0
     *     
     */
    public void setMaxDataPoints(Integer value) {
        // Check to avoid setting < 0 in the xml
        if (value <= 0)
            return;
        this.maxDataPoints = value;
    }

}
