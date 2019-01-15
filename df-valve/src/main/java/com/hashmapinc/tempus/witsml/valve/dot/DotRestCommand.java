package com.hashmapinc.tempus.witsml.valve.dot;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public class DotRestCommand extends HystrixCommand<HttpResponse<String>> {
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
        return null; // fail silently
    }
}
