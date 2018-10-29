package com.hashmapinc.tempus.witsml.server.api.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="WMLS_GetCapResponse)")
public class WMLS_GetCapResponse {

    private Short Result;
    private String CapabilitiesOut;
    private String SuppMsgOut;

    public String getSuppMsgOut() {
        return SuppMsgOut;
    }

    public void setSuppMsgOut(String suppMsgOut) {
        this.SuppMsgOut = suppMsgOut;
    }

    public Short getResult() {
        return Result;
    }

    public void setResult(Short result) {
        this.Result = result;
    }

    public String getCapabilitiesOut() {
        return CapabilitiesOut;
    }

    public void setCapabilitiesOut(String capabilitiesOut) {
        this.CapabilitiesOut = capabilitiesOut;
    }
}
