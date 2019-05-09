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
import com.hashmapinc.tempus.witsml.valve.ValveAuthException;
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
import javax.xml.bind.JAXBException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@WebService(serviceName = "WMLS", portName = "StoreSoapPort", targetNamespace = "http://www.witsml.org/wsdl/120", endpointInterface = "com.hashmapinc.tempus.witsml.server.api.IStore")
@Features(features = "org.apache.cxf.ext.logging.LoggingFeature")
public class StoreImpl implements IStore {
    private static final Logger LOG = Logger.getLogger(StoreImpl.class.getName());

    private ServerCap cap;
    private WitsmlApiConfig witsmlApiConfigUtil;
    private IValve valve;
    private ValveConfig config;
    @Value("${wmls.version}")
    private String version;

    @Value("${valve.name}")
    private String valveName;

    @Autowired
    private void setServerCap(ServerCap cap) {
        this.cap = cap;
    }

    @Autowired
    private void setWitsmlApiConfig(WitsmlApiConfig witsmlApiConfigUtil) {
        this.witsmlApiConfigUtil = witsmlApiConfigUtil;
    }

    @Autowired
    private void setValveConfig(ValveConfig config) {
        this.config = config;
    }

    @PostConstruct
    private void setValve() {
        // get the valve
        try {
            valve = ValveFactory.buildValve(valveName, config.getConfiguration());
        } catch (ValveAuthException e) {
            LOG.info("Error creating the valve: " + e.getMessage());
        }

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
            short validationResult = StoreValidator.validateAddToStore(WMLtypeIn, XMLin, optionsMap, valve);
            if (validationResult != 1){
                response.setResult(validationResult);
                return response;
            }
            String version = WitsmlUtil.getVersionFromXML(XMLin);
            // uses JAXB to marshall from XML
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
            uid = valve.createObject(qc).get();
            LOG.info(
                "Successfully added object: " + witsmlObjects.toString()
            );
        } catch (ValveException ve) {
            //TODO: handle exception
            LOG.warning("ValveException in addToStore: " + ve.getMessage());
            if (ve.getErrorCode() != -1){
                response.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + ve.getErrorCode()));
                response.setResult(ve.getErrorCode());
            } else {
                response.setSuppMsgOut(ve.getMessage());
                response.setResult(ve.getErrorCode());
            }
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

        // TODO This is just passing back what was already there from the client &
        //      not the API response.
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
            short validationResult = StoreValidator.validateUpdateInStore(WMLtypeIn, XMLin, optionsMap, valve);
            if (validationResult != 1){
                response.setResult(validationResult);
                response.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + response.getResult()));
                return response;
            }
            String version = WitsmlUtil.getVersionFromXML(XMLin);
            // uses JAXB to marshall from XML
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
            valve.updateObject(qc).get();
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
        response.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + response.getResult()));
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

        Map<String,String> optionsMap = WitsmlUtil.parseOptionsIn(OptionsIn);
        short validationResult = StoreValidator.validateDeleteFromStore(WMLtypeIn, QueryIn, optionsMap, valve);
        if (validationResult != 1){
            resp.setResult(validationResult);
            resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
            return resp;
        }
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
            this.valve.deleteObject(qc).get();
            resp.setResult((short) 1);
        } catch (ValveException e) {
            resp.setSuppMsgOut(e.getMessage());
            if (e.getErrorCode() != null){
                resp.setResult(e.getErrorCode());
            }
            return resp;
        } catch (Exception ex){
            resp.setSuppMsgOut(ex.getMessage());
            resp.setResult((short)-1);
            return resp;
        }

        resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
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
        WMLS_GetCapResponse resp = new WMLS_GetCapResponse();
        short validationResult = StoreValidator.validateGetCap(OptionsIn);
        if (validationResult != 1){
            resp.setResult(validationResult);
            resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
            return resp;
        }
        HashMap<String,String> options = WitsmlUtil.parseOptionsIn(OptionsIn);
        String requestedVersion = options.get("dataVersion");

        resp.setSuppMsgOut("");
        try {
            // get cap string and populate response data
            String data = cap.getWitsmlObject(requestedVersion);
            LOG.info("Returning cap: " + data);

            resp.setCapabilitiesOut(data);
            resp.setResult((short)1);
        } catch (UnsupportedOperationException e) {
            resp.setResult((short)-424);
            LOG.info("Unsupported version requested: " + e.getMessage());
        } catch (JAXBException e) {
            resp.setResult((short)-1001);
            resp.setSuppMsgOut("Unable to generate the capabilities object due to misconfiguration of the server");
            LOG.log(Level.FINE, "Unable to generate the capabilities object due to misconfiguration of the server: " + e.getMessage());
        }
        resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
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
        Map<String,String> optionsMap = WitsmlUtil.parseOptionsIn(OptionsIn);
        // validates to make sure conditions are met
        short validationResult = StoreValidator.validateGetFromStore(WMLtypeIn, QueryIn, optionsMap, valve);
        if (validationResult != 1){
            resp.setResult(validationResult);
            resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
            return resp;
        }

        String xmlOut=null;
        // check for the presence of the requestObjectSelectionCapability option --
        // it is not necessary to retrieve data for a query since the query is a
        // predefined constant that returns the capabilities of the server due to type
        if ( optionsMap.containsKey("requestObjectSelectionCapability") &&
                optionsMap.get("requestObjectSelectionCapability").equals("true") ) {
            xmlOut = valve.getObjectSelectionCapability(WMLtypeIn);
        } else {
            if ( optionsMap.containsKey("requestObjectSelectionCapability") &&
                    !optionsMap.get("requestObjectSelectionCapability").equals("none") ) {
                resp.setResult((short)-427);
                resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
                return resp;
            }
            if ( OptionsIn.contains("requestObjectSelectionCapability") &&
                    !optionsMap.containsKey("requestObjectSelectionCapability") ) {
                // value of the key must have been null since the parse never placed the key/value pair
                // into the map
                resp.setResult((short)-411);
                resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));
                return resp;
            }
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
                ValveUser user = (ValveUser) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
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

                xmlOut = this.valve.getObject(qc).get();
                // convert to WITSML XML
                //LogConverterExtended logConverter = new LogConverterExtended();
                /*
                try {
                    com.hashmapinc.tempus.WitsmlObjects.v1411.ObjLog witsmlXmlOut =
                            logConverter.convertTo1411(xmlOut);
                } catch (JAXBException jaxBEx) {
                    throw new Exception("JAXB failure trying to generate GetFromStore response: " +
                            jaxBEx.getMessage());
                }
                */
                // TODO xmlOut is a String; need to go from an ObjLog v1411 to a String & put it into xmlOut

            } catch (ValveException ve) {
                resp.setResult((short) -425);
                LOG.warning("Valve Exception in GetFromStore: " + ve.getMessage());
                resp.setSuppMsgOut(ve.getMessage());
                ve.printStackTrace();
            } catch (Exception e) {
                resp.setResult((short) -425);
                LOG.warning("Exception in generating GetFromStore response: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // populate response
        if (null != xmlOut) {
            resp.setSuppMsgOut("");
            resp.setResult((short) 1);
            resp.setXMLout(xmlOut);
        } else {
            resp.setSuppMsgOut("Unhandled error from REST backend.");
            resp.setResult((short) -1);
        }

        // return response
        if (resp.getSuppMsgOut().isEmpty())
            resp.setSuppMsgOut(witsmlApiConfigUtil.getProperty("basemessages." + resp.getResult()));

        return resp;
    }

}
