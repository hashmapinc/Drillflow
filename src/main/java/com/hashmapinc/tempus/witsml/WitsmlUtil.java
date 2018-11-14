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

import java.util.logging.Logger;

/** 
 * StoreObject is a wrapper class around the 4 possible store objects
 * supported by Drillflow right now: Well, WellBore, Trajectory, and 
 * Log (depth and time)
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
        } else if (!is1311candidate && !is1411candidate) {
            throw new Exception("could not find a valid version in raw xml");
        } else {
            throw new Exception("found both witsml version 1.3.1.1 and 1.4.1.1 in raw xml");
        }
        
        // successfully parsed version. Return here
        LOG.info("version parsed from raw xml is " + version);
        return version;
    }
}