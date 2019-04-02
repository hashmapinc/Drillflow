
package com.hashmapinc.tempus.witsml.server.api.model.log.channel;

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
public class DerivedFrom {

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
