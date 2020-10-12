package com.gmmapowell.script.sink.pdf;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;

public class BasicStock implements Stock {
	private PDDocument doc;

	@Override
	public void newDocument() {
		doc = new PDDocument();
	}

	@Override
	public PageCompositor getPage(Map<String, String> current) {
		return new SimplePageCompositor(doc);
	}
	
	@Override
	public void close(File output) throws IOException {
		doc.save(output);
		doc.close();
		this.doc = null;
	}
}
