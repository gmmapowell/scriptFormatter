package com.gmmapowell.script.elements.block;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.StyleCatalog;

public class BoxyAdBreak implements Break {

	@Override
	public String boxText() {
		return "AD BREAK";
	}

	@Override
	public float require() {
		return 0;
	}

	@Override
	public float top() {
		return 10;
	}

	@Override
	public float textY() {
		return 27;
	}

	@Override
	public float bottom() {
		return 42;
	}

	@Override
	public boolean box() {
		return true;
	}

	@Override
	public boolean horizLines() {
		return false;
	}

	@Override
	public PDFont textFont(StyleCatalog styles, PageStyle pageStyle) {
		return pageStyle.getPageNumberFont();
	}

	@Override
	public float fontSize(PageStyle pageStyle) {
		return pageStyle.getPageNumberFontSize();
	}

	@Override
	public boolean newPageAfter() {
		return true;
	}

	@Override
	public float total() {
		return 0;
	}
}
