package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.Map;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.styles.StyleCatalog;

public interface Stock {
	void newDocument(StyleCatalog styles) throws IOException;
	PageCompositor getPage(Map<String, String> current, boolean beginSection) throws IOException;
	void close(Place output) throws IOException;
}
