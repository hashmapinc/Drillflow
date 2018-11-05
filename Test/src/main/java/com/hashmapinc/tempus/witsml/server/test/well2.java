package com.hashmapinc.tempus.witsml.server.test;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author cathychen
 * This class performs getFromStore by id action and qurey one well by uuid
 *
 */

public class well2 extends GetHMIDrillOps implements IConnectToDrillOps{

	String category="well2";
	public well2(String path) {//e.g path=/well/v2 
			super(path);
		}

	/**
	 * 
	 * @param  String uuid this is PathVariable
	 * 
	 * This method will get a well from DrillOps api
	 * sc defined: https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}
	 * sc defined: method=Get
	 * sc defined: Request body= unknow
	 * sc defined: content format = application/json
	 * sc defined: 200 ok json representation is:
	 * {
	  "contentType": "string",
	  "data": {},
	  "streamingState": "ready",
	  "creationTimeUtc": "string",
	  "lastUpdateTimeUtc": "string"
	   }
	 */		
	public void getFromStore( String uuid) {
		String action="get";
		URI uri=getURI(uuid);
		HttpRequestBase httpget=prepareReqHeader(uri,action);
		doWork(httpget);	
	}
  
	/**
	 * get
	 * @see com.hashmapinc.tempus.witsml.server.test.IConnectToDrillOps#getBuider(java.lang.String)
	 * @param String pathVariable
	 * @return URIBuilder
	 * sc defined url:"https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}"
	 * sc defined request parameter:uuid
	 */
	@Override
	public URI getURI(String pathVariable) {
		//e.g myUrl="https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}"
		String myUrl=super.url.concat(pathVariable);
		URIBuilder builder=null;
		URI uri=null;
		try {
			builder = new URIBuilder(myUrl);
			uri = builder.build();
		} 
		catch (URISyntaxException e) {		
			e.printStackTrace();
		}   
		return uri;
	}
	public HttpRequestBase prepareReqHeader( URI uri,String action) {
		return super.prepareReqHeader(uri,category,action);
	}
	public void doWork(HttpRequestBase httpRqBase)
	{
		super.doWork(httpRqBase);
	}
	@Override
	public URI getURI(String pathVariable1, String pathVariable2) throws URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * delete
	 * @param wid
	 * @throws URISyntaxException
	 */
	public void deleteFromStore(String wid) throws URISyntaxException
	{
		String action="delete";
		URI uri=deleteURI(wid);
		HttpRequestBase httpget=prepareReqHeader(uri,action);
		doWork(httpget);
	}

	/**
	 * 
	 * delete an existent well matching the specified ID.
	 * sc defined: Request URL
			https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}
	 */
	@Override
	public URI deleteURI(String wid) throws URISyntaxException {
		String wbidStr="/".concat(wid);
		String myUrl=super.url.concat(wbidStr);
		URIBuilder builder=null;
		URI uri=null;
		try {
			builder = new URIBuilder(myUrl);
			uri = builder.build();
		} 
		catch (URISyntaxException e) {		
			e.printStackTrace();
		}   
		return uri;
	}
	
	/**
	 * add,create
	 * Provision a new well with specified UUID.
	 * sc defined url
	 *    https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}?contractId={contractId}");
	 * @throws URISyntaxException 

	 */
	public void addToStore(String uuid, Integer contractId) throws URISyntaxException {
		
		String action="add";
		URI uri=addURI(uuid,contractId);
		HttpRequestBase httpget=prepareReqHeader(uri,action);
		doWork(httpget);
		
	}
	/**
	 * https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}?contractId={contractId}");
	 * e.g https://api-demo.nam.drillops.slb.com/democore/well/v2/100?contractId=20");
	 */	
	public URI addURI(String uuid,Integer contractId)throws URISyntaxException
	{
		String uuidStr="/".concat(uuid);
		String contctIdStr=contractId.toString();
		String contentStr="/?contentType=".concat(contctIdStr);
		String qString=uuidStr.concat(contentStr);
		String myUrl=super.url.concat(qString);
		URIBuilder builder=null;
		URI uri=null;
		try {
			builder = new URIBuilder(myUrl);
			uri = builder.build();
		} 
		catch (URISyntaxException e) {		
			e.printStackTrace();
		}   
		return uri;
	}
	
	
	/**
	 * put,update
	 * Create or update a well using WITSML object
	 * sc defined url:https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}?contractId={contractId}
	 * @throws URISyntaxException 
	 */
	public void updateInStore(String uuid,Integer contractId) throws URISyntaxException
	{
		String action="put";
		URI uri=putURI(uuid,contractId);
		HttpRequestBase httpget=prepareReqHeader(uri,action);
		doWork(httpget);
		
	}
	
	public URI putURI(String uuid,Integer contractId)throws URISyntaxException
	{
		String uuidStr="/".concat(uuid);
		String contctIdStr=contractId.toString();
		String contentStr="/?contentType=".concat(contctIdStr);
		String qString=uuidStr.concat(contentStr);
		String myUrl=super.url.concat(qString);
		URIBuilder builder=null;
		URI uri=null;
		try {
			builder = new URIBuilder(myUrl);
			uri = builder.build();
		} 
		catch (URISyntaxException e) {		
			e.printStackTrace();
		}   
		return uri;
	}

	@Override
	public URI addURI(String uuid) throws URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI putURI(String uid) throws URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	
		
}


