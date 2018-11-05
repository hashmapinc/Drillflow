package com.hashmapinc.tempus.witsml.server.test;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;

/**
 * 
 * @author cathychen
 * This class perform getFromstore by company and name 
 * results is a set of well
 */

public class wells2 extends GetHMIDrillOps implements IConnectToDrillOps 
{
	String category="well2";
	public wells2(String path) {//e.g path=/well/v2 
		super(path);
	}
	
	/**
	 * get
	 * @param  String company.This is PathVariable
	 * @param String name. This is PathVariable
	 * @throws URISyntaxException 
	 */
	public void getFromStore( String company,String name) throws URISyntaxException {	
		String action="get";
		URI uri=getURI(company,name);
		HttpRequestBase httpget=prepareReqHeader(uri,category,action);
		doWork(httpget);
	}
	/**
	 * @see com.hashmapinc.tempus.witsml.server.test.IConnectToDrillOps#getBuilder(java.lang.String, java.lang.String)
	 * sc defined:Reqest URL 
	 * https://api-demo.nam.drillops.slb.com/democore/well/v2/[?query.company][&query.name][&query.streamingState][&query.liveState][&query.includeData]
	 * e.g
	 * https://api-demo.nam.drillops.slb.com/democore/well/v2/?query.company=BHI&query.name=peter
	 */
	@Override
	public URI getURI(String queryComp, String queryName) {
		//e.g myUrl="https://api-demo.nam.drillops.slb.com/democore/well/v2/{uuid}"
		//queryComp: company=BHI  queryName: name=peter
		
		String qCompName="/?query.company=";
		String qCompVal=queryComp;
		String qComp=qCompName.concat(qCompVal);
		
		
		String qParaName="&query.name";
		String qNameVal=queryName;
		String qName=qParaName.concat(qNameVal);
		
		String myUrl=super.url.concat(qComp).concat(qName);
		URIBuilder builder=null;
		URI uri=null;
		try {
			builder = new URIBuilder(myUrl);
			builder.setParameter("query.company", "{string}");
            builder.setParameter("query.name", "{string}");
			uri = builder.build();
		} 
		catch (URISyntaxException e) {		
			e.printStackTrace();
		}   
		return uri;
		
	}

	public HttpRequestBase prepareReqHeader(URI uri,String category,String action) {
		return super.prepareReqHeader(uri,category,action);		
	}
    	
	public void doWork(HttpRequestBase httpRqBase) {
		super.doWork(httpRqBase);		
	}

	@Override
	public URI getURI(String pathVariable) throws URISyntaxException {
		// TODO Auto-generated method stub
		return null;
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
