package com.gmmapowell.geofs.exceptions;

@SuppressWarnings("serial")
public class GeoFSException extends RuntimeException {
	public GeoFSException(String string) {
		super(string);
	}

	public GeoFSException(Exception ex) {
		super(ex);
	}
}
