package com.gmmapowell.script.flow;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class NonBreakingSpace implements SpanItem {
	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		float width = font.getStringWidth(" ")*sz/1000;
		return new BoundingBox(0, 0, width, sz);
	}

	@Override
	public String toString() {
		return "NBSPC";
	}
}
