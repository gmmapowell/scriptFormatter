package com.gmmapowell.script.loader;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.intf.FilesToProcess;

public interface Loader {
	void createWebeditIn(Place file, String title);
	FilesToProcess updateIndex() throws IOException, GeneralSecurityException, ConfigException;
}
