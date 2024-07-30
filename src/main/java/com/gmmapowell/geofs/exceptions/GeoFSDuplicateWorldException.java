package com.gmmapowell.geofs.exceptions;

@SuppressWarnings("serial")
public class GeoFSDuplicateWorldException extends GeoFSException {

	public GeoFSDuplicateWorldException(String world) {
		super("there is already a world in the universe called: " + world);
	}

}
