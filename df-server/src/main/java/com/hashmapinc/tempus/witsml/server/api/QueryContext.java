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
package com.hashmapinc.tempus.witsml.server.api;

import java.util.Map;

import com.hashmapinc.tempus.witsml.WitsmlUtil;

/**
 * QueryContext is used to hold the state of an individual query
 * to DRILLFLOW and is passed to Valves for query execution
 */
public class QueryContext {
    public final String CLIENT_VERSION; // the WITSML version used by the client that sent the query
    public final String OBJECT_TYPE; // the type of WITSML object being queried for
    public final Map<String, String> OPTIONS_IN; // MAP of options_in key/value pairs
    public final String QUERY_XML; // the raw WITSML xml query sent from the client

    /**
     * 
     * @param clientVersion - the WITSML version used by the client that sent the query
     * @param objectType - the type of WITSML object being queried for
     * @param optionsIn - String holding the options in for the query per the WITSML 1.3/1.4 specs
     * @param queryXML - String holding the raw xml query sent from the client
     */
    public QueryContext(
        String clientVersion,
        String objectType,
        String optionsIn,
        String queryXML
    ) {
        // instantiate values
        this.CLIENT_VERSION = clientVersion;
        this.OBJECT_TYPE = objectType;
        this.OPTIONS_IN = WitsmlUtil.parseOptionsIn(optionsIn); // parse this in the constructor to ensure it is immutable
        this.QUERY_XML = queryXML; 
    }
}