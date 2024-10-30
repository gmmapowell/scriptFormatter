package com.gmmapowell.script.intf;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.flow.SpanItem;

public class KeepTogether implements SpanItem {

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return null;
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		System.out.println("need to write IF for KeepTogether");
	}

	@Override
	public String toString() {
		return "KeepTogether";
	}
}
