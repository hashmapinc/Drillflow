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
package com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uom",
    "value",
    "numerator",
    "denominator"
})
public class StepIncrement {

    @JsonProperty("uom")
    private String uom;
    @JsonProperty("value")
    private String value;
    @JsonProperty("numerator")
    private Double numerator;
    @JsonProperty("denominator")
    private Double denominator;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("uom")
    public String getUom() {
        return uom;
    }

    @JsonProperty("uom")
    public void setUom(String uom) {
        this.uom = uom;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("numerator")
    public Double getNumerator() {
        return numerator;
    }

    @JsonProperty("numerator")
    public void setNumerator(Double numerator) {
        this.numerator = numerator;
    }

    @JsonProperty("denominator")
    public Double getDenominator() {
        return denominator;
    }

    @JsonProperty("denominator")
    public void setDenominator(Double denominator) {
        this.denominator = denominator;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public static StepIncrement from1411(com.hashmapinc.tempus.WitsmlObjects.v1411.RatioGenericMeasure stepIncrement){
        if (stepIncrement == null)
            return null;

        StepIncrement inc = new StepIncrement();
        inc.setNumerator(stepIncrement.getNumerator());
        inc.setDenominator(stepIncrement.getDenominator());
        inc.setUom(stepIncrement.getUom());
        inc.setValue(stepIncrement.getValue().toString());
        return inc;
    }

    public static StepIncrement from1311(com.hashmapinc.tempus.WitsmlObjects.v1311.RatioGenericMeasure stepIncrement){
        if (stepIncrement == null)
            return null;

        StepIncrement inc = new StepIncrement();
        inc.setNumerator(stepIncrement.getNumerator());
        inc.setDenominator(stepIncrement.getDenominator());
        inc.setUom(stepIncrement.getUom());
        inc.setValue(stepIncrement.getValue().toString());
        return inc;
    }

    public static com.hashmapinc.tempus.WitsmlObjects.v1411.RatioGenericMeasure to1411(StepIncrement stepIncrement){
        if (stepIncrement == null)
            return null;

        com.hashmapinc.tempus.WitsmlObjects.v1411.RatioGenericMeasure wmlInc = 
            new com.hashmapinc.tempus.WitsmlObjects.v1411.RatioGenericMeasure();
        wmlInc.setNumerator(stepIncrement.getNumerator());
        wmlInc.setDenominator(stepIncrement.getDenominator());
        wmlInc.setUom(stepIncrement.getUom());
        wmlInc.setValue(Double.parseDouble(stepIncrement.getValue()));
        return wmlInc;
    }

    public static com.hashmapinc.tempus.WitsmlObjects.v1311.RatioGenericMeasure to1311(StepIncrement stepIncrement){
        if (stepIncrement == null)
            return null;

        com.hashmapinc.tempus.WitsmlObjects.v1311.RatioGenericMeasure wmlInc = 
            new com.hashmapinc.tempus.WitsmlObjects.v1311.RatioGenericMeasure();
        wmlInc.setNumerator(stepIncrement.getNumerator());
        wmlInc.setDenominator(stepIncrement.getDenominator());
        wmlInc.setUom(stepIncrement.getUom());
        wmlInc.setValue(Double.parseDouble(stepIncrement.getValue()));
        return wmlInc;
    }

}
