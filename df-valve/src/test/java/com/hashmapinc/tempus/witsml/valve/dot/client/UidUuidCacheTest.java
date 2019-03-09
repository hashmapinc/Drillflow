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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UidUuidCacheTest {
    @Test
    public void shouldCacheParentlessObjects() {
        // set test values
        String uid = "cool_uid_A";
        String uuid = "cool_uuid_A";

        // assert null responses
        assertNull(UidUuidCache.getUuid(uid));
        assertNull(UidUuidCache.getUid(uuid));

        // cache data
        UidUuidCache.putInCache(uuid, uid);

        // assert responses match
        assertEquals(uid, UidUuidCache.getUid(uuid));
        assertEquals(uuid, UidUuidCache.getUuid(uid));
    }

    @Test
    public void shouldCacheParentOnlyObjects() {
        // set test values
        String uid = "cool_uid_B";
        String parentUid = "cool_parent_uid_B";
        String uuid = "cool_uuid_B";

        // assert null responses
        assertNull(UidUuidCache.getUuid(uid, parentUid));
        assertNull(UidUuidCache.getUid(uuid));

        // cache data
        UidUuidCache.putInCache(uuid, uid, parentUid);

        // assert responses match
        assertEquals(uid, UidUuidCache.getUid(uuid));
        assertEquals(uuid, UidUuidCache.getUuid(uid, parentUid));
    }

    @Test
    public void shouldCacheGrandprentOnlyObjects() {
        // set test values
        String uid = "cool_uid_C";
        String parentUid = "cool_parent_uid_C";
        String grandparentUid = "cool_grandparent_uid_C";
        String uuid = "cool_uuid_C";

        // assert null responses
        assertNull(UidUuidCache.getUuid(uid, parentUid, grandparentUid));
        assertNull(UidUuidCache.getUid(uuid));

        // cache data
        UidUuidCache.putInCache(uuid, uid, parentUid, grandparentUid);

        // assert responses match
        assertEquals(uid, UidUuidCache.getUid(uuid));
        assertEquals(uuid, UidUuidCache.getUuid(uid, parentUid, grandparentUid));
    }
}
