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
	private boolean showBorder = false;

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
		afterBlock = Math.max(afterBlock, baseStyle.getBeforeBlock());
		List<Line> lines = new ArrayList<>();
		List<Segment> segments = new ArrayList<>();
		Float fm = baseStyle.getFirstMargin();
		float lm = pageStyle.getLeftMargin() + baseStyle.getLeftMargin();
		if (fm == null)
			fm = lm;
		else
			fm += pageStyle.getLeftMargin();
		System.out.println(fm + " " + lm);
		float rm = pageStyle.getRightMargin() + baseStyle.getRightMargin();
		float wid = pageStyle.getPageWidth() - fm - rm;
		for (Span s : block) {
			Style style = baseStyle.apply(s.getStyle());
			String[] parts = s.getText().split(" ");
			boolean first = true;
			for (String p : parts) {
				if (p == null || p.length() == 0)
					continue;
				if (!first) {
					addSegment(lines, segments, wid, style, " ", false);
				}
				first = false;
				addSegment(lines, segments, wid, style, p, true);
				if (!lines.isEmpty())
					wid = pageStyle.getPageWidth() - lm - rm;
			}
		}
		if (!segments.isEmpty())
			lines.add(new Line(segments));
		
		if (!blockFits(pageStyle.getBottomMargin(), y-afterBlock, lines)) {
			closeCurrentPage();
		}
		boolean created = ensurePage();
		if (!created) {
			y -= afterBlock;
		}
		float xf = fm;
		for (Line l : lines) {
			y -= l.getBaseline();
			float len = l.getLineWidth();
			switch (baseStyle.getJustification()) {
			case LEFT:
				l.render(currentPage, xf, y);
				break;
			case RIGHT:
				l.render(currentPage, wid-len-rm, y);
				break;
			case CENTER:
				l.render(currentPage, xf + (wid-len)/2, y);
				break;
			}
			y -= l.height() - l.getBaseline();
			xf = lm;
		}
		afterBlock = baseStyle.getAfterBlock();
	}

	private boolean blockFits(float bottom, float ypos, List<Line> lines) throws IOException {
		for (Line l : lines) {
			ypos -= l.height();
		}
		return ypos >= bottom;
	}

	private void addSegment(List<Line> lines, List<Segment> segments, float wid, Style style, String p, boolean addToNext) throws IOException {
		Segment segment = new Segment(style, p);
		segments.add(segment);
		if (segments.size() > 1 && !Line.canHandle(wid, segments)) {
			segments.remove(segments.size()-1);
			lines.add(new Line(segments));
			segments.clear();
			if (addToNext)
				segments.add(segment);
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

	private boolean ensurePage() throws IOException {
		if (currentPage == null) {
			PDPage page = new PDPage();
			doc.addPage(page);
			currentPage = new PDPageContentStream(doc, page);
			if (showBorder) {
				currentPage.moveTo(pageStyle.getLeftMargin(), pageStyle.getBottomMargin());
				currentPage.lineTo(pageStyle.getLeftMargin(), pageStyle.getPageHeight()-pageStyle.getTopMargin());
				currentPage.lineTo(pageStyle.getPageWidth() - pageStyle.getRightMargin(), pageStyle.getPageHeight()-pageStyle.getTopMargin());
				currentPage.lineTo(pageStyle.getPageWidth() - pageStyle.getRightMargin(), pageStyle.getBottomMargin());
				currentPage.closeAndStroke();
			}
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
