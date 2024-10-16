package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class ImageOp implements SpanItem {
	public final String uri;

	public ImageOp(String uri) {
		this.uri = uri;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		os.writeShort(FlowStandard.IMAGE_OP);
		os.writeUTF(uri);
	}
}
