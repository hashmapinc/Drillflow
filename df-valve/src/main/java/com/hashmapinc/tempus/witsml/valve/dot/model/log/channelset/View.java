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

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uuid",
    "bhaRunNumber",
    "aliases",
    "citation",
    "customData",
    "extensionNameValue",
    "objectVersion",
    "existenceKind",
    "index",
    "objectGrowing",
    "dataUpateRate",
    "curveSensorsAligned",
    "dataGroup",
    "stepIncrement",
    "logParam",
    "dataDelimiter",
    "nullValue",
    "channelState",
    "timeDepth",
    "channelClass",
    "runNumber",
    "passNumber",
    "startIndex",
    "endIndex",
    "loggingCompanyName",
    "loggingCompanyCode",
    "toolName",
    "toolClass",
    "derivation",
    "loggingMethod",
    "nominalHoleSize",
    "dataContext",
    "commonData",
    "uid",
    "uidWell",
    "uidWellbore",
    "wellId",
    "wellboreId"
})
@XmlRootElement
public class View {

    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("bhaRunNumber")
    private Integer bhaRunNumber;
    @JsonProperty("aliases")
    private List<Alias> aliases = null;
            // new ArrayList<Alias>;
    @JsonProperty("citation")
    private Citation citation;
    @JsonProperty("customData")
    private String customData;
    @JsonProperty("extensionNameValue")
    private List<ExtensionNameValue> extensionNameValue = null;
            //new ArrayList<ExtensionNameValue>;
    @JsonProperty("objectVersion")
    private String objectVersion;
    @JsonProperty("existenceKind")
    private String existenceKind;
    @JsonProperty("index")
    private List<Index> index = null;
            // new ArrayList<Index>;
    private Boolean objectGrowing;
    @JsonProperty("dataUpateRate")
    private Integer dataUpateRate;
    @JsonProperty("curveSensorsAligned")
    private Boolean curveSensorsAligned;
    @JsonProperty("dataGroup")
    private String dataGroup;
    @JsonProperty("stepIncrement")
    private StepIncrement stepIncrement;
    @JsonProperty("logParam")
    private List<LogParam> logParam = null;
    @JsonProperty("dataDelimiter")
    private String dataDelimiter;
    @JsonProperty("nullValue")
    private String nullValue;
    @JsonProperty("channelState")
    private String channelState;
    @JsonProperty("timeDepth")
    private String timeDepth;
    @JsonProperty("channelClass")
    private ChannelClass channelClass;
    @JsonProperty("runNumber")
    private String runNumber;
    @JsonProperty("passNumber")
    private String passNumber;
    @JsonProperty("startIndex")
    private String startIndex;
    @JsonProperty("endIndex")
    private String endIndex;
    @JsonProperty("loggingCompanyName")
    private String loggingCompanyName;
    @JsonProperty("loggingCompanyCode")
    private String loggingCompanyCode;
    @JsonProperty("toolName")
    private String toolName;
    @JsonProperty("toolClass")
    private String toolClass;
    @JsonProperty("derivation")
    private String derivation;
    @JsonProperty("loggingMethod")
    private String loggingMethod;
    @JsonProperty("nominalHoleSize")
    private NominalHoleSize nominalHoleSize;
    @JsonProperty("dataContext")
    private String dataContext;
    @JsonProperty("commonData")
    private CommonData commonData;
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("uidWell")
    private String uidWell;
    @JsonProperty("uidWellbore")
    private String uidWellbore;
    @JsonProperty("wellId")
    private String wellId;
    @JsonProperty("wellboreId")
    private String wellboreId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("bhaRunNumber")
    public Integer getBhaRunNumber() {
        return bhaRunNumber;
    }

    @JsonProperty("bhaRunNumber")
    public void setBhaRunNumber(Integer bhaRunNumber) {
        this.bhaRunNumber = bhaRunNumber;
    }

    @JsonProperty("aliases")
    public List<Alias> getAliases() {
        return aliases;
    }

    @JsonProperty("aliases")
    public void setAliases(List<Alias> aliases) {
        this.aliases = aliases;
    }

