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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.json.JsonArray;
import java.io.IOException;;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//This class takes a WITSML Query search and converts it to a GraphQL search
class GraphQLQueryConverter {

    private final String delimeter = " ";
    private final QueryBuilder builder = new QueryBuilder();

    /**
     * Converts a WITSML search query to GraphQL
     *
     * @param wmlObject The search object
     * @return the object
     * @throws IOException Thrown if there is an error in creation of the query
     */
    String convertQuery(AbstractWitsmlObject wmlObject) throws IOException {
        switch (wmlObject.getObjectType()){
            case "well":
                return convertWell(wmlObject);
            default:
                return null;
        }
    }

    private String convertWell (AbstractWitsmlObject well) throws IOException {
        String objJson = well.getJSONString("1.4.1.1");
        this.createWellQuery(objJson);
        return this.builder.GetGraphQLQuery();
    }

    private void createWellQuery(String jsonObj) throws IOException{
        JSONObject obj = new JSONObject(jsonObj);
        StringBuilder querybuilder = new StringBuilder();
        querybuilder.append("query WellQuery($wellArgument: WellArgument) ");
        this.builder.addVariableGroup("wellArgument");
        querybuilder.append(delimeter);
        querybuilder.append("{");
        querybuilder.append(delimeter);
        querybuilder.append("wells(wellArgument: $wellArgument)");
        querybuilder.append("{");
        querybuilder.append(delimeter);
        String indentStr = "";
        querybuilder.append(this.getQuery(obj, indentStr));
        querybuilder.append(delimeter);
        querybuilder.append("}");
        querybuilder.append(delimeter);
        querybuilder.append("}");
        this.builder.setQuery(querybuilder.toString());
    }

    private String getQuery(JSONObject jsonWitsml, String indent) {
        Set<String> keyset = jsonWitsml.keySet();
        ArrayList<String> queryKeys = new ArrayList<>();
        HashMap variables = new HashMap();
        for (String key : keyset) {
            Object queryObj = jsonWitsml.get(key);
            if (queryObj instanceof JSONObject) {
                JSONObject subObj = (JSONObject)queryObj;
                queryKeys.add(indent + key);
                queryKeys.add(indent + "{");
                queryKeys.add(this.getQuery(subObj, indent));
                queryKeys.add(indent + "}");
                continue;
            }
            if (queryObj instanceof JSONArray) {
                JSONArray queryArray = (JSONArray)queryObj;
                for (int i = 0; i < queryArray.length(); ++i) {
                    Object arrObj = queryArray.get(i);
                    if (arrObj instanceof JSONObject) {
                        queryKeys.add(indent + key);
                        JSONObject subObj = (JSONObject) arrObj;
                        queryKeys.add(indent + "{");
                        queryKeys.add(this.getQuery(subObj, indent));
                        queryKeys.add(indent + "}");
                    }
                }
                continue;
            }
            String value = jsonWitsml.get(key).toString();
            if (!value.equals("") && !value.equals("null")) {
                this.builder.addVariable("wellArgument", key, value);
            }
            queryKeys.add(indent + key);
        }
        return String.join(this.delimeter, queryKeys);
    }

    // Internal class that handles the structuring and serialization of the GraphQL query
    class QueryBuilder
    {
        private String query;
        private Map<String, Map<String, String>> variables = new HashMap();

        public QueryBuilder() {}

        public String getQuery() { return query; }

        public void setQuery(String query)
        {
            this.query = query;
        }

        public Map<String, Map<String, String>> getVariables() {
            return variables;
        }

        public void addVariableGroup(String group) {
            variables.put(group, new HashMap());
        }

        public void addVariable(String group, String key, String value) {
            ((Map)variables.get(group)).put(key, value);
        }

        public String GetGraphQLQuery() throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            StringWriter query = new StringWriter();
            objectMapper.writeValue(query, this);
            return query.toString();
        }
    }
}
