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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.hashmapinc.tempus.WitsmlObjects.v1311.GenericMeasure;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ShortNameStruct;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogData;

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

public class Channel {

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
    @JsonDeserialize(using = ClassIndexDeserializer.class)
    private Short classIndex;
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
    private List<ExtensionNameValue> extensionNameValue = null;
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
    @JsonDeserialize(using = ClassIndexDeserializer.class)
    public Short getClassIndex() {
        return classIndex;
    }

    @JsonProperty("classIndex")
    @JsonDeserialize(using = ClassIndexDeserializer.class)
    public void setClassIndex(Short classIndex) {
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String channelToJson() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        om.setDateFormat(new StdDateFormat());
        return om.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    public static List<Channel> from1411(com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog witsmlObj) {

        if (witsmlObj.getLogCurveInfo() == null)
            return null;

        List<Channel> channels = new ArrayList<Channel>();
        // Create the index once for each channel
        List<Index> indicies = Index.from1411(witsmlObj);

        for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo lci : witsmlObj.getLogCurveInfo()) {
            try {
                Channel channel = new Channel();

                Citation c = new Citation();
                c.setTitle(lci.getMnemonic().getValue());
                c.setDescription(lci.getCurveDescription());
                channel.setClassWitsml(lci.getClassWitsml());
                channel.setCitation(c);
                channel.setUid(lci.getUid());
                channel.setNamingSystem(lci.getMnemonic().getNamingSystem());
                channel.setMnemonic(lci.getMnemonic().getValue());

                if (witsmlObj.getIndexType() != null) {
                    if (witsmlObj.getIndexType().toLowerCase().contains("depth")) {
                        channel.setTimeDepth("Depth");
                    } else {
                        if (witsmlObj.getIndexType().toLowerCase().contains("time"))
                            channel.setTimeDepth("Time");
                    }
                }


                channel.setClassIndex(lci.getClassIndex());

                if (lci.getUnit() == null) {
                    channel.setUom("unitless");
                } else {
                    channel.setUom(lci.getUnit());
                }

                channel.setIndex(indicies);
                channel.setMnemAlias(MnemAlias.from1411(lci.getMnemAlias()));
                channel.setNullValue(lci.getNullValue());
                channel.setAlternateIndex(lci.isAlternateIndex());
                channel.setDescription(channel.getDescription());
                channel.setSource(lci.getDataSource());
                channel.setTraceState(lci.getTraceState());
                channel.setTraceOrigin(lci.getTraceOrigin());
                channel.setDataType(lci.getTypeLogData());
                channel.setDensData(DensData.from1411(lci.getDensData()));
                channel.setAxisDefinition(AxisDefinition.from1411(lci.getAxisDefinition()));
                channel.setExtensionNameValue(ExtensionNameValue.from1411(lci.getExtensionNameValue()));
                channel.setWellDatum(WellDatum.from1411(lci.getWellDatum()));
                channel.setSensorOffset(SensorOffset.from1411(lci.getSensorOffset()));
                channels.add(channel);
            } catch (Exception ex) {
                continue;
            }
        }
        return channels;
    }

    public static List<Channel> from1311(com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog witsmlObj) {
        if (witsmlObj.getLogCurveInfo() == null)
            return null;

        List<Channel> channels = new ArrayList<Channel>();
        // Create the index once for each channel
        List<Index> indicies = Index.from1311(witsmlObj);

        for (com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo lci : witsmlObj.getLogCurveInfo()) {
            try {
                Channel channel = new Channel();

                Citation c = new Citation();
                c.setTitle(lci.getMnemonic());

                channel.setCitation(c);
                channel.setUid(lci.getUid());
                channel.setMnemonic(lci.getMnemonic());

                if (witsmlObj.getIndexType().toLowerCase().contains("depth"))
                    channel.setTimeDepth("Depth");
                else
                    channel.setTimeDepth("Time");
                channel.setClassWitsml(lci.getClassWitsml());

                if (lci.getUnit() == null) {
                    channel.setUom("unitless");
                } else {
                    channel.setUom(lci.getUnit());
                }

                channel.setIndex(indicies);
                channel.setNullValue(lci.getNullValue());
                channel.setAlternateIndex(lci.isAlternateIndex());
                channel.setDescription(channel.getDescription());
                channel.setSource(lci.getDataSource());
                channel.setTraceState(lci.getTraceState());
                channel.setTraceOrigin(lci.getTraceOrigin());
                channel.setDataType(lci.getTypeLogData());
                channel.setWellDatum(WellDatum.from1311(lci.getWellDatum()));
                channel.setSensorOffset(SensorOffset.from1311(lci.getSensorOffset()));
                channel.setDensData(DensData.from1311(lci.getDensData()));
                channel.setMnemAlias(MnemAlias.from1311(lci));
                channel.setAxisDefinition(AxisDefinition.from1311(lci.getAxisDefinition()));

                channels.add(channel);
            } catch (Exception ex) {
                continue;
            }
        }
        return channels;
    }

