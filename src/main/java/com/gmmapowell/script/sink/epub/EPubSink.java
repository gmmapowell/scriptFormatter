package com.gmmapowell.script.sink.epub;

import java.awt.Desktop;
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

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.Cursor;
import com.gmmapowell.script.flow.CursorClient;
import com.gmmapowell.script.flow.CursorFeedback;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.FlowCursor;
import com.gmmapowell.script.flow.NonBreakingSpace;
import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.flow.YieldToFlow;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.utils.Upload;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class EPubSink implements Sink, CursorClient {
//	private final StyleCatalog styles;
	private final Place output;
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

	public EPubSink(Region root, StyleCatalog styles, String output, boolean wantOpen, String upload, boolean debug, String sshid, VarMap vars) throws IOException, ConfigException {
//		this.styles = styles;
		this.debug = debug;
		this.sshid = sshid;
		this.output = root.ensurePlace(output);
		this.wantOpen = wantOpen;
		this.upload = upload;
//		String stockName = null;
		if (!vars.containsKey("bookid")) {
			throw new ConfigException("must specify a bookid");
		}
		if (!vars.containsKey("title")) {
			throw new ConfigException("must specify a title");
		}
		if (!vars.containsKey("identifier")) {
			throw new ConfigException("must specify a book identifier");
		}
		if (!vars.containsKey("author")) {
			throw new ConfigException("must specify a book author");
		}
		bookId = vars.remove("bookid");
		title = vars.remove("title");
		identifier = vars.remove("identifier");
		author = vars.remove("author");
//		stockName = options.remove("stock");
//		stock = styles.getStock(stockName);
	}

	public void prepare() {
	}
	
	@Override
	public void flow(Flow flow) {
		this.flows.add(flow);
	}
	
	@Override
	public void render() throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(GeoFSUtils.saveStreamTo(output))) {
			this.zos = zos;
			makeMimetype(zos);
			zos.putNextEntry(new ZipEntry("META-INF/container.xml"));
			FileUtils.writeToStream(makeContainer(), zos);
			this.opf = new OPFCreator(bookId, identifier, author, title);
			this.toc = new TOCCreator(identifier, title);
			// TODO: encryption?

			FlowCursor c = new FlowCursor(flows);
			c.run(this); // TODO: I feel that actually it should be a newly created object ...

//			extracted(zos, opf, toc);
			zos.putNextEntry(new ZipEntry("OPS/package.opf"));
			FileUtils.writeToStream(opf.makePackage(), zos);
			zos.putNextEntry(new ZipEntry("OPS/toc.ncx"));
			FileUtils.writeToStream(toc.makeNCX(), zos);
			zos.putNextEntry(new ZipEntry("OPS/Files/toc.xhtml"));
			FileUtils.writeToStream(toc.makeHTML(), zos);
		}
	}

	private void extracted(ZipOutputStream zos, OPFCreator opf, TOCCreator toc) throws IOException {
		List<Flow> mainFlows = new ArrayList<>();
		for (Flow f : flows) {
			if (f.isMain()) {
				mainFlows.add(f);
			}
		}
		int i=0;
		Map<String, String> current = new TreeMap<>();
		Set<Cursor> sections = new TreeSet<>();
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
							else if (tok.it instanceof EPubAware)
								((EPubAware)tok.it).handle(coll);
							else {
								System.out.println("cannot handle " + tok.it.getClass());
							}
						}
					}
				}
				addFile(zos, opf, toc, i, title.toString());
				title = null;
				coll.write(zos);
			}
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

	Map<String, String> current = new TreeMap<>();
	StringBuilder titleBuilder = new StringBuilder();
	XHTMLCollector coll = new XHTMLCollector();
	private ZipOutputStream zos;
	private OPFCreator opf;
	private TOCCreator toc;
	int chapNo = 0;
	
	@Override
	public void beginSection(Set<Cursor> cursors) {
		for (Cursor c : cursors) {
			current.put(c.flowName(), c.format());
		}
		++chapNo;
	}

	@Override
	public boolean processToken(CursorFeedback cursor, StyledToken tok) throws IOException {
		if (tok.it instanceof YieldToFlow) {
//			suspended.add(c);
//			Cursor en = findFlow(suspended, sections, ((YieldToFlow)tok.it).yieldTo());
//			if (en == c) {
//				throw new CantHappenException("can't enable the one you're suspending");
//			}
//			active.add(en);
//			sections.add(en);
//			active.remove(c);
//			sections.remove(c);
//			continue whileActive;
			cursor.suspend(tok, ((YieldToFlow) tok.it).yieldTo());
			return false;
		}
		if (titleBuilder != null && tok.styles.contains("chapter-title")) {
			if (tok.it instanceof TextSpanItem) {
				TextSpanItem tsi = (TextSpanItem) tok.it;
				titleBuilder.append(tsi.text);
			} else if (tok.it instanceof ParaBreak) {
				coll.title(titleBuilder.toString());
//				System.out.println("title: " + title);
			} else if (tok.it instanceof BreakingSpace || tok.it instanceof NonBreakingSpace) {
				titleBuilder.append(" ");
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
		else if (tok.it instanceof EPubAware)
			((EPubAware)tok.it).handle(coll);
		else {
			System.out.println("cannot handle " + tok.it.getClass());
		}
		return true;
	}

	@Override
	public void endSection() throws IOException {
		addFile(zos, opf, toc, chapNo, titleBuilder.toString());
		titleBuilder = new StringBuilder();
		coll.write(zos);
		coll = new XHTMLCollector();
	}

	@Override
	public void showFinal() {
		if (!wantOpen)
			return;
		try {
			if (debug)
				System.out.println("Opening " + output);
			Desktop.getDesktop().open(GeoFSUtils.file(output));
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

	@Override
	public void finish() throws Exception {
	}
}
