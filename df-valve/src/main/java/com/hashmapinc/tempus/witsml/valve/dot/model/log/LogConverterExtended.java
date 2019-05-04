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
package com.hashmapinc.tempus.witsml.valve.dot.model.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition;
import com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue;
import com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo;
import com.hashmapinc.tempus.WitsmlObjects.v1411.TimestampedTimeZone;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.AxisDefinition;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.Channel;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.DensData;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.MnemAlias;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.SensorOffset;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.AcquisitionTimeZone;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ChannelSet;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Citation;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.CommonData;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.DefaultDatum;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ExtensionNameValue;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Index;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.LogParam;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Md;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.StepIncrement;

public class LogConverterExtended extends com.hashmapinc.tempus.WitsmlObjects.Util.LogConverter {
    /**
     * convertToChannelSet1311 takes in a v1.3.1.1 JSON string that was produced
     * client-side from SOAP WITSML XML & translates as necessary to adhere to DoT's
     * "Create a new ChannelSet" API.
     * 
     * @param witsmlObj Represents the client's WITSML object (complete) It is a
     *                  JSON String created from the ObjLog1311
     * @return JSON String representing the conversion
     */
    public static ChannelSet getChannelSet(com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog witsmlObj) {
        ChannelSet cs = new ChannelSet();
        Citation citation = new Citation();
        citation.setTitle(witsmlObj.getName());
        citation.setDescription(witsmlObj.getDescription());
        cs.setCitation(citation);
        if (witsmlObj.getBhaRunNumber() != null)
            cs.setBhaRunNumber((int) witsmlObj.getBhaRunNumber());

        cs.setRunNumber(witsmlObj.getRunNumber());
        cs.setPassNumber(witsmlObj.getPass());
        cs.setCustomData(witsmlObj.getCustomData());
        cs.setLoggingCompanyName(witsmlObj.getServiceCompany());
        cs.setNullValue(witsmlObj.getNullValue());
        String indexType = "depth";
        if (witsmlObj.getIndexType().contains("depth")) {
            cs.setTimeDepth(indexType);
            cs.setStartIndex(witsmlObj.getStartIndex().getValue().toString());
            cs.setEndIndex(witsmlObj.getEndIndex().getValue().toString());
        } else {
            indexType = "time";
            cs.setTimeDepth(indexType);
            cs.setStartIndex(witsmlObj.getStartDateTimeIndex().toXMLFormat());
            cs.setEndIndex(witsmlObj.getEndDateTimeIndex().toXMLFormat());
        }
        cs.setNullValue(witsmlObj.getNullValue());
        cs.setObjectGrowing(witsmlObj.isObjectGrowing());

        if (witsmlObj.getStepIncrement() != null) {
            StepIncrement inc = new StepIncrement();
            inc.setNumerator(witsmlObj.getStepIncrement().getNumerator());
            inc.setDenominator(witsmlObj.getStepIncrement().getDenominator());
            inc.setUom(witsmlObj.getStepIncrement().getUom());
            inc.setValue(witsmlObj.getStepIncrement().getValue().toString());
            cs.setStepIncrement(inc);
        }
        Index index = new Index();
        index.setIndexType(indexType);
        index.setDirection(witsmlObj.getDirection());
        index.setMnemonic(witsmlObj.getIndexCurve().getValue());
        Optional<com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo> matchingObject = witsmlObj.getLogCurveInfo()
                .stream().filter(p -> p.getMnemonic().equals(witsmlObj.getIndexCurve().getValue())).findFirst();
        index.setUom(matchingObject.get().getUnit());
        List<Index> indices = new ArrayList<Index>();
        indices.add(index);
        cs.setIndex(indices);

        if (witsmlObj.getLogParam() != null) {
            for (int i = 0; i < witsmlObj.getLogParam().size(); i++) {
                LogParam param = new LogParam();
                param.setName(witsmlObj.getLogParam().get(i).getName());
                param.setDescription(witsmlObj.getLogParam().get(i).getDescription());
                param.setIndex(witsmlObj.getLogParam().get(i).getIndex());
                param.setUom(witsmlObj.getLogParam().get(i).getUom());
                param.setValue(witsmlObj.getLogParam().get(i).getValue());
            }
        }

        if (witsmlObj.getCommonData() != null) {
            CommonData cd = new CommonData();
            cd.setItemState(witsmlObj.getCommonData().getItemState());
            cd.setComments(witsmlObj.getCommonData().getComments());
            cd.setSourceName(witsmlObj.getCommonData().getSourceName());
            cs.setCommonData(cd);
        }
        return cs;
    }