    public static List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> to1411(
            List<Channel> channels,ChannelSet channelSet) {
        List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> curves = new ArrayList<>();

        if (channels == null || channels.isEmpty())
            return null;

        for (Channel c : channels) {
            try{
                com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo lci =
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo();
                ShortNameStruct name = new ShortNameStruct();
                name.setValue(c.getMnemonic());
                name.setNamingSystem(c.getNamingSystem());
                lci.setMnemonic(name);
                lci.setAlternateIndex(c.getAlternateIndex());
                lci.setClassWitsml(c.getClassWitsml());
                //NOTE: WE WILL ALWAYS SET THE INDEX TO THE FIRST COLUMN
                lci.setCurveDescription(c.getCitation().getDescription());
                lci.setDataSource(c.getSource());
                lci.setTraceOrigin(c.getTraceOrigin());
                lci.setTraceState(c.getTraceState());
                lci.setTypeLogData(c.getDataType());
                lci.setUid(c.getUid());
                lci.setUnit(c.getUom());
                lci.setNullValue(c.getNullValue());
                lci.setMnemAlias(MnemAlias.to1411(c.mnemAlias));
                lci.setAxisDefinition(AxisDefinition.to1411(c.getAxisDefinition()));
                lci.setDensData(DensData.to1411(c.getDensData()));
                lci.setSensorOffset(SensorOffset.to1411(c.getSensorOffset()));
                lci.setWellDatum(WellDatum.to1411(c.getWellDatum()));
                lci.setClassIndex(c.getClassIndex());
                if (c.getTimeDepth().toLowerCase().contains("time")){
                    if (c.getStartIndex() != null)
                        lci.setMinDateTimeIndex(convertIsoDateToXML(c.getStartIndex()));
                    if (c.getEndIndex() != null)
                        lci.setMaxDateTimeIndex(convertIsoDateToXML(c.getEndIndex()));
                } else {
                    if (channelSet.getStartIndex() != null){
                        com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure minMeasure =
                                new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                        minMeasure.setUom("m");
                        minMeasure.setValue(Double.parseDouble(channelSet.getStartIndex()));
                        lci.setMinIndex(minMeasure);
                    }
                    if (channelSet.getEndIndex() != null){
                        com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure maxMeasure =
                                new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                        maxMeasure.setUom("m");
                        maxMeasure.setValue(Double.parseDouble(channelSet.getEndIndex()));
                        lci.setMaxIndex(maxMeasure);
                    }
                }
                //Need to address this in wol...does not exist
                //lci.getExtensionNameValue()
                curves.add(lci);
            } catch (Exception ex){
                continue;
            }
        }
        return curves;
    }

    public static List<com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo> to1311(
            List<Channel> channels) {
        List<com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo> curves = new ArrayList<>();

        if (channels == null || channels.isEmpty())
            return null;

        for (Channel c : channels) {
            try {
                com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo lci =
                        new com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo();
                lci.setMnemonic(c.getCitation().getTitle());
                lci.setAlternateIndex(c.getAlternateIndex());
                lci.setClassWitsml(c.getClassWitsml());
                //NOTE: WE WILL ALWAYS SET THE INDEX TO THE FIRST COLUMN
                lci.setColumnIndex((short) 1);
                lci.setCurveDescription(c.getCitation().getDescription());
                lci.setDataSource(c.getSource());
                lci.setTraceOrigin(c.getTraceOrigin());
                lci.setTraceState(c.getTraceState());
                lci.setTypeLogData(c.getDataType());
                lci.setUid(c.getUid());
                lci.setUnit(c.getUom());
                lci.setNullValue(c.getNullValue());
                lci.setMnemAlias(c.getMnemAlias().getValue());
                lci.setAxisDefinition(AxisDefinition.to1311(c.getAxisDefinition()));
                lci.setDensData(DensData.to1311(c.getDensData()));
                lci.setSensorOffset(SensorOffset.to1311(c.getSensorOffset()));
                lci.setWellDatum(WellDatum.to1311(c.getWellDatum()));
                lci.setColumnIndex(c.classIndex);

                if (c.getTimeDepth().toLowerCase().contains("time")){
                    lci.setMinDateTimeIndex(convertIsoDateToXML(c.getStartIndex()));
                    lci.setMaxDateTimeIndex(convertIsoDateToXML(c.getEndIndex()));
                } else {
                    GenericMeasure minMeasure = new GenericMeasure();
                    minMeasure.setUom("m");
                    minMeasure.setValue(Double.parseDouble(c.getStartIndex()));
                    lci.setMinIndex(minMeasure);
                    GenericMeasure maxMeasure = new GenericMeasure();
                    maxMeasure.setUom("m");
                    maxMeasure.setValue(Double.parseDouble(c.getEndIndex()));
                    lci.setMaxIndex(maxMeasure);
                }

                curves.add(lci);
            } catch (Exception ex) {
                continue;
            }
        }
        return curves;
    }


