package com.gmmapowell.script.styles;

import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;

public interface Style {

	Style apply(List<String> list);

	Boolean beginNewPage();
	Boolean showAtBottom();
	Boolean isPreformatted();
	Float getRequireAfter();
	Float getAfterBlock();
	Float getBeforeBlock();
	Float getFirstMargin();
	Float getWidth();
	PDFont getFont();
	String getFontName();
	Float getFontSize();
	Float getAdjustFontSize();
	Float getBaselineAdjust();
	Boolean getBold();
	Boolean getItalic();
	Justification getJustification();
	Float getLeftMargin();
	Float getLineSpacing();
	Float getRightMargin();
	Boolean getUnderline();
}
