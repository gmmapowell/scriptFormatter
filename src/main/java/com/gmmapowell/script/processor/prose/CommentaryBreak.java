package com.gmmapowell.script.processor.prose;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.StyleCatalog;

public class CommentaryBreak implements Break, SpanItem {

	@Override
	public String boxText() {
		return "Commentary";
	}

	@Override
	public float require() {
		return 200f; // we are planning on taking up 60pt, then allow 32 for the next heading & 108 for the section to get started
	}

	@Override
	public float top() {
		return 40;
	}

	@Override
	public float textY() {
		return 20;
	}

	@Override
	public float bottom() {
		return 8;
	}

	@Override
	public boolean box() {
		return false;
	}

	@Override
	public boolean horizLines() {
		return true;
	}

	@Override
	public PDFont textFont(StyleCatalog styles, PageStyle pageStyle) {
		return styles.getFont("palatino", false, true);
	}

	@Override
	public float fontSize(PageStyle pageStyle) {
		return 16f;
	}

	@Override
	public boolean newPageAfter() {
		return false;
	}

	@Override
	public float total() {
		return 60;
	}
	
	@Override
	public String toString() {
		return "COMMENTARY";
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return new BoundingBox(0, 0, 200, 60);
	}
}
