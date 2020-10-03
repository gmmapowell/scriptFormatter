package com.gmmapowell.script.loader;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;

public interface Loader {
	void createWebeditIn(File file);
	FilesToProcess updateIndex() throws IOException, GeneralSecurityException, ConfigException;
}
