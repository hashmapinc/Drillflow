package com.hashmapinc.tempus.witsml.server.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class HashMapWithExpiryTest {

    @Test
    public void testExpiry() {
        int timeToLive = 100;
        HashMapWithExpiry<String, String> map = new HashMapWithExpiry<String, String>();
        map.put("a", "b", 2 * timeToLive);
        try {
            Thread.sleep(3 * timeToLive);
            assertNull(map.get("a"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSuccessful() {
        int timeToLive = 100;
        HashMapWithExpiry<String, String> map = new HashMapWithExpiry<String, String>();
        map.put("a", "b", 2 * timeToLive);
        try {
            Thread.sleep(1 * timeToLive);
            assertNotNull(map.get("a"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
