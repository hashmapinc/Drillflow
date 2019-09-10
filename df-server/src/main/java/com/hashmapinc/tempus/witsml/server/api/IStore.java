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

import com.hashmapinc.tempus.witsml.server.api.model.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://www.witsml.org/wsdl/120", name = "WMLS")
public interface IStore {

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_AddToStore", operationName = "WMLS_AddToStore")
    @SOAPBinding(style = SOAPBinding.Style.RPC, parameterStyle= SOAPBinding.ParameterStyle.BARE, use = SOAPBinding.Use.ENCODED)
    WMLS_AddToStoreResponse addToStore(
            @WebParam(partName = "WMLtypeIn") String WMLtypeIn,
            @WebParam(partName = "XMLin") String XMLin,
            @WebParam(partName = "OptionsIn") String OptionsIn,
            @WebParam(partName = "CapabilitiesIn") String CapabilitiesIn
    );

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_UpdateInStore", operationName = "WMLS_UpdateInStore")
    @SOAPBinding(style = SOAPBinding.Style.RPC, parameterStyle= SOAPBinding.ParameterStyle.BARE, use = SOAPBinding.Use.ENCODED)
    WMLS_UpdateInStoreResponse updateInStore(
            @WebParam(partName = "WMLtypeIn") String WMLtypeIn,
            @WebParam(partName = "XMLin") String XMLin,
            @WebParam(partName = "OptionsIn") String OptionsIn,
            @WebParam(partName = "CapabilitiesIn") String CapabilitiesIn
    );

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_DeleteFromStore", operationName = "WMLS_DeleteFromStore")
    @SOAPBinding(style=SOAPBinding.Style.RPC, parameterStyle=SOAPBinding.ParameterStyle.BARE, use=SOAPBinding.Use.ENCODED)
    WMLS_DeleteFromStoreResponse deleteFromStore(
            @WebParam(partName = "WMLtypeIn") String WMLtypeIn,
            @WebParam(partName = "QueryIn") String QueryIn,
            @WebParam(partName = "OptionsIn") String OptionsIn,
            @WebParam(partName = "CapabilitiesIn") String CapabilitiesIn
    );

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_GetFromStore", operationName = "WMLS_GetFromStore")
    @SOAPBinding(style = SOAPBinding.Style.RPC, parameterStyle= SOAPBinding.ParameterStyle.BARE, use = SOAPBinding.Use.ENCODED)
    WMLS_GetFromStoreResponse getFromStore(
            @WebParam(partName = "WMLtypeIn") String WMLtypeIn,
            @WebParam(partName = "QueryIn") String QueryIn,
            @WebParam(partName = "OptionsIn") String OptionsIn,
            @WebParam(partName = "CapabilitiesIn") String CapabilitiesIn
    );

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_GetVersion", operationName = "WMLS_GetVersion")
    @SOAPBinding(style = SOAPBinding.Style.RPC, parameterStyle= SOAPBinding.ParameterStyle.BARE, use = SOAPBinding.Use.ENCODED)
    WMLS_GetVersionResponse getVersion();

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_GetCap", operationName = "WMLS_GetCap")
    @SOAPBinding(style = SOAPBinding.Style.RPC, parameterStyle= SOAPBinding.ParameterStyle.BARE, use = SOAPBinding.Use.ENCODED)
    WMLS_GetCapResponse getCap(
            @WebParam(partName = "OptionsIn") String OptionsIn
    );


    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_GetBaseMsg", operationName = "WMLS_GetBaseMsg")
    @SOAPBinding(style = SOAPBinding.Style.RPC, parameterStyle= SOAPBinding.ParameterStyle.BARE, use = SOAPBinding.Use.ENCODED)
    WMLS_GetBaseMsgResponse getBaseMsg(
            @WebParam(partName= "ReturnValueIn") Short ReturnValueIn
    );
}