package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.flow.LinkFromRef;
import com.gmmapowell.script.flow.LinkFromTOC;
import com.gmmapowell.script.flow.LinkOp;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.Style;

public class Item {
	private final PageStyle pageStyle;
	private final Style style;
	private final float xpos;
	private final BoundingBox bbox;
	private final PDFont font;
	private final Float fontsz;
	private final SpanItem si;

	public Item(PageStyle pageStyle, Style style, float xpos, BoundingBox bbox, PDFont font, Float fontsz, SpanItem si) {
		this.pageStyle = pageStyle;
		this.style = style;
		this.xpos = xpos;
		this.bbox = bbox;
		this.font = font;
		this.fontsz = fontsz;
		this.si = si;
	}

	public float height() {
		return bbox.getHeight();
	}

	public void shove(PDPage meta, PDPageContentStream page, float x, float y) throws IOException {
		if (si instanceof TextSpanItem)
			showText(page, x, y, (TextSpanItem) si);
		else if (si instanceof Break)
			showBreak(page, x, y, (Break) si);
		else if (si instanceof LinkOp)
			showLink(meta, page, x, y, (LinkOp) si);
		else if (si instanceof LinkFromTOC)
			showTOCLink(meta, page, x, y, (LinkFromTOC) si);
		else if (si instanceof LinkFromRef)
			showRefLink(meta, page, x, y, (LinkFromRef) si);
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
		if (font == null)
			return;
		
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
		if (style.getUnderline()) {
			page.moveTo(x, y-2f);
			page.lineTo(x+bbox.getWidth(), y-2f);
			page.stroke();
		}
	}

	private void showLink(PDPage meta, PDPageContentStream page, float x, float y, LinkOp lk) throws IOException {
		page.beginText();
		try {
			page.setFont(font, fontsz);
			page.newLineAtOffset(x+xpos + bbox.getLowerLeftX(), y + bbox.getLowerLeftY());
			page.showText(lk.tx);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace(System.out);
		} finally {
			page.endText();
		}
//		if (style.getUnderline()) {
//			page.moveTo(x, y-2f);
//			page.lineTo(x+bbox.getWidth(), y-2f);
//			page.stroke();
//		}
		
		PDAnnotationLink link = new PDAnnotationLink();
		link.setRectangle(new PDRectangle(x + xpos + this.bbox.getLowerLeftX(), y + this.bbox.getLowerLeftY() - 2, this.bbox.getWidth(), this.bbox.getHeight()));
		PDBorderStyleDictionary uline = new PDBorderStyleDictionary();
		uline.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
		link.setBorderStyle(uline);
		PDActionURI action = new PDActionURI();
		action.setURI(lk.lk);
		link.setAction(action);
		
		meta.getAnnotations().add(link);
	}

	private void showTOCLink(PDPage meta, PDPageContentStream page, float x, float y, LinkFromTOC lk) throws IOException {
		page.beginText();
		try {
			page.setFont(font, fontsz);
			page.newLineAtOffset(x+xpos + bbox.getLowerLeftX(), y + bbox.getLowerLeftY());
			page.showText(lk.text);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace(System.out);
		} finally {
			page.endText();
		}

		PDAnnotationLink link = new PDAnnotationLink();
		link.setRectangle(new PDRectangle(x + xpos + this.bbox.getLowerLeftX(), y + this.bbox.getLowerLeftY() - 2, this.bbox.getWidth(), this.bbox.getHeight()));
		PDBorderStyleDictionary uline = new PDBorderStyleDictionary();
		uline.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
		link.setBorderStyle(uline);
		lk.pendingTarget(link);
		
		meta.getAnnotations().add(link);
	}

	private void showRefLink(PDPage meta, PDPageContentStream page, float x, float y, LinkFromRef lk) throws IOException {
		page.beginText();
		try {
			page.setFont(font, fontsz);
			page.newLineAtOffset(x+xpos + bbox.getLowerLeftX(), y + bbox.getLowerLeftY());
			page.showText(lk.text);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace(System.out);
		} finally {
			page.endText();
		}
		
		PDAnnotationLink link = new PDAnnotationLink();
		link.setRectangle(new PDRectangle(x + xpos + this.bbox.getLowerLeftX(), y + this.bbox.getLowerLeftY() - 2, this.bbox.getWidth(), this.bbox.getHeight()));
		PDBorderStyleDictionary uline = new PDBorderStyleDictionary();
		uline.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
		link.setBorderStyle(uline);
//		PDActionURI action = new PDActionURI();
//		action.setURI(lk.lk);
//		link.setAction(action);
		lk.target(link);
		
		meta.getAnnotations().add(link);
	}
}
