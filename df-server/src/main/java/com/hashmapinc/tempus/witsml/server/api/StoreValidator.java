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

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.witsml.valve.IValve;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoreValidator {

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
        if (!isTypeMatch(WMLtypeIn, xmlIn))
            return -486;
        if (!isObjectSupported(WMLtypeIn, valve, "WMLS_AddToStore"))
            return -487;
        if (isObjectEmpty(WMLtypeIn, xmlIn))
            return -408;
        if (!containsPluralRoot(WMLtypeIn, xmlIn))
            return -401;
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
    public static short validateGetFromStore(String WMLtypeIn, String xmlIn, Map<String,String> optionsIn, IValve valve){
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
     * Checks to make sure that the XMLIn is not empty as part of the add
     * @param WMLtypeIn The witsml type in
     * @param xmlIn The XML input from the client
     * @return true if empty false if not empty
     */
    private static boolean isObjectEmpty(String WMLtypeIn, String xmlIn){
        // This regex matches (for dataObjectType: well) <well uid="xxx" /> and <well uid="xxx"></well> and is multiline capable
        Pattern singularPattern =
                Pattern.compile("(<[" + WMLtypeIn + "][^<]*?/>)|<([" + WMLtypeIn + "][^<]*?>*[\\s\\S]</" + WMLtypeIn + ">)");
        Matcher m = singularPattern.matcher(xmlIn);
        return m.find();
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
            if (awo.getObjectType().equals(WMLtypeIn))
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
}
