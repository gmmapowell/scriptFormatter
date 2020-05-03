package com.gmmapowell.script.loader;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.gmmapowell.script.FilesToProcess;

public interface Loader {

	FilesToProcess updateIndex() throws IOException, GeneralSecurityException;

}
