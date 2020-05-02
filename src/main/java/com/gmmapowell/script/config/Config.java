package com.gmmapowell.script.config;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface Config {
	void updateIndex() throws IOException, GeneralSecurityException;
	void generate();
	void show();
}
