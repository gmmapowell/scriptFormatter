package com.gmmapowell.script.styles;

import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.sink.pdf.Stock;

public interface StyleCatalog {

	Style get(String style);
	Style getOptional(String s);

	void font(String string, PDFont load);
	PDFont getFont(String font, Boolean italic, Boolean bold);
	Stock getStock(String stockName) throws ConfigException;
}
