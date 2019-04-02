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
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "indexType",
    "uom",
    "direction",
    "mnemonic",
    "datumReference"
})
public class Index {

    @JsonProperty("indexType")
    private String indexType;
    @JsonProperty("uom")
    private String uom;
    @JsonProperty("direction")
    private String direction;
    @JsonProperty("mnemonic")
    private String mnemonic;
    @JsonProperty("datumReference")
    private String datumReference;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("indexType")
    public String getIndexType() {
        return indexType;
    }

    @JsonProperty("indexType")
    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    @JsonProperty("uom")
    public String getUom() {
        return uom;
    }

    @JsonProperty("uom")
    public void setUom(String uom) {
        this.uom = uom;
    }

    @JsonProperty("direction")
    public String getDirection() {
        return direction;
    }

    @JsonProperty("direction")
    public void setDirection(String direction) {
        this.direction = direction;
    }

    @JsonProperty("mnemonic")
    public String getMnemonic() {
        return mnemonic;
    }

    @JsonProperty("mnemonic")
    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @JsonProperty("datumReference")
    public String getDatumReference() {
        return datumReference;
    }

    @JsonProperty("datumReference")
    public void setDatumReference(String datumReference) {
        this.datumReference = datumReference;
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
