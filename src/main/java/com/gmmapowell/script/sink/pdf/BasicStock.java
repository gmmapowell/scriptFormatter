package com.gmmapowell.script.sink.pdf;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.gmmapowell.script.styles.StyleCatalog;

public class BasicStock implements Stock {
	private PDDocument doc;

	@Override
	public void newDocument() {
		doc = new PDDocument();
	}

	@Override
	public PageCompositor getPage(StyleCatalog styles, Map<String, String> current) {
		return new SimplePageCompositor(styles, doc);
	}
	
	@Override
	public void close(File output) throws IOException {
		doc.save(output);
		doc.close();
		this.doc = null;
	}
}
