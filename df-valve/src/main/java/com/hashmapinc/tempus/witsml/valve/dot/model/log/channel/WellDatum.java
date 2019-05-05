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
import com.hashmapinc.tempus.WitsmlObjects.v1411.RefNameString;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uidRef",
    "value"
})
public class WellDatum {

    @JsonProperty("uidRef")
    private String uidRef;
    @JsonProperty("value")
    private String value;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("uidRef")
    public String getUidRef() {
        return uidRef;
    }

    @JsonProperty("uidRef")
    public void setUidRef(String uidRef) {
        this.uidRef = uidRef;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public static WellDatum from1411(com.hashmapinc.tempus.WitsmlObjects.v1411.RefNameString wellDatum){
        if (wellDatum == null)
            return null;

        WellDatum datum = new WellDatum();
        datum.setUidRef(wellDatum.getUidRef());
        datum.setValue(wellDatum.getValue());
        return datum;
    }

    public static WellDatum from1311(com.hashmapinc.tempus.WitsmlObjects.v1311.RefNameString wellDatum){
        if (wellDatum == null)
            return null;

        WellDatum datum = new WellDatum();
        datum.setUidRef(wellDatum.getUidRef());
        datum.setValue(wellDatum.getValue());
        return datum;
    }

    public static com.hashmapinc.tempus.WitsmlObjects.v1411.RefNameString to1411(WellDatum wellDatum){
        if (wellDatum == null)
            return null;

        com.hashmapinc.tempus.WitsmlObjects.v1411.RefNameString wmlDatum = 
            new com.hashmapinc.tempus.WitsmlObjects.v1411.RefNameString();
        wmlDatum.setUidRef(wellDatum.getUidRef());
        wmlDatum.setValue(wellDatum.getValue());
        return wmlDatum;
    }

    public static com.hashmapinc.tempus.WitsmlObjects.v1311.RefNameString to1311(WellDatum wellDatum){
        if (wellDatum == null)
            return null;

        com.hashmapinc.tempus.WitsmlObjects.v1311.RefNameString wmlDatum = 
            new com.hashmapinc.tempus.WitsmlObjects.v1311.RefNameString();
        wmlDatum.setUidRef(wellDatum.getUidRef());
        wmlDatum.setValue(wellDatum.getValue());
        return wmlDatum;
    }
}
