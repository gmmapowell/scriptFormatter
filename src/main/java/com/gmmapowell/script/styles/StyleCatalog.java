package com.gmmapowell.script.styles;

import java.util.Map;

import org.apache.pdfbox.pdmodel.font.PDFont;

public interface StyleCatalog {

	Style get(String style);
	Style getOptional(String s);

	Map<String, PDFont> fonts();

	PDFont getFont(String font, Boolean italic, Boolean bold);


}
