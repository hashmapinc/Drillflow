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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class GraphQLQueryConverterTest {

    @Test
    public void generateProperGraphQLQueryForWellbore() throws Exception {
        final String variablesNodeName = "variables";
        final String queryNodeName = "query";
        final String wellboreArgNodeName = "wellboreArgument";
        final String wellboreOpenQuery = "wellboreOpenQuery1411.xml";
        JSONObject wellboreQueryFields = new JSONObject();

        // Get what is expected from the resource...
        String queryString = TestUtilities.getResourceAsString(wellboreOpenQuery);
        ObjWellbores queryObject = WitsmlMarshal.deserialize(queryString, ObjWellbores.class);
        ObjWellbore singularObject = queryObject.getWellbore().get(0);
        JSONObject wellboreJson = new JSONObject(singularObject.getJSONString("1.4.1.1"));

        // Get what the code-under-test produces...
        GraphQLQueryConverter converter = new GraphQLQueryConverter();
        String graphQLQuery = converter.getQuery(singularObject);

        // Perform the tests...

        // 1st: check that the query was produced...
        assertNotNull(graphQLQuery);

        // 2nd: Check if the produced JSON contains all of the populated query fields
        //      from the Resource and build wellbore query fields dynamically ...
        checkNbuildQuery("uid", wellboreJson, graphQLQuery, wellboreQueryFields );
        checkNbuildQuery("uidWell", wellboreJson, graphQLQuery, wellboreQueryFields );
        checkNbuildQuery("name", wellboreJson, graphQLQuery, wellboreQueryFields );
        checkNbuildQuery("nameWell", wellboreJson, graphQLQuery, wellboreQueryFields );

        // 3rd: Create comparable object (to perform an apples-to-apples comparison) & then
        //      perform a deep comparison of the json values...
        JSONObject comparable = new JSONObject();
        comparable.put( queryNodeName, GraphQLQueryConstants.WELLBORE_QUERY );

        // build variables section
        JSONObject variables = new JSONObject();
        variables.put( wellboreArgNodeName, wellboreQueryFields );
        comparable.put( variablesNodeName, variables );
        String comparableToString = comparable.toString(2);

        ObjectMapper om = new ObjectMapper();
        Map<String, Object> producedMap = (Map<String, Object>) (om.readValue(graphQLQuery, Map.class));
        Map<String, Object> expectedMap = (Map<String, Object>) (om.readValue(comparableToString, Map.class));
        assertEquals( producedMap, expectedMap );

    }

    /* ******************************************************************************* */
    /* checkNbuildQuery verifies that if the resource object contains the query field, */
    /* then the produced JSON also contains that query field.                          */
    /* ******************************************************************************* */
    private static void checkNbuildQuery(String queryField,
                                         JSONObject resourceObj,
                                         String producedJson,
                                         JSONObject wellboreQueryFields )
    {

        if ( resourceObj.has(queryField) && !JsonUtil.isEmpty(resourceObj.get(queryField))  ) {
            assertTrue( producedJson.contains(queryField) );
            assertTrue( producedJson.contains(resourceObj.getString(queryField)) );
            wellboreQueryFields.put(queryField, resourceObj.get(queryField));
        }

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
        String graphQLQuery = converter.getQuery(obj, "test");
        assertNotNull(graphQLQuery);
        assertTrue(graphQLQuery.contains("title"));
    }

    @Test
    public void generateProperGraphQLQueryUUIDUidMapping() throws Exception {
        String queryXML = TestUtilities.getResourceAsString("trajectoryGraphql/trajectoryGraphqlQuery1411.xml");
        ObjTrajectory obj = ((ObjTrajectorys) WitsmlMarshal.deserialize(queryXML, ObjTrajectorys.class)).getTrajectory().get(0);
        GraphQLQueryConverter converter = new GraphQLQueryConverter();
        String graphQLQuery = converter.getUidUUIDMappingQuery(obj);
        assertNotNull(graphQLQuery);
        assertTrue(graphQLQuery.contains("\"arg\": {\n" +
                "    \"uid\": \"uidWellbore\",\n" +
                "    \"uidWell\": \"uidWell\""));
    }

    @Test
    public void generateProperTrajectoryResponseFromGraphQL() throws Exception {
        String queryResp = TestUtilities.getResourceAsString("trajectoryGraphql/trajectoryGraphqlResponse.json");
        List<AbstractWitsmlObject> objs = GraphQLRespConverter.convert(queryResp, "trajectory");
        assertNotNull(objs);
    }
}
