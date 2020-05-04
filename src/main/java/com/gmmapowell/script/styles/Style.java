package com.gmmapowell.script.styles;

import org.apache.pdfbox.pdmodel.font.PDFont;

public interface Style {

	Style apply(String style);

	Float getAfterBlock();
	Float getBeforeBlock();
	PDFont getFont();
	Float getFontSize();
	Justification getJustification();
	Float getLeftMargin();
	Float getLineSpacing();
	Float getRightMargin();
	Boolean getUnderline();
}
