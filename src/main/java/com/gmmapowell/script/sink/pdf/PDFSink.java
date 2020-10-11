package com.gmmapowell.script.sink.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.elements.Group;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.styles.page.DefaultPageStyle;
import com.gmmapowell.script.utils.Upload;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class PDFSink implements Sink {
	private final StyleCatalog styles;
	private final File output;
	private final boolean wantOpen;
	private final String upload;
	private final boolean debug;
	private final String sshid;
	private final PDDocument doc;
	private PDPageContentStream currentPage;
	private float bottomY;
	private List<Line> bottomLines = new ArrayList<>();
	
	private float y;
	private PageStyle pageStyle;
	private float afterBlock;
	private boolean showBorder = false;
	private int pageNum = 1;

	public PDFSink(File root, StyleCatalog styles, String output, boolean wantOpen, String upload, boolean debug, String sshid) throws IOException {
		this.styles = styles;
		this.debug = debug;
		this.sshid = sshid;
		File f = new File(output);
		if (f.isAbsolute())
			this.output = f;
		else
			this.output = new File(root, output);
		this.wantOpen = wantOpen;
		this.upload = upload;
		doc = new PDDocument();
		pageStyle = new DefaultPageStyle();
		
		try {
			loadFonts();
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	private void loadFonts() throws IOException {
		styles.fonts().put("monospace", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/MonospaceRegular.ttf")));
		styles.fonts().put("monospace-bold", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/MonospaceBold.ttf")));
		styles.fonts().put("monospace-oblique", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/MonospaceOblique.ttf")));
		styles.fonts().put("palatino", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/Palatino.ttf")));
		styles.fonts().put("palatino-bold", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/Palatino Bold.ttf")));
		styles.fonts().put("palatino-italic", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/Palatino Italic.ttf")));
		styles.fonts().put("palatino-bolditalic", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/Palatino Bold Italic.ttf")));
	}

	@Override
	public void title(String title) throws IOException {
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
		String bsname = block.getStyle();
//		if (debug)
//			System.out.println("base style is " + bsname);
		Style baseStyle = styles.getOptional(bsname);
		if (baseStyle == null)
			throw new RuntimeException("no style found for " + bsname);
		List<Line> lines = new ArrayList<>();
		List<Segment> segments = new ArrayList<>();
		Float ifm = baseStyle.getFirstMargin();
		float lm = pageStyle.getLeftMargin() + baseStyle.getLeftMargin();
		if (ifm == null)
			ifm = lm;
		else
			ifm += pageStyle.getLeftMargin();
		float fm = ifm;
		float rm = pageStyle.getRightMargin() + baseStyle.getRightMargin();
		float wid = pageStyle.getPageWidth() - fm - rm;
		boolean inlink = false;
		for (Span s : block) {
//			if (debug)
//				System.out.println("span styles: " + s.getStyles());
			if (s.getStyles().contains("endlink"))
				inlink = false;
			else if (inlink)
				continue;
			else if (s.getStyles().contains("link"))
				inlink = true;
			Style style = baseStyle.apply(s.getStyles());
			if (baseStyle.isPreformatted()) {
				addSegment(lines, segments, wid, baseStyle, style, fm, rm, s.getText(), false);
				Float onl = style.getOverflowNewLine();
				if (onl != null && new Segment(baseStyle, s.getText()).width() > style.getWidth()) {
					finishLine(lines, segments, wid, baseStyle, fm, rm);
					segments.clear();
					fm = ifm + onl;
				}
				continue;
			}
			String tx = s.getText();
			String[] parts = tx.split(" ");
			/** TODO: we need to revisit this in such a way that we collect together one or more segments from parts
			 * and then either add them all or none of them (and move to the next line).
			 * We don't want to split except at designated splitting points (i.e. " ").
			 */
			boolean first = true;
			for (String p : parts) {
				if (p == null || p.length() == 0)
					continue;
				if (!first || tx.startsWith(" ")) {
					addSegment(lines, segments, wid, baseStyle, style, fm, rm, " ", false);
				}
				addSegment(lines, segments, wid, baseStyle, style, fm, rm, p, true);
				if (!lines.isEmpty()) {
					wid = pageStyle.getPageWidth() - lm - rm;
					fm = lm;
				}
				first = false;
			}
			if (tx.endsWith(" "))
				addSegment(lines, segments, wid, baseStyle, style, fm, rm, " ", false);
		}
		if (!segments.isEmpty())
			finishLine(lines, segments, wid, baseStyle, fm, rm);
		
		RenderInfo ret = new RenderInfo(baseStyle.beginNewPage(), baseStyle.showAtBottom(), Math.max(afterBlock, baseStyle.getBeforeBlock()), lines);
		afterBlock = baseStyle.getAfterBlock();
		return ret;
	}
	
	private float blockht(List<Line> lines) throws IOException {
		float ret = 0;
		for (Line l : lines) {
			ret += l.height();
		}
		return ret;
	}

	private void render(List<RenderInfo> ris) throws IOException {
		int k =0;
		while (k < ris.size()) {
			if (ris.get(k).showAtBottom) {
				RenderInfo ri = ris.remove(k);
				if (bottomLines.isEmpty())
					bottomY += 8; // to allow for the HR
				bottomLines.addAll(ri.lines);
				bottomY += blockht(ri.lines);
			} else
				k++;
		}
		if (!blocksFit(bottomY, y, ris)) {
			closeCurrentPage();
		}
		for (RenderInfo ri : ris) {
			if (ri.beginNewPage) {
				closeCurrentPage();
			}
			boolean created = ensurePage();
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
		boolean ret = true;
		for (RenderInfo ri : ris) {
			ret &= blockFits(ypos, bottomMargin, ri);
		}
		return ret;
	}

	private boolean blockFits(float ypos, float bottomMargin, RenderInfo ri) throws IOException {
		ypos -= ri.beforeBlock;
		for (Line l : ri.lines) {
			if (l.requiresMoreThan(ypos-bottomMargin))
				return false;
			ypos -= l.height();
		}
		return ypos > bottomMargin;
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
	public void brk(Break brk) throws IOException {
		if (y-brk.require() < bottomY) {
			closeCurrentPage();
			ensurePage();
		}
		float top = brk.top();
		float btm = brk.bottom();
		float skip = brk.total();
		if (brk.box()) {
			currentPage.setLineWidth(1.0f);
			currentPage.moveTo(pageStyle.getLeftMargin(), y-top);
			currentPage.lineTo(pageStyle.getPageWidth() - pageStyle.getRightMargin(), y-top);
			currentPage.lineTo(pageStyle.getPageWidth() - pageStyle.getRightMargin(), y-btm);
			currentPage.lineTo(pageStyle.getLeftMargin(), y-btm);
			currentPage.closeAndStroke();
		} else if (brk.horizLines()) {
			currentPage.setLineWidth(1.0f);
			currentPage.moveTo(pageStyle.getLeftMargin(), y-top);
			currentPage.lineTo(pageStyle.getPageWidth() - pageStyle.getRightMargin(), y-top);
			currentPage.moveTo(pageStyle.getLeftMargin(), y-btm);
			currentPage.lineTo(pageStyle.getPageWidth() - pageStyle.getRightMargin(), y-btm);
			currentPage.closeAndStroke();
		} else
			throw new RuntimeException("What is the plan?");
		PDFont pnf = brk.textFont(styles, pageStyle);
		float pns = brk.fontSize(pageStyle);
		currentPage.setFont(pnf, pns);
		String tx = brk.boxText();
		float txl = pnf.getStringWidth(tx)*pns/1000; 
		currentPage.beginText();
		currentPage.setFont(pnf, pns);
		currentPage.newLineAtOffset(pageStyle.pageNumberCenterX() - txl/2, y-brk.textY());
		currentPage.showText(tx);
		currentPage.endText();
		if (brk.newPageAfter()) {
			closeCurrentPage();
		} else {
			y -= skip;
		}
	}

	@Override
	public void fileEnd() {
		// no big deal
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
			if (debug)
				System.out.println("Opening " + output);
			Desktop.getDesktop().open(output);
		} catch (Exception e) {
			System.out.println("Failed to open " + output + " on desktop:\n  " + e.getMessage());
		}
	}
	
	@Override
	public void upload() throws JSchException, SftpException {
		if (upload != null) {
			new Upload(output, upload, sshid, true).send();
		}
	}

	private boolean ensurePage() throws IOException {
		if (currentPage == null) {
			PDPage page = new PDPage();
			doc.addPage(page);
			currentPage = new PDPageContentStream(doc, page);
			if (showBorder) {
				currentPage.setLineWidth(0.3f);
				currentPage.moveTo(pageStyle.getLeftMargin(), pageStyle.getBottomMargin());
				currentPage.lineTo(pageStyle.getLeftMargin(), pageStyle.getPageHeight()-pageStyle.getTopMargin());
				currentPage.lineTo(pageStyle.getPageWidth() - pageStyle.getRightMargin(), pageStyle.getPageHeight()-pageStyle.getTopMargin());
				currentPage.lineTo(pageStyle.getPageWidth() - pageStyle.getRightMargin(), pageStyle.getBottomMargin());
				currentPage.closeAndStroke();
			}
			if (pageStyle.wantPageNumbers()) {
				PDFont pnf = pageStyle.getPageNumberFont();
				float pns = pageStyle.getPageNumberFontSize();
				String tx = "Page " + pageNum++;
				float txl = pnf.getStringWidth(tx)*pns/1000; 
				currentPage.beginText();
				currentPage.setFont(pnf, pns);
				currentPage.newLineAtOffset(pageStyle.pageNumberCenterX() - txl/2, pageStyle.pageNumberBaselineY());
				currentPage.showText(tx);
				currentPage.endText();
			}
			y = pageStyle.getPageHeight() - pageStyle.getTopMargin();
			bottomY = pageStyle.getBottomMargin();
			// if we have straggling footnote content, consider that here
			if (!bottomLines.isEmpty()) {
				bottomY += blockht(bottomLines) + 8;
			}
			afterBlock = 0;
			return true;
		}
		return false;
	}

	private void closeCurrentPage() throws IOException {
		if (currentPage != null) {
			if (!bottomLines.isEmpty()) {
				float fromY = Math.min(this.y, bottomY);
				float actualBottom = pageStyle.getBottomMargin();
				currentPage.setLineWidth(0.5f);
				currentPage.moveTo(pageStyle.getLeftMargin(), fromY);
				currentPage.lineTo(pageStyle.getPageWidth() - pageStyle.getRightMargin(), fromY);
				currentPage.closeAndStroke();
				fromY -= 5;
				while (!bottomLines.isEmpty()) { // while they fit ...
					Line l = bottomLines.remove(0);
					fromY -= l.height();
					if (fromY < actualBottom) {
						bottomLines.add(0, l);
						break;
					}
					l.render(currentPage, fromY);
				}
				// if they didn't all fit, some will be left over in the array
			}
			currentPage.close();
			currentPage = null;
			afterBlock = 0;
		}
	}
}
