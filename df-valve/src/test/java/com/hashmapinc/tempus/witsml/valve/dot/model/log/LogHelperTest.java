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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1411.CsLogData;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog;

import com.hashmapinc.tempus.witsml.valve.dot.TestUtilities;
import org.junit.Test;

public class LogHelperTest {

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
        String result = DotLogDataHelper.convertDataToDotFrom1411(log);
        assertEquals("{\"mnemonicList\":\"a,b,c,d,e,f,g,h\",\"data\":\"[[[1],[2, 3, 4, 5, 6, 7, 8]],[[2],[3, 4, 5, 6, 7, 8, 9]],[[3],[4, 5, 6, 7, 8, 9, 10]]]\"}", result);
    }

    @Test
    public void shouldConvertLegal1411LogToDot() throws IOException, JAXBException {
        String logXml = TestUtilities.getResourceAsString("log1411.xml");
        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs logs =
                WitsmlMarshal.deserialize(logXml, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs.class);

        com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog log = logs.getLog().get(0);
        String result = DotLogDataHelper.convertDataToDotFrom1411(log);
        assertEquals("{\"mnemonicList\":\"Mdepth,Vdepth,Bit Dist,TQ on btm,TQ off btm,ROP,WOP,HKLD,Surf RPM,Mtr RPM,Avg TQ,Max TQ,Min TQ,Max - Min TQ,Max - Min TQ,Pump p avg,Mud D avg,Mud Temp avg,Bit RPM,DXC,ECD\",\"data\":\"[[[499],[498.99, 1.25, 0, 1.45, 3.67, 11.02, 187.66, 0.29, 116.24, 0.01, 0.05, 0.01, 0, 886.03, 1089.99, 1.11, 14.67, 0.29, 1.12, 1.11]],[[500.01],[500, 1.9, 0.01, 1.42, 9.94, 11.32, 185.7, 0.29, 116.24, 0.01, 0.01, 0.01, 0, 795.19, 973.48, 1.11, 14.67, 0.29, 0.95, 1.11]],[[501.03],[501.02, 2.92, 0.02, 1.41, 20.46, 11.62, 184.23, 0.29, 120, 0.01, 0.01, 0.01, 0, 796.68, 956.25, 1.11, 14.67, 0.29, 0.83, 1.11]],[[502.01],[502, 3.9, 0.06, 1.44, 21.73, 10.37, 185.49, 0.29, 120, 0.01, 0.01, 0.01, 0, 802.96, 1005.68, 1.1, 14.66, 0.3, 0.8, 1.11]],[[503.01],[503, 4.9, 0.11, 1.48, 17.65, 10.31, 185.55, 0.29, 118.09, 0.01, 0.01, 0.01, 0, 801.19, 1007.77, 1.11, 14.66, 0.3, 0.83, 1.11]],[[504.05],[504.04, 5.94, 0.18, 1.55, 15.58, 10.4, 185.43, 0.29, 120, 0.01, 0.01, 0.01, 0, 800.83, 1015.89, 1.1, 14.67, 0.29, 0.86, 1.11]],[[505.03],[505.00, 612.03, 1.83, 3.32, 37.11, 18.5, 243.38, 91.93, 0, 8.07, 8.35, 7.68, 0.19, 900.07, 3205, 1.26, 29.67, 93.74, 0.75, 1.31]],[[506.04],[505.95, 613.04, 1.9, 3.4, 9.85, 27.79, 233.9, 79, 0, 8.31, 10.24, 6.83, 1.02, 907.9, 3210, 1.26, 29.8, 95, 1.09, 1.31]],[[507.04],[506.91, 614.04, 1.97, 3.46, 32.44, 23.13, 238.59, 77.35, 0, 7.93, 8.96, 7.76, 0.14, 911.55, 3223.33, 1.26, 29.88, 89.19, 0.78, 1.31]],[[508.01],[507.84, 615.01, 2, 3.49, 29.03, 19.38, 242.36, 90.59, 0, 8.32, 8.78, 7.76, 0.32, 899.74, 3222.17, 1.26, 29.95, 91.66, 0.8, 1.31]],[[509.01],[508.75, 616.01, 2.08, 3.54, 13.09, 15.89, 245.92, 93.38, 0, 7.62, 11.87, 6.43, 0.86, 900.93, 3215.78, 1.26, 30.06, 98.51, 0.92, 1.31]]]\"}",result);
    }

    @Test
    public void shouldConvertLegal1311LogToDot() throws IOException, JAXBException {
        String logXml = TestUtilities.getResourceAsString("log1311.xml");
        com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs logs =
                WitsmlMarshal.deserialize(logXml, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs.class);

        com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog log = logs.getLog().get(0);
        String result = DotLogDataHelper.convertDataToDotFrom1311(log);
        assertEquals("{\"mnemonicList\":\"Mdepth,Vdepth,Bit Dist,TQ on btm,TQ off btm,ROP,WOP,HKLD,Surf RPM,Mtr RPM,Avg TQ,Max TQ,Min TQ,Max - Min TQ,Mud Flow in Avg,Pump p avg,Mud D avg,Mud Temp avg,Bit RPM,DXC,ECD\",\"data\":\"[[[499],[498.99, 1.25, 0, 1.45, 3.67, 11.02, 187.66, 0.29, 116.24, 0.01, 0.05, 0.01, 0, 886.03, 1089.99, 1.11, 14.67, 0.29, 1.12, 1.11]],[[500.01],[500, 1.9, 0.01, 1.42, 9.94, 11.32, 185.7, 0.29, 116.24, 0.01, 0.01, 0.01, 0, 795.19, 973.48, 1.11, 14.67, 0.29, 0.95, 1.11]],[[501.03],[501.02, 2.92, 0.02, 1.41, 20.46, 11.62, 184.23, 0.29, 120, 0.01, 0.01, 0.01, 0, 796.68, 956.25, 1.11, 14.67, 0.29, 0.83, 1.11]],[[502.01],[502, 3.9, 0.06, 1.44, 21.73, 10.37, 185.49, 0.29, 120, 0.01, 0.01, 0.01, 0, 802.96, 1005.68, 1.1, 14.66, 0.3, 0.8, 1.11]],[[503.01],[503, 4.9, 0.11, 1.48, 17.65, 10.31, 185.55, 0.29, 118.09, 0.01, 0.01, 0.01, 0, 801.19, 1007.77, 1.11, 14.66, 0.3, 0.83, 1.11]],[[504.05],[504.04, 5.94, 0.18, 1.55, 15.58, 10.4, 185.43, 0.29, 120, 0.01, 0.01, 0.01, 0, 800.83, 1015.89, 1.1, 14.67, 0.29, 0.86, 1.11]],[[505.03],[505.00, 612.03, 1.83, 3.32, 37.11, 18.5, 243.38, 91.93, 0, 8.07, 8.35, 7.68, 0.19, 900.07, 3205, 1.26, 29.67, 93.74, 0.75, 1.31]],[[506.04],[505.95, 613.04, 1.9, 3.4, 9.85, 27.79, 233.9, 79, 0, 8.31, 10.24, 6.83, 1.02, 907.9, 3210, 1.26, 29.8, 95, 1.09, 1.31]],[[507.04],[506.91, 614.04, 1.97, 3.46, 32.44, 23.13, 238.59, 77.35, 0, 7.93, 8.96, 7.76, 0.14, 911.55, 3223.33, 1.26, 29.88, 89.19, 0.78, 1.31]],[[508.01],[507.84, 615.01, 2, 3.49, 29.03, 19.38, 242.36, 90.59, 0, 8.32, 8.78, 7.76, 0.32, 899.74, 3222.17, 1.26, 29.95, 91.66, 0.8, 1.31]],[[509.01],[508.75, 616.01, 2.08, 3.54, 13.09, 15.89, 245.92, 93.38, 0, 7.62, 11.87, 6.43, 0.86, 900.93, 3215.78, 1.26, 30.06, 98.51, 0.92, 1.31]]]\"}", result);
    }
}
