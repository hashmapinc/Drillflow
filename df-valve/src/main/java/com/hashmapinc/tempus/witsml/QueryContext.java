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
package com.hashmapinc.tempus.witsml;

import java.util.Map;

/**
 * QueryContext is used to hold the state of an individual query
 * to DRILLFLOW and is passed to Valves for query execution
 */
public class QueryContext {
    public final String CLIENT_VERSION; // the WITSML version used by the client that sent the query
    public final String OBJECT_TYPE; // the type of WITSML object being queried for
    public final Map<String, String> OPTIONS_IN; // MAP of options_in key/value pairs
    public final String QUERY_XML; // the raw WITSML xml query sent from the client

    // initial json object
    public JSON queryJSON; // first parsing of input query XML
    public JSON populatedJSON; // data-filled query JSON

    /**
     * 
     * @param clientVersion - the WITSML version used by the client that sent the query
     * @param objectType - the type of WITSML object being queried for
     * @param optionsIn - MAP of options_in key/value pairs
     * @param queryXML - String holding the raw xml query sent from the client
     */
    public QueryContext(
        String clientVersion,
        String objectType,
        Map<String, String> optionsIn, 
        String queryXML
    ) {
        // instantiate values
        this.CLIENT_VERSION = clientVersion;
        this.OBJECT_TYPE = objectType;
        this.OPTIONS_IN = optionsIn;
        this.QUERY_XML = queryXML; 
    }
}