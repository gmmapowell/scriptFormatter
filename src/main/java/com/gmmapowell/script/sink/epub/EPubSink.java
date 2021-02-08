package com.gmmapowell.script.sink.epub;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.utils.FileUtils;
import org.zinutils.xml.XML;
import org.zinutils.xml.XMLElement;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.NonBreakingSpace;
import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.flow.YieldToFlow;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.pdf.Cursor;
import com.gmmapowell.script.sink.pdf.StyledToken;
import com.gmmapowell.script.sink.pdf.Suspension;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.utils.Upload;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class EPubSink implements Sink {
	private final StyleCatalog styles;
	private final File output;
	private final boolean wantOpen;
	private final String upload;
	private final boolean debug;
	private final String sshid;
	private final List<Flow> flows = new ArrayList<>();
//	private final Stock stock;
	private final String bookId;
	private final String title;
	private final String identifier;
	private final String author;

	public EPubSink(File root, StyleCatalog styles, String output, boolean wantOpen, String upload, boolean debug, String sshid, Map<String, String> options) throws IOException, ConfigException {
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
//		String stockName = null;
		if (!options.containsKey("bookid")) {
			throw new ConfigException("must specify a bookid");
		}
		if (!options.containsKey("title")) {
			throw new ConfigException("must specify a title");
		}
		if (!options.containsKey("identifier")) {
			throw new ConfigException("must specify a book identifier");
		}
		if (!options.containsKey("author")) {
			throw new ConfigException("must specify a book author");
		}
		bookId = options.remove("bookid");
		title = options.remove("title");
		identifier = options.remove("identifier");
		author = options.remove("author");
//		stockName = options.remove("stock");
//		stock = styles.getStock(stockName);
	}

	@Override
	public void flow(Flow flow) {
		this.flows.add(flow);
	}
	
	@Override
	public void render() throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output))) {
			makeMimetype(zos);
			zos.putNextEntry(new ZipEntry("META-INF/container.xml"));
			FileUtils.writeToStream(makeContainer(), zos);
			OPFCreator opf = new OPFCreator(bookId, identifier, author, title);
			TOCCreator toc = new TOCCreator(identifier, title);
			// TODO: encryption?

//		stock.newDocument(styles);
		List<Flow> mainFlows = new ArrayList<>();
		for (Flow f : flows) {
			if (f.isMain()) {
				mainFlows.add(f);
			}
		}
		int i=0;
		Map<String, String> current = new TreeMap<>();
		Set<Cursor> sections = new TreeSet<>();
//		PageCompositor page = null;
//		forever:
		while (true) {
			for (Flow f : mainFlows) {
				if (f.sections.size() > i) {
					Section si = f.sections.get(i);
					current.put(f.name, si.format);
					sections.add(new Cursor(f.name, si));
				}
			}
			i++;
			if (sections.isEmpty())
				break;

			XHTMLCollector coll = new XHTMLCollector();
			
			StringBuilder title = new StringBuilder();
			
			List<Cursor> suspended = new ArrayList<>();
//			Set<AnchorOp> records = new HashSet<>();
			while (!sections.isEmpty()) {
				Set<Cursor> active = new TreeSet<>(sections);
				whileActive:
				while (!active.isEmpty()) {
					for (Cursor c : active) { // try and populate each main section
						while (true) {
							StyledToken tok = c.next();
							if (tok == null) {
								sections.remove(c);
								active.remove(c);
								continue whileActive;
							}
//							System.out.println(tok);
							if (tok.it instanceof YieldToFlow) {
								suspended.add(c);
								Cursor en = findFlow(suspended, sections, ((YieldToFlow)tok.it).yieldTo());
								if (en == c) {
									throw new CantHappenException("can't enable the one you're suspending");
								}
								active.add(en);
								sections.add(en);
								active.remove(c);
								sections.remove(c);
								continue whileActive;
							}
							if (title != null && tok.styles.contains("chapter-title")) {
								if (tok.it instanceof TextSpanItem) {
									TextSpanItem tsi = (TextSpanItem) tok.it;
									title.append(tsi.text);
								} else if (tok.it instanceof ParaBreak) {
									coll.title(title.toString());
//									System.out.println("title: " + title);
									addFile(zos, opf, toc, i, title.toString());
									
									title = null;
								} else if (tok.it instanceof BreakingSpace || tok.it instanceof NonBreakingSpace) {
									title.append(" ");
								}
							}
							coll.styles(tok.styles);
							if (tok.it instanceof TextSpanItem) {
								coll.text(((TextSpanItem)tok.it).text);
							} else if (tok.it instanceof NonBreakingSpace)
								coll.text("&nbsp;");
							else if (tok.it instanceof BreakingSpace)
								coll.text(" ");
							else if (tok.it instanceof ParaBreak)
								coll.closePara();
//							if (tok.it instanceof AnchorOp) {
//								records.add((AnchorOp)tok.it);
//								continue;
//							}
//							if (tok.it instanceof ReleaseFlow) {
//								Cursor en = findFlow(suspended, sections, ((ReleaseFlow)tok.it).release());
//								if (en == c) {
//									throw new CantHappenException("can't enable the one you're suspending");
//								}
//								active.add(en);
//								sections.add(en);
//								continue whileActive;
//							}
//							Acceptance a = page.token(tok);
//							if (a == null) {
//								System.out.println("---- a == null, for " + tok);
//								continue;
//							}
//							switch (a.status) {
//							case PENDING: // it thinks it may accept it but it may end up having to reject it
//								break;
//							case PROCESSED: {// it has taken it and fully processed it
//								for (AnchorOp anch : records) {
//									anch.recordPage(page.meta(), page.currentPageName());
//								}
//								records.clear();
//								break;
//							}
//							case BACKUP: // we are being asked to try again, probably new region
//								c.backTo(a.lastAccepted);
//								continue;
//							case NOROOM: // we are done; the outlet is full
//								c.backTo(a.lastAccepted);
//								active.remove(c);
//								records.clear();
//								continue whileActive;
						}
					}
				}
//				boolean advanced = page.nextRegions();
//				if (!advanced) {
//					page = null;
//				}
				coll.write(zos);
			}
//			if (!suspended.isEmpty())
//				throw new CantHappenException("suspended is not empty: " + suspended);
		}
//		stock.close(output);]
			zos.putNextEntry(new ZipEntry("OPS/package.opf"));
			FileUtils.writeToStream(opf.makePackage(), zos);
			zos.putNextEntry(new ZipEntry("OPS/toc.ncx"));
			FileUtils.writeToStream(toc.makeTOC(), zos);
		}
	}

	private void makeMimetype(ZipOutputStream zos) throws IOException {
		ZipEntry zem = new ZipEntry("mimetype");
		String text = "application/epub+zip";
		zem.setMethod(ZipEntry.STORED);
		zem.setSize(text.length());
		zem.setCompressedSize(text.length());
		CRC32 crc = new CRC32();
		crc.update(text.getBytes());
		zem.setCrc(crc.getValue());
		zos.putNextEntry(zem);
		FileUtils.writeToStream(text, zos);
	}

	private String makeContainer() {
		XML xml = XML.createNS("1.0", "container", "urn:oasis:names:tc:opendocument:xmlns:container");
		XMLElement container = xml.top();
		container.setAttribute("version", "1.0");
		XMLElement rootfiles = container.addElement("rootfiles");
		XMLElement rootfile = rootfiles.addElement("rootfile");
		rootfile.setAttribute("full-path", "OPS/package.opf");
		rootfile.setAttribute("media-type", "application/oebps-package+xml");
		return xml.top().serialize();
	}

	private void addFile(ZipOutputStream zos, OPFCreator opf, TOCCreator toc, int cnt, String title) throws IOException {
		String file = "file" + cnt;
		opf.addFile(file);
		toc.addEntry(title, file, cnt);
		zos.putNextEntry(new ZipEntry("OPS/Files/" + file + ".xhtml"));
	}

	private Cursor findFlow(List<Cursor> suspended, Set<Cursor> sections, String enable) {
		for (Cursor susp : suspended) {
			if (susp.isFlow(enable)) {
				suspended.remove(susp);
				return susp;
			}
		}
		for (Cursor c : sections) {
			if (c.isFlow(enable))
				return c;
		}
		throw new CantHappenException("could not enable flow " + enable + " because it did not exist");
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
}
