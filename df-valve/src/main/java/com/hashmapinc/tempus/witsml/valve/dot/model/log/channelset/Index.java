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

import java.util.*;

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

    public static List<Index> from1411(com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog log){
        if (log == null){
            return null;
        }

        String indexType = null;

        if (log.getIndexType()!=null) {
            if (log.getIndexType().contains("time")) {
                indexType = "time";
            } else {
                indexType = "depth";
            }
        }

        Index index = new Index();
        index.setDirection(log.getDirection());
        index.setMnemonic(log.getIndexCurve());
        if (indexType != null)
            index.setIndexType(indexType);

        Optional<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> matchingObject =
                log
                .getLogCurveInfo()
                .stream()
                .filter(p -> p.getMnemonic().getValue().equals(log.getIndexCurve()))
                .findFirst();
        if (!matchingObject.isEmpty()) {
            index.setUom(matchingObject.get().getUnit());
        }

        List<Index> indices = new ArrayList<Index>();
        indices.add(index);
        
        return indices;
    }

    public static List<Index> from1311(com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog log){
        if (log == null){
            return null;
        }

        String indexType = "depth";
        if (log.getIndexType().contains("time"))
            indexType = "time";

        Index index = new Index();
        index.setIndexType(indexType);
        index.setDirection(log.getDirection());
        index.setMnemonic(log.getIndexCurve().getValue());
        Optional<com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo> matchingObject = log.getLogCurveInfo()
                .stream().filter(p -> p.getMnemonic().equals(log.getIndexCurve().getValue())).findFirst();
        index.setUom(matchingObject.get().getUnit());
        List<Index> indices = new ArrayList<Index>();
        indices.add(index);

        return indices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return Objects.equals(indexType, index.indexType) &&
                Objects.equals(uom, index.uom) &&
                Objects.equals(direction, index.direction) &&
                Objects.equals(mnemonic, index.mnemonic) &&
                Objects.equals(datumReference, index.datumReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexType, uom, direction, mnemonic);
    }
}
