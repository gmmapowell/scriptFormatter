package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class YieldToFlow implements SpanItem {
	private final String flow;

	public YieldToFlow(String flow) {
		this.flow = flow;
	}
	
	public String yieldTo() {
		return flow;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return null;
	}
	
	@Override
	public void intForm(DataOutputStream os) throws IOException {
		os.writeShort(FlowStandard.YIELD);
		os.writeUTF(flow);
	}

	@Override
	public String toString() {
		return "YTF[" + flow + "]";
	}
}
