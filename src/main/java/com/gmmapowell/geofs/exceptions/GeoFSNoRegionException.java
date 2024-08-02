package com.gmmapowell.geofs.exceptions;

@SuppressWarnings("serial")
public class GeoFSNoRegionException extends GeoFSException {

	public GeoFSNoRegionException(String path) {
		super("region does not exist: " + path);
	}

}
