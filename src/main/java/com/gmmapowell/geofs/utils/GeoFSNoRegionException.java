package com.gmmapowell.geofs.utils;

import com.gmmapowell.geofs.exceptions.GeoFSException;

@SuppressWarnings("serial")
public class GeoFSNoRegionException extends GeoFSException {

	public GeoFSNoRegionException(String path) {
		super("cannot use a relative path directly from a world: " + path);
	}

}
