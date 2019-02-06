/**
 * Copyright © 2018-2018 Hashmap, Inc
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
package com.hashmapinc.tempus.witsml;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;


/** 
 * General static util methods for witsml stuff
 */
public class WitsmlUtil {
    // get logger
    private static final Logger LOG = Logger.getLogger(WitsmlUtil.class.getName());

    /**
     * this method gets the witsml version from the raw object xml
     * 
     * @param rawXML - String with the raw xml to parse for version
     * 
     * @return version - String with the parsed version, either 1.3.1.1, 1.4.1.1
     */
    public static String getVersionFromXML(
        String rawXML
    ) throws Exception {
        LOG.info("trying to parse version from raw xml...");
        
        /**
         * This is lazy, I know. Please blame me (Randy Pitcher).
         * However, I'm pretty confident this works. We can change it at some point
         */
        // try to parse the version
        String version = null;
        boolean is1311candidate = rawXML.contains("version=\"1.3.1.1\"");
        boolean is1411candidate = rawXML.contains("version=\"1.4.1.1\"");

        if (is1311candidate && !is1411candidate) {
            version = "1.3.1.1";
        } else if (!is1311candidate && is1411candidate) {
            version = "1.4.1.1";
        } else {
            return null;
        }
        
        // successfully parsed version. Return here
        LOG.info("version parsed from raw xml is " + version);
        return version;
    }

    /**
     * this method parses a witsml optionsIn string into a String->String map
     * 
     * @param optionsIn - String with witsml optionsIn - see witsml spec for format
     * 
     * @return map - Map<String, String> containing Key-values parsed from options in
     */
    public static HashMap<String,String> parseOptionsIn(
        String optionsIn
    ) {
        LOG.info("trying to parse optionsIn...");
        if (optionsIn.isEmpty())
            return new HashMap<>();
        //parse the string
        HashMap<String, String> map = new HashMap<>();
        Arrays.stream(optionsIn.split(";")).forEach(optionString -> {
            String[] option = optionString.split("=");
            map.put(option[0], option[1]);
        });

        return map;
    }
}