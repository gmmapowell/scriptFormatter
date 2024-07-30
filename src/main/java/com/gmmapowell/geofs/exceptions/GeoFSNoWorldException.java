package com.gmmapowell.geofs.exceptions;

@SuppressWarnings("serial")
public class GeoFSNoWorldException extends GeoFSException {

	public GeoFSNoWorldException(String world) {
		super("there is no world in the universe called: " + world);
	}

}
