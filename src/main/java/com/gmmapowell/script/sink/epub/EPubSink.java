package com.gmmapowell.script.sink.epub;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.utils.FileUtils;
import org.zinutils.xml.XML;
import org.zinutils.xml.XMLElement;
import org.zinutils.xml.XMLNamespace;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.pdf.Cursor;
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
//		if (!options.containsKey("stock")) {
//			throw new ConfigException("must specify a stock to render to");
//		}
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
			zos.putNextEntry(new ZipEntry("META-INF/container.xml"));
			FileUtils.writeToStream(makeContainer(), zos);
			// TODO: encryption
//		stock.newDocument(styles);
//		List<Flow> mainFlows = new ArrayList<>();
//		for (Flow f : flows) {
//			if (f.isMain()) {
//				mainFlows.add(f);
//			}
//		}
//		int i=0;
//		Map<String, String> current = new TreeMap<>();
//		Set<Cursor> sections = new TreeSet<>();
//		PageCompositor page = null;
//		boolean beginSection = false;
//		forever:
//		while (true) {
//			for (Flow f : mainFlows) {
//				if (f.sections.size() > i) {
//					Section si = f.sections.get(i);
//					current.put(f.name, si.format);
//					sections.add(new Cursor(f.name, si));
//					beginSection = true;
//				}
//			}
//			i++;
//			if (sections.isEmpty())
//				break forever;
//				
//			List<Suspension> suspended = new ArrayList<>();
//			Set<AnchorOp> records = new HashSet<>();
//			while (!sections.isEmpty()) {
//				if (page == null) {
//					page = stock.getPage(current, beginSection);
//					page.begin();
//				}
//				beginSection = false;
//				Set<Cursor> active = new TreeSet<>(sections);
//				whileActive:
//				while (!active.isEmpty()) {
//					for (Cursor c : active) { // try and populate each main section
//						while (true) {
//							StyledToken tok = c.next();
//							if (tok == null) {
//								sections.remove(c);
//								active.remove(c);
//								continue whileActive;
//							}
////							System.out.println(tok);
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
//							case SUSPEND: // we cannot proceed until we have seen something from elsewhere
//								suspended.add(new Suspension(c, a.lastAccepted));
//								Cursor en = findFlow(suspended, sections, a.enable());
//								if (en == c) {
//									throw new CantHappenException("can't enable the one you're suspending");
//								}
//								active.add(en);
//								sections.add(en);
//								active.remove(c);
//								sections.remove(c);
//								continue whileActive;
//							}
//						}
//					}
//				}
//				boolean advanced = page.nextRegions();
//				if (!advanced) {
//					page = null;
//				}
//			}
//			if (!suspended.isEmpty())
//				throw new CantHappenException("suspended is not empty: " + suspended);
//		}
//		stock.close(output);]
			String bookId = "bookId";
			String title = "Ziniki Developer Guide";
			String identifier = "ziniki-developer-guide";
			zos.putNextEntry(new ZipEntry("OPS/package.opf"));
			FileUtils.writeToStream(makePackage(bookId, title, identifier), zos);
			zos.putNextEntry(new ZipEntry("OPS/toc.ncx"));
			FileUtils.writeToStream(makeTOC(title, identifier), zos);
			zos.putNextEntry(new ZipEntry("OPS/Files/intro.xhtml"));
			FileUtils.writeToStream("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>" + title + "</title></head><body><h1>Hello, World</h1></body></html>", zos);
		}
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

	private String makePackage(String bookId, String title, String identifier) {
		XML xml = XML.createNS("1.0", "package", "http://www.idpf.org/2007/opf");
//		XMLNamespace xns = xml.namespace("xml", "http://www.w3.org/XML/1998/namespace");
		XMLElement pkg = xml.top();
		pkg.setAttribute("version", "2.0");
//		pkg.setAttribute(xns.attr("lang"), "en");
		pkg.setAttribute("unique-identifier", bookId);

		// metadata
		XMLElement md = pkg.addElement("metadata");
		XMLNamespace opf = md.namespace("opf", "http://www.idpf.org/2007/opf");
		XMLNamespace dc = md.namespace("dc", "http://purl.org/dc/elements/1.1/");
		
		XMLElement ident = md.addElement(dc.tag("identifier"));
		ident.setAttribute("id", bookId);
		ident.addText(identifier);
		XMLElement titleElt = md.addElement(dc.tag("title"));
		titleElt.setAttribute("id", "title");
		titleElt.addText(title);
		XMLElement date = md.addElement(dc.tag("date"));
		DateFormat df = new SimpleDateFormat("YYYY-MM-dd");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		date.addText(df.format(new Date()));
		XMLElement creator = md.addElement(dc.tag("creator"));
		creator.addText("Ziniki");
		XMLElement lang = md.addElement(dc.tag("language"));
		lang.addText("en-US");
		XMLElement modified = md.addElement(dc.tag("date"));
		modified.setAttribute(opf.attr("event"), "modification");

		DateFormat iso = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss'Z'");
		iso.setTimeZone(TimeZone.getTimeZone("UTC"));
		modified.addText(iso.format(new Date()));
		
		// manifest
		XMLElement man = pkg.addElement("manifest");
		XMLElement toc = man.addElement("item");
		toc.setAttribute("id", "ncx");
		toc.setAttribute("href", "toc.ncx");
		toc.setAttribute("media-type", "application/x-dtbncx+xml");
		// spine
		XMLElement spine = pkg.addElement("spine");
		spine.setAttribute("toc", "ncx");

		XMLElement item = man.addElement("item");
		item.setAttribute("id", "intro");
		item.setAttribute("href", "Files/intro.xhtml");
		item.setAttribute("media-type", "application/xhtml+xml");
		
		XMLElement ir = spine.addElement("itemref");
		ir.setAttribute("idref", "intro");
		
		// guide is deprecated, so let's try and avoid it ...
		
		return pkg.serialize();
	}

	private String makeTOC(String title, String identifier) {
		XML xml = XML.createNS("1.0", "ncx", "http://www.daisy.org/z3986/2005/ncx/");
		XMLElement ncx = xml.top();
		ncx.setAttribute("version", "2005-1");
		XMLElement head = ncx.addElement("head");
		XMLElement metaUid = head.addElement("meta");
		metaUid.setAttribute("name", "dtb:uid");
		metaUid.setAttribute("content", identifier);
		XMLElement docTitle = ncx.addElement("docTitle");
		XMLElement docText = docTitle.addElement("text");
		docText.addText(title);
		XMLElement map = ncx.addElement("navMap");
		XMLElement point = map.addElement("navPoint");
		point.setAttribute("id", "point1");
		point.setAttribute("playOrder", "1");
		XMLElement nl = point.addElement("navLabel");
		XMLElement nlt = nl.addElement("text");
		nlt.addText("Introduction");
		XMLElement npc = point.addElement("content");
		npc.setAttribute("src", "Files/intro.xhtml");
		return ncx.serialize();
	}

	private Cursor findFlow(List<Suspension> suspended, Set<Cursor> sections, String enable) {
		for (Suspension susp : suspended) {
			if (susp.isFlow(enable)) {
				suspended.remove(susp);
				return susp.cursor;
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
