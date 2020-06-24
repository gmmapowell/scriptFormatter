package com.gmmapowell.script.elements;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.StyleCatalog;

public interface Break {

	String boxText();

	float require();
	float top();
	float textY();
	float bottom();

	boolean box();
	boolean horizLines();

	PDFont textFont(StyleCatalog styles, PageStyle pageStyle);
	float fontSize(PageStyle pageStyle);

	boolean newPageAfter();

	float total();
}
