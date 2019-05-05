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
package com.hashmapinc.tempus.witsml.valve.dot.model.log.channel;

import com.fasterxml.jackson.annotation.*;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ExtensionNameValue;

import java.util.ArrayList;
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
    private Short axisCount;
    @JsonProperty("axisName")
    private String axisName;
    @JsonProperty("axisPropertyKind")
    private String axisPropertyKind;
    @JsonProperty("axisUom")
    private String axisUom;
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("order")
    private Short order;
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
    public Short getAxisCount() {
        return axisCount;
    }

    @JsonProperty("axisCount")
    public void setAxisCount(Short axisCount) {
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
    public Short getOrder() {
        return order;
    }

    @JsonProperty("order")
    public void setOrder(Short order) {
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

    public static List<AxisDefinition> from1411(List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition> axisDefinitions){
        if (axisDefinitions == null) {
            return null;
        }
        List<AxisDefinition> axes = new ArrayList<>();

        for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition wmlAxis : axisDefinitions) {

            AxisDefinition axis = new AxisDefinition();
            axis.setAxisCount(wmlAxis.getCount());
            axis.setAxisName(wmlAxis.getName());
            axis.setAxisPropertyKind(wmlAxis.getPropertyType());
            axis.setAxisUom(wmlAxis.getUom());
            axis.setUid(wmlAxis.getUid());
            axis.setOrder(wmlAxis.getOrder());

            if (wmlAxis.getDoubleValues() != null)
                axis.setDoubleValues(String.join(",", wmlAxis.getDoubleValues()));
            if (wmlAxis.getStringValues() != null)
                axis.setStringValues(String.join(",", wmlAxis.getStringValues()));

            axis.setExtensionNameValue(ExtensionNameValue.from1411(wmlAxis.getExtensionNameValue()));
            
            axes.add(axis);
        }

        return axes;
    }

    public static List<AxisDefinition> from1311(List<com.hashmapinc.tempus.WitsmlObjects.v1311.CsAxisDefinition> axisDefinitions){
        if (axisDefinitions == null) {
            return null;
        }
        List<AxisDefinition> axes = new ArrayList<>();

        for (com.hashmapinc.tempus.WitsmlObjects.v1311.CsAxisDefinition wmlAxis : axisDefinitions) {

            AxisDefinition axis = new AxisDefinition();
            axis.setAxisCount(wmlAxis.getCount());
            axis.setAxisName(wmlAxis.getName());
            axis.setAxisPropertyKind(wmlAxis.getPropertyType());
            axis.setAxisUom(wmlAxis.getUom());
            axis.setUid(wmlAxis.getUid());
            axis.setOrder( wmlAxis.getOrder());

            List<String> dblValues = new ArrayList<>();
            for (Double val : wmlAxis.getDoubleValues()) {
                dblValues.add(val.toString());
            }

            if (wmlAxis.getDoubleValues() != null)
                axis.setDoubleValues(String.join(",", dblValues));

            if (wmlAxis.getStringValues() != null)
                axis.setStringValues(String.join(",", wmlAxis.getStringValues()));
            
            axes.add(axis);
        }

        return axes;
    }

}
