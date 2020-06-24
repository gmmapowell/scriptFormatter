package com.gmmapowell.script.sink.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

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
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class PDFSink implements Sink {
	private final StyleCatalog styles;
	private final File output;
	private final boolean wantOpen;
	private final String upload;
	private final boolean debug;
	private final PDDocument doc;
	private PDPageContentStream currentPage;
	
	private float y;
	private PageStyle pageStyle;
	private float afterBlock;
	private boolean showBorder = false;
	private int pageNum = 1;

	public PDFSink(File root, StyleCatalog styles, String output, boolean wantOpen, String upload, boolean debug) {
		this.styles = styles;
		this.debug = debug;
		File f = new File(output);
		if (f.isAbsolute())
			this.output = f;
		else
			this.output = new File(root, output);
		this.wantOpen = wantOpen;
		this.upload = upload;
		doc = new PDDocument();
		pageStyle = new DefaultPageStyle();
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
		Style baseStyle = styles.get(block.getStyle());
		List<Line> lines = new ArrayList<>();
		List<Segment> segments = new ArrayList<>();
		Float fm = baseStyle.getFirstMargin();
		float lm = pageStyle.getLeftMargin() + baseStyle.getLeftMargin();
		if (fm == null)
			fm = lm;
		else
			fm += pageStyle.getLeftMargin();
		float rm = pageStyle.getRightMargin() + baseStyle.getRightMargin();
		float wid = pageStyle.getPageWidth() - fm - rm;
		for (Span s : block) {
			Style style = baseStyle.apply(s.getStyles());
			String tx = s.getText();
			String[] parts = tx.split(" ");
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
	public void brk(Break ad) throws IOException {
		// I think the brk should have more info in it
		currentPage.moveTo(pageStyle.getLeftMargin(), y-10);
		currentPage.lineTo(pageStyle.getPageWidth() - pageStyle.getRightMargin(), y-10);
		currentPage.lineTo(pageStyle.getPageWidth() - pageStyle.getRightMargin(), y-42);
		currentPage.lineTo(pageStyle.getLeftMargin(), y-42);
		currentPage.closeAndStroke();
		PDFont pnf = pageStyle.getPageNumberFont();
		float pns = pageStyle.getPageNumberFontSize();
		currentPage.setFont(pnf, pns);
		String tx = "AD BREAK";
		float txl = pnf.getStringWidth(tx)*pns/1000; 
		currentPage.beginText();
		currentPage.setFont(pnf, pns);
		currentPage.newLineAtOffset(pageStyle.pageNumberCenterX() - txl/2, y-27);
		currentPage.showText(tx);
		currentPage.endText();
		closeCurrentPage();
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
			if (debug)
				System.out.println("uploading to " + upload);
			Pattern p = Pattern.compile("sftp:([a-zA-Z0-9_]+)@([a-zA-Z0-9_.]+)(:[0-9]+)?/(.+)");
			Matcher matcher = p.matcher(upload);
			if (!matcher.matches())
				throw new RuntimeException("Could not match path " + upload);
			
			String username = matcher.group(1);
			String host = matcher.group(2);
			int port = 22;
			if (matcher.group(3) != null)
				port = Integer.parseInt(matcher.group(3).substring(1));
			String to = matcher.group(4);
			
			File privateKeyPath = new File(System.getProperty("user.home"), ".ssh/id_rsa_dorothy");
			JSch jsch = new JSch();
			jsch.addIdentity(privateKeyPath.getPath());
			Session s = null;
			try {
				s = jsch.getSession(username, host, port);
				s.setConfig("StrictHostKeyChecking", "no");
				s.connect();
				ChannelSftp openChannel = (ChannelSftp) s.openChannel("sftp");
				openChannel.connect();
				openChannel.put(output.getPath(), to);
			} finally {
				if (s != null)
					s.disconnect();
			}
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
