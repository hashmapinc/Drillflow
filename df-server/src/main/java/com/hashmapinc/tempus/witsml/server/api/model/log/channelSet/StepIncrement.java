
package com.hashmapinc.tempus.witsml.server.api.model.log.channelSet;

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

}
