package com.gmmapowell.script.styles;

import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public abstract class FontCatalog implements StyleCatalog {
	private Map<String, PDFont> fonts = new HashMap<>();

	@Override
	public void font(String name, PDFont f) {
		this.fonts.put(name, f);
	}

	@Override
	public PDFont getFont(String font, Boolean italic, Boolean bold) {
		if (font == null)
			return null;
		switch (font) {
		case "courier":
		case "monospace":
			if (bold && italic)
				return PDType1Font.COURIER_BOLD_OBLIQUE;
			else if (bold)
				return PDType1Font.COURIER_BOLD;
			else if (italic)
				return PDType1Font.COURIER_OBLIQUE;
			else
				return PDType1Font.COURIER;
		case "helvetica":
			if (bold && italic)
				return PDType1Font.HELVETICA_BOLD_OBLIQUE;
			else if (bold)
				return PDType1Font.HELVETICA_BOLD;
			else if (italic)
				return PDType1Font.HELVETICA_OBLIQUE;
			else
				return PDType1Font.HELVETICA;
		default:
			String fn = font;
			if (bold)
				fn += "-bold";
			if (italic)
				fn += "-italic";
			PDFont ret = fonts.get(fn);
			if (ret == null)
				throw new RuntimeException("No such font: " + fn);
			return ret;
		}
	}

}
