package com.gmmapowell.script.styles.simple;

import com.gmmapowell.script.styles.Justification;
import com.gmmapowell.script.styles.Style;

public interface StyleBuilder {
	StyleBuilder setAfterBlock(float f);
	StyleBuilder setBeforeBlock(float f);
	StyleBuilder setJustification(Justification center);
	StyleBuilder setFirstMargin(float f);
	StyleBuilder setLeftMargin(float f);
	StyleBuilder setLineSpacing(float f);
	StyleBuilder setRightMargin(float f);
	StyleBuilder setFont(String font);
	StyleBuilder setUnderline(boolean b);
	StyleBuilder setItalic(boolean b);
	Style build();
}