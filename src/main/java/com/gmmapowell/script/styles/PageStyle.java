package com.gmmapowell.script.styles;

import org.apache.pdfbox.pdmodel.font.PDFont;

public interface PageStyle {

	Float getPageWidth();
	Float getPageHeight();
	Float getTopMargin();
	Float getBottomMargin();
	Float getLeftMargin();
	Float getRightMargin();
	boolean wantPageNumbers();
	float pageNumberCenterX();
	float pageNumberBaselineY();
	PDFont getPageNumberFont();
	float getPageNumberFontSize();
	boolean wantHeader();

}
