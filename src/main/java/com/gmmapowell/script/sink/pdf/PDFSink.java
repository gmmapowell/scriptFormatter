package com.gmmapowell.script.sink.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;

public class PDFSink implements Sink {
	private final StyleCatalog styles;
	private final File output;
	private final boolean wantOpen;
	private final PDDocument doc;
	private PDPageContentStream currentPage;
	
	// All this lot should come from a "PageFormat" ...
	private float width = 8.5f * 72;
	private float height = 11f * 72;
	private float topMargin = 72;
	private float rightMargin = 72;
	private float bottomMargin = 72;
	private float leftMargin = 72;
	
	private float y;

	public PDFSink(File root, StyleCatalog styles, String output, boolean wantOpen, boolean debug) {
		this.styles = styles;
		File f = new File(output);
		if (f.isAbsolute())
			this.output = f;
		else
			this.output = new File(root, output);
		this.wantOpen = wantOpen;
		doc = new PDDocument();
	}

	@Override
	public void block(Block block) throws IOException {
		ensurePage();
		Style baseStyle = styles.get(block.getStyle());
		for (Span s : block) {
			Style style = baseStyle.apply(s.getStyle());
			// TODO: font and size should come from block.getStyle()
			PDFont font = PDType1Font.HELVETICA;
			float sz = 12.0f;
			float lineSpacing = 14.0f;
			// TODO: need to handle wrapping
			// TODO: need to handle spans within the block
			currentPage.beginText();
			currentPage.setFont(font, sz);
	//		font.getStringWidth(block.getText());
			currentPage.newLineAtOffset(leftMargin, y);
			y -= lineSpacing;
			currentPage.showText(s.getText());
			currentPage.endText();
		}
	}

	@Override
	public void close() throws IOException {
		closeCurrentPage();
		doc.save(output);
		doc.close();
	}

	@Override
	public void showFinal() {
		if (!wantOpen)
			return;
		try {
			Desktop.getDesktop().open(output);
		} catch (Exception e) {
			System.out.println("Failed to open " + output + " on desktop:\n  " + e.getMessage());
		}
	}

	private void ensurePage() throws IOException {
		if (currentPage == null) {
			PDPage page = new PDPage();
			doc.addPage(page);
			currentPage = new PDPageContentStream(doc, page);
			y = height - topMargin;
		}
	}

	private void closeCurrentPage() throws IOException {
		if (currentPage != null) {
			currentPage.close();
			currentPage = null;
		}
	}
}
