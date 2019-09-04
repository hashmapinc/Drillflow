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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.hashmapinc.tempus.WitsmlObjects.v1311.GenericMeasure;
import com.hashmapinc.tempus.WitsmlObjects.v1311.IndexCurve;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.Channel;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "uuid", "bhaRunNumber", "aliases", "citation", "customData", "extensionNameValue", "objectVersion",
        "existenceKind", "index", "objectGrowing", "dataUpateRate", "curveSensorsAligned", "dataGroup", "stepIncrement",
        "logParam", "dataDelimiter", "nullValue", "channelState", "timeDepth", "channelClass", "runNumber",
        "passNumber", "startIndex", "endIndex", "loggingCompanyName", "loggingCompanyCode", "toolName", "toolClass",
        "derivation", "loggingMethod", "nominalHoleSize", "dataContext", "commonData", "uid", "uidWell", "uidWellbore",
        "wellId", "wellboreId" })
public class ChannelSet {

    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("bhaRunNumber")
    private Short bhaRunNumber;
    @JsonProperty("aliases")
    private List<Alias> aliases = null;
    @JsonProperty("citation")
    private Citation citation;
    @JsonProperty("customData")
    private String customData;
    @JsonProperty("extensionNameValue")
    private List<ExtensionNameValue> extensionNameValue = null;
    @JsonProperty("objectVersion")
    private String objectVersion;
    @JsonProperty("existenceKind")
    private String existenceKind;
    @JsonProperty("index")
    private List<Index> index = null;
    @JsonProperty("objectGrowing")
    private String objectGrowing;
    @JsonProperty("dataUpateRate")
    private Integer dataUpateRate;
    @JsonProperty("curveSensorsAligned")
    private String curveSensorsAligned;
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
    public Short getBhaRunNumber() {
        return bhaRunNumber;
    }

