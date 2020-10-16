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
		// TODO Auto-generated method stub
		return null;
	}

}
