package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.zinutils.exceptions.NotImplementedException;

public class ReleaseFlow implements SpanItem {
	private final String flow;

	public ReleaseFlow(String flow) {
		this.flow = flow;
	}
	
	public String release() {
		return flow;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return null;
	}

	@Override
	public String toString() {
		return "RF[" + flow + "]";
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		throw new NotImplementedException();
	}
}
