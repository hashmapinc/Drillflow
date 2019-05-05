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
import com.hashmapinc.tempus.WitsmlObjects.v1411.Extensionvalue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "value", "measureClass", "dTim", "index", "description", "dataType", "md", "uid" })
public class ExtensionNameValue {

    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private Value value;
    @JsonProperty("measureClass")
    private String measureClass;
    @JsonProperty("dTim")
    private String dTim;
    @JsonProperty("index")
    private Long index;
    @JsonProperty("description")
    private String description;
    @JsonProperty("dataType")
    private String dataType;
    @JsonProperty("md")
    private Md md;
    @JsonProperty("uid")
    private String uid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("value")
    public Value getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Value value) {
        this.value = value;
    }

    @JsonProperty("measureClass")
    public String getMeasureClass() {
        return measureClass;
    }

    @JsonProperty("measureClass")
    public void setMeasureClass(String measureClass) {
        this.measureClass = measureClass;
    }

    @JsonProperty("dTim")
    public String getDTim() {
        return dTim;
    }

    @JsonProperty("dTim")
    public void setDTim(String dTim) {
        this.dTim = dTim;
    }

    @JsonProperty("index")
    public Long getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(Long index) {
        this.index = index;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("dataType")
    public String getDataType() {
        return dataType;
    }

    @JsonProperty("dataType")
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @JsonProperty("md")
    public Md getMd() {
        return md;
    }

    @JsonProperty("md")
    public void setMd(Md md) {
        this.md = md;
    }

    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public static List<ExtensionNameValue> from1411(
            List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue> extensionNameValues) {

        if (extensionNameValues == null) {
            return null;
        }

        List<ExtensionNameValue> envs = new ArrayList<ExtensionNameValue>();

        for (com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue wmlEnv : extensionNameValues) {
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
        return envs;
    }

    public static List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue> to1411(
            List<ExtensionNameValue> enValues) throws DatatypeConfigurationException, ParseException {
        if (enValues == null)
            return null;

        List<com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue> wmlEnvs = new ArrayList<>();

        for (ExtensionNameValue enValue : enValues) {
            com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue wmlEnv = new com.hashmapinc.tempus.WitsmlObjects.v1411.CsExtensionNameValue();

            wmlEnv.setName(enValue.getName());

            if (enValue.getValue() != null) {
                Extensionvalue val = new Extensionvalue();
                val.setUom(enValue.getValue().getUom());
                val.setValue(enValue.getValue().getValue());
                wmlEnv.setValue(val);
            }

            wmlEnv.setMeasureClass(enValue.getMeasureClass());
            wmlEnv.setDTim(convertIsoDateToXML(enValue.getDTim()));
            wmlEnv.setIndex(enValue.getIndex());
            wmlEnv.setDescription(enValue.getDescription());
            wmlEnv.setDataType(enValue.getDataType());
            wmlEnv.setMd(Md.to1411(enValue.md));
            wmlEnv.setUid(enValue.getUid());
            wmlEnvs.add(wmlEnv);
        }

        return wmlEnvs;
    }

    private static XMLGregorianCalendar convertIsoDateToXML(String dateTime)
            throws DatatypeConfigurationException, ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss.SSSXXX");
        Date date = format.parse("2014-04-24 11:15:00");

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);

        XMLGregorianCalendar xmlGregCal =  DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        return xmlGregCal;
    }
}
