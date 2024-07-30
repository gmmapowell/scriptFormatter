package com.gmmapowell.geofs.exceptions;

@SuppressWarnings("serial")
public class GeoFSInvalidWorldException extends GeoFSException {

	public GeoFSInvalidWorldException() {
		super("cannot use relative paths in a different world");
	}
	
}
