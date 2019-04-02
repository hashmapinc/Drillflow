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
package com.hashmapinc.tempus.witsml.server.api.model.log.channel;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "axisStart",
    "axisSpacing",
    "axisCount",
    "axisName",
    "axisPropertyKind",
    "axisUom",
    "uid",
    "order",
    "doubleValues",
    "stringValues",
    "extensionNameValue"
})
public class AxisDefinition {

    @JsonProperty("axisStart")
    private Double axisStart;
    @JsonProperty("axisSpacing")
    private Double axisSpacing;
    @JsonProperty("axisCount")
    private Integer axisCount;
    @JsonProperty("axisName")
    private String axisName;
    @JsonProperty("axisPropertyKind")
    private String axisPropertyKind;
    @JsonProperty("axisUom")
    private String axisUom;
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("order")
    private Integer order;
    @JsonProperty("doubleValues")
    private String doubleValues;
    @JsonProperty("stringValues")
    private String stringValues;
    @JsonProperty("extensionNameValue")
    private List<ExtensionNameValue> extensionNameValue = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("axisStart")
    public Double getAxisStart() {
        return axisStart;
    }

    @JsonProperty("axisStart")
    public void setAxisStart(Double axisStart) {
        this.axisStart = axisStart;
    }

    @JsonProperty("axisSpacing")
    public Double getAxisSpacing() {
        return axisSpacing;
    }

    @JsonProperty("axisSpacing")
    public void setAxisSpacing(Double axisSpacing) {
        this.axisSpacing = axisSpacing;
    }

    @JsonProperty("axisCount")
    public Integer getAxisCount() {
        return axisCount;
    }

    @JsonProperty("axisCount")
    public void setAxisCount(Integer axisCount) {
        this.axisCount = axisCount;
    }

    @JsonProperty("axisName")
    public String getAxisName() {
        return axisName;
    }

    @JsonProperty("axisName")
    public void setAxisName(String axisName) {
        this.axisName = axisName;
    }

    @JsonProperty("axisPropertyKind")
    public String getAxisPropertyKind() {
        return axisPropertyKind;
    }

    @JsonProperty("axisPropertyKind")
    public void setAxisPropertyKind(String axisPropertyKind) {
        this.axisPropertyKind = axisPropertyKind;
    }

    @JsonProperty("axisUom")
    public String getAxisUom() {
        return axisUom;
    }

    @JsonProperty("axisUom")
    public void setAxisUom(String axisUom) {
        this.axisUom = axisUom;
    }

    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonProperty("order")
    public Integer getOrder() {
        return order;
    }

    @JsonProperty("order")
    public void setOrder(Integer order) {
        this.order = order;
    }

    @JsonProperty("doubleValues")
    public String getDoubleValues() {
        return doubleValues;
    }

    @JsonProperty("doubleValues")
    public void setDoubleValues(String doubleValues) {
        this.doubleValues = doubleValues;
    }

    @JsonProperty("stringValues")
    public String getStringValues() {
        return stringValues;
    }

    @JsonProperty("stringValues")
    public void setStringValues(String stringValues) {
        this.stringValues = stringValues;
    }

    @JsonProperty("extensionNameValue")
    public List<ExtensionNameValue> getExtensionNameValue() {
        return extensionNameValue;
    }

    @JsonProperty("extensionNameValue")
    public void setExtensionNameValue(List<ExtensionNameValue> extensionNameValue) {
        this.extensionNameValue = extensionNameValue;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
