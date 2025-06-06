package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class BreakingSpace implements SpanItem {
	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		float width = 0;
		if (font != null)
			width = font.getStringWidth(" ")*sz/1000;
		return new BoundingBox(0, 0, width, sz);
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		os.writeShort(FlowStandard.BREAKING_SPACE);
	}

	@Override
	public String toString() {
		return "SPC";
	}
}
