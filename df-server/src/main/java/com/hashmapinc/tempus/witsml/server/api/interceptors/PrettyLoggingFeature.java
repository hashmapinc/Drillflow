package com.hashmapinc.tempus.witsml.server.api.interceptors;

import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.event.LogEventSender;

public class PrettyLoggingFeature extends LoggingFeature {

    public PrettyLoggingFeature(){
        LogEventSender sender = new SensitiveEventSender();
        super.setPrettyLogging(false);
        super.setSender(new SoapLogEventSender(sender));
        super.setOutSender(new SoapLogEventSender(sender));
    }
}