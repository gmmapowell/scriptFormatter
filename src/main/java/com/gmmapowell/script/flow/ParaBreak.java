package com.gmmapowell.script.flow;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class ParaBreak implements SpanItem {

	public ParaBreak() {
		System.out.println("BRKPara");
	}
	
	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return null;
	}

	@Override
	public String toString() {
		return "BRKPara";
	}
}
