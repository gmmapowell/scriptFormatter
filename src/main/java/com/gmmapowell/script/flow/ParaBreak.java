package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class ParaBreak implements SpanItem {

	public ParaBreak() {
	}
	
	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return null;
	}
	
	@Override
	public void intForm(DataOutputStream os) throws IOException {
		os.writeShort(FlowStandard.PARA_BREAK);
	}

	@Override
	public String toString() {
		return "BRKPara";
	}
}
