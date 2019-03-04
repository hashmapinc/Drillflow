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
package com.hashmapinc.tempus.witsml.valve.dot;

public class GraphQLQueryConstants {
    //=========================================================================
    // TRAJECTORY QUERY
    //=========================================================================
    public static final String TRAJECTORY_QUERY =
            "query TrajectoryQuery($trajArg: TrajectoryArgument, $trajStationArg: TrajectoryStationArgument) {\n" +
            "\ttrajectories(trajectoryArgument: $trajArg) {\n" +
            "\t\t...trajFields\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "fragment trajFields on TrajectoryType {\n" +
            "\taliases {\n" +
            "\t\tauthority\n" +
            "\t\tdescription\n" +
            "\t\tidentifier\n" +
            "\t}\n" +
            "\taziRef\n" +
            "\taziVertSect {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tcitation {\n" +
            "\t\tcreation\n" +
            "\t\tdescription\n" +
            "\t\tdescriptiveKeywords\n" +
            "\t\teditor\n" +
            "\t\tformat\n" +
            "\t\tlastUpdate\n" +
            "\t\toriginator\n" +
            "\t\ttitle\n" +
            "\t\tversionString\n" +
            "\t}\n" +
            "\tcommonData {\n" +
            "\t\tacquisitionTimeZone {\n" +
            "\t\t\tdTim\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tcomments\n" +
            "\t\tdefaultDatum {\n" +
            "\t\t\tuidRef\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\textensionAny\n" +
            "\t\textensionNameValue {\n" +
            "\t\t\tdataType\n" +
            "\t\t\tdescription\n" +
            "\t\t\tdTim\n" +
            "\t\t\tindex\n" +
            "\t\t\tmd {\n" +
            "\t\t\t\tdatum\n" +
            "\t\t\t\tuom\n" +
            "\t\t\t\tvalue\n" +
            "\t\t\t}\n" +
            "\t\t\tmeasureClass\n" +
            "\t\t\tname\n" +
            "\t\t\tuid\n" +
            "\t\t\tvalue {\n" +
            "\t\t\t\tuom\n" +
            "\t\t\t\tvalue\n" +
            "\t\t\t}\n" +
            "\t\t}\n" +
            "\t\titemState\n" +
            "\t\tprivateGroupOnly\n" +
            "\t\tserviceCategory\n" +
            "\t\tsourceName\n" +
            "\t}\n" +
            "\tcreationTimeUtc\n" +
            "\tcustomData\n" +
            "\tdefinitive\n" +
            "\tdispEwVertSectOrig {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tdispNsVertSectOrig {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tdTimTrajEnd\n" +
            "\tdTimTrajStart\n" +
            "\texistenceKind\n" +
            "\textensionNameValue {\n" +
            "\t\tdataType\n" +
            "\t\tdescription\n" +
            "\t\tdTim\n" +
            "\t\tindex\n" +
            "\t\tmd {\n" +
            "\t\t\tdatum\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmeasureClass\n" +
            "\t\tname\n" +
            "\t\tuid\n" +
            "\t\tvalue {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tfinalTraj\n" +
            "\tgridConUsed {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tgridCorUsed {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tgrowingStatus\n" +
            "\tlastUpdateTimeUtc\n" +
            "\tmagDeclUsed {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tmdMn {\n" +
            "\t\tdatum\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tmdMx {\n" +
            "\t\tdatum\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tmemory\n" +
            "\tname\n" +
            "\tnameWell\n" +
            "\tnameWellbore\n" +
            "\tobjectVersion\n" +
            "\tparentTrajectory {\n" +
            "\t\tcontentType\n" +
            "\t\ttitle\n" +
            "\t\tuidRef\n" +
            "\t\turi\n" +
            "\t\tuuid\n" +
            "\t\tuuidAuthority\n" +
            "\t\tversionString\n" +
            "\t}\n" +
            "\tschemaVersion\n" +
            "\tserviceCompany\n" +
            "\tuid\n" +
            "\tuuid\n" +
            "\tuuidWell\n" +
            "\tuuidWellbore\n" +
            "\twellbore {\n" +
            "\t\tcontentType\n" +
            "\t\ttitle\n" +
            "\t\tuidRef\n" +
            "\t\turi\n" +
            "\t\tuuid\n" +
            "\t\tuuidAuthority\n" +
            "\t\tversionString\n" +
            "\t}\n" +
            "\t\n" +
            "\t\n" +
            "\ttrajectoryStation(trajectoryStationArgument: $trajStationArg) {\n" +
            "\t\t...stationFields\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "fragment stationFields on TrajectoryStation {\n" +
            "\taxialMagInterferenceCorUsed\n" +
            "\tazi {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tcalcAlgorithm\n" +
            "\tcommonData {\n" +
            "\t\tacquisitionTimeZone {\n" +
            "\t\t\tdTim\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tcomments\n" +
            "\t\tdefaultDatum {\n" +
            "\t\t\tuidRef\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\textensionAny\n" +
            "\t\textensionNameValue {\n" +
            "\t\t\tdataType\n" +
            "\t\t\tdescription\n" +
            "\t\t\tdTim\n" +
            "\t\t\tindex\n" +
            "\t\t\tmd {\n" +
            "\t\t\t\tdatum\n" +
            "\t\t\t\tuom\n" +
            "\t\t\t\tvalue\n" +
            "\t\t\t}\n" +
            "\t\t\tmeasureClass\n" +
            "\t\t\tname\n" +
            "\t\t\tuid\n" +
            "\t\t\tvalue {\n" +
            "\t\t\t\tuom\n" +
            "\t\t\t\tvalue\n" +
            "\t\t\t}\n" +
            "\t\t}\n" +
            "\t\titemState\n" +
            "\t\tprivateGroupOnly\n" +
            "\t\tserviceCategory\n" +
            "\t\tsourceName\n" +
            "\t}\n" +
            "\tcorUsed {\n" +
            "\t\tdirSensorOffset {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tgravAxialAccelCor {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tgravTran1AccelCor {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tgravTran2AccelCor {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmagAxialDrlstrCor {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmagAxialMSACor {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmagTran1DrlstrCor {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmagTran1MSACor {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmagTran2DrlstrCor {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmagTran2MSACor {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tsagAziCor {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tsagIncCor {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tstnGridConUsed {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tstnGridCorUsed {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tstnMagDeclUsed {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tcosagCorUsed\n" +
            "\tcreationTimeUtc\n" +
            "\tdipAngleUncert {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tdispEw {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tdispNs {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tdls {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tdTimStn\n" +
            "\textensionNameValue {\n" +
            "\t\tdataType\n" +
            "\t\tdescription\n" +
            "\t\tdTim\n" +
            "\t\tindex\n" +
            "\t\tmd {\n" +
            "\t\t\tdatum\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmeasureClass\n" +
            "\t\tname\n" +
            "\t\tuid\n" +
            "\t\tvalue {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tgeoModelUsed\n" +
            "\tgravAccelCorUsed\n" +
            "\tgravTotalFieldReference {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tgravTotalUncert {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tgtf {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tincl {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tinfieldRefCorUsed\n" +
            "\tinHoleRefCorUsed\n" +
            "\tinterpolatedInfieldRefCorUsed\n" +
            "\tiscwsaToolErrorModel {\n" +
            "\t\tcontentType\n" +
            "\t\ttitle\n" +
            "\t\tuidRef\n" +
            "\t\turi\n" +
            "\t\tuuid\n" +
            "\t\tuuidAuthority\n" +
            "\t\tversionString\n" +
            "\t}\n" +
            "\tlastUpdateTimeUtc\n" +
            "\tlocation {\n" +
            "\t\tdescription\n" +
            "\t\teasting {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\textensionNameValue {\n" +
            "\t\t\tdataType\n" +
            "\t\t\tdescription\n" +
            "\t\t\tdTim\n" +
            "\t\t\tindex\n" +
            "\t\t\tmd {\n" +
            "\t\t\t\tdatum\n" +
            "\t\t\t\tuom\n" +
            "\t\t\t\tvalue\n" +
            "\t\t\t}\n" +
            "\t\t\tmeasureClass\n" +
            "\t\t\tname\n" +
            "\t\t\tuid\n" +
            "\t\t\tvalue {\n" +
            "\t\t\t\tuom\n" +
            "\t\t\t\tvalue\n" +
            "\t\t\t}\n" +
            "\t\t}\n" +
            "\t\tlatitude {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tlocalX {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tlocalY {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tlongitude {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tnorthing {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\toriginal\n" +
            "\t\tprojectedX {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tprojectedY {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tsouthing {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tuid\n" +
            "\t\twellCRS {\n" +
            "\t\t\tuidRef\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\twesting {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tmagDipAngleReference {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tmagDrlstrCorUsed\n" +
            "\tmagModelUsed\n" +
            "\tmagModelValid\n" +
            "\tmagTotalFieldReference {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tmagTotalUncert {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tmagXAxialCorUsed\n" +
            "\tmanuallyEntered\n" +
            "\tmatrixCov {\n" +
            "\t\tbiasE {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tbiasN {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tbiasVert {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tvarianceEE {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tvarianceEVert {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tvarianceNE {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tvarianceNN {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tvarianceNVert {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tvarianceVertVert {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tmd {\n" +
            "\t\tdatum\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tmdDelta {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\tmodelToolError\n" +
            "\tmSACorUsed\n" +
            "\tmtf {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\trateBuild {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\trateTurn {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\trawData {\n" +
            "\t\tgravAxialRaw {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tgravTran1Raw {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tgravTran2Raw {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmagAxialRaw {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmagTran1Raw {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmagTran2Raw {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tsagCorUsed\n" +
            "\tsourceStation {\n" +
            "\t\tstationReference\n" +
            "\t\ttrajectoryParent {\n" +
            "\t\t\tuidRef\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\twellboreParent {\n" +
            "\t\t\tuidRef\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tstatusTrajStation\n" +
            "\ttarget {\n" +
            "\t\tuidRef\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\ttvd {\n" +
            "\t\tdatum\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\ttvdDelta {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "\ttypeSurveyTool\n" +
            "\ttypeTrajStation\n" +
            "\tuid\n" +
            "\tvalid {\n" +
            "\t\tgravTotalFieldCalc {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmagDipAngleCalc {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t\tmagTotalFieldCalc {\n" +
            "\t\t\tuom\n" +
            "\t\t\tvalue\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tvertSect {\n" +
            "\t\tuom\n" +
            "\t\tvalue\n" +
            "\t}\n" +
            "}";
    //=========================================================================
}