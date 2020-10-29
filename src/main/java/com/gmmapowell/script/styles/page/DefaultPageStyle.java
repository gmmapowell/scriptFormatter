package com.gmmapowell.script.styles.page;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.gmmapowell.script.styles.PageStyle;

public class DefaultPageStyle implements PageStyle {

	@Override
	public Float getPageWidth() {
		return 8.5f * 72;
	}

	@Override
	public Float getPageHeight() {
		return 11f * 72;
	}

	@Override
	public Float getTopMargin() {
		return 60f;
	}

	@Override
	public Float getBottomMargin() {
		return 72f;
	}

	@Override
	public Float getLeftMargin() {
		return 72f;
	}

	@Override
	public Float getRightMargin() {
		return 72f;
	}
	
	@Override
	public Boolean wantHeader() {
		return false;
	}

	@Override
	public Boolean wantFooter() {
		return false;
	}

	@Override
	public Boolean wantPageNumbers() {
		return true;
	}
	
	@Override
	public Float pageNumberCenterX() {
		return 4.25f*72;
	}
	
	@Override
	public Float pageNumberBaselineY() {
		return 54f;
	}

	@Override
	public PDFont getPageNumberFont() {
		return PDType1Font.COURIER;
	}

	@Override
	public Float getPageNumberFontSize() {
		return 12f;
	}
}
