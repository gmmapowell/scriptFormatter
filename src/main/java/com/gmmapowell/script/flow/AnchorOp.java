package com.gmmapowell.script.flow;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.processor.prose.TOCEntry;

public class AnchorOp implements SpanItem {
	private final TOCEntry entry;

	public AnchorOp(TOCEntry entry) {
		this.entry = entry;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return new BoundingBox(0, 0, 0, 0);
	}
	
	public void recordPage(PDPage meta, String name) {
		entry.recordPage(meta, name);
	}

	@Override
	public String toString() {
		return "Anchor";
	}
}
