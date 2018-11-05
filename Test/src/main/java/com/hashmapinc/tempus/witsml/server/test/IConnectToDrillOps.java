package com.hashmapinc.tempus.witsml.server.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;

public interface IConnectToDrillOps {
	//URIBuilder getBuilder(String pathVariable);
	URI getURI(String pathVariable1,String pathVariable2) throws URISyntaxException;
	URI getURI(String pathVariable) throws URISyntaxException;	
	URI deleteURI(String pathVariable)throws URISyntaxException;	
	URI addURI(String uuid,Integer contractId)throws URISyntaxException;
	URI addURI(String uuid)throws URISyntaxException;
	URI putURI(String uid)throws URISyntaxException;
	
	//HttpRequestBase prepareReqHeader(URI uri); //
	//void doWork(HttpRequestBase httpRqBase);

}