    public static ChannelSet getChannelSet(com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog witsmlObj) {

        ChannelSet cs = new ChannelSet();
        Citation citation = new Citation();
        citation.setTitle(witsmlObj.getName());
        citation.setDescription(witsmlObj.getDescription());
        cs.setCitation(citation);
        if (witsmlObj.getBhaRunNumber() != null)
            cs.setBhaRunNumber((int) witsmlObj.getBhaRunNumber());
        cs.setDataGroup(witsmlObj.getDataGroup());
        cs.setDataDelimiter(witsmlObj.getDataDelimiter());
        cs.setRunNumber(witsmlObj.getRunNumber());
        cs.setPassNumber(witsmlObj.getPass());
        cs.setCurveSensorsAligned(witsmlObj.isCurveSensorsAligned());
        cs.setCustomData(witsmlObj.getCustomData());
        cs.setLoggingCompanyName(witsmlObj.getServiceCompany());
        cs.setNullValue(witsmlObj.getNullValue());
        String indexType = "depth";
        if (witsmlObj.getIndexType().contains("depth")) {
            cs.setTimeDepth(indexType);
            cs.setStartIndex(witsmlObj.getStartIndex().getValue().toString());
            cs.setEndIndex(witsmlObj.getEndIndex().getValue().toString());
        } else {
            indexType = "time";
            cs.setTimeDepth(indexType);
            cs.setStartIndex(witsmlObj.getStartDateTimeIndex().toXMLFormat());
            cs.setEndIndex(witsmlObj.getEndDateTimeIndex().toXMLFormat());
        }
        cs.setNullValue(witsmlObj.getNullValue());
        cs.setObjectGrowing(witsmlObj.isObjectGrowing());

        if (witsmlObj.getStepIncrement() != null) {
            StepIncrement inc = new StepIncrement();
            inc.setNumerator(witsmlObj.getStepIncrement().getNumerator());
            inc.setDenominator(witsmlObj.getStepIncrement().getDenominator());
            inc.setUom(witsmlObj.getStepIncrement().getUom());
            inc.setValue(witsmlObj.getStepIncrement().getValue().toString());
            cs.setStepIncrement(inc);
        }
        Index index = new Index();
        index.setDirection(witsmlObj.getDirection());
        index.setMnemonic(witsmlObj.getIndexCurve());
        index.setIndexType(indexType);

        Optional<CsLogCurveInfo> matchingObject = witsmlObj.getLogCurveInfo().stream()
                .filter(p -> p.getMnemonic().getValue().equals(witsmlObj.getIndexCurve())).findFirst();
        index.setUom(matchingObject.get().getUnit());
        List<Index> indices = new ArrayList<Index>();
        indices.add(index);
        cs.setIndex(indices);

        if (witsmlObj.getLogParam() != null) {
            for (int i = 0; i < witsmlObj.getLogParam().size(); i++) {
                LogParam param = new LogParam();
                param.setName(witsmlObj.getLogParam().get(i).getName());
                param.setDescription(witsmlObj.getLogParam().get(i).getDescription());
                param.setIndex(witsmlObj.getLogParam().get(i).getIndex());
                param.setUid(witsmlObj.getLogParam().get(i).getUid());
                param.setUom(witsmlObj.getLogParam().get(i).getUom());
                param.setValue(witsmlObj.getLogParam().get(i).getValue());
            }
        }

        if (witsmlObj.getCommonData() != null) {
            CommonData cd = new CommonData();
            cd.setItemState(witsmlObj.getCommonData().getItemState());
            cd.setComments(witsmlObj.getCommonData().getComments());
            cd.setServiceCategory(witsmlObj.getCommonData().getServiceCategory());
            cd.setPrivateGroupOnly(witsmlObj.getCommonData().isPrivateGroupOnly());
            cd.setSourceName(witsmlObj.getCommonData().getSourceName());

            if (witsmlObj.getCommonData().getAcquisitionTimeZone() != null) {
                List<AcquisitionTimeZone> tzs = new ArrayList<>();
                for (TimestampedTimeZone tz : witsmlObj.getCommonData().getAcquisitionTimeZone()) {
                    AcquisitionTimeZone atz = new AcquisitionTimeZone();
                    atz.setDTim(tz.getDTim().toXMLFormat());
                    atz.setValue(tz.getValue());
                    tzs.add(atz);
                }
                cd.setAcquisitionTimeZone(tzs);
            }

            if (witsmlObj.getCommonData().getDefaultDatum() != null) {
                DefaultDatum dd = new DefaultDatum();
                dd.setUidRef(witsmlObj.getCommonData().getDefaultDatum().getUidRef());
                dd.setValue(witsmlObj.getCommonData().getDefaultDatum().getValue());
                cd.setDefaultDatum(dd);
            }
            cs.setCommonData(cd);
        }
        return cs;
    }

