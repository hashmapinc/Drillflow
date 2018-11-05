package com.hashmapinc.tempus.witsml.server.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class GetHMIDrillOps {

	String drillopsUrl="https://api-demo.nam.drillops.slb.com/democore";
	String path=null;
	HttpClient httpclient=null;
	String url=null;
	/**
	 * 
	 * @param path String e.g /well/v2   or /wellbore/v1
	 */
	public GetHMIDrillOps(String path){
		url=drillopsUrl.concat(path);
		//httpclient = HttpClients.createDefault();	
	}
	/**
	 * 
	 * @param uri
	 * @param category String e.g well2, wellbore1
	 * @return
	 */
	public HttpRequestBase prepareReqHeader( URI uri,String category,String action) {//HttpRequestBase HttpGet
	    HttpGet request = new HttpGet(uri);
	    request.setHeader("Authorization", "");
	    if(category.equals("well2") && action.equals("get")){
		    request.setHeader("X-Prism-Data-Representation", "");
		    request.setHeader("X-Prism-Data-Schema", "");
	    }
	    if(category.equals("well2") && action.equals("add")|| action.equals("put"))//post....
	    {
	    	request.setHeader("Content-Type", "application/json");
	    }
	    if(category.equals("wellbore1") && action.equals("add")|| action.equals("put"))
	    {
	    	request.setHeader("Content-Type", "application/json");
	    }
	    // Request body,payload
	    try {
			StringEntity reqEntity = new StringEntity("{body}");
			//ToDo:
			//request.setEntity(reqEntity);//find out why not work
		} 
	    catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}    
		return request;
	}
	
	/*
	 * client execute the request, the result is either success then extract data 
	 * or faile then throw exception
	 */
	public void doWork(HttpRequestBase httpRqBase) {
		// TODO Auto-generated method stub
		HttpResponse response;
		int status=-1;
		try {
			response = httpclient.execute(httpRqBase);
			status=response.getStatusLine().getStatusCode();
			if(status!=200)
		    {
		    	throw new RuntimeException("Failed : HTTP error code : "
						+ status);
		    }
			HttpEntity entity = response.getEntity();

		    if (entity != null) 
		    {
		        System.out.println(EntityUtils.toString(entity));
		    }		
		} 
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   	
	}
		
}
