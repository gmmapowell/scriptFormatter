package com.gmmapowell.script.sink.pdf;

import java.io.File;
import java.io.IOException;

import com.gmmapowell.script.styles.StyleCatalog;

public interface Ream {

	void newDocument(StyleCatalog styles) throws IOException;
	PageCompositor newPage() throws IOException;
	void close(File output) throws IOException;

}