    public static List<Channel> getChannelList(com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog witsmlObj) {

        if (witsmlObj.getLogCurveInfo() == null)
            return null;

        List<Channel> channels = new ArrayList<Channel>();

        // Get the index type once for each channel
        String indexType;
        if (witsmlObj.getIndexType().toLowerCase().contains("depth"))
            indexType = "depth";
        else
            indexType = "time";

        // Create the index once for each channel
        Index index = new Index();
        index.setIndexType(indexType);
        index.setMnemonic(witsmlObj.getIndexCurve());
        Optional<CsLogCurveInfo> matchingObject = witsmlObj.getLogCurveInfo().stream()
            .filter(p -> p.getMnemonic().getValue().equals(witsmlObj.getIndexCurve())).findFirst();
        index.setUom(matchingObject.get().getUnit());
        index.setDirection(witsmlObj.getDirection());

        List<Index> indicies = new ArrayList<>();
        indicies.add(index);

        for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo lci : witsmlObj.getLogCurveInfo()) {
            try{
                Channel channel = new Channel();

                Citation c = new Citation();
                c.setTitle(lci.getMnemonic().getValue());
                
                channel.setCitation(c);
                channel.setUid(lci.getUid());
                channel.setNamingSystem(lci.getMnemonic().getNamingSystem());
                channel.setMnemonic(lci.getMnemonic().getValue());
                if (witsmlObj.getIndexType().toLowerCase().contains("depth"))
                    channel.setTimeDepth("depth");
                else
                    channel.setTimeDepth("time");
                channel.setClassWitsml(lci.getClassWitsml());

                if (lci.getClassIndex() != null)
                    channel.setClassIndex((int) lci.getClassIndex());

                if (lci.getUnit() == null){
                    channel.setUom("unitless");
                } else {
                    channel.setUom(lci.getUnit());
                }
                
                channel.setIndex(indicies);

                if (lci.getMnemAlias() != null) {
                    MnemAlias alias = new MnemAlias();
                    alias.setValue(lci.getMnemAlias().getValue());
                    alias.setNamingSystem(lci.getMnemAlias().getNamingSystem());
                    channel.setMnemAlias(alias);
                }

                channel.setNullValue(lci.getNullValue());
                channel.setAlternateIndex(lci.isAlternateIndex());

                if (lci.getWellDatum() != null) {
                    com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.WellDatum ref = new com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.WellDatum();
                    ref.setUidRef(lci.getWellDatum().getUidRef());
                    ref.setValue(lci.getWellDatum().getValue());
                    channel.setWellDatum(ref);
                }

                channel.setDescription(channel.getDescription());

                if (lci.getSensorOffset() != null) {
                    SensorOffset offset = new SensorOffset();
                    offset.setUom(lci.getSensorOffset().getUom());
                    offset.setValue(lci.getSensorOffset().getValue().toString());
                    channel.setSensorOffset(offset);
                }

                channel.setSource(lci.getDataSource());
                channel.setTraceState(lci.getTraceState());
                channel.setTraceOrigin(lci.getTraceOrigin());
                channel.setDataType(lci.getTypeLogData());

                if (lci.getDensData() != null) {
                    DensData dd = new DensData();
                    dd.setUom(lci.getDensData().getUom());
                    dd.setValue(lci.getDensData().getValue().toString());
                    channel.setDensData(dd);
                }

                if (lci.getAxisDefinition() != null) {
                    List<AxisDefinition> axes = new ArrayList<>();

                    for (CsAxisDefinition wmlAxis : lci.getAxisDefinition()) {

                        AxisDefinition axis = new AxisDefinition();
                        axis.setAxisCount((int) wmlAxis.getCount());
                        axis.setAxisName(wmlAxis.getName());
                        axis.setAxisPropertyKind(wmlAxis.getPropertyType());
                        axis.setAxisUom(wmlAxis.getUom());
                        axis.setUid(wmlAxis.getUid());
                        axis.setOrder((int) wmlAxis.getOrder());

                        if (wmlAxis.getDoubleValues() != null)
                            axis.setDoubleValues(String.join(",", wmlAxis.getDoubleValues()));
                        if (wmlAxis.getStringValues() != null)
                            axis.setStringValues(String.join(",", wmlAxis.getStringValues()));

                        if (wmlAxis.getExtensionNameValue() != null) {

                            List<ExtensionNameValue> envs = new ArrayList<ExtensionNameValue>();

                            for (CsExtensionNameValue wmlEnv : wmlAxis.getExtensionNameValue()) {
                                ExtensionNameValue env = new ExtensionNameValue();
                                env.setName(wmlEnv.getName());
                                if (wmlEnv.getValue() != null) {
                                    com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Value value = new com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Value();
                                    value.setUom(wmlEnv.getValue().getUom());
                                    value.setValue(wmlEnv.getValue().getValue());
                                    env.setValue(value);
                                }
                                env.setMeasureClass(wmlEnv.getMeasureClass());
                                env.setDTim(wmlEnv.getDTim().toXMLFormat());
                                env.setIndex(wmlEnv.getIndex());
                                env.setDescription(wmlEnv.getDescription());
                                env.setDataType(wmlEnv.getDataType());

                                if (wmlEnv.getMd() != null) {
                                    Md md = new Md();
                                    md.setUom(wmlEnv.getMd().getUom());
                                    md.setValue(wmlEnv.getMd().getValue().toString());
                                    md.setDatum(wmlEnv.getMd().getDatum());
                                    env.setMd(md);
                                }

                                env.setUid(wmlEnv.getUid());
                                envs.add(env);
                            }
                        }
                        axes.add(axis);
                    }
                    channel.setAxisDefinition(axes);
                }

                if (lci.getExtensionNameValue() != null) {

                    List<ExtensionNameValue> envs = new ArrayList<>();

                    for (CsExtensionNameValue wmlEnv : lci.getExtensionNameValue()) {
                        ExtensionNameValue env = new ExtensionNameValue();
                        env.setDTim(wmlEnv.getDTim().toXMLFormat());
                        env.setName(wmlEnv.getName());
                        env.setMeasureClass(wmlEnv.getMeasureClass());

                        if (wmlEnv.getValue() != null) {
                            com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Value value = new com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.Value();
                            value.setUom(wmlEnv.getValue().getUom());
                            value.setValue(wmlEnv.getValue().getValue());
                            env.setValue(value);
                        }
                        envs.add(env);
                    }
                    // channel.setExtensionNameValues(envs);
                }
                channels.add(channel);
            } catch (Exception ex){
                continue;
            }
        }
        return channels;
    }

    public static List<Channel> getChannelList(com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog witsmlObj) {
        if (witsmlObj.getLogCurveInfo() == null)
            return null;

        List<Channel> channels = new ArrayList<Channel>();

        // Get the index type once for each channel
        String indexType;
        if (witsmlObj.getIndexType().toLowerCase().contains("depth"))
            indexType = "depth";
        else
            indexType = "time";

        // Create the index once for each channel
        Index index = new Index();
        index.setIndexType(indexType);
        index.setMnemonic(witsmlObj.getIndexCurve().getValue());
        Optional<com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo> matchingObject = witsmlObj.getLogCurveInfo().stream()
            .filter(p -> p.getMnemonic().equals(witsmlObj.getIndexCurve().getValue())).findFirst();
        index.setUom(matchingObject.get().getUnit());
        index.setDirection(witsmlObj.getDirection());

        List<Index> indicies = new ArrayList<>();
        indicies.add(index);

        for (com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo lci : witsmlObj.getLogCurveInfo()) {
            try{
                Channel channel = new Channel();

                Citation c = new Citation();
                c.setTitle(lci.getMnemonic());
                
                channel.setCitation(c);
                channel.setUid(lci.getUid());
                channel.setMnemonic(lci.getMnemonic());
                if (witsmlObj.getIndexType().toLowerCase().contains("depth"))
                    channel.setTimeDepth("depth");
                else
                    channel.setTimeDepth("time");
                channel.setClassWitsml(lci.getClassWitsml());

                if (lci.getUnit() == null){
                    channel.setUom("unitless");
                } else {
                    channel.setUom(lci.getUnit());
                }
                
                channel.setIndex(indicies);

                if (lci.getMnemAlias() != null) {
                    MnemAlias alias = new MnemAlias();
                    alias.setValue(lci.getMnemAlias());
                    channel.setMnemAlias(alias);
                }

                channel.setNullValue(lci.getNullValue());
                channel.setAlternateIndex(lci.isAlternateIndex());

                if (lci.getWellDatum() != null) {
                    com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.WellDatum ref = new com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.WellDatum();
                    ref.setUidRef(lci.getWellDatum().getUidRef());
                    ref.setValue(lci.getWellDatum().getValue());
                    channel.setWellDatum(ref);
                }

                channel.setDescription(channel.getDescription());

                if (lci.getSensorOffset() != null) {
                    SensorOffset offset = new SensorOffset();
                    offset.setUom(lci.getSensorOffset().getUom());
                    offset.setValue(lci.getSensorOffset().getValue().toString());
                    channel.setSensorOffset(offset);
                }

                channel.setSource(lci.getDataSource());
                channel.setTraceState(lci.getTraceState());
                channel.setTraceOrigin(lci.getTraceOrigin());
                channel.setDataType(lci.getTypeLogData());

                if (lci.getDensData() != null) {
                    DensData dd = new DensData();
                    dd.setUom(lci.getDensData().getUom());
                    dd.setValue(lci.getDensData().getValue().toString());
                    channel.setDensData(dd);
                }

                if (lci.getAxisDefinition() != null) {
                    List<AxisDefinition> axes = new ArrayList<>();

                    for (com.hashmapinc.tempus.WitsmlObjects.v1311.CsAxisDefinition wmlAxis : lci.getAxisDefinition()) {

                        AxisDefinition axis = new AxisDefinition();
                        axis.setAxisCount((int) wmlAxis.getCount());
                        axis.setAxisName(wmlAxis.getName());
                        axis.setAxisPropertyKind(wmlAxis.getPropertyType());
                        axis.setAxisUom(wmlAxis.getUom());
                        axis.setUid(wmlAxis.getUid());
                        axis.setOrder((int) wmlAxis.getOrder());

                        List<String> dblValues = new ArrayList<>();
                        for (Double val : wmlAxis.getDoubleValues()){
                            dblValues.add(val.toString());
                        }

                        if (wmlAxis.getDoubleValues() != null)
                            axis.setDoubleValues(String.join(",", dblValues));
                        if (wmlAxis.getStringValues() != null)
                            axis.setStringValues(String.join(",", wmlAxis.getStringValues()));

                    }
                    channel.setAxisDefinition(axes);
                }
                channels.add(channel);
            } catch (Exception ex){
                continue;
            }
        }
        return channels;
    }

    public static String channelListToJson(List<Channel> channels) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        om.setDateFormat(new StdDateFormat());
        return om.writerWithDefaultPrettyPrinter().writeValueAsString(channels);
    }
}