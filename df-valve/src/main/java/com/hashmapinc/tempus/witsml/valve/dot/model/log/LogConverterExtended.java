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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.hashmapinc.tempus.WitsmlObjects.v1311.GenericMeasure;
import com.hashmapinc.tempus.WitsmlObjects.v1311.LengthMeasure;
import com.hashmapinc.tempus.WitsmlObjects.v1311.PerLengthMeasure;
import com.hashmapinc.tempus.WitsmlObjects.v1311.RefNameString;
import com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition;
import com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ShortNameStruct;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.AxisDefinition;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channel.Channel;

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

    

    public static List<com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo> get1311LogCurveInfos(
            List<Channel> channels) {
        List<com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo> curves = new ArrayList<>();

        if (channels == null || channels.isEmpty())
            return null;

        for (Channel c : channels) {
            try{
                com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo lci = 
                new com.hashmapinc.tempus.WitsmlObjects.v1311.CsLogCurveInfo();
                lci.setMnemonic(c.getCitation().getTitle());
                lci.setAlternateIndex(c.getAlternateIndex());
                lci.setClassWitsml(c.getClassWitsml());
                //NOTE: WE WILL ALWAYS SET THE INDEX TO THE FIRST COLUMN
                lci.setColumnIndex((short)1);
                lci.setCurveDescription(c.getCitation().getDescription());
                lci.setDataSource(c.getSource());
                lci.setTraceOrigin(c.getTraceOrigin());
                lci.setTraceState(c.getTraceState());
                lci.setTypeLogData(c.getDataType());
                lci.setUid(c.getUid());
                lci.setUnit(c.getUom());
                lci.setNullValue(c.getNullValue());
                lci.setMnemAlias(c.getMnemAlias().getValue());
                
                if (c.getAxisDefinition() != null){
                    List<CsAxisDefinition> axisDefs = new ArrayList<>();
                    for (AxisDefinition axisDefinition : c.getAxisDefinition()){
                        CsAxisDefinition axisDef = new CsAxisDefinition();
                        if (axisDefinition.getAxisCount() != null){
                            axisDef.setCount(axisDefinition.getAxisCount());
                            axisDef.setName(axisDefinition.getAxisName());
                            axisDef.setOrder(axisDefinition.getOrder());
                            axisDef.setPropertyType(axisDefinition.getAxisPropertyKind());
                            axisDef.setUid(axisDefinition.getUid());
                            axisDef.setUom(axisDefinition.getAxisUom());
                            if (axisDefinition.getDoubleValues() != null){
                                List<Double> values = Stream.of(axisDefinition.getDoubleValues().split(","))
                                .map(Double::valueOf)
                                .collect(Collectors.toList());
                                axisDef.setDoubleValues(values);
                            }
                            if (axisDefinition.getStringValues() != null){
                                List<String> values = Arrays.asList(axisDefinition.getStringValues().split("\\s*,\\s*"));
                                axisDef.setStringValues(values);
                            }
                        }
                        axisDefs.add(axisDef);
                    }
                }

                if (c.getDensData() != null) {
                    PerLengthMeasure plm = new PerLengthMeasure();
                    plm.setValue(Double.parseDouble(c.getDensData().getValue()));
                    plm.setUom(c.getDensData().getUom());
                    lci.setDensData(plm);
                }
                
                if (c.getTimeDepth().toLowerCase().contains("depth")){
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
                
                if (c.getSensorOffset() != null){
                    LengthMeasure sensOffset = new LengthMeasure();
                    sensOffset.setUom(c.getSensorOffset().getUom());
                    sensOffset.setValue(Double.parseDouble(c.getSensorOffset().getValue()));
                    lci.setSensorOffset(sensOffset);
                }
               
                if (c.getWellDatum() != null){
                    RefNameString datum = new RefNameString();
                    datum.setUidRef(c.getWellDatum().getUidRef());
                    datum.setValue(c.getWellDatum().getValue());
                    lci.setWellDatum(datum);
                }
                curves.add(lci);
            } catch (Exception ex){
                continue;
            }
        }
        return curves;
    }

    public static List<CsLogCurveInfo> get1411LogCurveInfos(List<Channel> channels){
        List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo> curves = new ArrayList<>();

        if (channels == null || channels.isEmpty())
            return null;

        for (Channel c : channels) {
            try{
                com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo lci = 
                new com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogCurveInfo();

                ShortNameStruct snsMnemonic = new ShortNameStruct();
                snsMnemonic.setNamingSystem(c.getNamingSystem());
                snsMnemonic.setValue(c.getMnemonic());
                
                lci.setMnemonic(snsMnemonic);
                lci.setAlternateIndex(c.getAlternateIndex());
                lci.setClassWitsml(c.getClassWitsml());
                lci.setCurveDescription(c.getCitation().getDescription());
                lci.setDataSource(c.getSource());
                lci.setClassIndex(c.getClassIndex());
                lci.setNullValue(c.getNullValue());
                lci.setTraceOrigin(c.getTraceOrigin());
                lci.setTraceState(c.getTraceState());
                lci.setTypeLogData(c.getDataType());
                lci.setUid(c.getUid());
                lci.setUnit(c.getUom());
                
                if (c.getAxisDefinition() != null){
                    List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition> axisDefs = new ArrayList<>();
                    for (AxisDefinition axisDefinition : c.getAxisDefinition()){
                        com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition axisDef = 
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.CsAxisDefinition();
                        if (axisDefinition.getAxisCount() != null){
                            axisDef.setCount(axisDefinition.getAxisCount());
                            axisDef.setName(axisDefinition.getAxisName());
                            axisDef.setOrder(axisDefinition.getOrder());
                            axisDef.setPropertyType(axisDefinition.getAxisPropertyKind());
                            axisDef.setUid(axisDefinition.getUid());
                            axisDef.setUom(axisDefinition.getAxisUom());
                            if (axisDefinition.getDoubleValues() != null){
                                List<Double> values = Stream.of(axisDefinition.getDoubleValues().split(","))
                                .map(Double::valueOf)
                                .collect(Collectors.toList());
                                axisDef.setDoubleValues(values);
                            }
                            if (axisDefinition.getStringValues() != null){
                                List<String> values = Arrays.asList(axisDefinition.getStringValues().split("\\s*,\\s*"));
                                axisDef.setStringValues(values);
                            }
                        }
                        axisDefs.add(axisDef);
                    }
                }

                if (c.getDensData() != null) {
                    com.hashmapinc.tempus.WitsmlObjects.v1411.PerLengthMeasure plm = 
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.PerLengthMeasure();
                    plm.setValue(Double.parseDouble(c.getDensData().getValue()));
                    plm.setUom(c.getDensData().getUom());
                    lci.setDensData(plm);
                }
                
                if (c.getTimeDepth().toLowerCase().contains("depth")){
                    lci.setMinDateTimeIndex(convertIsoDateToXML(c.getStartIndex()));
                    lci.setMaxDateTimeIndex(convertIsoDateToXML(c.getEndIndex()));
                } else {
                    com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure minMeasure = 
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                    minMeasure.setUom("m");
                    minMeasure.setValue(Double.parseDouble(c.getStartIndex()));
                    lci.setMinIndex(minMeasure);

                    com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure maxMeasure = 
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.GenericMeasure();
                    maxMeasure.setUom("m");
                    maxMeasure.setValue(Double.parseDouble(c.getEndIndex()));
                    lci.setMaxIndex(maxMeasure);
                }

                ShortNameStruct snsMnemAlias = new ShortNameStruct();
                snsMnemAlias.setNamingSystem(c.getMnemAlias().getNamingSystem());
                snsMnemAlias.setValue(c.getMnemAlias().getValue());

                lci.setMnemAlias(snsMnemAlias);

                if (c.getSensorOffset() != null){
                    com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure sensOffset = 
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.LengthMeasure();
                    sensOffset.setUom(c.getSensorOffset().getUom());
                    sensOffset.setValue(Double.parseDouble(c.getSensorOffset().getValue()));
                    lci.setSensorOffset(sensOffset);
                }

                if (c.getWellDatum() != null){
                    com.hashmapinc.tempus.WitsmlObjects.v1411.RefNameString datum = 
                        new com.hashmapinc.tempus.WitsmlObjects.v1411.RefNameString();
                    datum.setUidRef(c.getWellDatum().getUidRef());
                    datum.setValue(c.getWellDatum().getValue());
                    lci.setWellDatum(datum);
                }

                curves.add(lci);
            } catch (Exception ex){
                continue;
            }
        }
        return curves;
    }

    private static XMLGregorianCalendar convertIsoDateToXML(String dateTime)
            throws DatatypeConfigurationException, ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss");
        Date date = format.parse("2014-04-24 11:15:00");

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);

        XMLGregorianCalendar xmlGregCal =  DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        return xmlGregCal;
    }
}