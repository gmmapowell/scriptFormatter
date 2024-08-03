package com.gmmapowell.geofs.exceptions;

@SuppressWarnings("serial")
public class GeoFSCannotCreateRegionException extends GeoFSException {
	public GeoFSCannotCreateRegionException(String path) {
		super("cannot create region " + path);
	}
}
