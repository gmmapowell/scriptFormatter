package com.gmmapowell.script.loader.drive;

import com.gmmapowell.script.loader.Loader;

public class DriveLoader implements Loader {
	private final String creds;
	private final String index;

	public DriveLoader(String creds, String index) {
		this.creds = creds;
		this.index = index;
	}

}
