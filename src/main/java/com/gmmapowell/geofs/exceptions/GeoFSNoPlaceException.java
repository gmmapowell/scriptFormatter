package com.gmmapowell.geofs.exceptions;

@SuppressWarnings("serial")
public class GeoFSNoPlaceException extends GeoFSException {

	public GeoFSNoPlaceException(String path) {
		super("place does not exist: " + path);
	}

}
