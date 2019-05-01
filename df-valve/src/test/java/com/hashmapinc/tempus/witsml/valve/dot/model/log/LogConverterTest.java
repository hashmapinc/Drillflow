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

import org.json.JSONArray;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import static org.assertj.core.api.Assertions.assertThat;

public class LogConverterTest {
    @Test
    public void shouldConvertTo1411() throws JAXBException {
        JSONArray jsonRespCS = new JSONArray(
                "[{\"uuid\":\"af60b3b5-3e7e-4d8e-a8fc-f6a357f62e37\","
              + "\"citation\":{\"title\":\"Baker Logs Section1 - MD Log\"},"
              + "\"objectGrowing\":false,"
              + "\"timeDepth\":\"time\","
              + "\"loggingCompanyName\":\"Schlumberger\","
              + "\"commonData\":{},"
              + "\"uid\":\"HM_TMS2009\","
              + "\"uidWell\":\"U2\","
              + "\"uidWellbore\":\"WBDD600\","
              + "\"wellId\":\"5c19761c-8cf8-4a04-aa64-da6d1d6acefa\","
              + "\"wellboreId\":\"1ff82216-4b92-47a5-a8ba-d4c8481cbfb0\"}]"
        );
        JSONArray jsonRespCH = new JSONArray(
                "[{\"uuid\":\"eml://witsml20/well(5c19761c-8cf8-4a04-aa64-da6d1d6acefa)/wellbore(1ff82216-4b92-47a5-a8ba-d4c8481cbfb0)/channelSet(af60b3b5-3e7e-4d8e-a8fc-f6a357f62e37)/Mdepth\","
              + "\"growingStatus\":\"inactive\","
              + "\"uid\":\"Mdepth\","
              + "\"classIndex\":0,"
              + "\"mnemAlias\":{\"namingSystem\":\"\",\"value\":\"Mdepth\"},"
              + "\"mnemonic\":\"Mdepth\","
              + "\"dataType\":\"double\","
              + "\"uom\":\"m\","
              + "\"timeDepth\":\"time\","
              + "\"classWitsml\":\"measured depth of hole\","
              + "\"index\":[{\"indexType\":\"time\",\"uom\":\"double\",\"direction\":\"increasing\",\"mnemonic\":\"Mdepth\"}],"
              + "\"citation\":{\"title\":\"TQA\"},"
              + "\"customData\":\"{}\"}]"
        );
        String currentString = "{\"timeDepth\":\"time\"";
        //JSONObject responseFromDoT = new JSONObject(jsonRespCS);
        LogConverterExtended logConverterExtended = new LogConverterExtended();
        String convertedResponse = logConverterExtended.convertTo1411(jsonRespCS, jsonRespCH);
        System.out.println(convertedResponse);
        assertThat(convertedResponse.equalsIgnoreCase(currentString));
    }
}
