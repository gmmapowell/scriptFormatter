package com.gmmapowell.script.config;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.intf.FilesToProcess;

public interface Config {
	Region root();
	void prepare() throws Exception;
	FilesToProcess updateIndex() throws IOException, GeneralSecurityException, ConfigException;
	void indexLoad(FilesToProcess files);
	void reset();
	void generate(FilesToProcess files) throws IOException;
	void dump() throws IOException;
	void sink() throws IOException;
	void show() throws IOException;
	void upload() throws Exception;
	void finish() throws Exception;
}
