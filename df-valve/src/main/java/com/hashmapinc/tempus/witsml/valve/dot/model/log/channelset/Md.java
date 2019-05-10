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

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uom",
    "value",
    "datum"
})
public class Md {

    @JsonProperty("uom")
    private String uom;
    @JsonProperty("value")
    private String value;
    @JsonProperty("datum")
    private String datum;
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

    @JsonProperty("datum")
    public String getDatum() {
        return datum;
    }

    @JsonProperty("datum")
    public void setDatum(String datum) {
        this.datum = datum;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public static Md from1411(com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord wmlMd){
        if (wmlMd == null)
            return null;

        Md md = new Md();
        md.setUom(wmlMd.getUom());
        md.setValue(wmlMd.getValue().toString());
        md.setDatum(wmlMd.getDatum());
        return md;  
    }

    public static com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord to1411(Md md){
        if (md == null)
            return null;

        com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord wmlMd = 
            new com.hashmapinc.tempus.WitsmlObjects.v1411.MeasuredDepthCoord();

        wmlMd.setUom(wmlMd.getUom());
        wmlMd.setValue(wmlMd.getValue());
        wmlMd.setDatum(wmlMd.getDatum());
        return wmlMd;  
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Md md = (Md) o;
        return Objects.equals(uom, md.uom) &&
                Objects.equals(value, md.value) &&
                Objects.equals(datum, md.datum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uom, value, datum);
    }
}
