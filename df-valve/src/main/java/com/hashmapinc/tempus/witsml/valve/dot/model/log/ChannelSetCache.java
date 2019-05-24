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


import com.fasterxml.jackson.core.JsonProcessingException;
import com.hashmapinc.tempus.witsml.valve.dot.model.log.channelset.ChannelSet;

import java.util.concurrent.ConcurrentHashMap;

/**
 *  This cache stores channelSets.
 *
 *  Search in the cache is never granular; it is against the entire
 *  channelSet.
 */
public class ChannelSetCache {
    private static ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    private static final String SEPARATOR = "|===|"; // separator used when building composite keys

    /**
     * This function stores a ChannelSet (JSON format) in cache along with the
     * composite key of uuid + SEPARATOR + hashCode() (from the ChannelSet).
     *
     * @param uuid - string uuid used as a cache key
     * @param cs - query's ChannelSet converted for DoT's API
     *             (v1.4.1.1 or v1.3.1.1)
     */
    public static void putInCache( String uuid,
                                   ChannelSet cs )
                                        throws JsonProcessingException
    {
        // store mapping in cache
        // **********************
        // use a composite key of uuid and the hash code of the
        // payload's JSON String (which can be seen as an object's
        // equality boiled down to an integer value; note that
        // while instances with the same hash code are not necessarily
        // equal, equal instances have the same hash code)
        // build composite key
        String compositeKey = uuid + SEPARATOR + (cs.hashCode());
        cache.put(compositeKey, cs.toJson());
    }

    /**
     * This function returns the matching ChannelSet from cache to the
     * one passed in.
     *
     * @param uuid - string value of the uuid to use for ChannelSet lookup
     * @param cs - ChannelSet to check for a match within the cache
     *
     * @return - ChannelSet found channel set; null otherwise
     */
    public static String getCS( String uuid,
                                ChannelSet cs )
    {
        // check cache
        if (cache.containsKey(uuid + SEPARATOR + (cs.hashCode()))) {
            return cache.get(uuid);
        }

        return null; // no match found
    }

    // TODO Should there be a "cache cleanup" function just in case it needs
    //      to be cleared out?
}
