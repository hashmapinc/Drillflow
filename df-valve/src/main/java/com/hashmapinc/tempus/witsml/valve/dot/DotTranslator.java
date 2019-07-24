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
import com.hashmapinc.tempus.WitsmlObjects.v1411.*;
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
                case "log":
                    if (wmlObj instanceof com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog) return wmlObj;
                    return LogConverter.convertTo1311((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog) wmlObj);
                case "fluidsReport":
                    if (wmlObj instanceof com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReport) return wmlObj;
                    return FluidsReportConverter.convertTo1311((com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReport) wmlObj);
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

        if ("requested".equals(optionsIn.get("returnElements") )|| "id-only".equals(optionsIn.get("returnElements"))) {
            // Check if the "id-only" case needs to be handled...
            if ("id-only".equals(optionsIn.get("returnElements"))) {
                queryJson = QueryTemplateProvider.getIDOnly(wmlObject.getObjectType());
            }
            result = JsonUtil.merge(queryJson, responseJson).toString();   // WARNING: this method modifies query internally
        }

        // doctor some commonly-butchered json keys
        //TODO: This must be fixed in wol
        result = result.replaceAll("\"dtimTrajStart\":","\"dTimTrajStart\":");
        result = result.replaceAll("\"dtimTrajEnd\":","\"dTimTrajEnd\":");
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
                    ObjTrajectory traj = WitsmlMarshal.deserializeFromJSON(result, ObjTrajectory.class);

                    if("station-location-only".equals(optionsIn.get("returnElements"))
                            || "data-only".equals(optionsIn.get("returnElements"))) {
                        return buildStationOnlyTrajectory(traj);
                    } else if ("header-only".equals(optionsIn.get("returnElements"))) {
                        traj.setTrajectoryStation(null);
                        return traj;
                    }
                    return traj;
                case "log":
                    return WitsmlMarshal.deserializeFromJSON(
                            result, ObjLog.class);
                case "fluidsReport":
                    return WitsmlMarshal.deserializeFromJSON(
                            result, ObjFluidsReport.class);
                default:
                    throw new ValveException("unsupported object type");
            }
        } catch (IOException ioe) {
            throw new ValveException(ioe.getMessage());
        }
    }

    /**
     * This function builds a station-location-only response for trajectory
     * @param traj The full trajectory
     * @return The smaller station-location-only trajectory
     */
    private static ObjTrajectory buildStationOnlyTrajectory(ObjTrajectory traj){
        ObjTrajectory smallTraj = new ObjTrajectory();
        smallTraj.setUid(traj.getUid());
        smallTraj.setUidWell(traj.getUidWell());
        smallTraj.setUidWellbore(traj.getUidWellbore());
        smallTraj.setNameWell(traj.getNameWell());
        smallTraj.setNameWellbore(traj.getNameWellbore());
        smallTraj.setName(traj.getName());
        smallTraj.setObjectGrowing(traj.isObjectGrowing());
        if (traj.getCommonData() != null){
            CsCommonData commonData = new CsCommonData();
            commonData.setDTimLastChange(traj.getCommonData().getDTimLastChange());
        }
        if (traj.getTrajectoryStation() != null) {
            for (CsTrajectoryStation station : traj.getTrajectoryStation()) {
                CsTrajectoryStation smallStation = new CsTrajectoryStation();
                smallStation.setUid(station.getUid());
                smallStation.setDTimStn(station.getDTimStn());
                smallStation.setTypeTrajStation(station.getTypeTrajStation());
                smallStation.setMd(station.getMd());
                smallStation.setTvd(station.getTvd());
                smallStation.setIncl(station.getIncl());
                smallStation.setAzi(station.getAzi());
                smallStation.setLocation(station.getLocation());
                smallTraj.getTrajectoryStation().add(smallStation);
            }
        }
        return smallTraj;
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
     * converts witsmlObjects list of fluidReports to valid
     * witsml XML string based on version and whether
     * the list is empty or not
     * @param witsmlObjects - list of objects to serialize
     * @param version - witsml version to serialize to
     * @return STRING xml serialization result
     * @throws ValveException
     */
    private static String consolidateFluidsReportsToXML(
            ArrayList<AbstractWitsmlObject> witsmlObjects,
            String version
    ) throws ValveException {
        String xml;
        boolean is1411 = "1.4.1.1".equals(version);

        // handle empty list
        if (witsmlObjects == null || 0 == witsmlObjects.size()) {
            try {
                xml = is1411 ?
                        WitsmlMarshal.serialize(new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReports()):
                        WitsmlMarshal.serialize(new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReports());
            } catch (JAXBException jxbe) {
                throw new ValveException("Could not serialize empty fluidsReports object");
            }
        } else {
            // handle non empty list
            xml = is1411 ?
                    consolidate1411FluidsReportsToXML(witsmlObjects) :
                    consolidate1311FluidsReportsToXML(witsmlObjects);
        }

        return xml;
    }

    /**
     * converts witsmlObjects list of trajectories to valid
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
            case "fluidsReport":
                xmlString = consolidateFluidsReportsToXML(witsmlObjects, version);
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
                        (ObjLog) child
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

    private static String consolidate1311FluidsReportsToXML(
            ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReports parent =
                    new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReports();

            // consolidate children
            for (AbstractWitsmlObject child : witsmlObjects) {
                // TODO FIX THIS
                //parent.addFluidReport(
                //        (com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReport) get1311WitsmlObject(child)
                //);
            }

            // return xml
            return WitsmlMarshal.serialize(parent);
        } catch (Exception e ) {
            throw new ValveException(e.getMessage());
        }
    }

    private static String consolidate1411FluidsReportsToXML(
            ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReports parent =
                    new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReports();

            // consolidate children
            // TODO FIX THIS
            for (AbstractWitsmlObject child : witsmlObjects) {
            //    parent.addFluidReport(
            //           (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReport) child
            //    );
            }

            // return xml
            return WitsmlMarshal.serialize(parent);
        } catch (Exception e ) {
            throw new ValveException(e.getMessage());
        }
    }
}
