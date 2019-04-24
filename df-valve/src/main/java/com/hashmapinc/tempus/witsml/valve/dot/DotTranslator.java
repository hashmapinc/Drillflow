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

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.*;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore;
import com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import org.json.JSONObject;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class DotTranslator {
    private static final Logger LOG = Logger.getLogger(DotTranslator.class.getName());

    /**
     * returns a valid 1311 AbstractWitsmlObject
     * @param wmlObj - AbstractWitsmlObject to convert
     */
    public static AbstractWitsmlObject get1311WitsmlObject(
        AbstractWitsmlObject wmlObj
    ) throws ValveException {
        try {
            switch (wmlObj.getObjectType()) {
                case "well":
                    if (wmlObj instanceof com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell) return wmlObj;
                    return WellConverter.convertTo1311((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) wmlObj);
                case "wellbore":
                    if (wmlObj instanceof com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore) return wmlObj;
                    return WellboreConverter.convertTo1311((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) wmlObj);
                case "trajectory":
                    if (wmlObj instanceof com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory) return wmlObj;
                    return TrajectoryConverter.convertTo1311((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) wmlObj);
                default:
                    throw new ValveException("unsupported object type: " + wmlObj.getObjectType());
            }
        } catch (Exception e) {
            throw new ValveException(e.getMessage());
        }
    }

    /**
     * merges response 1.4.1.1 object
     * 
     * @param wmlObject - object queried for
     * @param jsonResponseString - json string response from request
     * @return obj - parsed abstract object
     */
    public static AbstractWitsmlObject translateQueryResponse(
            // One of the properties is Object Type.
        AbstractWitsmlObject wmlObject,
        String jsonResponseString,
        Map<String, String> optionsIn
    ) throws ValveException {
        // get JSON objects
        JSONObject queryJson = new JSONObject(wmlObject.getJSONString("1.4.1.1"));
        JSONObject responseJson = new JSONObject(jsonResponseString);

        String result = responseJson.toString();

        // if options does not contain "returnElements" key OR "all" is not specified within it...
        if (!optionsIn.containsKey("returnElements") || !("all".equals(optionsIn.get("returnElements")))) {

            // Check if the "id-only" case needs to be handled...
            if ("id-only".equals(optionsIn.get("returnElements"))) {
                queryJson = QueryTemplateProvider.getIDOnly(wmlObject.getObjectType());
            }

            // Perform the selective merge since "all" was not specified
            //                             OR    the query JSON has been changed for "id-only" case
            //                             OR    the "returnElements" was not given in "optionsIn" --
            result = JsonUtil.merge(queryJson, responseJson).toString(); // WARNING: this method modifies query internally
        }

        // doctor some commonly-butchered json keys
        result = result.replaceAll("\"dtimStn\":","\"dTimStn\":");

        // convert the queryJSON back to valid xml
        LOG.finest("Converting merged query JSON to valid XML string");
        try {
            switch (wmlObject.getObjectType()) {
                case "well":
                    return WitsmlMarshal.deserializeFromJSON(
                        result, ObjWell.class);
                case "wellbore":
                    return WitsmlMarshal.deserializeFromJSON(
                        result, ObjWellbore.class);
                case "trajectory":
                    return WitsmlMarshal.deserializeFromJSON(
                        result, ObjTrajectory.class);
                case "log":
                    return WitsmlMarshal.deserializeFromJSON(
                            result, ObjLog.class);
                default:
                    throw new ValveException("unsupported object type");
            }
        } catch (IOException ioe) {
            throw new ValveException(ioe.getMessage());
        }
    }

    /**
     * converts witsmlObjects list of wells to valid
     * witsml XML string based on version and whether
     * the list is empty or not
     * @param witsmlObjects - list of wells to serialize. Should be 1 or 0 wells
     * @param version - witsml version to serialize to
     * @return STRING xml serialization result
     * @throws ValveException
     */
    private static String consolidateWellsToXML(
        ArrayList<AbstractWitsmlObject> witsmlObjects,
        String version
    ) throws ValveException {
        String xml;
        boolean is1411 = "1.4.1.1".equals(version);

        // handle empty well list
        if (witsmlObjects == null || 0 == witsmlObjects.size()) {
            try {
                xml = is1411 ?
                    WitsmlMarshal.serialize(new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells()) :
                    WitsmlMarshal.serialize(new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells());
            } catch (JAXBException jxbe) {
                throw new ValveException("Could not serialize empty wells object");
            }
        } else {
            // handle non empty well list
            xml = is1411 ? 
                consolidate1411WellsToXML(witsmlObjects) : 
                consolidate1311WellsToXML(witsmlObjects);
        }

        return xml;
    }

    /**
     * converts witsmlObjects list of wellbores to valid
     * witsml XML string based on version and whether
     * the list is empty or not
     * @param witsmlObjects - list of objects to serialize
     * @param version - witsml version to serialize to
     * @return STRING xml serialization result
     * @throws ValveException
     */
    private static String consolidateWellboresToXML(
        ArrayList<AbstractWitsmlObject> witsmlObjects,
        String version
    ) throws ValveException {
        String xml;
        boolean is1411 = "1.4.1.1".equals(version);

        // handle empty list
        if (witsmlObjects == null || 0 == witsmlObjects.size()) {
            try {
                xml = is1411 ?
                    WitsmlMarshal.serialize(new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores()):
                    WitsmlMarshal.serialize(new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores());
            } catch (JAXBException jxbe) {
                throw new ValveException("Could not serialize empty wellbores object");
            }
        } else {
            // handle non empty list
            xml = is1411 ?
                consolidate1411WellboresToXML(witsmlObjects) :
                consolidate1311WellboresToXML(witsmlObjects);
        }

        return xml;
    }

    /**
     * converts witsmlObjects list of wellbores to valid
     * witsml XML string based on version and whether
     * the list is empty or not
     * @param witsmlObjects - list of objects to serialize
     * @param version - witsml version to serialize to
     * @return STRING xml serialization result
     * @throws ValveException
     */
    private static String consolidateTrajectoriesToXML(
            ArrayList<AbstractWitsmlObject> witsmlObjects,
            String version
    ) throws ValveException {
        String xml;
        boolean is1411 = "1.4.1.1".equals(version);

        // handle empty list
        if (witsmlObjects == null || 0 == witsmlObjects.size()) {
            try {
                xml = is1411 ?
                    WitsmlMarshal.serialize(new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys()):
                    WitsmlMarshal.serialize(new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectorys());
            } catch (JAXBException jxbe) {
                throw new ValveException("Could not serialize empty trajectories object");
            }
        } else {
            // handle non empty list
            xml = is1411 ?
                    consolidate1411TrajectoriesToXML(witsmlObjects) :
                    consolidate1311TrajectoriesToXML(witsmlObjects);
        }

        return xml;
    }

    // for consolidateLogsToXML

    /**
     * converts witsmlObjects list of logs to valid
     * witsml XML string based on version and whether
     * the list is empty or not
     * @param witsmlObjects - list of objects to serialize
     * @param version - witsml version to serialize to
     * @return STRING xml serialization result
     * @throws ValveException
     */
    private static String consolidateLogsToXML(
            ArrayList<AbstractWitsmlObject> witsmlObjects,
            String version
    ) throws ValveException {
        String xml;
        boolean is1411 = "1.4.1.1".equals(version);

        // handle empty list
        if (witsmlObjects == null || 0 == witsmlObjects.size()) {
            try {
                xml = is1411 ?
                        WitsmlMarshal.serialize(new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs()):
                        WitsmlMarshal.serialize(new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs());
            } catch (JAXBException jxbe) {
                throw new ValveException("Could not serialize empty trajectories object");
            }
        } else {
            // handle non empty list
            xml = is1411 ?
                    consolidate1411LogsToXML(witsmlObjects) :
                    consolidate1311LogsToXML(witsmlObjects);
        }

        return xml;
    }

    /**
     * Consolidates each object under 1 parent and serializes
     * the consolidated object into an XML string in the proper
     * WITSML version format
     *
     * @param witsmlObjects - list of objects to consolidate
     * @param version - witsml version to serialize to
     * @param objectType - type of objects being consolidated (well, wellbore, trajectory, or log)
     * @return = serialized parent object in requested WITSML version format
     * @throws ValveException
     */
    public static String consolidateObjectsToXML(
        ArrayList<AbstractWitsmlObject> witsmlObjects,
        String version,
        String objectType
    ) throws ValveException {
        // validate version
        if(!"1.3.1.1".equals(version) && !"1.4.1.1".equals(version)) {
            throw new ValveException("Unsupported client version <" + version + "> in DoT GET");
        }

        // get xmlString
        String xmlString;
        switch (objectType) {
            case "well": // no consolidation needed for wells
                xmlString = consolidateWellsToXML(witsmlObjects, version);
                break;
            case "wellbore": // no consolidation needed for wells
                xmlString = consolidateWellboresToXML(witsmlObjects, version);
                break;
            case "trajectory":
                xmlString = consolidateTrajectoriesToXML(witsmlObjects, version);
                break;
            case "log":
                xmlString = consolidateLogsToXML(witsmlObjects, version);
                break;
            default:
                throw new ValveException("Unsupported object type: " + witsmlObjects.get(0).getObjectType());
        }

        return xmlString;
    }

    private static String consolidate1311TrajectoriesToXML(
        ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectorys parent =
                    new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectorys();

            // consolidate children
            for (AbstractWitsmlObject child : witsmlObjects) {
                parent.addTrajectory(
                    (com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory) get1311WitsmlObject(child)
                );
            }

            // return xml
            return WitsmlMarshal.serialize(parent);
        } catch (Exception e ) {
            throw new ValveException(e.getMessage());
        }
    }

    // code for consolidate1311LogsToXML

    private static String consolidate1311LogsToXML(
            ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs parent =
                    new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs();

            // consolidate children
            for (AbstractWitsmlObject child : witsmlObjects) {
                parent.addLog(
                        (com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) get1311WitsmlObject(child)
                );
            }

            // return xml
            return WitsmlMarshal.serialize(parent);
        } catch (Exception e ) {
            throw new ValveException(e.getMessage());
        }
    }

    private static String consolidate1411TrajectoriesToXML(
        ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys parent =
                    new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys();

            // consolidate children
            for (AbstractWitsmlObject child : witsmlObjects) {
                parent.addTrajectory(
                        (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory) child
                );
            }

            // return xml
            return WitsmlMarshal.serialize(parent);
        } catch (Exception e ) {
            throw new ValveException(e.getMessage());
        }
    }

    //code for consolidate1411LogsToXML

    private static String consolidate1411LogsToXML(
            ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs parent =
                    new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs();

            // consolidate children
            for (AbstractWitsmlObject child : witsmlObjects) {
                parent.addLog(
                        (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) child
                );
            }

            // return xml
            return WitsmlMarshal.serialize(parent);
        } catch (Exception e ) {
            throw new ValveException(e.getMessage());
        }
    }

    private static String consolidate1311WellsToXML(
        ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells parent =
                    new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells();

            // consolidate children
            for (AbstractWitsmlObject child : witsmlObjects) {
                parent.addWell(
                        (com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell) get1311WitsmlObject(child)
                );
            }

            // return xml
            return WitsmlMarshal.serialize(parent);
        } catch (Exception e ) {
            throw new ValveException(e.getMessage());
        }
    }

    private static String consolidate1411WellsToXML(
        ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells parent =
                new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells();

            // consolidate children
            for (AbstractWitsmlObject child : witsmlObjects) {
                parent.addWell(
                    (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell) child
                );
            }

            // return xml
            return WitsmlMarshal.serialize(parent);
        } catch (Exception e ) {
            throw new ValveException(e.getMessage());
        }
    }

    private static String consolidate1311WellboresToXML(
        ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores parent = 
                new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores();

            // consolidate children
            for (AbstractWitsmlObject child : witsmlObjects) {
                parent.addWellbore(
                    (com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore) get1311WitsmlObject(child)
                );
            }

            // return xml
            return WitsmlMarshal.serialize(parent);
        } catch (Exception e ) {
            throw new ValveException(e.getMessage());
        }
    }

    private static String consolidate1411WellboresToXML(
        ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores parent = 
                new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores();

            // consolidate children
            for (AbstractWitsmlObject child : witsmlObjects) {
                parent.addWellbore(
                    (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore) child
                );
            }

            // return xml
            return WitsmlMarshal.serialize(parent);
        } catch (Exception e ) {
            throw new ValveException(e.getMessage());
        }
    }
}
