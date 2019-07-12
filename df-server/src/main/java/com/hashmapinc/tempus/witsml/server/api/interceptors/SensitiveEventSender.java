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