    public static String channelListToJson(List<Channel> channels) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        om.setDateFormat(new StdDateFormat());
        return om.writerWithDefaultPrettyPrinter().writeValueAsString(channels);
    }

    public static List<Channel> jsonToChannelList(String channelsList) throws ValveException{
        return fromJSON(new TypeReference<List<Channel>>() {}, channelsList);
    }

    public static <T> T fromJSON(final TypeReference<T> type, final String jsonPacket) throws ValveException {
        T data = null;

        try {
            data = new ObjectMapper().readValue(jsonPacket, type);
        } catch (Exception e) {  // Handle the problem
            throw new ValveException(e.getMessage());
        }
        return data;
    }

    private static XMLGregorianCalendar convertIsoDateToXML(String dateTime)
            throws DatatypeConfigurationException, ParseException {
        //DateFormat format = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss.SSSXXX");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        //Date date = format.parse("2014-04-24 11:15:00");
        Date date = format.parse(dateTime);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);

        XMLGregorianCalendar xmlGregCal =  DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        return xmlGregCal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(uuid, channel.uuid) &&
                Objects.equals(uid, channel.uid) &&
                Objects.equals(wellDatum, channel.wellDatum) &&
                Objects.equals(nullValue, channel.nullValue) &&
                Objects.equals(channelState, channel.channelState) &&
                Objects.equals(classIndex, channel.classIndex) &&
                Objects.equals(mnemAlias, channel.mnemAlias) &&
                Objects.equals(alternateIndex, channel.alternateIndex) &&
                Objects.equals(sensorOffset, channel.sensorOffset) &&
                Objects.equals(densData, channel.densData) &&
                Objects.equals(traceOrigin, channel.traceOrigin) &&
                Objects.equals(traceState, channel.traceState) &&
                Objects.equals(namingSystem, channel.namingSystem) &&
                Objects.equals(mnemonic, channel.mnemonic) &&
                Objects.equals(dataType, channel.dataType) &&
                Objects.equals(description, channel.description) &&
                Objects.equals(uom, channel.uom) &&
                Objects.equals(source, channel.source) &&
                Objects.equals(axisDefinition, channel.axisDefinition) &&
                Objects.equals(timeDepth, channel.timeDepth) &&
                Objects.equals(channelClass, channel.channelClass) &&
                Objects.equals(classWitsml, channel.classWitsml) &&
                Objects.equals(runNumber, channel.runNumber) &&
                Objects.equals(passNumber, channel.passNumber) &&
                Objects.equals(loggingCompanyName, channel.loggingCompanyName) &&
                Objects.equals(loggingCompanyCode, channel.loggingCompanyCode) &&
                Objects.equals(toolName, channel.toolName) &&
                Objects.equals(toolClass, channel.toolClass) &&
                Objects.equals(derivation, channel.derivation) &&
                Objects.equals(loggingMethod, channel.loggingMethod) &&
                Objects.equals(nominalHoleSize, channel.nominalHoleSize) &&
                Objects.equals(index, channel.index) &&
                Objects.equals(aliases, channel.aliases) &&
                Objects.equals(citation, channel.citation) &&
                Objects.equals(customData, channel.customData) &&
                Objects.equals(objectVersion, channel.objectVersion) &&
                Objects.equals(existenceKind, channel.existenceKind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, uid, wellDatum, nullValue, channelState, classIndex, mnemAlias, alternateIndex, sensorOffset, densData, traceOrigin, traceState, namingSystem, mnemonic, dataType, description, uom, source, axisDefinition, timeDepth, channelClass, classWitsml, runNumber, passNumber, loggingCompanyName, loggingCompanyCode, toolName, toolClass, derivation, loggingMethod, nominalHoleSize, index, aliases, citation, customData, objectVersion, existenceKind);
    }
}