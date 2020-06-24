package com.gmmapowell.script.processor.prose;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.StyleCatalog;

public class CommentaryBreak implements Break {

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
		return 20;
	}

	@Override
	public float textY() {
		return 40;
	}

	@Override
	public float bottom() {
		return 52;
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
}
