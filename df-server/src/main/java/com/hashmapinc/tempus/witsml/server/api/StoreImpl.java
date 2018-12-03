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
import com.hashmapinc.tempus.WitsmlObjects.v1311.Data;
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.WitsmlObjectParser;
import com.hashmapinc.tempus.witsml.WitsmlUtil;
import com.hashmapinc.tempus.witsml.server.WitsmlApiConfig;
import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetCapResponse;
import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetFromStoreResponse;
import com.hashmapinc.tempus.witsml.server.api.model.cap.DataObject;
import com.hashmapinc.tempus.witsml.server.api.model.cap.ServerCap;
import com.hashmapinc.tempus.witsml.valve.IValve;
import com.hashmapinc.tempus.witsml.valve.ValveFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jws.WebService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@WebService(serviceName = "StoreSoapBinding", portName = "StoreSoapBindingSoap",
        targetNamespace = "http://www.witsml.org/wsdl/120",
        endpointInterface = "com.hashmapinc.tempus.witsml.server.api.IStore")
public class StoreImpl implements IStore {

    private static final Logger LOG = Logger.getLogger(StoreImpl.class.getName());

    private ServerCap cap;
    private WitsmlApiConfig witsmlApiConfigUtil;
    private IValve valve;
    @Value("${valve.name}")
    private String valveName;

    @Autowired
    private void setServerCap(ServerCap cap){
        this.cap = cap;
    }

    @Autowired
    private void setWitsmlApiConfig(WitsmlApiConfig witsmlApiConfigUtil){
        this.witsmlApiConfigUtil = witsmlApiConfigUtil;
    }

    @Value("${wmls.version:7}")
    private String version;

    @Value("#{${valveprop}}")
    private Map<String,String> valveProps;

    @PostConstruct
    private void setValve(){
        // get the valve
        valve = ValveFactory.buildValve(valveName, valveProps);

        //=====================================================================
        // update the cap with this valve's capabililies
        //=====================================================================
        // get the valve capabilities
        Map<String, AbstractWitsmlObject[]> valveCaps = valve.getCap();
        LOG.info("Got the following capabilities from valve: " + valveCaps.toString());

        // populate the cap object from the valveCaps
        for (String key : valveCaps.keySet()) {
            // get list of data objects
            List<AbstractWitsmlObject> supportedAbstractObjects = Arrays.asList(valveCaps.get(key));
            List<DataObject> supportedDataObjects = supportedAbstractObjects.stream().map((awo) -> {
                DataObject dataObject = new DataObject();
                dataObject.setName(awo.getObjectType());
                return dataObject;
            }).collect(Collectors.toList());

            // add function to cap
            this.cap.addFunction(key, supportedDataObjects);
        }
        // =====================================================================
    }

    @Override
    public int addToStore(
        String WMLtypeIn,
        String XMLin,
        String OptionsIn, 
        String CapabilitiesIn
    ) {
        LOG.info("Executing addToStore");
        
        // try to add to store
        List<AbstractWitsmlObject> witsmlObjects;
        String uid;
        try {
            // build the query context
            Map<String,String> optionsMap = WitsmlUtil.parseOptionsIn(OptionsIn);
            String version = WitsmlUtil.getVersionFromXML(XMLin);
            witsmlObjects = WitsmlObjectParser.parse(WMLtypeIn, XMLin, version);
            ValveUser user = (ValveUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            QueryContext qc = new QueryContext(
                version, 
                WMLtypeIn, 
                optionsMap, 
                XMLin, 
                witsmlObjects, 
                user.getUserName(),
                user.getPassword()
            );

            // handle each object
            uid = valve.createObject(qc);
            LOG.info(
                "Successfully added object: " + witsmlObjects.toString()
            );
        } catch (Exception e) {
            //TODO: handle exception
            LOG.warning(
                "could not add witsml object to store: \n" +
                "Error: " + e
            );

            return -1; // TODO: Proper error codes
        }

        LOG.info("Successfully added object: " + witsmlObjects.get(0).toString());

        return 1; // TODO: Proper success codes
    }

    @Override
    public String getVersion() {
        LOG.info("Executing GetVersion");
        return version;
    }

    @Override
    public WMLS_GetCapResponse getCap(String OptionsIn) {
        LOG.info("Executing GetCap");
        String requestedVersion = OptionsIn.substring(OptionsIn.lastIndexOf("=") +1);
        WMLS_GetCapResponse resp = new WMLS_GetCapResponse();
        resp.setSuppMsgOut("");
        try {
            // get cap string and populate response data
            String data = cap.getWitsmlObject(requestedVersion);
            resp.setCapabilitiesOut(data);
            resp.setResult((short)1);
        } catch (Exception e) {
            resp.setResult((short)-424);
            LOG.info("Exception in generating GetCap response: " + e.getMessage());
        }
        return resp;
    }

    @Override
    public String getBaseMsg(Short returnValueIn) {
        LOG.info("Executing GetBaseMsg");

        String errMsg = witsmlApiConfigUtil.getProperty("basemessages." + returnValueIn);
        if (errMsg == null){
            errMsg = witsmlApiConfigUtil.getProperty("basemessages.-999");
        }
        return errMsg;
    }

    @Override
    public WMLS_GetFromStoreResponse getFromStore(
        String WMLtypeIn, 
        String QueryIn, 
        String OptionsIn, 
        String CapabilitiesIn
    ) {
        LOG.info("Executing GetFromStore");
        WMLS_GetFromStoreResponse resp = new WMLS_GetFromStoreResponse();
        LOG.info(valve.getName());

        // try to deserialize
        List<AbstractWitsmlObject> witsmlObjects;
        String clientVersion;
        try {
            clientVersion = WitsmlUtil.getVersionFromXML(QueryIn);
            witsmlObjects = WitsmlObjectParser.parse(WMLtypeIn, QueryIn, clientVersion);
        } catch (Exception e) {
            // TODO: handle exception
            LOG.warning("could not deserialize witsml object: \n" + 
                        "WMLtypeIn: " + WMLtypeIn + " \n" + 
                        "QueryIn: " + QueryIn + " \n" + 
                        "OptionsIn: " + OptionsIn + " \n" + 
                        "CapabilitiesIn: " + CapabilitiesIn
            );
            return resp; // TODO: proper error handling should go here
        }

        // try to query
        try {
            // construct query context
            Map<String,String> optionsMap = WitsmlUtil.parseOptionsIn(OptionsIn);
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String password = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
            QueryContext qc = new QueryContext(
                clientVersion,
                WMLtypeIn,
                optionsMap,
                QueryIn,
                witsmlObjects,
                username,
                password
            );

            // populate response
            resp.setSuppMsgOut("");
            resp.setResult((short)1);
            resp.setXMLout("");
        } catch (Exception e) {
            resp.setResult((short)-425);
            LOG.warning("Exception in generating GetFromStore response: " + e.getMessage());
        }
        return resp;
    }

}

