package com.gmmapowell.script.sink.pdf;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.gmmapowell.script.styles.StyleCatalog;

public interface Stock {
	void newDocument(StyleCatalog styles) throws IOException;
	PageCompositor getPage(Map<String, String> current) throws IOException;
	void close(File output) throws IOException;
}
