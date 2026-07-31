package com.gmmapowell.script.modules.doc.github;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.flow.SpanItem;

public class RedirectOp implements SpanItem {

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return new BoundingBox(0, 0, 0, 0);
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		// TODO Auto-generated method stub

	}

}
