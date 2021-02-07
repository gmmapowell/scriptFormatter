package com.gmmapowell.script.flow;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;

import com.gmmapowell.script.processor.prose.TableOfContents;

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
	public String toString() {
		return "LinkFromRef[" + anchor + ":" + text + "]";
	}
}
