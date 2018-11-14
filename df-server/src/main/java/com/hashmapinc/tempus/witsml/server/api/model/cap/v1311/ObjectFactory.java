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
package com.hashmapinc.tempus.witsml.server.api.model.cap.v1311;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.hashmapinc.com.tempus.witsml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CapServers_QNAME = new QName("http://www.witsml.org/api/131", "capServers");

    /**
     * Create an instance of {@link ObjCapServers }
     * 
     */
    public ObjCapServers createObjCapServers() {
        return new ObjCapServers();
    }

    /**
     * Create an instance of {@link ObjCapServer }
     * 
     */
    public ObjCapServer createObjCapServer() {
        return new ObjCapServer();
    }

    /**
     * Create an instance of {@link CsFunction }
     * 
     */
    public CsFunction createCsFunction() {
        return new CsFunction();
    }

    /**
     * Create an instance of {@link CsContact }
     * 
     */
    public CsContact createCsContact() {
        return new CsContact();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObjCapServers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.witsml.org/api/131", name = "capServers")
    public JAXBElement<ObjCapServers> createCapServers(ObjCapServers value) {
        return new JAXBElement<ObjCapServers>(_CapServers_QNAME, ObjCapServers.class, null, value);
    }

}
