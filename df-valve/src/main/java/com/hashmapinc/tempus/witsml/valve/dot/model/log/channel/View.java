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

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uuid",
    "startIndex",
    "endIndex",
    "growingStatus",
    "uid",
    "wellDatum",
    "nullValue",
    "channelState",
    "classIndex",
    "mnemAlias",
    "alternateIndex",
    "sensorOffset",
    "densData",
    "traceOrigin",
    "traceState",
    "namingSystem",
    "mnemonic",
    "dataType",
    "description",
    "uom",
    "source",
    "axisDefinition",
    "timeDepth",
    "channelClass",
    "classWitsml",
    "runNumber",
    "passNumber",
    "loggingCompanyName",
    "loggingCompanyCode",
    "toolName",
    "toolClass",
    "derivation",
    "loggingMethod",
    "nominalHoleSize",
    "pointMetadata",
    "derivedFrom",
    "index",
    "aliases",
    "citation",
    "customData",
    "extensionNameValue",
    "objectVersion",
    "existenceKind"
})
// TODO Is this change necessary? TMS
@XmlRootElement
public class View {

    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("startIndex")
    private String startIndex;
    @JsonProperty("endIndex")
    private String endIndex;
    @JsonProperty("growingStatus")
    private String growingStatus;
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("wellDatum")
    private WellDatum wellDatum;
    @JsonProperty("nullValue")
    private String nullValue;
    @JsonProperty("channelState")
    private String channelState;
    @JsonProperty("classIndex")
    private Integer classIndex;
    @JsonProperty("mnemAlias")
    private MnemAlias mnemAlias;
    @JsonProperty("alternateIndex")
    private Boolean alternateIndex;
    @JsonProperty("sensorOffset")
    private SensorOffset sensorOffset;
    @JsonProperty("densData")
    private DensData densData;
    @JsonProperty("traceOrigin")
    private String traceOrigin;
    @JsonProperty("traceState")
    private String traceState;
    @JsonProperty("namingSystem")
    private String namingSystem;
    @JsonProperty("mnemonic")
    private String mnemonic;
    @JsonProperty("dataType")
    private String dataType;
    @JsonProperty("description")
    private String description;
    @JsonProperty("uom")
    private String uom;
    @JsonProperty("source")
    private String source;
    @JsonProperty("axisDefinition")
    private List<AxisDefinition> axisDefinition = null;
    @JsonProperty("timeDepth")
    private String timeDepth;
    @JsonProperty("channelClass")
    private ChannelClass channelClass;
    @JsonProperty("classWitsml")
    private String classWitsml;
    @JsonProperty("runNumber")
    private String runNumber;
    @JsonProperty("passNumber")
    private String passNumber;
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
    @JsonProperty("pointMetadata")
    private List<PointMetadatum> pointMetadata = null;
    @JsonProperty("derivedFrom")
    private List<DerivedFrom> derivedFrom = null;
    @JsonProperty("index")
    private List<Index> index = null;
    @JsonProperty("aliases")
    private List<Alias> aliases = null;
    @JsonProperty("citation")
    private Citation citation;
    @JsonProperty("customData")
    private String customData;
    @JsonProperty("extensionNameValue")
    private List<ExtensionNameValue_> extensionNameValue = null;
    @JsonProperty("objectVersion")
    private String objectVersion;
    @JsonProperty("existenceKind")
    private String existenceKind;
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

    @JsonProperty("growingStatus")
    public String getGrowingStatus() {
        return growingStatus;
    }

    @JsonProperty("growingStatus")
    public void setGrowingStatus(String growingStatus) {
        this.growingStatus = growingStatus;
    }

    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonProperty("wellDatum")
    public WellDatum getWellDatum() {
        return wellDatum;
    }

