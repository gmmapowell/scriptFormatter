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
import com.gmmapowell.script.elements.Group;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;
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
		List<RenderInfo> ris = new ArrayList<>();
		collectRI(ris, block);
		render(ris);
	}
	
	private void collectRI(List<RenderInfo> ris, Block block) throws IOException {
		if (block instanceof SpanBlock) {
			RenderInfo ri = handleSpanBlock((SpanBlock)block);
			ris.add(ri);
		} else if (block instanceof Group) {
			Group grp = (Group) block;
			for (Block b : grp)
				collectRI(ris, b);
		} else
			throw new RuntimeException("not implemented");
	}

	private RenderInfo handleSpanBlock(SpanBlock block) throws IOException {
		Style baseStyle = styles.get(block.getStyle());
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
					addSegment(lines, segments, wid, baseStyle, style, fm, rm, " ", false);
				}
				first = false;
				addSegment(lines, segments, wid, baseStyle, style, fm, rm, p, true);
				if (!lines.isEmpty()) {
					wid = pageStyle.getPageWidth() - lm - rm;
					fm = lm;
				}
			}
		}
		if (!segments.isEmpty())
			finishLine(lines, segments, wid, baseStyle, fm, rm);
		
		RenderInfo ret = new RenderInfo(Math.max(afterBlock, baseStyle.getBeforeBlock()), lines);
		afterBlock = baseStyle.getAfterBlock();
		return ret;
	}
	
	private void render(List<RenderInfo> ris) throws IOException {
		if (!blocksFit(pageStyle.getBottomMargin(), y, ris)) {
			closeCurrentPage();
		}
		boolean created = ensurePage();
		for (RenderInfo ri : ris) {
			if (!created) {
				y -= ri.beforeBlock;
			}
			created = false;
			for (Line l : ri.lines) {
				y -= l.getBaseline();
				l.render(currentPage, y);
				y -= l.height() - l.getBaseline();
			}
		}
	}

	private boolean blocksFit(float bottomMargin, float ypos, List<RenderInfo> ris) throws IOException {
		for (RenderInfo ri : ris) {
			ypos = blockFits(ypos, ri);
		}
		return ypos >= bottomMargin;
	}

	private float blockFits(float ypos, RenderInfo ri) throws IOException {
		ypos -= ri.beforeBlock;
		for (Line l : ri.lines) {
			ypos -= l.height();
		}
		return ypos;
	}

	private void addSegment(List<Line> lines, List<Segment> segments, float wid, Style baseStyle, Style style, float fm, float rm, String p, boolean addToNext) throws IOException {
		Segment segment = new Segment(style, p);
		segments.add(segment);
		if (segments.size() > 1 && !Line.canHandle(wid, segments)) {
			segments.remove(segments.size()-1);
			finishLine(lines, segments, wid, baseStyle, fm, rm);
			segments.clear();
			if (addToNext)
				segments.add(segment);
		}
	}

	private void finishLine(List<Line> lines, List<Segment> segments, float wid, Style baseStyle, float fm, float rm) throws IOException {
		Line l = new Line(segments);
		lines.add(l);
		float len = l.getLineWidth();
		switch (baseStyle.getJustification()) {
		case LEFT:
			l.xpos(fm);
			break;
		case RIGHT:
			l.xpos(wid-len-rm);
			break;
		case CENTER:
			l.xpos(fm + (wid-len)/2);
			break;
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
