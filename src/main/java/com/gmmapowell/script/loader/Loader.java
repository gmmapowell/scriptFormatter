package com.gmmapowell.script.loader;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface Loader {

	void updateIndex() throws IOException, GeneralSecurityException;

}
