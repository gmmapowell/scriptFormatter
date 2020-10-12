package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class SimplePageCompositor implements PageCompositor {
	private final PDDocument doc;
	private PDPageContentStream currentPage;

	public SimplePageCompositor(PDDocument doc) {
		this.doc = doc;
	}

	@Override
	public void begin() throws IOException {
		PDPage page = new PDPage();
		doc.addPage(page);
		currentPage = new PDPageContentStream(doc, page);
	}

	@Override
	public Acceptance token(StyledToken next) {
		return new Acceptance(Acceptability.PENDING, null);
	}

	@Override
	public void close() throws IOException {
		currentPage.close();
		currentPage = null;
	}
}
