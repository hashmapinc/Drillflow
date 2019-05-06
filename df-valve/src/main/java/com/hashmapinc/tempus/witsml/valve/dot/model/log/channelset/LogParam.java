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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "index",
    "name",
    "uom",
    "description",
    "uid",
    "value"
})
public class LogParam {

    @JsonProperty("index")
    private String index;
    @JsonProperty("name")
    private String name;
    @JsonProperty("uom")
    private String uom;
    @JsonProperty("description")
    private String description;
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("value")
    private String value;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("index")
    public String getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(String index) {
        this.index = index;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("uom")
    public String getUom() {
        return uom;
    }

    @JsonProperty("uom")
    public void setUom(String uom) {
        this.uom = uom;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
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

    public static List<LogParam> from1411(List<com.hashmapinc.tempus.WitsmlObjects.v1411.IndexedObject> logParams){
        if (logParams == null)
            return null;

        List<LogParam> params = new ArrayList<>();
        for (com.hashmapinc.tempus.WitsmlObjects.v1411.IndexedObject logParam : logParams) {
            LogParam param = new LogParam();
            param.setName(logParam.getName());
            param.setDescription(logParam.getDescription());
            param.setIndex(logParam.getIndex());
            param.setUid(logParam.getUid());
            param.setUom(logParam.getUom());
            param.setValue(logParam.getValue());
            params.add(param);
        }

        return params;
    }

    public static List<LogParam> from1311(List<com.hashmapinc.tempus.WitsmlObjects.v1311.IndexedObject> logParams){
        if (logParams == null)
            return null;

        List<LogParam> params = new ArrayList<>();
        for (com.hashmapinc.tempus.WitsmlObjects.v1311.IndexedObject logParam : logParams) {
            LogParam param = new LogParam();
            param.setName(logParam.getName());
            param.setDescription(logParam.getDescription());
            param.setIndex(logParam.getIndex());
            param.setUom(logParam.getUom());
            param.setValue(logParam.getValue());
            params.add(param);
        }

        return params;
    }

    public static List<com.hashmapinc.tempus.WitsmlObjects.v1311.IndexedObject> to1311(List<LogParam> logParams){
        if (logParams == null)
            return null;

        List<com.hashmapinc.tempus.WitsmlObjects.v1311.IndexedObject> params = new ArrayList<>();
        for (LogParam logParam : logParams) {
            com.hashmapinc.tempus.WitsmlObjects.v1311.IndexedObject param = 
                new com.hashmapinc.tempus.WitsmlObjects.v1311.IndexedObject();
            param.setName(logParam.getName());
            param.setDescription(logParam.getDescription());
            param.setIndex(logParam.getIndex());
            param.setUom(logParam.getUom());
            param.setValue(logParam.getValue());
            params.add(param);
        }

        return params;
    }

    public static List<com.hashmapinc.tempus.WitsmlObjects.v1411.IndexedObject> to1411(List<LogParam> logParams){
        if (logParams == null)
            return null;

        List<com.hashmapinc.tempus.WitsmlObjects.v1411.IndexedObject> params = new ArrayList<>();
        for (LogParam logParam : logParams) {
            com.hashmapinc.tempus.WitsmlObjects.v1411.IndexedObject param = 
                new com.hashmapinc.tempus.WitsmlObjects.v1411.IndexedObject();
            param.setName(logParam.getName());
            param.setDescription(logParam.getDescription());
            param.setIndex(logParam.getIndex());
            param.setUom(logParam.getUom());
            param.setValue(logParam.getValue());
            params.add(param);
        }

        return params;
    }
}
