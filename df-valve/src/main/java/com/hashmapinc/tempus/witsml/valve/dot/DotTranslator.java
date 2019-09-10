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
import com.hashmapinc.tempus.WitsmlObjects.v20.FluidsReport;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import org.json.JSONObject;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class DotTranslator {
    private static final Logger LOG = Logger.getLogger(DotTranslator.class.getName());

    /**
     * returns a valid 1311 AbstractWitsmlObject
     *
     * @param wmlObj - AbstractWitsmlObject to convert
     *
     * @return AbstractWitsmlObject
     */
    public static AbstractWitsmlObject get1311WitsmlObject(AbstractWitsmlObject wmlObj)
            throws ValveException {
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
                case "fluidsreport":
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
     * merges response object as required
     *
     * @param wmlObject          - query object
     * @param jsonResponseString - JSON response from DoT
     * @param optionsIn          - options provided by the query
     *
     * @return AbstractWitsmlObject -
     */
    public static AbstractWitsmlObject translateQueryResponse( AbstractWitsmlObject wmlObject,
                                                               String jsonResponseString,
                                                               Map<String, String> optionsIn )
            throws ValveException,
            DatatypeConfigurationException
    {
        JSONObject queryJson;

        if ("1.4.1.1".equals(wmlObject.getVersion())) {
            queryJson = new JSONObject(wmlObject.getJSONString("1.4.1.1"));
        } else {
            queryJson = new JSONObject(wmlObject.getJSONString("1.3.1.1"));
        }

        // ********************* manipulate JSON response string from DoT ********************* //
        JSONObject responseJson = new JSONObject(jsonResponseString);

        String result = responseJson.toString();

        if ( "requested".equals(optionsIn.get("returnElements")) ||
                "id-only".equals(optionsIn.get("returnElements")) )
        {
            // Check if the "id-only" case needs to be handled...
            if ("id-only".equals(optionsIn.get("returnElements"))) {
                queryJson = QueryTemplateProvider.getIDOnly(wmlObject.getObjectType());
            }
            // WARNING: this method modifies the query internally
            result = JsonUtil.merge(queryJson, responseJson).toString();
        }

        // doctor some commonly-butchered json keys
        //TODO: This must be fixed in wol
        result = result.replaceAll("\"dtimTrajStart\":","\"dTimTrajStart\":");
        result = result.replaceAll("\"dtimTrajEnd\":","\"dTimTrajEnd\":");
        result = result.replaceAll("\"dtimStn\":","\"dTimStn\":");
        // ************************************************************************************ //

        // convert the JSON response back to valid xml (it is already in the correct version)
        LOG.finest("Converting DoT JSON response to valid XML string");

        try {
            switch ( wmlObject.getObjectType() )
            {
                case "well":
                    return WitsmlMarshal.deserializeFromJSON( result, ObjWell.class);

                case "wellbore":
                    return WitsmlMarshal.deserializeFromJSON( result, ObjWellbore.class);

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
                    if ("1.3.1.1".equals(wmlObject.getVersion())) {
                        return WitsmlMarshal.deserializeFromJSON(result, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog.class);
                    } else {
                        return WitsmlMarshal.deserializeFromJSON(result, ObjLog.class);
                    }

                default:
                    throw new ValveException("unsupported object type");
            }

        } catch (IOException ioe) {
            throw new ValveException(ioe.getMessage());
        }
    }

    /**
     * creates 2.0 response object as required
     *
     * @param jsonResponseString - JSON response from DoT
     *
     * @return AbstractWitsmlObject - converted FluidsReport object to either 1.3.1.1
     *                                or 1.4.1.1 (null if not one of these versions)
     */
    public static AbstractWitsmlObject createFsRQueryResponse( String jsonResponseString,
                                                               AbstractWitsmlObject witsmlObject )
                                                                            throws  ValveException,
                                                                                    DatatypeConfigurationException
    {
        String version = witsmlObject.getVersion();
        String wellUID = witsmlObject.getParentUid();
        String wellBoreUID = witsmlObject.getGrandParentUid();

        // convert the JSON response back to valid xml
        LOG.finest("Converting DoT JSON 2.0 response to valid XML string");
        try {
            // all responses from DoT for Fluids Reports are 2.0
            // TODO --
            // jsonResponseString has: "hardnessCA":{"uom":"ppm","value":3200.0}
            // why doesn't WitsmlMarshal.deserializeFromJSON put hardnessCA into fluidsReport (it is null after the marshal)
            FluidsReport fluidsReport = WitsmlMarshal.deserializeFromJSON(jsonResponseString, FluidsReport.class);
            AbstractWitsmlObject convertedFluidsReport;
            if ("1.4.1.1".equals(version)) {
                convertedFluidsReport = FluidsReportConverter.convertTo1411(fluidsReport);
                ((ObjFluidsReport) convertedFluidsReport).setUidWell(wellUID);
                ((ObjFluidsReport) convertedFluidsReport).setUidWellbore(wellBoreUID);
                return convertedFluidsReport;
            } else if ("1.3.1.1".equals(version)) {
                convertedFluidsReport = FluidsReportConverter.convertTo1311(fluidsReport);
                ((ObjFluidsReport) convertedFluidsReport).setUidWell(wellUID);
                ((ObjFluidsReport) convertedFluidsReport).setUidWellbore(wellBoreUID);
                return convertedFluidsReport;
            } else {
                // no conversion is required for 2.0
                // TODO but 2.0 needs well & wellBore UID's; trace to the caller to set these values
                return null;
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
     * converts witsmlObjects list of fluidsreports to valid
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
                throw new ValveException("Could not serialize empty trajectories object");
            }
        } else {
            // handle non empty list
            xml = is1411 ?
                    consolidate1411FluidReportsToXML(witsmlObjects) :
                    consolidate1311FluidReportsToXML(witsmlObjects);
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
        switch (objectType.toLowerCase()) {
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
            case "fluidsreport":
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

    private static String consolidate1311FluidReportsToXML(
            ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReports parent =
                    new com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReports();

            // consolidate children
            for (AbstractWitsmlObject child : witsmlObjects) {
                parent.addFluidReport(
                        (com.hashmapinc.tempus.WitsmlObjects.v1311.ObjFluidsReport) get1311WitsmlObject(child)
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

    private static String consolidate1411FluidReportsToXML(
            ArrayList<AbstractWitsmlObject> witsmlObjects
    ) throws ValveException {
        try {
            // get parent object from first child
            com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReports parent =
                    new com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReports();

            // consolidate children
            for (AbstractWitsmlObject child : witsmlObjects) {
                parent.addFluidReport(
                        (com.hashmapinc.tempus.WitsmlObjects.v1411.ObjFluidsReport) child
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
}

