package com.gmmapowell.script.flow;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class SectionTitle implements SpanItem {
	private final String title;

	public SectionTitle(String title) {
		this.title = title;
	}
	
	public String title() {
		return title;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return null;
	}

	@Override
	public String toString() {
		return "Section[" + title + "]";
	}
}
