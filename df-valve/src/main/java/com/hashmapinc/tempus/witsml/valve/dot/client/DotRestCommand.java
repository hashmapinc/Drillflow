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
package com.hashmapinc.tempus.witsml.valve.dot.client;

import com.hashmapinc.tempus.witsml.ValveLogging;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;

import java.util.logging.Logger;

public class DotRestCommand extends HystrixCommand<HttpResponse<String>> {
    private static final Logger LOG = Logger.getLogger(DotRestCommand.class.getName());
    private HttpRequest request;

    /**
     * builds a new rest command
     * @param req - request to execute in run
     */
    public DotRestCommand(HttpRequest req) {
        super(HystrixCommandGroupKey.Factory.asKey("DotValve"));
        this.request = req;
    }

    /**
     * Hystrix run command for executing rest requests
     *
     * @return - response from executing the http request
     * @throws UnirestException
     */
    @Override
    protected HttpResponse<String> run() throws UnirestException {
        return this.request.asString();
    }

    /**
     * This runs when an unexpected error occurs in run.
     * Returning null informs the caller that Hystrix is
     * preventing execution.
     * @return null
     */
    @Override
    protected HttpResponse<String> getFallback() {
        LOG.warning(ValveLogging.getLogMsg("DotRestCommand encountered broken circuit. Getting Fallback now..."));
        return null; // fail silently
    }
}
