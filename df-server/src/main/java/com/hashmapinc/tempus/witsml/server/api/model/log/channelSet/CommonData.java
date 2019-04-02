
package com.hashmapinc.tempus.witsml.server.api.model.log.channelSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "itemState",
    "serviceCategory",
    "comments",
    "acquisitionTimeZone",
    "defaultDatum",
    "privateGroupOnly",
    "extensionAny",
    "extensionNameValue",
    "sourceName",
    "dTimCreation",
    "dTimLastChange"
})
public class CommonData {

    @JsonProperty("itemState")
    private String itemState;
    @JsonProperty("serviceCategory")
    private String serviceCategory;
    @JsonProperty("comments")
    private String comments;
    @JsonProperty("acquisitionTimeZone")
    private List<AcquisitionTimeZone> acquisitionTimeZone = null;
    @JsonProperty("defaultDatum")
    private DefaultDatum defaultDatum;
    @JsonProperty("privateGroupOnly")
    private Boolean privateGroupOnly;
    @JsonProperty("extensionAny")
    private String extensionAny;
    @JsonProperty("extensionNameValue")
    private List<ExtensionNameValue_> extensionNameValue = null;
    @JsonProperty("sourceName")
    private String sourceName;
    @JsonProperty("dTimCreation")
    private String dTimCreation;
    @JsonProperty("dTimLastChange")
    private String dTimLastChange;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("itemState")
    public String getItemState() {
        return itemState;
    }

    @JsonProperty("itemState")
    public void setItemState(String itemState) {
        this.itemState = itemState;
    }

    @JsonProperty("serviceCategory")
    public String getServiceCategory() {
        return serviceCategory;
    }

    @JsonProperty("serviceCategory")
    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    @JsonProperty("comments")
    public String getComments() {
        return comments;
    }

    @JsonProperty("comments")
    public void setComments(String comments) {
        this.comments = comments;
    }

    @JsonProperty("acquisitionTimeZone")
    public List<AcquisitionTimeZone> getAcquisitionTimeZone() {
        return acquisitionTimeZone;
    }

    @JsonProperty("acquisitionTimeZone")
    public void setAcquisitionTimeZone(List<AcquisitionTimeZone> acquisitionTimeZone) {
        this.acquisitionTimeZone = acquisitionTimeZone;
    }

    @JsonProperty("defaultDatum")
    public DefaultDatum getDefaultDatum() {
        return defaultDatum;
    }

    @JsonProperty("defaultDatum")
    public void setDefaultDatum(DefaultDatum defaultDatum) {
        this.defaultDatum = defaultDatum;
    }

    @JsonProperty("privateGroupOnly")
    public Boolean getPrivateGroupOnly() {
        return privateGroupOnly;
    }

    @JsonProperty("privateGroupOnly")
    public void setPrivateGroupOnly(Boolean privateGroupOnly) {
        this.privateGroupOnly = privateGroupOnly;
    }

    @JsonProperty("extensionAny")
    public String getExtensionAny() {
        return extensionAny;
    }

    @JsonProperty("extensionAny")
    public void setExtensionAny(String extensionAny) {
        this.extensionAny = extensionAny;
    }

    @JsonProperty("extensionNameValue")
    public List<ExtensionNameValue_> getExtensionNameValue() {
        return extensionNameValue;
    }

    @JsonProperty("extensionNameValue")
    public void setExtensionNameValue(List<ExtensionNameValue_> extensionNameValue) {
        this.extensionNameValue = extensionNameValue;
    }

    @JsonProperty("sourceName")
    public String getSourceName() {
        return sourceName;
    }

    @JsonProperty("sourceName")
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @JsonProperty("dTimCreation")
    public String getDTimCreation() {
        return dTimCreation;
    }

    @JsonProperty("dTimCreation")
    public void setDTimCreation(String dTimCreation) {
        this.dTimCreation = dTimCreation;
    }

    @JsonProperty("dTimLastChange")
    public String getDTimLastChange() {
        return dTimLastChange;
    }

    @JsonProperty("dTimLastChange")
    public void setDTimLastChange(String dTimLastChange) {
        this.dTimLastChange = dTimLastChange;
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
