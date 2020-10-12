package com.gmmapowell.script.flow;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class TextSpanItem implements SpanItem {
	public final String text;

	public TextSpanItem(String tx) {
		this.text = tx;
	}
	
	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		float width = font.getStringWidth(text)*sz/1000;
		return new BoundingBox(0, 0, width, sz);
	}

	@Override
	public String toString() {
		return "Tx[" + text + "]";
	}
}
