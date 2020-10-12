package com.gmmapowell.script.sink.pdf;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.gmmapowell.script.styles.StyleCatalog;

public interface Stock {

	PageCompositor getPage(StyleCatalog styles, Map<String, String> current);

	void newDocument();

	void close(File output) throws IOException;


}
