package com.gmmapowell.script.config;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.gmmapowell.script.intf.FilesToProcess;

public interface Config {
	void prepare() throws Exception;
	FilesToProcess updateIndex() throws IOException, GeneralSecurityException, ConfigException;
	void generate(FilesToProcess files) throws IOException;
	void show();
	void upload() throws Exception;
}
