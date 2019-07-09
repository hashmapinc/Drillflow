package com.hashmapinc.tempus.witsml.server.api.interceptors;

import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.ext.logging.event.LogEventSender;

import java.util.Base64;
import java.util.Map;

public class SoapLogEventSender implements LogEventSender {
    private LogEventSender next;

    public SoapLogEventSender(LogEventSender next){
        this.next = next;
    }

    @Override
    public void send(LogEvent event) {
        event.setPayload(maskSensibleParameters(event.getPayload(), event.getHeaders()));
        next.send(event);
    }

    private String maskSensibleParameters(String pIn, Map<String, String> headers) {
        if (headers.containsKey("Authorization")){
            String basic = headers.get("Authorization");
            byte[] decodedBytes = Base64.getDecoder().decode(basic.split(" ")[1]);
            String decodedString = new String(decodedBytes);
            headers.put("user", decodedString.split(":")[0]);
        }

        return pIn;

    }
}