    @JsonProperty("bhaRunNumber")
    public void setBhaRunNumber(Short bhaRunNumber) {
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
    public String getObjectGrowing() {
        return objectGrowing;
    }

    @JsonProperty("objectGrowing")
    public void setObjectGrowing(String objectGrowing) {
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
    public String getCurveSensorsAligned() {
        return curveSensorsAligned;
    }

    @JsonProperty("curveSensorsAligned")
    public void setCurveSensorsAligned(String curveSensorsAligned) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelSet that = (ChannelSet) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(bhaRunNumber, that.bhaRunNumber) &&
                Objects.equals(aliases, that.aliases) &&
                Objects.equals(citation, that.citation) &&
                Objects.equals(customData, that.customData) &&
                Objects.equals(objectVersion, that.objectVersion) &&
                Objects.equals(index, that.index) &&
                Objects.equals(dataUpateRate, that.dataUpateRate) &&
                Objects.equals(curveSensorsAligned, that.curveSensorsAligned) &&
                Objects.equals(dataGroup, that.dataGroup) &&
                Objects.equals(stepIncrement, that.stepIncrement) &&
                Objects.equals(logParam, that.logParam) &&
                Objects.equals(dataDelimiter, that.dataDelimiter) &&
                Objects.equals(nullValue, that.nullValue) &&
                Objects.equals(channelState, that.channelState) &&
                Objects.equals(timeDepth, that.timeDepth) &&
                Objects.equals(channelClass, that.channelClass) &&
                Objects.equals(runNumber, that.runNumber) &&
                Objects.equals(passNumber, that.passNumber) &&
                Objects.equals(loggingCompanyName, that.loggingCompanyName) &&
                Objects.equals(loggingCompanyCode, that.loggingCompanyCode) &&
                Objects.equals(toolName, that.toolName) &&
                Objects.equals(toolClass, that.toolClass) &&
                Objects.equals(derivation, that.derivation) &&
                Objects.equals(loggingMethod, that.loggingMethod) &&
                Objects.equals(nominalHoleSize, that.nominalHoleSize) &&
                Objects.equals(dataContext, that.dataContext) &&
                Objects.equals(commonData, that.commonData) &&
                Objects.equals(uid, that.uid) &&
                Objects.equals(uidWell, that.uidWell) &&
                Objects.equals(uidWellbore, that.uidWellbore) &&
                Objects.equals(wellId, that.wellId) &&
                Objects.equals(wellboreId, that.wellboreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, bhaRunNumber, aliases, citation, customData, objectVersion,
                            index, dataUpateRate, curveSensorsAligned, dataGroup, stepIncrement,
                            logParam, dataDelimiter, nullValue, channelState, timeDepth,
                            channelClass, runNumber, passNumber, loggingCompanyName, loggingCompanyCode,
                            toolName, toolClass, derivation, loggingMethod, nominalHoleSize,
                            dataContext, commonData, uid, uidWell, uidWellbore, wellId, wellboreId);
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

    public String toJson() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        om.setDateFormat(new StdDateFormat());
        return om.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    public static ChannelSet from1411(com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog witsmlObj) {

        ChannelSet cs = new ChannelSet();
        Citation citation = new Citation();
        citation.setTitle(witsmlObj.getName());
        citation.setDescription(witsmlObj.getDescription());
        if (witsmlObj.getCreationDate() != null)
            citation.setCreation(witsmlObj.getCreationDate());
        cs.setCitation(citation);
        cs.setBhaRunNumber(witsmlObj.getBhaRunNumber());
        cs.setDataGroup(witsmlObj.getDataGroup());
        cs.setDataDelimiter(witsmlObj.getDataDelimiter());
        cs.setRunNumber(witsmlObj.getRunNumber());
        cs.setPassNumber(witsmlObj.getPass());
        cs.setCurveSensorsAligned(witsmlObj.isCurveSensorsAligned());
        cs.setCustomData(witsmlObj.getCustomData());
        cs.setLoggingCompanyName(witsmlObj.getServiceCompany());
        cs.setNullValue(witsmlObj.getNullValue());

        // Sort out if this is a time or a depth log
        String indexType;
        if (witsmlObj.getIndexType() != null){
            if (witsmlObj.getIndexType().contains("depth")) {
                indexType = "depth";
                cs.setTimeDepth(indexType);
                if (witsmlObj.getStartIndex() != null)
                    cs.setStartIndex(witsmlObj.getStartIndex().getValue().toString());
                if (witsmlObj.getEndIndex() != null)
                    cs.setEndIndex(witsmlObj.getEndIndex().getValue().toString());
            } else {
                indexType = "time";
                cs.setTimeDepth(indexType);
                if (witsmlObj.getStartDateTimeIndex() != null)
                    cs.setStartIndex(witsmlObj.getStartDateTimeIndex());
                if (witsmlObj.getEndDateTimeIndex() != null)
                    cs.setEndIndex(witsmlObj.getEndDateTimeIndex());
            }
        }

        cs.setNullValue(witsmlObj.getNullValue());
        cs.setObjectGrowing(witsmlObj.getObjectGrowing());
        if (StepIncrement.from1411(witsmlObj.getStepIncrement())!=null)
            cs.setStepIncrement(StepIncrement.from1411(witsmlObj.getStepIncrement()));
        cs.setIndex(Index.from1411(witsmlObj));
        cs.setLogParam(LogParam.from1411(witsmlObj.getLogParam()));
        cs.setCommonData(CommonData.getCommonDataFrom1411(witsmlObj.getCommonData()));
        return cs;
    }

    public static ChannelSet from1311(com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog witsmlObj) {
        ChannelSet cs = new ChannelSet();
        Citation citation = new Citation();
        citation.setTitle(witsmlObj.getName());
        citation.setDescription(witsmlObj.getDescription());
        cs.setCitation(citation);
        cs.setBhaRunNumber(witsmlObj.getBhaRunNumber());
        cs.setRunNumber(witsmlObj.getRunNumber());
        cs.setPassNumber(witsmlObj.getPass());
        cs.setCustomData(witsmlObj.getCustomData());
        cs.setLoggingCompanyName(witsmlObj.getServiceCompany());
        cs.setNullValue(witsmlObj.getNullValue());
        String indexType;
        if (witsmlObj.getIndexType()!=null){
            if (witsmlObj.getIndexType().toLowerCase().contains("depth")) {
                indexType = "depth";
                cs.setTimeDepth(indexType);
                if (witsmlObj.getStartIndex() != null)
                    cs.setStartIndex(witsmlObj.getStartIndex().getValue().toString());
                if (witsmlObj.getEndIndex() != null)
                    cs.setEndIndex(witsmlObj.getEndIndex().getValue().toString());
            } else {
                indexType = "time";
                cs.setTimeDepth(indexType);
                if (witsmlObj.getStartIndex() != null)
                    cs.setStartIndex(witsmlObj.getStartDateTimeIndex());
                if (witsmlObj.getEndIndex() != null)
                    cs.setEndIndex(witsmlObj.getEndDateTimeIndex());
            }
        }
        cs.setNullValue(witsmlObj.getNullValue());
        cs.setObjectGrowing(witsmlObj.isObjectGrowing());
        if (StepIncrement.from1311(witsmlObj.getStepIncrement())!=null)
            cs.setStepIncrement(StepIncrement.from1311(witsmlObj.getStepIncrement()));
        cs.setIndex(Index.from1311(witsmlObj));
        cs.setLogParam(LogParam.from1311(witsmlObj.getLogParam()));
        cs.setCommonData(CommonData.getCommonDataFrom1311(witsmlObj.getCommonData()));
        return cs;
    }

    public static com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog to1411LogData(ChannelSet channelSet, List<Channel> channels)
            throws DatatypeConfigurationException, ParseException {
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog wmlLog =
                new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog();
        // get max startIndex from channels

        String maxStartIndex = channels.stream().filter(emp -> emp.getStartIndex() != null).map(Channel::getStartIndex).max(String::compareTo).get();

        wmlLog.setUid(channelSet.getUid());
        wmlLog.setCurveSensorsAligned(channelSet.getCurveSensorsAligned());
        wmlLog.setDataGroup(channelSet.getDataGroup());
        wmlLog.setDataDelimiter(channelSet.getDataDelimiter());
        wmlLog.setName(channelSet.getCitation().getTitle());
        wmlLog.setServiceCompany(channelSet.getLoggingCompanyName());
        wmlLog.setRunNumber(channelSet.getRunNumber());
        wmlLog.setBhaRunNumber(channelSet.getBhaRunNumber());
        wmlLog.setPass(channelSet.getPassNumber());
        wmlLog.setDescription(channelSet.getCitation().getDescription());
        //wmlLog.setIndexType(channelSet.getTimeDepth());
        wmlLog.setStepIncrement(StepIncrement.to1411(channelSet.getStepIncrement()));
        if (channelSet.getIndex() != null && channelSet.getIndex().size() > 0) {
            wmlLog.setIndexType(channelSet.getIndex().get(0).getIndexType());
            wmlLog.setIndexCurve(channelSet.getIndex().get(0).getMnemonic());
        }
        wmlLog.setNullValue(channelSet.getNullValue());
        wmlLog.setObjectGrowing(channelSet.getObjectGrowing());
        wmlLog.setLogParam(LogParam.to1411(channelSet.getLogParam()));

        // Set commonData
        com.hashmapinc.tempus.WitsmlObjects.v1411.CsCommonData commonData = new com.hashmapinc.tempus.WitsmlObjects.v1411.CsCommonData();
        commonData.setDTimCreation(convertIsoDateToXML(channelSet.getCitation().getCreation()));
        commonData.setItemState(channelSet.getCommonData().getItemState());
        commonData.setComments(channelSet.getCommonData().getComments());
        wmlLog.setCommonData(commonData);

        if (channelSet.getTimeDepth().toLowerCase().contains("depth")) {
            if (channelSet.getStartIndex() != null){
                com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure startMeasure =
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                startMeasure.setUom("m");
                startMeasure.setValue(Double.parseDouble(channelSet.getStartIndex()));
                wmlLog.setStartIndex(startMeasure);
                com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure endMeasure =
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                endMeasure.setUom("m");
                endMeasure.setValue(Double.parseDouble(channelSet.getEndIndex()));
                wmlLog.setEndIndex(endMeasure);
            }
        } else {
            if (channelSet.getEndIndex() != null){
                wmlLog.setStartDateTimeIndex(channelSet.getStartIndex());
                wmlLog.setEndDateTimeIndex(channelSet.getEndIndex());
            }
        }
        return wmlLog;
    }

    public static com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog to1411(ChannelSet channelSet)
            throws DatatypeConfigurationException, ParseException {
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog wmlLog =
                new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog();

        wmlLog.setUid(channelSet.getUid());
        wmlLog.setCurveSensorsAligned(channelSet.getCurveSensorsAligned());
        wmlLog.setDataGroup(channelSet.getDataGroup());
        wmlLog.setDataDelimiter(channelSet.getDataDelimiter());
        wmlLog.setName(channelSet.getCitation().getTitle());
        wmlLog.setServiceCompany(channelSet.getLoggingCompanyName());
        wmlLog.setRunNumber(channelSet.getRunNumber());
        wmlLog.setBhaRunNumber(channelSet.getBhaRunNumber());
        wmlLog.setPass(channelSet.getPassNumber());
        wmlLog.setDescription(channelSet.getCitation().getDescription());
        //wmlLog.setIndexType(channelSet.getTimeDepth());
        wmlLog.setStepIncrement(StepIncrement.to1411(channelSet.getStepIncrement()));
        if (channelSet.getIndex() != null && channelSet.getIndex().size() > 0) {
            wmlLog.setIndexType(channelSet.getIndex().get(0).getIndexType());
            wmlLog.setIndexCurve(channelSet.getIndex().get(0).getMnemonic());
        }
        wmlLog.setNullValue(channelSet.getNullValue());
        wmlLog.setObjectGrowing(channelSet.getObjectGrowing());
        wmlLog.setLogParam(LogParam.to1411(channelSet.getLogParam()));
        wmlLog.setCreationDate(channelSet.getCitation().getCreation());

       // Set commonData
        com.hashmapinc.tempus.WitsmlObjects.v1411.CsCommonData commonData = new com.hashmapinc.tempus.WitsmlObjects.v1411.CsCommonData();
        if(channelSet.getCitation().getCreation() != null){
            commonData.setDTimCreation(convertIsoDateToXML(channelSet.getCitation().getCreation()));
        }
        if(channelSet.commonData.getDTimLastChange()!= null){
            commonData.setDTimLastChange(convertIsoDateToXML(channelSet.getCommonData().getDTimLastChange()));
        }
        commonData.setItemState(channelSet.getCommonData().getItemState());
        commonData.setComments(channelSet.getCommonData().getComments());
        wmlLog.setCommonData(commonData);

        if (channelSet.getTimeDepth().toLowerCase().contains("depth")) {
            if (channelSet.getStartIndex() != null){
                com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure startMeasure =
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                startMeasure.setUom("m");
                startMeasure.setValue(Double.parseDouble(channelSet.getStartIndex()));
                wmlLog.setStartIndex(startMeasure);
                com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure endMeasure =
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                endMeasure.setUom("m");
                endMeasure.setValue(Double.parseDouble(channelSet.getEndIndex()));
                wmlLog.setEndIndex(endMeasure);
            }
        } else {
            if (channelSet.getEndIndex() != null){
                wmlLog.setStartDateTimeIndex(channelSet.getStartIndex());
                wmlLog.setEndDateTimeIndex(channelSet.getEndIndex());
            }
        }
        return wmlLog;
    }

    public static com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog to1311(ChannelSet channelSet)
            throws DatatypeConfigurationException, ParseException {
        com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog wmlLog =
                new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog();

        wmlLog.setUid(channelSet.getUid());
        wmlLog.setName(channelSet.getCitation().getTitle());
        wmlLog.setServiceCompany(channelSet.getLoggingCompanyName());
        wmlLog.setRunNumber(channelSet.getRunNumber());
        wmlLog.setBhaRunNumber(channelSet.getBhaRunNumber());
        wmlLog.setPass(channelSet.getPassNumber());
        wmlLog.setDescription(channelSet.getCitation().getDescription());
        //wmlLog.setIndexType(channelSet.getTimeDepth());
        wmlLog.setStepIncrement(StepIncrement.to1311(channelSet.getStepIncrement()));
        if (channelSet.getIndex() != null && channelSet.getIndex().size() > 0) {
            IndexCurve curve = new IndexCurve();
            curve.setColumnIndex((short) 1);
            curve.setValue(channelSet.getIndex().get(0).getMnemonic());
            wmlLog.setIndexCurve(curve);
            wmlLog.setIndexType(channelSet.getIndex().get(0).getIndexType());
        }
        wmlLog.setNullValue(channelSet.getNullValue());
        wmlLog.setObjectGrowing(channelSet.getObjectGrowing());
        wmlLog.setLogParam(LogParam.to1311(channelSet.getLogParam()));
        wmlLog.setCreationDate(channelSet.getCitation().getCreation());

        if (channelSet.getTimeDepth().toLowerCase().contains("depth")) {
            GenericMeasure startMeasure = new GenericMeasure();
            startMeasure.setUom("m");
            startMeasure.setValue(Double.parseDouble(channelSet.getStartIndex()));
            wmlLog.setStartIndex(startMeasure);
            GenericMeasure endMeasure = new GenericMeasure();
            endMeasure.setUom("m");
            endMeasure.setValue(Double.parseDouble(channelSet.getEndIndex()));
            wmlLog.setEndIndex(endMeasure);
        } else {
            wmlLog.setStartDateTimeIndex(channelSet.getStartIndex());
            wmlLog.setEndDateTimeIndex(channelSet.getEndIndex());
        }
        return wmlLog;
    }

    public static List<ChannelSet> jsonToChannelSetList(String channelsList){
        return fromJSON(new TypeReference<List<ChannelSet>>() {}, channelsList);
    }

    public static <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) {
        T data = null;

        try {
            data = new ObjectMapper().readValue(jsonPacket, type);
        } catch (Exception e) {  // Handle the problem
        }
        return data;
    }

    public static ChannelSet jsonToChannelSet(String channelSet)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(channelSet, ChannelSet.class);  
    }
    private static XMLGregorianCalendar convertIsoDateToXML(String dateTime)
        throws DatatypeConfigurationException, ParseException {
        //DateFormat format = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss.SSSXXX");
        // Date date = format.parse("2014-04-24 11:15:00");
        //Date date = format.parse(dateTime);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        TemporalAccessor accessor = timeFormatter.parse(dateTime);

        Date date = Date.from(Instant.from(accessor));

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);

        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }
}
