package com.gmmapowell.script.sink.pdf;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface Stock {

	PageCompositor getPage(Map<String, String> current);

	void newDocument();

	void close(File output) throws IOException;

}
