package com.gmmapowell.script.styles.simple;

import com.gmmapowell.script.styles.Justification;
import com.gmmapowell.script.styles.Style;

public interface StyleBuilder {
	StyleBuilder beginNewPage(boolean b);
	StyleBuilder setPreformatted(boolean b);
	StyleBuilder showAtBottom(boolean b);
	StyleBuilder setBeforeBlock(float f);
	StyleBuilder setAfterBlock(float f);
	StyleBuilder setRequireAfter(float f);
	StyleBuilder setJustification(Justification center);
	StyleBuilder setFirstMargin(float f);
	StyleBuilder setLeftMargin(float f);
	StyleBuilder setLineSpacing(float f);
	StyleBuilder setRightMargin(float f);
	StyleBuilder setFont(String font);
	StyleBuilder setFontSize(Float f);
	StyleBuilder setAdjustFontSize(Float f);
	StyleBuilder setUnderline(boolean b);
	StyleBuilder setBold(boolean b);
	StyleBuilder setItalic(boolean b);
	StyleBuilder setBaselineAdjust(float f);
	StyleBuilder setWidth(float f);
	Style build();
}