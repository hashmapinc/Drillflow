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
package com.hashmapinc.tempus.witsml.valve;

public class ObjectSelectionConstants {
    //=========================================================================
    // WELL_OBJ_SELECTION variable
    //=========================================================================
    public static final String WELL_OBJ_SELECTION =
            "<wells xmlns=\"http://www.witsml.org/schemas/1series\"" +
                    "version=\"1.4.1.1\">" +
                    "<well uid=\"uid_dfTestWell1\">" +
                    "<name>dfTestWell1</name>" +
                    "<numGovt>12345</numGovt>" +
                    "<numAPI>12345</numAPI>" +
                    "<commonData>" +
                    "<dTimLastChange>2019-03-08T19:47:27.545Z</dTimLastChange>" +
                    "</commonData>" +
                    "</well>" +
                    "</wells>";
    //=========================================================================

    //=========================================================================
    // WELLBORE_OBJ_SELECTION variable
    //=========================================================================
    public static final String WELLBORE_OBJ_SELECTION =
            "<wellbores xmlns=\"http://www.witsml.org/schemas/1series\"" +
                    "version=\"1.4.1.1\">" +
                    "<wellbore uidWell=\"uid_dfTestWell1\" uid=\"uid_dfTestWellbore1\">" +
                    "<name>dfTestWell1</name>" +
                    "<nameWellbore>dfTestWellbore1</nameWellbore>" +
                    "<commonData>" +
                    "<dTimLastChange>2019-03-08T19:47:27.545Z</dTimLastChange>" +
                    "</commonData>" +
                    "</wellbore>" +
                    "</wellsbores>";
    //=========================================================================

    //=========================================================================
    // TRAJECTORY_OBJ_SELECTION variable
    //=========================================================================
    public static final String TRAJECTORY_OBJ_SELECTION =
            "<trajectorys xmlns=\"http://www.witsml.org/schemas/1series\"" +
                    "version=\"1.4.1.1\">" +
                    "<trajectory uidWell=\"uid_dfTestWell1\" uidWellbore=\"uid_dfTestWellbore1\" uid=\"uid_dfTestTraj1\">" +
                    "<nameWell>dfTestWell1</nameWell>" +
                    "<nameWellbore>dfTestWellbore1</nameWellbore>" +
                    "<name>dfTestTraj1</name>" +
                    "<objectGrowing>false</objectGrowing>" +
                    "<commonData>" +
                    "<dTimLastChange>2019-03-08T19:47:27.545Z</dTimLastChange>" +
                    "</commonData>" +
                    "</trajectory>" +
                    "</trajectorys>";
    //=========================================================================

}
