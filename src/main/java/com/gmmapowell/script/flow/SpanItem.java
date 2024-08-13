package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

public interface SpanItem {

	BoundingBox bbox(PDFont font, float sz) throws IOException;

	void intForm(DataOutputStream os) throws IOException;
	
}
