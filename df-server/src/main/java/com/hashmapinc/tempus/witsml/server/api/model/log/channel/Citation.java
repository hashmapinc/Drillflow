
package com.hashmapinc.tempus.witsml.server.api.model.log.channel;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "title",
    "originator",
    "creation",
    "format",
    "editor",
    "lastUpdate",
    "versionString",
    "description",
    "descriptiveKeywords"
})
public class Citation {

    @JsonProperty("title")
    private String title;
    @JsonProperty("originator")
    private String originator;
    @JsonProperty("creation")
    private String creation;
    @JsonProperty("format")
    private String format;
    @JsonProperty("editor")
    private String editor;
    @JsonProperty("lastUpdate")
    private String lastUpdate;
    @JsonProperty("versionString")
    private String versionString;
    @JsonProperty("description")
    private String description;
    @JsonProperty("descriptiveKeywords")
    private String descriptiveKeywords;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("originator")
    public String getOriginator() {
        return originator;
    }

    @JsonProperty("originator")
    public void setOriginator(String originator) {
        this.originator = originator;
    }

    @JsonProperty("creation")
    public String getCreation() {
        return creation;
    }

    @JsonProperty("creation")
    public void setCreation(String creation) {
        this.creation = creation;
    }

    @JsonProperty("format")
    public String getFormat() {
        return format;
    }

    @JsonProperty("format")
    public void setFormat(String format) {
        this.format = format;
    }

    @JsonProperty("editor")
    public String getEditor() {
        return editor;
    }

    @JsonProperty("editor")
    public void setEditor(String editor) {
        this.editor = editor;
    }

    @JsonProperty("lastUpdate")
    public String getLastUpdate() {
        return lastUpdate;
    }

    @JsonProperty("lastUpdate")
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @JsonProperty("versionString")
    public String getVersionString() {
        return versionString;
    }

    @JsonProperty("versionString")
    public void setVersionString(String versionString) {
        this.versionString = versionString;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("descriptiveKeywords")
    public String getDescriptiveKeywords() {
        return descriptiveKeywords;
    }

    @JsonProperty("descriptiveKeywords")
    public void setDescriptiveKeywords(String descriptiveKeywords) {
        this.descriptiveKeywords = descriptiveKeywords;
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
