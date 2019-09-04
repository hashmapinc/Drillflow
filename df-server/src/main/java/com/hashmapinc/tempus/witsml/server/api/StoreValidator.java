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
package com.hashmapinc.tempus.witsml.server.api;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.WitsmlUtil;
import com.hashmapinc.tempus.witsml.valve.IValve;

import java.util.HashMap;
import java.util.Map;

public class StoreValidator {

    /**
     * Validate the GetCap Request
     * @param optionsIn The optionsIn provided by the client
     * @return 1 if its all good, other if its not.
     */
    public static short validateGetCap(String optionsIn){
        HashMap<String, String> options = WitsmlUtil.parseOptionsIn(optionsIn);
        if (!optionsIn.isEmpty() && options.size() == 0)
            return -411;
        if (!options.containsKey("dataVersion"))
            return -424;
        return 1;
    }

    /***
     * Validate the AddToStore query from the client to see if the query flow even needs to continue.
     *
     * @param WMLtypeIn The WITSML type in from the API query
     * @param xmlIn The XML Query template sent in
     * @param optionsIn The options sent in as part of the query
     * @param valve The valve that is currently configured for use with DrillFlow
     * @return The return code, 1 if its all good and can proceed, not one if there is an issue that should be reported
     */
    public static short validateAddToStore(String WMLtypeIn, String xmlIn, Map<String,String> optionsIn, IValve valve){
        if (WMLtypeIn.isEmpty())
            return -407;
        if (xmlIn.isEmpty())
            return -408;
        if (!isTypeMatch(WMLtypeIn, xmlIn))
            return -486;
        if (!isObjectSupported(WMLtypeIn, valve, "WMLS_AddToStore"))
            return -487;
        if (!containsPluralRoot(WMLtypeIn, xmlIn))
            return -401;
        if (!containsVersion(xmlIn))
            return -468;
        if (!containsDefaultNamespace(xmlIn))
            return -403;
        return 1;
    }

    /***
     * Validate the GetFromStore query from the client to see if the query flow even needs to continue.
     *
     * @param WMLtypeIn The WITSML type in from the API query
     * @param xmlIn The XML Query template sent in
     * @param optionsMap The options sent in as part of the query, parsed by WitsmlUtil into Map format
     * @param valve The valve that is currently configured for use with DrillFlow
     * @return The return code, 1 if its all good and can proceed, not one if there is an issue that should be reported
     *         (refer to basemessages.properties for available error codes)
     */
    public static short validateGetFromStore(String WMLtypeIn, String xmlIn, Map<String,String> optionsMap, IValve valve){

        // perform standard validations for any GetFromStore query
        short validation = standardValidations(WMLtypeIn, xmlIn, valve);
        if (validation<0) {
            // failed standard validations...no need to continue
            return validation;
        }

        if ( optionsMap.containsKey("requestObjectSelectionCapability") ) {
            String keyValue = optionsMap.get("requestObjectSelectionCapability");
            if (keyValue == null || keyValue.isBlank()) {
                return -1001;
            }
            if (keyValue.equals("true")) {
                if (xmlIn.contains("uid")) {
                    return -428;
                }
                if (optionsMap.size() > 2)
                    return -427;
                // there must not be another optionIn present
                if (optionsMap.size() >= 2 && !optionsMap.containsKey("returnElements")) {
                    return -427;
                }
            }
            return 1;
        }
        return 1;

    }

    private static short standardValidations(String WMLtypeIn, String xmlIn, IValve valve) {
        // The WITSML type in from the API query is EMPTY
        // or contains ONLY WHITESPACE (NOT equivalent to "none").
        if ( WMLtypeIn.isBlank() )
            return -407;
        if ( !isObjectSupported(WMLtypeIn, valve, "WMLS_GetFromStore") )
            return -487;
        if ( xmlIn.isEmpty() )
            return -408;
        if ( !containsVersion(xmlIn) )
            return -468;
        return 1;
    }

    /***
     * Validate the AddToStore query from the client to see if the query flow even needs to continue.
     *
     * @param WMLtypeIn The WITSML type in from the API query
     * @param xmlIn The XML Query template sent in
     * @param optionsIn The options sent in as part of the query
     * @param valve The valve that is currently configured for use with DrillFlow
     * @return The return code, 1 if its all good and can proceed, not one if there is an issue that should be reported
     */
    public static short validateUpdateInStore(String WMLtypeIn, String xmlIn, Map<String,String> optionsIn, IValve valve){
        return 1;
    }

    /***
     * Validate the AddToStore query from the client to see if the query flow even needs to continue.
     *
     * @param WMLtypeIn The WITSML type in from the API query
     * @param xmlIn The XML Query template sent in
     * @param optionsIn The options sent in as part of the query
     * @param valve The valve that is currently configured for use with DrillFlow
     * @return The return code, 1 if its all good and can proceed, not one if there is an issue that should be reported
     */
    public static short validateDeleteFromStore(String WMLtypeIn, String xmlIn, Map<String,String> optionsIn, IValve valve){
        return 1;
    }

    /***
     * Make sure that the WMLtypeIn matches the XML In
     * @param WMLtypeIn The type of the object that was identified by the client
     * @param xmlIn The actual query that was input
     * @return true if the type of XmlIn and WMLtypeIn are the same, false if not
     */
    private static boolean isTypeMatch(String WMLtypeIn, String xmlIn){
        // This regex matches (for dataObjectType: well) <well uid="xxx" /> and <well uid="xxx"></well> and is multiline capable
        return xmlIn.contains(WMLtypeIn + " ");
    }

    /***
     * Checks to see if the object is supported by the valve, based only on the WMLtypeIn, not the XMLIn
     * (this should be validated by isTypeMatch to make sure those 2 agree)
     * @param WMLtypeIn The type of the query as provided by the client
     * @param valve The valve that is autowired
     * @param operation The operation that the client is trying to perform
     * @return true if the object is supported, false if its not
     */
    private static boolean isObjectSupported(String WMLtypeIn, IValve valve, String operation){
        if (!valve.getCap().containsKey(operation))
            return false;
        AbstractWitsmlObject[] supportedObjs = valve.getCap().get(operation);
        if (supportedObjs == null)
            return false;
        for (AbstractWitsmlObject awo : supportedObjs){
            if (awo.getObjectType().equals(WMLtypeIn.toLowerCase()))
                return true;
        }
        return false;
    }

    /***
     * Verify if the object contains a plural root
     * @param wmlTypeIn The type of the object that is being operated on as provided by the client
     * @param xmlIn The XML query template that was submitted by the client
     * @return true if it contains the plural root, false if it does not
     */
    private static boolean containsPluralRoot(String wmlTypeIn, String xmlIn){
        return xmlIn.contains(wmlTypeIn + "s");
    }

    /**
     * Determines whether or not the xmlIn contains the default namespace
     * @param xmlIn The XML In provided by the client
     * @return true if the default namespace is provided, false if it is not
     */
    private static boolean containsDefaultNamespace(String xmlIn){
        return xmlIn.contains("xmlns=");
    }

    /***
     * Determines whether or not the version tag is contained
     * @param xmlIn The XML query from the client
     * @return true if it exists, false if it does not
     */
    private static boolean containsVersion(String xmlIn) {
        try {
            String version = WitsmlUtil.getVersionFromXML(xmlIn);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
