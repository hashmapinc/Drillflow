package com.hashmapinc.tempus.witsml.server.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;

/**
 * 
 * @author cathychen
 * This class perfore getFromStore by wellid and result is a set of wellbores that under this wellid
 * sc defined: request url 
 *             https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores?wellId={wellId}
 *             request parameter
 *             wellId string Id of well to be queried to get all the wellbores belongs to it.
 */

public class wellbores1 extends GetHMIDrillOps implements IConnectToDrillOps{
	
	String category="wellbore1";
	public wellbores1(String path) {//e.g path=/wellbore/v1
		super(path);
	}
	
	public void getFromStore( String id) throws URISyntaxException {	
		String action="get";
		URI uri=getURI(id);
		HttpRequestBase httpget=prepareReqHeader(uri,action);
		doWork(httpget);	
	}

	@Override
	public URI getURI(String pathVariable1, String pathVariable2) throws URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
    /*
     * get URI for wellbores by the wellboreID
     * sc defined: url as
     * "https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores?wellId={wellId}"
     */
	@Override
	public URI getURI(String wbid) throws URISyntaxException {
		String qString="/?wellId=".concat(wbid);
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
	
	public HttpRequestBase prepareReqHeader(URI uri,String action)
	{
		return super.prepareReqHeader(uri,category,action);
	}
	public void doWork(HttpRequestBase httpget)
	{
		super.doWork(httpget);
	}

	@Override
	public URI deleteURI(String pathVariable) throws URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI addURI(String uuid, Integer contractId) throws URISyntaxException {
		// TODO Auto-generated method stub
		return null;
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
