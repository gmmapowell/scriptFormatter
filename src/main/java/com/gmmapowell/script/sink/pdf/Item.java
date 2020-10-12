package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class Item {
	private final float xpos;
	private final BoundingBox bbox;
	private final PDFont font;
	private final Float fontsz;
	private final String text;

	public Item(float xpos, BoundingBox bbox, PDFont font, Float fontsz, String text) {
		this.xpos = xpos;
		this.bbox = bbox;
		this.font = font;
		this.fontsz = fontsz;
		this.text = text;
	}

	public float height() {
		return bbox.getHeight();
	}

	public void shove(PDPageContentStream page, float x, float y) throws IOException {
		page.beginText();
		try {
			page.setFont(font, fontsz);
			page.newLineAtOffset(x+xpos, y);
			page.showText(text);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace(System.out);
		} finally {
			page.endText();
		}
	}

}
