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

import com.hashmapinc.tempus.WitsmlObjects.Util.WitsmlMarshal;

import java.util.logging.Logger;

/** 
 * StoreObject is a wrapper class around the 4 possible store objects
 * supported by Drillflow right now: Well, WellBore, Trajectory, and 
 * Log (depth and time)
 */
public class StoreObject {
    // get logger
    private static final Logger LOG = Logger.getLogger(StoreObject.class.getName());

    //=========================================================================
    // Define fields
    //=========================================================================
    public String objectType; // the type of object that has been parsed
    public String rawXML; // the raw xml from which the object was parsed, or ""
    public String version; // version of the witsml object parsed, or ""
    private boolean isEmpty = true; // true if a parsed object is stored

    // only 1 of these objects can ever be non-null
    com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog log1311 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog log1411 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory trajectory1311 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory trajectory1411 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell well1311 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell well1411 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore wellbore1311 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore wellbore1411 = null;
    //=========================================================================

    public StoreObject() {
    }

    /**
     * this method simply resets the class instance's internal state
     * to that of a new instance. It "empties" the instance
     */
    public void empty() {
        LOG.info("emptying StoreObject...");
        // reset the fields to the default state
        this.objectType = "";
        this.rawXML = "";
        this.version = "";
        this.isEmpty = true;
        this.log1311 = null;
        this.log1411 = null;
        this.trajectory1311 = null;
        this.trajectory1411 = null;
        this.well1311 = null;
        this.well1411 = null;
        this.wellbore1311 = null;
        this.wellbore1411 = null;
        return;
    }

    /**
     * this method parses a WitsmlObjects object from the given params
     * and stores it in the corresponding class field.
     * 
     * If this instance is not empty, it is emptied before parsing happens
     * 
     * @param objectType_ - string, one of "well", "wellbore", "log", or "trajectory"
     * @param rawXML_ - string, contains the raw xml to parse
     * @param version_ - string, one of "1.3.1.1" or "1.4.1.1"
     * 
     * @return - boolean indicating error. True = there was an error (this is a C/Golang convention)
     */
    boolean parseInPlace(
        String objectType_, 
        String rawXML_,
        String version_
    ) {
        // empty this object if it is not empty
        if (!this.isEmpty) {
            empty();
        }

        // assign field values
        this.objectType = objectType_;
        this.rawXML = rawXML_;
        this.version = version_;

        // try to parse the object
        LOG.info("Parsing witsml object");
        try {
            // parse a log object
            if (objectType_.equals("log")) {
                if (this.version.equals("1.3.1.1")) {
                    this.log1311 = WitsmlMarshal.deserialize(
                        this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLog.class
                    );
                } else if (this.version.equals("1.4.1.1")) {
                    this.log1411 = WitsmlMarshal.deserialize(
                        this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog.class
                    );
                } else {
                    throw new Exception("unsupported witsml version " + this.version);
                }

            // parse a trajectory object
            } else if (objectType_.equals("trajectory")) {
                if (this.version.equals("1.3.1.1")) {
                    this.trajectory1311 = WitsmlMarshal.deserialize(
                        this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectory.class
                    );
                } else if (this.version.equals("1.4.1.1")) {
                    this.trajectory1411 = WitsmlMarshal.deserialize(
                        this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectory.class
                    );
                } else {
                    throw new Exception("unsupported witsml version " + this.version);
                }

            // parse a well object
            } else if (objectType_.equals("well")) {
                if (this.version.equals("1.3.1.1")) {
                    this.well1311 = WitsmlMarshal.deserialize(
                        this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWell.class
                    );
                } else if (this.version.equals("1.4.1.1")) {
                    this.well1411 = WitsmlMarshal.deserialize(
                        this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWell.class
                    );
                } else {
                    throw new Exception("unsupported witsml version " + this.version);
                }

            // parse a wellbore object
            } else if (objectType_.equals("wellbore")) {
                if (this.version.equals("1.3.1.1")) {
                    this.wellbore1311 = WitsmlMarshal.deserialize(
                        this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbore.class
                    );
                } else if (this.version.equals("1.4.1.1")) {
                    this.wellbore1411 = WitsmlMarshal.deserialize(
                        this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbore.class
                    );
                } else {
                    throw new Exception("unsupported witsml version " + this.version);
                }

            // handle an unsupported witsml object
            } else {
                throw new Exception("unsupported witsml object type: " + this.objectType);
            }
        } catch (Exception e) {
            //TODO: handle exception
            empty();
            LOG.warning("Could not parse xml: " + this.rawXML);
            return true; // true = there was an error
        }

        // parsing was successful.
        LOG.info(
            "Successfully parsed object of type " + 
            this.objectType + 
            " and version " + 
            this.version
        );
        return false; // false = there was no error
    } 
}