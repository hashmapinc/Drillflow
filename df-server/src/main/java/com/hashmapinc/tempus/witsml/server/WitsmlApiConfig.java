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
package com.hashmapinc.tempus.witsml.server;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.feature.transform.XSLTOutInterceptor;
import org.apache.cxf.interceptor.StaxOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.common.gzip.GZIPOutInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.hashmapinc.tempus.witsml.server.api.StoreImpl;

import java.net.URL;

@Configuration
public class WitsmlApiConfig {

    private final String XSLT_REQUEST_PATH = "interceptor/removeReturn.xsl";

    private Bus bus;
    private Environment env;
    private StoreImpl storeImpl;
    @Value("${wmls.compression}")
    private boolean compression;

    @Autowired
    private void setEnv(Environment env){
        this.env = env;
    }

    @Autowired
    private void setBus(Bus bus){
        this.bus = bus;
    }

    @Autowired
    private void setStoreImpl(StoreImpl storeImpl){
        this.storeImpl = storeImpl;
    }

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, storeImpl);
        if (compression)
            endpoint.getOutInterceptors().add(new GZIPOutInterceptor());

        // Removes the <return> element that causes the certification test to fail
        XSLTOutInterceptor returnRemoval = new XSLTOutInterceptor(Phase.PRE_STREAM, StaxOutInterceptor.class, null,
                XSLT_REQUEST_PATH);
        URL wsdl = getClass().getResource("/schema/WMLS.WSDL");
        endpoint.getOutInterceptors().add(returnRemoval);
        endpoint.setWsdlLocation(wsdl.toString());
        endpoint.publish("/WMLS");
        LoggingFeature dumbFeature = new LoggingFeature();
        dumbFeature.setPrettyLogging(false);
        endpoint.getFeatures().add(dumbFeature);
        return endpoint;
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        SpringBus springBus = new SpringBus();
        return springBus;
    }

    public String getProperty(String pPropertyKey) {
        return env.getProperty(pPropertyKey);
    }
}