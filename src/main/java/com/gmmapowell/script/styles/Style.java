package com.gmmapowell.script.styles;

import org.apache.pdfbox.pdmodel.font.PDFont;

public interface Style {

	Style apply(String style);

	Float getAfterBlock();
	Float getBeforeBlock();
	Float getFirstMargin();
	PDFont getFont();
	Float getFontSize();
	Boolean getItalic();
	Justification getJustification();
	Float getLeftMargin();
	Float getLineSpacing();
	Float getRightMargin();
	Boolean getUnderline();

	PDFont getFontInternal(Style style);

}
