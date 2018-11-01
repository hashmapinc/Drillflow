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

import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetCapResponse;
import com.hashmapinc.tempus.witsml.server.api.model.cap.DataObject;
import com.hashmapinc.tempus.witsml.server.api.model.cap.ServerCap;
import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
@WebService(serviceName = "StoreService", portName = "StoreSoapPort",
        targetNamespace = "http://www.witsml.org/wsdl/120",
        endpointInterface = "com.hashmapinc.tempus.witsml.server.api.IStore")
public class StoreImpl implements IStore {

    private static final Logger LOG = Logger.getLogger(StoreImpl.class.getName());

    @Value("${wmls.version:7}")
    private String version;

    @Value("${wmls.capserver.contact.name}")
    private String contactName;

    @Value("${wmls.capserver.contact.email}")
    private String contactEmail;

    @Value("${wmls.capserver.contact.phone}")
    private String contactPhone;

    //Server information
    @Value("${wmls.capserver.description}")
    private String description;

    @Value("${wmls.capserver.name}")
    private String name;

    @Value("${wmls.capserver.vendor}")
    private String vendor;

    @Value("${wmls.capserver.version}")
    private String capVersion;

    @Value("${wmls.capserver.schemaVersion}")
    private String schemaVersion;

    @Value("${wmls.capserver.changeDetectionPeriod}")
    private String changeDetectionPeriod;

    @Value("${wmls.capserver.cascadedDelete:false}")
    private String cascadedDelete;

    @Value("${wmls.capserver.supportUomConversion:true}")
    private String supportUomConversion;

    @Value("${wmls.capserver.compressionMethod:gzip}")
    private String compressionMethod;

    @Value("${wmls.capserver}")
    private String capServer;


    @Override
    public String WMLS_GetVersion() {
        LOG.info("Executing GetVersion");
        return version;
    }

    @Override
    public WMLS_GetCapResponse WMLS_GetCap(String OptionsIn) {
        LOG.info("Executing GetCap");
        String requestedVersion = OptionsIn.substring(OptionsIn.lastIndexOf("=") +1);
        WMLS_GetCapResponse resp = new WMLS_GetCapResponse();
        resp.setSuppMsgOut("");
        ServerCap cap = new ServerCap();
        cap.setContactName(contactName);
        cap.setServerName(name);
        cap.addFunction("WMLS_GetBaseMsg", null);
        cap.addFunction("WMLS_GetVersion", null);
        DataObject object = new DataObject();
        object.setName(capServer);
        List<DataObject> objects = new ArrayList<>();
        objects.add(object);
        cap.addFunction("WMLS_GetCap", objects);
        try {
            String data = cap.getWitsmlObject(requestedVersion);
            resp.setCapabilitiesOut(data);
            resp.setResult((short)1);
        } catch (Exception e) {
            resp.setResult((short)-424);
            LOG.info("Exception in generating GetCap response: " + e.getMessage());
        }
        //TODO: Dynamically set the capabilities based off of a properties file that can be set at deploy time.
        //resp.setCapabilitiesOut("<capServers xmlns=\"http://www.witsml.org/api/141\" version=\"1.4.1\"><capServer apiVers=\"1.4.1.1\"><function name=\"WMLS_GetFromStore\"><DDataObject>well</DDataObject></function><function name=\"WMLS_GetBaseMsg\" /><function name=\"WMLS_GetVersion\" /></capServer></capServers>");
        return resp;
    }

    @Override
    public String WMLS_GetBaseMsg(Short ReturnValueIn) {
        LOG.info("Executing GetBaseMsg");
        //TODO: Implement the hash table of error codes that can be appended to at deploy time
        return "This is the return value for: " + ReturnValueIn;
    }
}