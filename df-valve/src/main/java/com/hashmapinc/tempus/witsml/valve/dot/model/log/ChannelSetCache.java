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
        int checksum = 0;

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
     * This function stores a mapping between a uid and uuid.
     *
     * This function is used for parent-only objects
     *
     * @param uuid - string uuid to cache
     * @param uid - string uid to cache
     * @param parentUid - string uid of the parent object needed for
     *                  building the composite key
     */
    public static void putInCache(
            String uuid,
            String uid,
            String parentUid
    ) {
        // build composite key
        String compositeKey = parentUid + SEPARATOR + uid;

        // store 2-way mappings in cache
        cache.put(uuid, uid);          // uuid->uid
        cache.put(compositeKey, uuid); // uid->uuid
    }

    /**
     * This function stores a mapping between a uid and uuid.
     *
     * This function is used for grandparent-only objects
     *
     * @param uuid - string uuid to cache
     * @param uid - string uid to cache
     * @param parentUid - string uid of the parent object needed for
     *                    building the composite key
     * @param grandparentUid - string uid of the grandparent object needed
     *                         for building the composite key
     */
    public static void putInCache(
            String uuid,
            String uid,
            String parentUid,
            String grandparentUid
    ) {
        // build composite key
        String compositeKey = grandparentUid + SEPARATOR + parentUid + SEPARATOR + uid;

        // store 2-way mappings in cache
        cache.put(uuid, uid);          // uuid->uid
        cache.put(compositeKey, uuid); // uid->uuid
    }

    /**
     * This function checks the cache for a matching uid.
     *
     * If a match is found, it is returned.
     *
     * @param uuid - string value of the uuid to use for uid lookup
     * @return - uid String found mapped to the given uuid.
     */
    public static String getUid(String uuid) {
        // check cache
        if (cache.containsKey(uuid))
            return cache.get(uuid);

        return null; // no match found
    }

    /**
     * returns the uuid for a parentless object with given uid.
     *
     * @param uid - string identifier for the object uuid being requested
     * @return - uuid string if a uuid can be found, otherwise null is returned
     */
    public static String getUuid(String uid) {
        // check cache
        if (cache.containsKey(uid))
            return cache.get(uid);

        return null; // no match found
    }

    /**
     * returns the uuid for a grandparent-only object with
     * the given uid and parentUid.
     *
     * @param uid - string identifier for the object uuid being requested
     * @param parentUid - string uid of the parent of the object UUID being requested
     * @return - uuid string if a uuid can be found, otherwise null is returned
     */
    public static String getUuid(
            String uid,
            String parentUid
    ) {
        // build composite key
        String compositeKey = parentUid + SEPARATOR + uid;

        // check cache
        if (cache.containsKey(compositeKey))
            return cache.get(compositeKey);

        return null; // no match found
    }

    /**
     * returns the uuid for a grandparent-only object with
     * the given uid, parentUid, and grandparentUid.
     *
     * @param uid - string identifier for the object uuid being requested
     * @param parentUid - string uid of the parent of the object UUID being requested
     * @return - uuid string if a uuid can be found, otherwise null is returned
     */
    public static String getUuid(
            String uid,
            String parentUid,
            String grandparentUid
    ) {
        // build composite key
        String compositeKey = grandparentUid + SEPARATOR + parentUid + SEPARATOR + uid;

        // check cache
        if (cache.containsKey(compositeKey))
            return cache.get(compositeKey);

        return null; // no match found
    }
}
