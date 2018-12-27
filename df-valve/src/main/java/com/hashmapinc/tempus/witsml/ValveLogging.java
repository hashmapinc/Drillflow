package com.hashmapinc.tempus.witsml;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;

public class ValveLogging {
	private String id;
	private String message;
	private AbstractWitsmlObject abstObject;
	
	public ValveLogging() {}
	public ValveLogging(String id, String message, AbstractWitsmlObject abstObject)
	{
		this.abstObject = abstObject;
		this.message = message;
		this.id = id;
	}
	@Override
	public String toString() {
		return "id is - "+id+" Message is - "+message+" for object "+abstObject;
	}

}
