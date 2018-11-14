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
import org.springframework.stereotype.Service;
import java.util.logging.Logger;


/** 
 * StoreObject is a wrapper class around the 4 possible store objects
 * supported by Drillflow right now: Well, WellBore, Trajectory, and 
 * Log (depth and time)
 */
@Service
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
    com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs log1311 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs log1411 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectorys trajectory1311 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys trajectory1411 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells well1311 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells well1411 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores wellbore1311 = null;
    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores wellbore1411 = null;
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
     * this method handles log objects after instance fields are
     * populated by the parseInPlace function
     */
    public void parseLogObject() throws Exception {
        if (this.version.equals("1.3.1.1")) {
            this.log1311 = WitsmlMarshal.deserialize(
                this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjLogs.class
            );
        } else if (this.version.equals("1.4.1.1")) {
            this.log1411 = WitsmlMarshal.deserialize(
                this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLogs.class
            );
        } else {
            throw new Exception("unsupported witsml version " + this.version);
        }
    }

    /**
     * this method handles trajectory objects after instance fields are
     * populated by the parseInPlace function
     */
    public void parseTrajectoryObject() throws Exception {
        if (this.version.equals("1.3.1.1")) {
            this.trajectory1311 = WitsmlMarshal.deserialize(
                this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjTrajectorys.class
            );
        } else if (this.version.equals("1.4.1.1")) {
            this.trajectory1411 = WitsmlMarshal.deserialize(
                this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjTrajectorys.class
            );
        } else {
            throw new Exception("unsupported witsml version " + this.version);
        }
    }

    /**
     * this method handles well objects after instance fields are
     * populated by the parseInPlace function
     */
    public void parseWellObject() throws Exception {
        if (this.version.equals("1.3.1.1")) {
            this.well1311 = WitsmlMarshal.deserialize(
                this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWells.class
            );
        } else if (this.version.equals("1.4.1.1")) {
            this.well1411 = WitsmlMarshal.deserialize(
                this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWells.class
            );
        } else {
            throw new Exception("unsupported witsml version " + this.version);
        }
    }

    /**
     * this method handles wellbore objects after instance fields are
     * populated by the parseInPlace function
     */
    public void parseWellboreObject() throws Exception {
        if (this.version.equals("1.3.1.1")) {
            this.wellbore1311 = WitsmlMarshal.deserialize(
                this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1311.ObjWellbores.class
            );
        } else if (this.version.equals("1.4.1.1")) {
            this.wellbore1411 = WitsmlMarshal.deserialize(
                this.rawXML, com.hashmapinc.tempus.WitsmlObjects.v1411.ObjWellbores.class
            );
        } else {
            throw new Exception("unsupported witsml version " + this.version);
        }
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
     */
    public void parseInPlace (
        String objectType_, 
        String rawXML_,
        String version_
    ) throws Exception {
        // empty this object if it is not empty
        if (!this.isEmpty) {
            empty();
        }

        // assign field values
        this.objectType = objectType_;
        this.rawXML = rawXML_;
        this.version = version_;

        //parse the object
        LOG.info("Parsing witsml object");
        switch(this.objectType) { 
            case "log": 
                parseLogObject();
                break; 
            case "trajectory": 
                parseTrajectoryObject(); 
                break; 
            case "well": 
                parseWellObject();
                break; 
            case "wellbore": 
                parseWellboreObject();
                break; 
            default: 
                throw new Exception("unsupported witsml object type: " + this.objectType); 
        } 

        // parsing was successful.
        LOG.info(
            "Successfully parsed object of type " + 
            this.objectType + 
            " and version " + 
            this.version
        );
    } 
}