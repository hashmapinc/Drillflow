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

    public static short validateGetFromStore(String WMLtypeIn, String xmlIn, Map<String,String> optionsIn){
        return 1;
    }

    public static short validateUpdateInStore(String WMLtypeIn, String xmlIn, Map<String,String> optionsIn){
        return 1;
    }

    public static short validateDeleteFromStore(String WMLtypeIn, String xmlIn, Map<String,String> optionsIn){
        return 1;
    }

    private static boolean isObjectEmpty(String WMLtypeIn, String xmlIn){
        // This regex matches (for dataObjectType: well) <well uid="xxx" /> and <well uid="xxx"></well> and is multiline capable
        Pattern singularPattern =
                Pattern.compile("(<[" + WMLtypeIn + "][^<]*?/>)|<([" + WMLtypeIn + "][^<]*?>*[\\s\\S]</" + WMLtypeIn + ">)");
        Matcher m = singularPattern.matcher(xmlIn);
        return m.find();
    }

    private static boolean isTypeMatch(String WMLtypeIn, String xmlIn){
        // This regex matches (for dataObjectType: well) <well uid="xxx" /> and <well uid="xxx"></well> and is multiline capable
        return xmlIn.contains(WMLtypeIn + " ");
    }

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

    private static boolean containsPluralRoot(String wmlTypeIn, String xmlIn){
        return xmlIn.contains(wmlTypeIn + "s");
    }
}
