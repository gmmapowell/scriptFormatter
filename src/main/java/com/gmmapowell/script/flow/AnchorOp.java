package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.modules.doc.DocModule;
import com.gmmapowell.script.modules.doc.toc.TOCEntry;

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
	public void intForm(DataOutputStream os) throws IOException {
		os.write(DocModule.ID);
		os.write(DocModule.ANCHOR_OP);
		entry.intForm(os);
	}

	@Override
	public String toString() {
		return "Anchor";
	}
}
