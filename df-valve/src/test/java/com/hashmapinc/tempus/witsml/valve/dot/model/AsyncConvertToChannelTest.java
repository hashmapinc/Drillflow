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
package com.hashmapinc.tempus.witsml.valve.dot.model;

import org.json.JSONObject;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class AsyncConvertToChannelTest {

    @Test
    public void shouldConvertToChannel1411() throws Exception {
        String srcString = new String(Files.readAllBytes(
                Paths.get("src/test/resources/log/log1411src.json")));

        // JSON 1.1.4.1 String
        JSONObject joUnderTest = new JSONObject(srcString);
/*
        JSONObject renamedJO = AsyncConvertToChannel.renameString("pass", "passNumber", joUnderTest);

        String actual = merged.toString(2);
        String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/utilTest/well1311merged.json")));

        assertEquals(expected, actual);
        ///Users/theresastewart/Documents/GitHub/Drillflow/df-valve/src/test/resources/WellGraphQLResponseFull.json
        //WellGraphQLResponseFull.json
*/
    }

}