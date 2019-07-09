package com.hashmapinc.tempus.witsml.server.api.interceptors;

import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.ext.logging.event.LogMessageFormatter;
import org.apache.cxf.ext.logging.slf4j.Slf4jEventSender;

public class SensitiveEventSender extends Slf4jEventSender {

    @Override
    protected String getLogMessage(LogEvent event) {
        String message = LogMessageFormatter.format(event);
        if (event.getHeaders().containsKey("Authorization")){
            return message.replace(event.getHeaders().get("Authorization"), "*****");
        }
        return LogMessageFormatter.format(event);
    }

}
