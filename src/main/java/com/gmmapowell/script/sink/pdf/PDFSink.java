package com.gmmapowell.script.sink.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.styles.page.DefaultPageStyle;

public class PDFSink implements Sink {
	private final StyleCatalog styles;
	private final File output;
	private final boolean wantOpen;
	private final PDDocument doc;
	private PDPageContentStream currentPage;
	
	private float y;
	private PageStyle pageStyle;
	private float afterBlock;

	public PDFSink(File root, StyleCatalog styles, String output, boolean wantOpen, boolean debug) {
		this.styles = styles;
		File f = new File(output);
		if (f.isAbsolute())
			this.output = f;
		else
			this.output = new File(root, output);
		this.wantOpen = wantOpen;
		doc = new PDDocument();
		pageStyle = new DefaultPageStyle();
	}

	@Override
	public void block(Block block) throws IOException {
		Style baseStyle = styles.get(block.getStyle());
		boolean created = ensurePage();
		if (!created) {
			y -= Math.max(afterBlock, baseStyle.getBeforeBlock());
		}
		List<Line> lines = new ArrayList<>();
		List<Segment> segments = new ArrayList<>();
		for (Span s : block) {
			// TODO: Get title to format correctly (implement center and underline)
			// TODO: Then work on wrapping lines
			Style style = baseStyle.apply(s.getStyle());
			// TODO: need to handle wrapping
			// TODO: need to handle spans within the block
			segments.add(new Segment(style, s.getText()));
		}
		if (!segments.isEmpty())
			lines.add(new Line(segments));
		
		// We actually need a list of lines containing a list of segments
		for (Line l : lines) {
			float lm = baseStyle.getLeftMargin();
			float rm = baseStyle.getRightMargin();
			float len = l.getLineWidth();
			float wid = pageStyle.getPageWidth() - lm - rm;
			switch (baseStyle.getJustification()) {
			case LEFT:
				l.render(currentPage, lm, y);
				break;
			case RIGHT:
				l.render(currentPage, wid-len-rm, y);
				break;
			case CENTER:
				l.render(currentPage, lm + (wid-len)/2, y);
				break;
			}
			y -= l.height();
		}
		afterBlock = baseStyle.getAfterBlock();
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

	private boolean ensurePage() throws IOException {
		if (currentPage == null) {
			PDPage page = new PDPage();
			doc.addPage(page);
			currentPage = new PDPageContentStream(doc, page);
			y = pageStyle.getPageHeight() - pageStyle.getTopMargin();
			afterBlock = 0;
			return true;
		}
		return false;
	}

	private void closeCurrentPage() throws IOException {
		if (currentPage != null) {
			currentPage.close();
			currentPage = null;
			afterBlock = 0;
		}
	}
}
