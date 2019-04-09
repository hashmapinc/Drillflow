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
    "contentType",
    "title",
    "uuid",
    "uuidAuthority",
    "uri",
    "versionString"
})
public class ChannelClass {

    @JsonProperty("contentType")
    private String contentType;
    @JsonProperty("title")
    private String title;
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("uuidAuthority")
    private String uuidAuthority;
    @JsonProperty("uri")
    private String uri;
    @JsonProperty("versionString")
    private String versionString;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("contentType")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("contentType")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("uuidAuthority")
    public String getUuidAuthority() {
        return uuidAuthority;
    }

    @JsonProperty("uuidAuthority")
    public void setUuidAuthority(String uuidAuthority) {
        this.uuidAuthority = uuidAuthority;
    }

    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    @JsonProperty("uri")
    public void setUri(String uri) {
        this.uri = uri;
    }

    @JsonProperty("versionString")
    public String getVersionString() {
        return versionString;
    }

    @JsonProperty("versionString")
    public void setVersionString(String versionString) {
        this.versionString = versionString;
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
