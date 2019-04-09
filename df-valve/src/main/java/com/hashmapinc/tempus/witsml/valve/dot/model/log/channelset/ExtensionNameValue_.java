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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "value",
    "measureClass",
    "dTim",
    "index",
    "description",
    "dataType",
    "md",
    "uid"
})
public class ExtensionNameValue_ {

    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private Value_ value;
    @JsonProperty("measureClass")
    private String measureClass;
    @JsonProperty("dTim")
    private String dTim;
    @JsonProperty("index")
    private Integer index;
    @JsonProperty("description")
    private String description;
    @JsonProperty("dataType")
    private String dataType;
    @JsonProperty("md")
    private Md_ md;
    @JsonProperty("uid")
    private String uid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("value")
    public Value_ getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Value_ value) {
        this.value = value;
    }

    @JsonProperty("measureClass")
    public String getMeasureClass() {
        return measureClass;
    }

    @JsonProperty("measureClass")
    public void setMeasureClass(String measureClass) {
        this.measureClass = measureClass;
    }

    @JsonProperty("dTim")
    public String getDTim() {
        return dTim;
    }

    @JsonProperty("dTim")
    public void setDTim(String dTim) {
        this.dTim = dTim;
    }

    @JsonProperty("index")
    public Integer getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(Integer index) {
        this.index = index;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("dataType")
    public String getDataType() {
        return dataType;
    }

    @JsonProperty("dataType")
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @JsonProperty("md")
    public Md_ getMd() {
        return md;
    }

    @JsonProperty("md")
    public void setMd(Md_ md) {
        this.md = md;
    }

    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
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
