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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogData;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog;

import org.junit.Test;

public class LogHelperTest {
    @Test
    public void shouldConvert1411LogtoWitsml20() throws JAXBException {
        ObjLog log = new ObjLog();
        CsLogData logData = new CsLogData();
        List<String> data = new ArrayList<>();
        data.add("1,2,3,4,5,6,7,8");
        data.add("2,3,4,5,6,7,8,9");
        data.add("3,4,5,6,7,8,9,10");
        logData.setData(data);
        List<CsLogData> composedData = new ArrayList<>();
        composedData.add(logData);
        log.setLogData(composedData);
        String result = DotLogDataHelper.convertDataToWitsml20(log);
        assertEquals(result, "[[[1],[2, 3, 4, 5, 6, 7, 8]],[[2],[3, 4, 5, 6, 7, 8, 9]],[[3],[4, 5, 6, 7, 8, 9, 10]]]");
    }

    @Test
    public void shouldConvert1411LogtoDot() throws JAXBException {
        ObjLog log = new ObjLog();
        CsLogData logData = new CsLogData();
        List<String> data = new ArrayList<>();
        data.add("1,2,3,4,5,6,7,8");
        data.add("2,3,4,5,6,7,8,9");
        data.add("3,4,5,6,7,8,9,10");
        logData.setData(data);
        logData.setMnemonicList("a,b,c,d,e,f,g,h");
        List<CsLogData> composedData = new ArrayList<>();
        composedData.add(logData);
        log.setLogData(composedData);
        String result = DotLogDataHelper.convertDataToDot(log);
        assertNotNull(result);
    }
}
