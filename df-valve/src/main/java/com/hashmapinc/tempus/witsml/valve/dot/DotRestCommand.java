package com.hashmapinc.tempus.witsml.valve.dot;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;

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

    @Override
    protected HttpResponse<String> run() throws UnirestException {
        return this.request.asString();
    }
}
