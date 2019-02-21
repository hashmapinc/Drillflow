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

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GraphQLQueryConverterTest {

    @Test
    public void generateProperGraphQLQueryForWellbore() throws Exception {
        String query = TestUtilities.getResourceAsString("wellboreOpenQuery1411.xml");
        ObjWellbores queryObject = WitsmlMarshal.deserialize(query, ObjWellbores.class);
        ObjWellbore singularObject = queryObject.getWellbore().get(0);
        GraphQLQueryConverter converter = new GraphQLQueryConverter();
        String graphQLQuery = converter.convertQuery(singularObject);
        assertNotNull(graphQLQuery);
        assertTrue(graphQLQuery.contains("title"));
    }

    @Test
    public void generateProperWellboreResponseFromGraphQL() throws Exception {
        String queryResp = TestUtilities.getResourceAsString("GraphQLResponse.json");
        GraphQLRespConverter converter = new GraphQLRespConverter();
        List<AbstractWitsmlObject> objs =  converter.convert(queryResp, "wellbore");
        assertNotNull(objs);
    }

    @Test
    public void generateProperGraphQLQueryForTrajectory() throws Exception {
        String queryXML = TestUtilities.getResourceAsString("trajectoryGraphql/trajectoryGraphqlQuery1411.xml");
        ObjTrajectory obj = ((ObjTrajectorys) WitsmlMarshal.deserialize(queryXML, ObjTrajectorys.class)).getTrajectory().get(0);
        GraphQLQueryConverter converter = new GraphQLQueryConverter();
        String graphQLQuery = converter.convertQuery(obj);
        assertNotNull(graphQLQuery);
        assertTrue(graphQLQuery.contains("title"));
    }

    @Test
    public void generateProperTrajectoryResponseFromGraphQL() throws Exception {
        String queryResp = TestUtilities.getResourceAsString("GraphQLResponse.json");
        GraphQLRespConverter converter = new GraphQLRespConverter();
        List<AbstractWitsmlObject> objs =  converter.convert(queryResp, "trajectory");
        assertNotNull(objs);
    }
}
