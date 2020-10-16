package com.gmmapowell.script.flow;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class LinkOp implements SpanItem {
	public final String lk;
	public final String tx;

	public LinkOp(String lk, String tx) {
		this.lk = lk;
		this.tx = tx;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		float width = 0;
		try {
			width = font.getStringWidth(tx)*sz/1000;
		} catch (IllegalArgumentException ex) {
		}
		return new BoundingBox(0, 0, width, sz);
	}

}
