package com.gmmapowell.script.flow;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class NestedSpan implements SpanItem {
	public final HorizSpan nested;

	public NestedSpan(HorizSpan nested) {
		this.nested = nested;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return null;
	}
}
