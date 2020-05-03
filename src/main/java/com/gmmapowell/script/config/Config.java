package com.gmmapowell.script.config;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.gmmapowell.script.FilesToProcess;

public interface Config {
	FilesToProcess updateIndex() throws IOException, GeneralSecurityException;
	void generate(FilesToProcess files);
	void show();
}
