package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;

import com.gmmapowell.script.modules.doc.DocModule;
import com.gmmapowell.script.modules.doc.toc.TableOfContents;

public class LinkFromRef implements SpanItem {
	private TableOfContents toc;
	public final String anchor;
	public final String text;

	public LinkFromRef(TableOfContents toc, String anchor, String text) {
		this.toc = toc;
		this.anchor = anchor;
		this.text = text;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		float width = 0;
		try {
			width = font.getStringWidth(text)*sz/1000;
		} catch (IllegalArgumentException ex) {
		}
		return new BoundingBox(0, 0, width, sz);
	}
	
	public void target(PDAnnotationLink link) {
		toc.refAnchor(anchor, link);
	}
	
	@Override
	public void intForm(DataOutputStream os) throws IOException {
		os.write(DocModule.ID);
		os.write(DocModule.LINK_FROM_REF);
		// not quite sure what to write here
	}

	@Override
	public String toString() {
		return "LinkFromRef[" + anchor + ":" + text + "]";
	}
}