    @JsonProperty("wellDatum")
    public void setWellDatum(WellDatum wellDatum) {
        this.wellDatum = wellDatum;
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

    @JsonProperty("classIndex")
    public Integer getClassIndex() {
        return classIndex;
    }

    @JsonProperty("classIndex")
    public void setClassIndex(Integer classIndex) {
        this.classIndex = classIndex;
    }

    @JsonProperty("mnemAlias")
    public MnemAlias getMnemAlias() {
        return mnemAlias;
    }

    @JsonProperty("mnemAlias")
    public void setMnemAlias(MnemAlias mnemAlias) {
        this.mnemAlias = mnemAlias;
    }

    @JsonProperty("alternateIndex")
    public Boolean getAlternateIndex() {
        return alternateIndex;
    }

    @JsonProperty("alternateIndex")
    public void setAlternateIndex(Boolean alternateIndex) {
        this.alternateIndex = alternateIndex;
    }

    @JsonProperty("sensorOffset")
    public SensorOffset getSensorOffset() {
        return sensorOffset;
    }

    @JsonProperty("sensorOffset")
    public void setSensorOffset(SensorOffset sensorOffset) {
        this.sensorOffset = sensorOffset;
    }

    @JsonProperty("densData")
    public DensData getDensData() {
        return densData;
    }

    @JsonProperty("densData")
    public void setDensData(DensData densData) {
        this.densData = densData;
    }

    @JsonProperty("traceOrigin")
    public String getTraceOrigin() {
        return traceOrigin;
    }

    @JsonProperty("traceOrigin")
    public void setTraceOrigin(String traceOrigin) {
        this.traceOrigin = traceOrigin;
    }

    @JsonProperty("traceState")
    public String getTraceState() {
        return traceState;
    }

    @JsonProperty("traceState")
    public void setTraceState(String traceState) {
        this.traceState = traceState;
    }

    @JsonProperty("namingSystem")
    public String getNamingSystem() {
        return namingSystem;
    }

    @JsonProperty("namingSystem")
    public void setNamingSystem(String namingSystem) {
        this.namingSystem = namingSystem;
    }

    @JsonProperty("mnemonic")
    public String getMnemonic() {
        return mnemonic;
    }

    @JsonProperty("mnemonic")
    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @JsonProperty("dataType")
    public String getDataType() {
        return dataType;
    }

    @JsonProperty("dataType")
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("uom")
    public String getUom() {
        return uom;
    }

    @JsonProperty("uom")
    public void setUom(String uom) {
        this.uom = uom;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("axisDefinition")
    public List<AxisDefinition> getAxisDefinition() {
        return axisDefinition;
    }

    @JsonProperty("axisDefinition")
    public void setAxisDefinition(List<AxisDefinition> axisDefinition) {
        this.axisDefinition = axisDefinition;
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

    @JsonProperty("classWitsml")
    public String getClassWitsml() {
        return classWitsml;
    }

    @JsonProperty("classWitsml")
    public void setClassWitsml(String classWitsml) {
        this.classWitsml = classWitsml;
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

    @JsonProperty("pointMetadata")
    public List<PointMetadatum> getPointMetadata() {
        return pointMetadata;
    }

    @JsonProperty("pointMetadata")
    public void setPointMetadata(List<PointMetadatum> pointMetadata) {
        this.pointMetadata = pointMetadata;
    }

    @JsonProperty("derivedFrom")
    public List<DerivedFrom> getDerivedFrom() {
        return derivedFrom;
    }

    @JsonProperty("derivedFrom")
    public void setDerivedFrom(List<DerivedFrom> derivedFrom) {
        this.derivedFrom = derivedFrom;
    }

    @JsonProperty("index")
    public List<Index> getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(List<Index> index) {
        this.index = index;
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
    public List<ExtensionNameValue_> getExtensionNameValue() {
        return extensionNameValue;
    }

    @JsonProperty("extensionNameValue")
    public void setExtensionNameValue(List<ExtensionNameValue_> extensionNameValue) {
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
