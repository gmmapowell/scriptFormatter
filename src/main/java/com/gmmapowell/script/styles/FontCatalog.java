package com.gmmapowell.script.styles;

import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public abstract class FontCatalog implements StyleCatalog {
	private Map<String, PDFont> fonts = new HashMap<>();

	@Override
	public Map<String, PDFont> fonts() {
		return fonts;
	}

	@Override
	public PDFont getFont(String font, Boolean italic) {
		if (font == null)
			return null;
		switch (font) {
		case "courier":
			if (italic)
				return PDType1Font.COURIER_OBLIQUE;
			else
				return PDType1Font.COURIER;
		case "helvetica":
			if (italic)
				return PDType1Font.HELVETICA_OBLIQUE;
			else
				return PDType1Font.HELVETICA;
		default:
			String fn = font;
			if (italic)
				fn += "-italic";
			PDFont ret = fonts.get(fn);
			if (ret == null)
				throw new RuntimeException("No such font: " + font);
			return ret;
		}
	}

}
