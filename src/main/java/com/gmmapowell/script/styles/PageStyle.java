package com.gmmapowell.script.styles;

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
	String getPageNumberFont();
	Float getPageNumberFontSize();
	String getPageNumberJust();
	Boolean wantHeader();
	Boolean wantFooter();

}
