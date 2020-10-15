package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.styles.PageStyle;

public class Item {
	private final PageStyle pageStyle;
	private final float xpos;
	private final BoundingBox bbox;
	private final PDFont font;
	private final Float fontsz;
	private final SpanItem si;

	public Item(PageStyle pageStyle, float xpos, BoundingBox bbox, PDFont font, Float fontsz, SpanItem si) {
		this.pageStyle = pageStyle;
		this.xpos = xpos;
		this.bbox = bbox;
		this.font = font;
		this.fontsz = fontsz;
		this.si = si;
	}

	public float height() {
		return bbox.getHeight();
	}

	public void shove(PDPageContentStream page, float x, float y) throws IOException {
		if (si instanceof TextSpanItem)
			showText(page, x, y, (TextSpanItem) si);
		else if (si instanceof Break)
			showBreak(page, x, y, (Break) si);
		else
			throw new RuntimeException("Cannot handle shoving a " + si.getClass());
	}

	private void showBreak(PDPageContentStream page, float x, float y, Break brk) throws IOException {
		float top = brk.top();
		float btm = brk.bottom();
		if (brk.box()) {
			page.setLineWidth(1.0f);
			page.moveTo(x, y+top);
			page.lineTo(x + pageStyle.getPageWidth() - pageStyle.getLeftMargin() - pageStyle.getRightMargin(), y+top);
			page.lineTo(x + pageStyle.getPageWidth() - pageStyle.getLeftMargin() - pageStyle.getRightMargin(), y+btm);
			page.lineTo(x, y+btm);
			page.closeAndStroke();
		} else if (brk.horizLines()) {
			page.setLineWidth(1.0f);
			page.moveTo(x, y+top);
			page.lineTo(x + pageStyle.getPageWidth() - pageStyle.getLeftMargin() - pageStyle.getRightMargin(), y+top);
			page.moveTo(x, y+btm);
			page.lineTo(x + pageStyle.getPageWidth() - pageStyle.getLeftMargin() - pageStyle.getRightMargin(), y+btm);
			page.closeAndStroke();
		} else
			throw new RuntimeException("What is the plan?");
		page.setFont(font, fontsz);
		String tx = brk.boxText();
		float txl = font.getStringWidth(tx)*fontsz/1000; 
		page.beginText();
		page.setFont(font, fontsz);
		page.newLineAtOffset(x + (pageStyle.getPageWidth()-pageStyle.getLeftMargin()-pageStyle.getRightMargin())/2 - txl/2, y+brk.textY());
		page.showText(tx);
		page.endText();
	}

	private void showText(PDPageContentStream page, float x, float y, TextSpanItem si) throws IOException {
		page.beginText();
		try {
			page.setFont(font, fontsz);
			page.newLineAtOffset(x+xpos + bbox.getLowerLeftX(), y + bbox.getLowerLeftY());
			page.showText(si.text);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace(System.out);
		} finally {
			page.endText();
		}
	}

}