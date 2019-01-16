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
package com.hashmapinc.tempus.witsml.valve.dot;

import org.json.JSONObject;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;

public class UtilTest {

    @Test
    public void shouldProperlyMerge1311Well() throws Exception {
        String destString = new String(Files.readAllBytes(Paths.get("src/test/resources/utilTest/well1311dest.json")));
        String srcString = new String(Files.readAllBytes(Paths.get("src/test/resources/utilTest/well1311src.json")));

        JSONObject dest = new JSONObject(destString);
        JSONObject src = new JSONObject(srcString);

        JSONObject merged = Util.merge(dest, src);

        String actual = merged.toString(2);
        String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/utilTest/well1311merged.json")));

        assertEquals(expected, actual);
    }


    @Test
    public void shouldReturnEmptyMergeObject() throws Exception {
        String destString = "{\"fakeField\": null}";
        String srcString = new String(Files.readAllBytes(Paths.get("src/test/resources/utilTest/well1311src.json")));

        JSONObject dest = new JSONObject(destString);
        JSONObject src = new JSONObject(srcString);

        JSONObject merged = Util.merge(dest, src);

        String actual = merged.toString(2);
        String expected = "{}";

        assertEquals(expected, actual);
    }
}