    @JsonProperty("citation")
    public Citation getCitation() {
        return citation;
    }

    @JsonProperty("citation")
    public void setCitation(Citation citation) {
        this.citation = citation;
    }

    @JsonProperty("customData")
    public String getCustomData() {
        return customData;
    }

    @JsonProperty("customData")
    public void setCustomData(String customData) {
        this.customData = customData;
    }

    @JsonProperty("extensionNameValue")
    public List<ExtensionNameValue> getExtensionNameValue() {
        return extensionNameValue;
    }

    @JsonProperty("extensionNameValue")
    public void setExtensionNameValue(List<ExtensionNameValue> extensionNameValue) {
        this.extensionNameValue = extensionNameValue;
    }

    @JsonProperty("objectVersion")
    public String getObjectVersion() {
        return objectVersion;
    }

    @JsonProperty("objectVersion")
    public void setObjectVersion(String objectVersion) {
        this.objectVersion = objectVersion;
    }

    @JsonProperty("existenceKind")
    public String getExistenceKind() {
        return existenceKind;
    }

    @JsonProperty("existenceKind")
    public void setExistenceKind(String existenceKind) {
        this.existenceKind = existenceKind;
    }

    @JsonProperty("index")
    public List<Index> getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(List<Index> index) {
        this.index = index;
    }

    @JsonProperty("objectGrowing")
    public Boolean getObjectGrowing() {
        return objectGrowing;
    }

    @JsonProperty("objectGrowing")
    public void setObjectGrowing(Boolean objectGrowing) {
        this.objectGrowing = objectGrowing;
    }

    @JsonProperty("dataUpateRate")
    public Integer getDataUpateRate() {
        return dataUpateRate;
    }

    @JsonProperty("dataUpateRate")
    public void setDataUpateRate(Integer dataUpateRate) {
        this.dataUpateRate = dataUpateRate;
    }

    @JsonProperty("curveSensorsAligned")
    public Boolean getCurveSensorsAligned() {
        return curveSensorsAligned;
    }

    @JsonProperty("curveSensorsAligned")
    public void setCurveSensorsAligned(Boolean curveSensorsAligned) {
        this.curveSensorsAligned = curveSensorsAligned;
    }

    @JsonProperty("dataGroup")
    public String getDataGroup() {
        return dataGroup;
    }

    @JsonProperty("dataGroup")
    public void setDataGroup(String dataGroup) {
        this.dataGroup = dataGroup;
    }

    @JsonProperty("stepIncrement")
    public StepIncrement getStepIncrement() {
        return stepIncrement;
    }

    @JsonProperty("stepIncrement")
    public void setStepIncrement(StepIncrement stepIncrement) {
        this.stepIncrement = stepIncrement;
    }

    @JsonProperty("logParam")
    public List<LogParam> getLogParam() {
        return logParam;
    }

    @JsonProperty("logParam")
    public void setLogParam(List<LogParam> logParam) {
        this.logParam = logParam;
    }

    @JsonProperty("dataDelimiter")
    public String getDataDelimiter() {
        return dataDelimiter;
    }

    @JsonProperty("dataDelimiter")
    public void setDataDelimiter(String dataDelimiter) {
        this.dataDelimiter = dataDelimiter;
    }

    @JsonProperty("nullValue")
    public String getNullValue() {
        return nullValue;
    }

