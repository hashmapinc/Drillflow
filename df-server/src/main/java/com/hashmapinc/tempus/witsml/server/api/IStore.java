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
import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetFromStoreResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(targetNamespace = "http://www.witsml.org/wsdl/120", name = "StoreSoapBinding")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IStore {

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_AddToStore", operationName = "WMLS_AddToStore")
    @RequestWrapper(targetNamespace = "http://www.witsml.org/message/120")
    int addToStore(
        @WebParam(partName = "WMLtypeIn") String WMLtypeIn,
        @WebParam(partName = "XMLin") String XMLin,
        @WebParam(partName = "OptionsIn") String OptionsIn,
        @WebParam(partName = "CapabilitiesIn") String CapabilitiesIn
    );

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_GetVersion", operationName = "WMLS_GetVersion")
    @RequestWrapper(targetNamespace = "http://www.witsml.org/message/120")
    String getVersion();

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_GetCap", operationName = "WMLS_GetCap")
    @RequestWrapper(targetNamespace = "http://www.witsml.org/message/120", localName = "Store.WMLS_GetCap")
    @ResponseWrapper(targetNamespace = "http://www.witsml.org/message/120",
                     className = "com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetCapResponse")
    WMLS_GetCapResponse getCap(@WebParam(partName = "OptionsIn") String OptionsIn);


    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_GetBaseMsg", operationName = "WMLS_GetBaseMsg")
    @RequestWrapper(targetNamespace = "http://www.witsml.org/message/120")
    String getBaseMsg(@WebParam(partName= "ReturnValueIn") Short ReturnValueIn);

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_GetFromStore")
    @RequestWrapper(targetNamespace = "http://www.witsml.org/message/120", localName = "Store.WMLS_GetFromStore")
    @ResponseWrapper(targetNamespace = "http://www.witsml.org/message/120",
            className = "com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetFromStoreResponse")
    WMLS_GetFromStoreResponse getFromStore(@WebParam(partName = "WMLtypeIn") String WMLtypeIn, @WebParam(partName = "QueryIn") String QueryIn, @WebParam(partName = "OptionsIn") String OptionsIn, @WebParam(partName = "CapabilitiesIn") String CapabilitiesIn);

}