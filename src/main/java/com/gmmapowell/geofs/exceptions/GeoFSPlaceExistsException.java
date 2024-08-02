package com.gmmapowell.geofs.exceptions;

@SuppressWarnings("serial")
public class GeoFSPlaceExistsException extends GeoFSException {

	public GeoFSPlaceExistsException(String path) {
		super("place already exists: " + path);
	}

}
