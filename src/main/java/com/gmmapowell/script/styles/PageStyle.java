package com.gmmapowell.script.styles;

import org.apache.pdfbox.pdmodel.font.PDFont;

public interface PageStyle {

	Float getPageWidth();
	Float getPageHeight();
	Float getTopMargin();
	Float getBottomMargin();
	Float getLeftMargin();
	Float getRightMargin();
	Boolean wantPageNumbers();
	Float pageNumberCenterX();
	Float pageNumberBaselineY();
	PDFont getPageNumberFont();
	Float getPageNumberFontSize();
	Boolean wantHeader();
	Boolean wantFooter();

}
