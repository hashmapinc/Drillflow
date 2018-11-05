package com.hashmapinc.tempus.witsml.server.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;

/**
 * 
 * @author cathychen
 * This class perform getFromStore to by wellid and result is one wellbore
 * 
 * sc defined: 
 * request url :https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores/{id}[?contentType]
 * request parametr: 1:wellId string Id of well to be queried to get all the wellbores belongs to it.
 *                   2:contentType (optional) string Only Witsml2.0 is accepted now.
 */

public class wellbore1 extends GetHMIDrillOps implements IConnectToDrillOps{
	
	String category="wellbore1";
	public wellbore1(String path) {//e.g path=/wellbore/v1
		super(path);
	}
	/**
	 * get
	 * @param wellId
	 * @throws URISyntaxException
	 */
	public void getFromStore( String wellId) throws URISyntaxException {	
		String action="get";
		URI uri=getURI(wellId);
		HttpRequestBase httpget=prepareReqHeader(uri,category,action);
		doWork(httpget);	
	}

	/**
     * get URI for wellbores by the wellboreID
     * sc defined: request url
     * 				"https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/{id}[?contentType]"
     * 				e.g: "https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/100?contentType=Witsml2.0"
     * sc defined: request parameters:
     *              id string Uuid of the wellbore.
     *              contentType (optional) string Only Witsml2.0 is accepted now.
     */
	@Override
	public URI getURI(String wbid, String contentTypeVal) throws URISyntaxException {
		
			//super.url="https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores"
			
			String wbidStr="/".concat(wbid);
			String contentStr="/?contentType=".concat(contentTypeVal);
			String qString=wbidStr.concat(contentStr);
			String myUrl=super.url.concat(qString);
			URIBuilder builder=null;
			URI uri=null;
			try {
				builder = new URIBuilder(myUrl);
				builder.setParameter("contentType", "{string}");
				uri = builder.build();
			} 
			catch (URISyntaxException e) {		
				e.printStackTrace();
			}   
			return uri;
	}
    
	@Override
	public URI getURI(String wbid) throws URISyntaxException {		
		return null;
	}
	
	public HttpRequestBase prepareReqHeader(URI uri,String category,String action)
	{
		return super.prepareReqHeader(uri,category,action);
	}
	public void doWork(HttpRequestBase httpget)
	{
		super.doWork(httpget);
	}
	/**
	 * delete
	 * delete an existent wellbore matching the specified ID.
	 * @param wbid String wellbore id
	 * sc defined request url:https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores/{id}
	 * @throws URISyntaxException 
	 */
    public void deleteFromStore(String wbid) throws URISyntaxException
    {
    	String action="delete";
    	URI uri=deleteURI(wbid);
		HttpRequestBase httpget=prepareReqHeader(uri,category,action);
		doWork(httpget);
    }

    /**
     * delete an existent wellbore matching the specified ID
     * sc defined url:https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores/{id}
     */
	@Override
	public URI deleteURI(String wbid) throws URISyntaxException {		
		String wbidStr="/".concat(wbid);
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
	 * post/add a wellbore
	 * Create a new wellbore or upsert an existing wellbore in format of witsml2.0.
	 * sc defined:Request URL
				https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores/{id}
				req parameter
				id string Uuid of the wellbore
	 */
	public void addToStore(String wbid) throws URISyntaxException
	{
		String action="add";
    	URI uri=addURI(wbid);
		HttpRequestBase httpget=prepareReqHeader(uri,category,action);
		doWork(httpget);
	}
	
	/**
	 * sc defined urll "https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores/{id}"
	 * 
	 */
	@Override
	public URI addURI(String uuid) throws URISyntaxException {
		String myUrl=super.url.concat(uuid);
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
	public URI addURI(String uuid, Integer contractId) throws URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * put/update
	 * Create a new wellbore with an existed wellId and a wellbore name
	 * sc defined url 
	 *            https://api-demo.nam.drillops.slb.com/democore/wellbore/v1/wellbores
	 *            
	 *            ??? do we need to handle this ?????ß
	 * @throws URISyntaxException 
	 */
	public void updateInstore(String uuid) throws URISyntaxException
	{
		String action="put";
    	URI uri=putURI(uuid);
		HttpRequestBase httpget=prepareReqHeader(uri,category,action);
		doWork(httpget);
	}
	@Override
	public URI putURI(String uuid) throws URISyntaxException {
		String myUrl=super.url.concat(uuid);
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
}
