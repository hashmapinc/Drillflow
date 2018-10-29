package com.hashmapinc.tempus.witsml.server.api;

import com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetCapResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(targetNamespace = "http://www.witsml.org/wsdl/120", name = "WMLS")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IStore {

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_GetVersion")
    @RequestWrapper(targetNamespace = "http://www.witsml.org/message/120")
    String WMLS_GetVersion();

    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_GetCap")
    @RequestWrapper(targetNamespace = "http://www.witsml.org/message/120", localName = "Store.WMLS_GetCap")
    @ResponseWrapper(targetNamespace = "http://www.witsml.org/message/120",
                     className = "com.hashmapinc.tempus.witsml.server.api.model.WMLS_GetCapResponse")
    WMLS_GetCapResponse WMLS_GetCap(@WebParam(partName = "OptionsIn") String OptionsIn);


    @WebMethod(action = "http://www.witsml.org/action/120/Store.WMLS_GetBaseMsg")
    @RequestWrapper(targetNamespace = "http://www.witsml.org/message/120")
    String WMLS_GetBaseMsg(@WebParam(partName= "ReturnValueIn") Short ReturnValueIn);
}