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