    @JsonProperty("nullValue")
    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }

    @JsonProperty("channelState")
    public String getChannelState() {
        return channelState;
    }

    @JsonProperty("channelState")
    public void setChannelState(String channelState) {
        this.channelState = channelState;
    }

    @JsonProperty("timeDepth")
    public String getTimeDepth() {
        return timeDepth;
    }

    @JsonProperty("timeDepth")
    public void setTimeDepth(String timeDepth) {
        this.timeDepth = timeDepth;
    }

    @JsonProperty("channelClass")
    public ChannelClass getChannelClass() {
        return channelClass;
    }

    @JsonProperty("channelClass")
    public void setChannelClass(ChannelClass channelClass) {
        this.channelClass = channelClass;
    }

    @JsonProperty("runNumber")
    public String getRunNumber() {
        return runNumber;
    }

    @JsonProperty("runNumber")
    public void setRunNumber(String runNumber) {
        this.runNumber = runNumber;
    }

    @JsonProperty("passNumber")
    public String getPassNumber() {
        return passNumber;
    }

    @JsonProperty("passNumber")
    public void setPassNumber(String passNumber) {
        this.passNumber = passNumber;
    }

    @JsonProperty("startIndex")
    public String getStartIndex() {
        return startIndex;
    }

    @JsonProperty("startIndex")
    public void setStartIndex(String startIndex) {
        this.startIndex = startIndex;
    }

    @JsonProperty("endIndex")
    public String getEndIndex() {
        return endIndex;
    }

    @JsonProperty("endIndex")
    public void setEndIndex(String endIndex) {
        this.endIndex = endIndex;
    }

    @JsonProperty("loggingCompanyName")
    public String getLoggingCompanyName() {
        return loggingCompanyName;
    }

    @JsonProperty("loggingCompanyName")
    public void setLoggingCompanyName(String loggingCompanyName) {
        this.loggingCompanyName = loggingCompanyName;
    }

    @JsonProperty("loggingCompanyCode")
    public String getLoggingCompanyCode() {
        return loggingCompanyCode;
    }

    @JsonProperty("loggingCompanyCode")
    public void setLoggingCompanyCode(String loggingCompanyCode) {
        this.loggingCompanyCode = loggingCompanyCode;
    }

    @JsonProperty("toolName")
    public String getToolName() {
        return toolName;
    }

    @JsonProperty("toolName")
    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    @JsonProperty("toolClass")
    public String getToolClass() {
        return toolClass;
    }

    @JsonProperty("toolClass")
    public void setToolClass(String toolClass) {
        this.toolClass = toolClass;
    }

    @JsonProperty("derivation")
    public String getDerivation() {
        return derivation;
    }

    @JsonProperty("derivation")
    public void setDerivation(String derivation) {
        this.derivation = derivation;
    }

    @JsonProperty("loggingMethod")
    public String getLoggingMethod() {
        return loggingMethod;
    }

    @JsonProperty("loggingMethod")
    public void setLoggingMethod(String loggingMethod) {
        this.loggingMethod = loggingMethod;
    }

    @JsonProperty("nominalHoleSize")
    public NominalHoleSize getNominalHoleSize() {
        return nominalHoleSize;
    }

    @JsonProperty("nominalHoleSize")
    public void setNominalHoleSize(NominalHoleSize nominalHoleSize) {
        this.nominalHoleSize = nominalHoleSize;
    }

    @JsonProperty("dataContext")
    public String getDataContext() {
        return dataContext;
    }

    @JsonProperty("dataContext")
    public void setDataContext(String dataContext) {
        this.dataContext = dataContext;
    }

    @JsonProperty("commonData")
    public CommonData getCommonData() {
        return commonData;
    }

    @JsonProperty("commonData")
    public void setCommonData(CommonData commonData) {
        this.commonData = commonData;
    }

    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonProperty("uidWell")
    public String getUidWell() {
        return uidWell;
    }

    @JsonProperty("uidWell")
    public void setUidWell(String uidWell) {
        this.uidWell = uidWell;
    }

    @JsonProperty("uidWellbore")
    public String getUidWellbore() {
        return uidWellbore;
    }

    @JsonProperty("uidWellbore")
    public void setUidWellbore(String uidWellbore) {
        this.uidWellbore = uidWellbore;
    }

    @JsonProperty("wellId")
    public String getWellId() {
        return wellId;
    }

    @JsonProperty("wellId")
    public void setWellId(String wellId) {
        this.wellId = wellId;
    }

    @JsonProperty("wellboreId")
    public String getWellboreId() {
        return wellboreId;
    }

    @JsonProperty("wellboreId")
    public void setWellboreId(String wellboreId) {
        this.wellboreId = wellboreId;
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
