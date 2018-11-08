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
/*
  Copyright © 2018-2018 Hashmap, Inc

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.hashmapinc.tempus.witsml.server.api;

import com.hashmapinc.tempus.witsml.server.WitsmlApiConfig;
import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetCapResponse;
import com.hashmapinc.tempus.witsml.server.api.model.cap.ServerCap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.logging.Logger;

@Service
@WebService(serviceName = "StoreService", portName = "StoreSoapPort",
        targetNamespace = "http://www.witsml.org/wsdl/120",
        endpointInterface = "com.hashmapinc.tempus.witsml.server.api.IStore")
public class StoreImpl implements IStore {

    private static final Logger LOG = Logger.getLogger(StoreImpl.class.getName());

    private ServerCap cap;
    private WitsmlApiConfig witsmlApiConfigUtil;

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


    @Override
    public int addToStore(
        String WMLtypeIn,
        String XMLin,
        String OptionsIn,
        String CapabilitiesIn
    ) {
        LOG.info("Executing addToStore");
        return 1;
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
}