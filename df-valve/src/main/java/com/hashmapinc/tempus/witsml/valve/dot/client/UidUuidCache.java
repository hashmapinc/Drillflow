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
package com.hashmapinc.tempus.witsml.valve.dot.client;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This cache stores mappings between uid/uuid pairs. The same
 * hashmap is used for uid->uuid and for uuid->uid.
 *
 * There are 3 types of objects supported in this map:
 * 1: parentless objects with no parent uid
 * 2: objects with a single parent, but no grandparent
 * 3: objects with a parent and a grandparent, but no more
 *
 * UUID values always map directly to a uid string. However, uid's
 * are only guaranteed to be unique within a same parent. Therefore,
 * to ensure no uid->uuid collisions, composite keys must be used to map
 * to UUID values.
 *
 * The composite keys for each supported object case is:
 * 1: parentless objects use "object_uid" as the key
 * 2: single parent objects use "parent_uid|===|object_uid" as the key
 * 3: single grandparent objects use "grandparent_uid|===|parent_uid|===|object_uid" as the key
 *
 * each composite key points directly to a UUID string.
 *
 * This class can be easily update to support external caching (like REDIS) by replacing
 * the concurrent hashmap with calls to an external service.
 */
public class UidUuidCache {
    // cache used for uid->uuid AND uuid->uid mapping
    private static ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    private static final String SEPARATOR = "|===|"; // separator used when building composite keys

    /**
     * This function stores a mapping between a uid and uuid.
     *
     * This function is used for parentless objects
     *
     * @param uuid - string uuid used as a cache key
     * @param uid - string uid used as a cache value
     */
    public static void putInCache(
        String uuid,
        String uid
    ) {
        // store 2-way mappings in cache
        cache.put(uuid, uid); // uuid->uid mapping
        cache.put(uid, uuid); // uid->uuid mapping
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
