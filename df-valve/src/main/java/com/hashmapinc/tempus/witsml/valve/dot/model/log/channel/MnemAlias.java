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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "namingSystem",
    "value"
})
public class MnemAlias {

    @JsonProperty("namingSystem")
    private String namingSystem;
    @JsonProperty("value")
    private String value;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("namingSystem")
    public String getNamingSystem() {
        return namingSystem;
    }

    @JsonProperty("namingSystem")
    public void setNamingSystem(String namingSystem) {
        this.namingSystem = namingSystem;
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

    public static MnemAlias from1411(com.hashmapinc.tempus.WitsmlObjects.v1411.ShortNameStruct mnemAlias){
        if (mnemAlias == null) 
            return null;
        
        MnemAlias alias = new MnemAlias();
        alias.setValue(mnemAlias.getValue());
        alias.setNamingSystem(mnemAlias.getNamingSystem());
        return alias;
    }

    //There is no ShortNameStruct in 1311 so just pass in the LogCurveInfo to build the MnemAlias
    public static MnemAlias from1311(com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo lci){
        if (lci.getMnemAlias() == null) 
            return null;
        
        MnemAlias alias = new MnemAlias();
        alias.setValue(lci.getMnemAlias());
        return alias;
    }

    public static com.hashmapinc.tempus.WitsmlObjects.v1411.ShortNameStruct to1411(MnemAlias mnemAlias){
        if (mnemAlias == null) 
            return null;
        
            com.hashmapinc.tempus.WitsmlObjects.v1411.ShortNameStruct alias = 
                new com.hashmapinc.tempus.WitsmlObjects.v1411.ShortNameStruct();
        alias.setValue(mnemAlias.getValue());
        alias.setNamingSystem(mnemAlias.getNamingSystem());
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MnemAlias mnemAlias = (MnemAlias) o;
        return Objects.equals(namingSystem, mnemAlias.namingSystem) &&
                Objects.equals(value, mnemAlias.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namingSystem, value);
    }
}
