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
import com.hashmapinc.tempus.witsml.QueryContext;
import com.hashmapinc.tempus.witsml.WitsmlObjectParser;
import com.hashmapinc.tempus.witsml.WitsmlUtil;
import com.hashmapinc.tempus.witsml.server.WitsmlApiConfig;
import com.hashmapinc.tempus.witsml.server.api.model.*;
import com.hashmapinc.tempus.witsml.server.api.model.cap.DataObject;
import com.hashmapinc.tempus.witsml.server.api.model.cap.ServerCap;
import com.hashmapinc.tempus.witsml.valve.IValve;
import com.hashmapinc.tempus.witsml.valve.ValveException;
import com.hashmapinc.tempus.witsml.valve.ValveFactory;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.feature.Features;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
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
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
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

    private String getExchangeId(){
        Message message = PhaseInterceptorChain.getCurrentMessage();
        if (message == null){
            return "99999999-9999-9999-9999-999999999999"; // TODO: is this smart? (I don't know it isn't, just checking)
        }
        return message.getExchange().get(LogEvent.KEY_EXCHANGE_ID).toString();
    }

    @Override
    public WMLS_AddToStoreResponse addToStore(
        String WMLtypeIn,
        String XMLin,
        String OptionsIn,
        String CapabilitiesIn
    ) {
        LOG.info("Executing addToStore for query <" + getExchangeId() + ">");
        // try to add to store
        List<AbstractWitsmlObject> witsmlObjects;
        String uid;
        WMLS_AddToStoreResponse response = new WMLS_AddToStoreResponse();
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
                user.getPassword(),
                getExchangeId()
            );

            // handle each object
            uid = valve.createObject(qc);
            LOG.info(
                "Successfully added object: " + witsmlObjects.toString()
            );
        } catch (ValveException ve) {
            //TODO: handle exception
            LOG.warning("ValveException in addToStore: " + ve.getMessage());
            response.setSuppMsgOut(ve.getMessage());
            response.setResult((short)-1);
            return response;
        } catch (Exception e) {
            //TODO: handle exception
            LOG.warning(
                "could not add witsml object to store: \n" +
                "Error: " + e
            );
            response.setSuppMsgOut("Error adding to store: " + e.getMessage());
            response.setResult((short)-1);
            return response;
        }

        LOG.info("Successfully added object: " + witsmlObjects.get(0).toString());
        response.setSuppMsgOut(uid);
        response.setResult((short)1);
        return response;
    }

    @Override
    public WMLS_UpdateInStoreResponse updateInStore(
        String WMLtypeIn,
        String XMLin,
        String OptionsIn,
        String CapabilitiesIn
    ) {
        LOG.info("Executing updateInStore");
        // try to update in store
        List<AbstractWitsmlObject> witsmlObjects;
        WMLS_UpdateInStoreResponse response = new WMLS_UpdateInStoreResponse();
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
                    user.getPassword(),
                    getExchangeId()
            );

            // perform update
            valve.updateObject(qc);
        } catch (ValveException ve) {
            //TODO: handle exception
            LOG.warning("ValveException in updateInStore: " + ve.getMessage());
            response.setSuppMsgOut(ve.getMessage());
            response.setResult((short)-1);
            return response;
        } catch (Exception e) {
            //TODO: handle exception
            LOG.warning(
                    "could not add witsml object to store: \n" +
                            "Error: " + e
            );
            response.setSuppMsgOut("Error updating in store: " + e.getMessage());
            response.setResult((short)-1);
            return response;
        }

        LOG.info("Successfully updated object: " + witsmlObjects.toString());
        response.setResult((short)1);
        return response;
    }

    @Override
    public WMLS_DeleteFromStoreResponse deleteFromStore(
        String WMLtypeIn,
        String QueryIn,
        String OptionsIn,
        String CapabilitiesIn
    ) {
        LOG.info("Deleting object from store.");
        WMLS_DeleteFromStoreResponse resp = new WMLS_DeleteFromStoreResponse();
        // set initial ERROR state for resp
        resp.setResult((short) -1);

        // try to deserialize
        List<AbstractWitsmlObject> witsmlObjects;
        try {
            String clientVersion = WitsmlUtil.getVersionFromXML(QueryIn);
            witsmlObjects = WitsmlObjectParser.parse(WMLtypeIn, QueryIn, clientVersion);
        } catch (Exception e) {
            // TODO: handle exception
            LOG.warning("could not deserialize witsml object: \n" +
                    "WMLtypeIn: " + WMLtypeIn + " \n" +
                    "QueryIn: " + QueryIn + " \n" +
                    "OptionsIn: " + OptionsIn + " \n" +
                    "CapabilitiesIn: " + CapabilitiesIn
            );
            resp.setSuppMsgOut("Bad QueryIn. Got error message: " + e.getMessage());
            return resp;
        }

        // try to delete
        try {
            // construct query context
            Map<String,String> optionsMap = WitsmlUtil.parseOptionsIn(OptionsIn);
            ValveUser user = (ValveUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            QueryContext qc = new QueryContext(
                    null, // client version not needed
                    WMLtypeIn,
                    optionsMap,
                    QueryIn,
                    witsmlObjects,
                    user.getUserName(),
                    user.getPassword(),
                    getExchangeId()
            );
            this.valve.deleteObject(qc);
            resp.setResult((short) 1);
        } catch (Exception e) {
            resp.setSuppMsgOut(e.getMessage());
        }

        // return response
        return resp;
    }

    @Override
    public WMLS_GetVersionResponse getVersion() {
        LOG.info("Executing GetVersion");
        WMLS_GetVersionResponse resp = new WMLS_GetVersionResponse();
        resp.setResult(version);
        return resp;
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
            LOG.info("Returning cap: " + data);
            LOG.info("Returning cap again: " + cap.getWitsmlObject(requestedVersion));

            resp.setCapabilitiesOut(data);
            resp.setResult((short)1);
        } catch (Exception e) {
            resp.setResult((short)-424);
            LOG.info("Exception in generating GetCap response: " + e.getMessage());
        }

        return resp;
    }

    @Override
    public WMLS_GetBaseMsgResponse getBaseMsg(Short returnValueIn) {
        LOG.info("Executing GetBaseMsg");

        String errMsg = witsmlApiConfigUtil.getProperty("basemessages." + returnValueIn);
        if (errMsg == null){
            errMsg = witsmlApiConfigUtil.getProperty("basemessages.-999");
        }

        WMLS_GetBaseMsgResponse response = new WMLS_GetBaseMsgResponse();
        response.setResult(errMsg);

        return response;
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

            resp.setSuppMsgOut("Error parsing input: " + e.getMessage());
            resp.setResult((short) -1);
            return resp;
        }

        // try to query
        try {
            // construct query context
            Map<String,String> optionsMap = WitsmlUtil.parseOptionsIn(OptionsIn);
            ValveUser user = (ValveUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            QueryContext qc = new QueryContext(
                clientVersion,
                WMLtypeIn,
                optionsMap,
                QueryIn,
                witsmlObjects,
                user.getUserName(),
                user.getPassword(),
                getExchangeId()
            );

            // get query XML
            String xmlOut = this.valve.getObject(qc);

            // populate response
            if (null != xmlOut) {
                resp.setSuppMsgOut("");
                resp.setResult((short) 1);
                resp.setXMLout(xmlOut);
            } else {
                resp.setSuppMsgOut("Unhandled error from REST backend.");
                resp.setResult((short) -1);
            }
        } catch (ValveException ve) {
            resp.setResult((short)-425);
            LOG.warning("Valve Exception in GetFromStore: " + ve.getMessage());
            resp.setSuppMsgOut(ve.getMessage());
            ve.printStackTrace();
        } catch (Exception e) {
            resp.setResult((short)-425);
            LOG.warning("Exception in generating GetFromStore response: " + e.getMessage());
            e.printStackTrace();
        }

        // return response
        return resp;
    }

}

