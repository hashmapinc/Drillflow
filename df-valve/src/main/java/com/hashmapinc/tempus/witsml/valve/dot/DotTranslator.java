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

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

/**
 * ABANDON ALL HOPE, YE WHO ENTER HERE
 */
public class DotTranslator {
    private static final Logger LOG = Logger.getLogger(DotTranslator.class.getName());

    /**
     * This function serializes the object to a 1.4.1.1 JSON string
     * @param obj - object to serialize
     * @return jsonString - String serialization of a JSON version of the 1.4.1.1 witsml object
     */
    public static String get1411JSONString(AbstractWitsmlObject obj) {
        LOG.info("Getting 1.4.1.1 json string for object: " + obj.toString());
        return obj.getJSONString("1.4.1.1");
    }

    /**
     * returns a valid 1311 AbstractWitsmlObject
     * @param obj1411 - 1411 AbstractWitsmlObject to convert
     */
    // TODO: delete this method and use the AbstractWitsmlObject.getXMLString method when WOL fixes the namespace bug.
    public static AbstractWitsmlObject get1311WitsmlObject(
        AbstractWitsmlObject obj1411
    ) throws ValveException {
        LOG.info("converting to 1311 from 1411 object" + obj1411.toString());

        // get 1311 string
        String xml1311 = obj1411.getXMLString("1.3.1.1");

        // convert to 1311 object
        try {
            switch (obj1411.getObjectType()) { //TODO: support log and trajectory
                case "well":
                    return ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells) WitsmlMarshal.deserialize(
                        xml1311, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells.class)
                    ).getWell().get(0);
                case "wellbore":
                    return ((com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores) WitsmlMarshal.deserialize(
                        xml1311, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores.class)
                    ).getWellbore().get(0);
                default:
                    throw new ValveException("unsupported object type: " + obj1411.getObjectType());
            }
        } catch (Exception e) {
            throw new ValveException(e.getMessage());
        }
    }

    /**
     * merges response into query and returns the parsed 1.4.1.1 object
     * 
     * @param query    - JSON object representing the query
     * @param response - JSON object representing the response from DoT
     * @return obj - parsed abstract object
     */
    public static AbstractWitsmlObject translateQueryResponse(
        JSONObject query, 
        JSONObject response,
        String objectType
    ) throws ValveException {
        // Merge the 2 objects
        JSONObject result = Util.merge(query,response); // WARNING: this method modifies query internally

        // convert the queryJSON back to valid xml
        LOG.info("Converting merged query JSON to valid XML string");
        try {
            switch (objectType) { // TODO: support log and trajectory
                case "well":
                    return WitsmlMarshal.deserializeFromJSON(
                        result.toString(), com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class
                    );
                case "wellbore":
                    return WitsmlMarshal.deserializeFromJSON(
                        result.toString(), com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore.class
                    );
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
        if (0 == witsmlObjects.size()) {
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
        if (0 == witsmlObjects.size()) {
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
            default:
                throw new ValveException("Unsupported object type: " + witsmlObjects.get(0).getObjectType());
        }

        return xmlString;
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