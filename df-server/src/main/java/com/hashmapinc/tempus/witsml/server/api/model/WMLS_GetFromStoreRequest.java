package com.hashmapinc.tempus.witsml.server.api.model;

import java.util.List;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;

public class WMLS_GetFromStoreRequest {
	private String clientVersion;
	private String XMLin;
	private List<AbstractWitsmlObject> witsmlObjects;

	public WMLS_GetFromStoreRequest() {
	}

	public WMLS_GetFromStoreRequest(String clientVerison, String XMLin, List<AbstractWitsmlObject> witsmlObjects) {
		this.clientVersion = clientVerison;
		this.XMLin = XMLin;
		this.witsmlObjects = witsmlObjects;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getXMLin() {
		return XMLin;
	}

	public void setXMLin(String xMLin) {
		XMLin = xMLin;
	}

	public List<AbstractWitsmlObject> getWitsmlObjects() {
		return witsmlObjects;
	}

	public void setWitsmlObjects(List<AbstractWitsmlObject> witsmlObjects) {
		this.witsmlObjects = witsmlObjects;
	}

}
