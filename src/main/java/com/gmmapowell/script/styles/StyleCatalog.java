package com.gmmapowell.script.styles;

import java.util.Map;

import org.apache.pdfbox.pdmodel.font.PDFont;

public interface StyleCatalog {

	Style get(String style);

	Map<String, PDFont> fonts();

	PDFont getFont(String font, Boolean italic);

}
