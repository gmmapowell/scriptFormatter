package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.gmmapowell.script.styles.Style;

public class Segment {
	private final Style style;
	private final String text;

	public Segment(Style style, String text) {
		this.style = style;
		this.text = text;
	}

	public void render(PDPageContentStream page, float x, float y) throws IOException {
		page.beginText();
		page.setFont(style.getFont(), style.getFontSize());
		page.newLineAtOffset(x, y+style.getBaselineAdjust());
		page.showText(text);
		page.endText();
		
		if (style.getUnderline()) {
			page.moveTo(x, y-2f);
			page.lineTo(x+width(), y-2f);
			page.stroke();
		}

	}
	
	public float height() {
		return style.getLineSpacing();
	}

	public float width() throws IOException {
		Float sw = style.getWidth();
		if (sw != null)
			return sw;
		return style.getFont().getStringWidth(text) * style.getFontSize() / 1000;
	}

	public float baseline() {
		return style.getFontSize();
	}
	
	@Override
	public String toString() {
		return text;
	}

	public boolean requiresMoreThan(float f) {
		Float ra =style.getRequireAfter();
		return ra != null && ra > f;
	}
}
