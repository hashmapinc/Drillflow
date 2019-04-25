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
package com.hashmapinc.tempus.witsml;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/** 
 * WitsmlObjectParser is a class that provides
 * simple static methods for parsing witsml objects into
 * AbstractWitsmlObjects
 */
public class WitsmlObjectParser {
    // get logger
    private static final Logger LOG = Logger.getLogger(WitsmlObjectParser.class.getName());

    /**
     * this method parses log objects
     * 
     * @param rawXML - string value with the raw xml to parse
     * @param version - string value with witsml version of rawXML: 1.3.1.1 or 1.4.1.1
     * 
     * @return witsmlObjects - list of AbstractWitsmlObjects parsed from rawXml
     */
    public static List<AbstractWitsmlObject> parseLogObject(
        String rawXML,
        String version
    ) throws Exception {

        // TODO Where is aliases? Why is logParam messed up (value = "\n\t\t\t  " & all other fields are null)?
        List<AbstractWitsmlObject> witsmlObjects = new ArrayList<AbstractWitsmlObject>();
        
        // handle version 1.3.1.1
        if ("1.3.1.1".equals(version)) {
            com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs objs = WitsmlMarshal.deserialize(
                rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs.class
            );
            for (com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog obj : objs.getLog()) {
                witsmlObjects.add(obj);
            } 

        // handle version 1.4.1.1
        } else if ("1.4.1.1".equals(version)) {
            com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs objs = WitsmlMarshal.deserialize(
                rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs.class
            );
            for (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog obj : objs.getLog()) {
                witsmlObjects.add(obj);
            } 

        } else {
            throw new WitsmlException("unsupported witsml version " + version);
        }

        // return the objects
        return witsmlObjects;
    }

    /**
     * this method parses trajectory objects
     * 
     * @param rawXML - string value with the raw xml to parse
     * @param version - string value with witsml version of rawXML: 1.3.1.1 or 1.4.1.1
     * 
     * @return witsmlObjects - list of AbstractWitsmlObjects parsed from rawXml
     */
    public static List<AbstractWitsmlObject> parseTrajectoryObject(
        String rawXML,
        String version
    ) throws Exception {
        List<AbstractWitsmlObject> witsmlObjects = new ArrayList<AbstractWitsmlObject>();
        
        // handle version 1.3.1.1
        if ("1.3.1.1".equals(version)) {
            com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectorys objs = WitsmlMarshal.deserialize(
                rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectorys.class
            );
            for (com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory obj : objs.getTrajectory()) {
                witsmlObjects.add(obj);
            } 

        // handle version 1.4.1.1
        } else if ("1.4.1.1".equals(version)) {
            com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys objs = WitsmlMarshal.deserialize(
                rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys.class
            );
            for (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory obj : objs.getTrajectory()) {
                witsmlObjects.add(obj);
            } 

        } else {
            throw new Exception("unsupported witsml version " + version);
        }

        // return the objects
        return witsmlObjects;
    }

    /**
     * this method parses well objects
     * 
     * @param rawXML - string value with the raw xml to parse
     * @param version - string value with witsml version of rawXML: 1.3.1.1 or 1.4.1.1
     * 
     * @return witsmlObjects - list of AbstractWitsmlObjects parsed from rawXml
     */
    public static List<AbstractWitsmlObject> parseWellObject(
        String rawXML,
        String version
    ) throws Exception {
        List<AbstractWitsmlObject> witsmlObjects = new ArrayList<AbstractWitsmlObject>();
        
        // handle version 1.3.1.1
        if ("1.3.1.1".equals(version)) {
            com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells objs = WitsmlMarshal.deserialize(
                rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells.class
            );
            for (com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell obj : objs.getWell()) {
                witsmlObjects.add(obj);
            } 

        // handle version 1.4.1.1
        } else if ("1.4.1.1".equals(version)) {
            com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells objs = WitsmlMarshal.deserialize(
                rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells.class
            );
            for (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell obj : objs.getWell()) {
                witsmlObjects.add(obj);
            } 

        } else {
            throw new Exception("unsupported witsml version " + version);
        }

        // return the objects
        return witsmlObjects;
    }

    /**
     * this method parses wellbore objects
     * 
     * @param rawXML - string value with the raw xml to parse
     * @param version - string value with witsml version of rawXML: 1.3.1.1 or 1.4.1.1
     * 
     * @return witsmlObjects - list of AbstractWitsmlObjects parsed from rawXml
     */
    public static List<AbstractWitsmlObject> parseWellboreObject(
        String rawXML,
        String version
    ) throws Exception {
        List<AbstractWitsmlObject> witsmlObjects = new ArrayList<AbstractWitsmlObject>();
        
        // handle version 1.3.1.1
        if ("1.3.1.1".equals(version)) {
            com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores objs = WitsmlMarshal.deserialize(
                rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores.class
            );
            for (com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore obj : objs.getWellbore()) {
                witsmlObjects.add(obj);
            } 

        // handle version 1.4.1.1
        } else if ("1.4.1.1".equals(version)) {
            com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores objs = WitsmlMarshal.deserialize(
                rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores.class
            );
            for (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore obj : objs.getWellbore()) {
                witsmlObjects.add(obj);
            } 

        } else {
            throw new Exception("unsupported witsml version " + version);
        }

        // return the objects
        return witsmlObjects;
    }

    /**
     * this method parses AbstractWitsmlObjects object from the given 
     * params and returns them as a list.
     * 
     * @param objectType - string, one of "well", "wellbore", "log", or "trajectory"
     * @param rawXML - string, contains the raw xml to parse
     * @param version - string, one of "1.3.1.1" or "1.4.1.1"
     */
    public static List<AbstractWitsmlObject> parse (
        String objectType, 
        String rawXML,
        String version
    ) throws Exception {
        //parse the object
        LOG.info("Parsing witsml object");
        switch(objectType) { 
            case "log": 
                return parseLogObject(rawXML, version);
            case "trajectory": 
                return parseTrajectoryObject(rawXML, version);
            case "well": 
                return parseWellObject(rawXML, version);
            case "wellbore": 
                return parseWellboreObject(rawXML, version);
            default: 
                throw new WitsmlException("unsupported witsml object type: " + objectType); 
        }
    } 
}