package com.gmmapowell.script.sink.epub;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.zinutils.xml.XML;
import org.zinutils.xml.XMLElement;
import org.zinutils.xml.XMLNamespace;

public class OPFCreator {
	private final String bookId;
	private final String identifier;
	private final String author;
	private final String title;
	private final XML xml;
	private final XMLElement pkg;
	private final XMLElement man;
	private final XMLElement spine;
	private XMLElement md;

	public OPFCreator(String bookId, String identifier, String author, String title) {
		this.bookId = bookId;
		this.identifier = identifier;
		this.author = author;
		this.title = title;
		this.xml = XML.createNS("1.0", "package", "http://www.idpf.org/2007/opf");
		pkg = xml.top();
		md = pkg.addElement("metadata");
		man = pkg.addElement("manifest");
		spine = pkg.addElement("spine");
		
		setup();
	}

	private void setup() {
		pkg.setAttribute("version", "3.0");
		pkg.setAttribute("unique-identifier", bookId);

		/*XMLNamespace opf = */md.namespace("opf", "http://www.idpf.org/2007/opf");
		XMLNamespace dc = md.namespace("dc", "http://purl.org/dc/elements/1.1/");
		/*XMLNamespace dcterms = */md.namespace("dcterms", "http://purl.org/dc/terms/");
		
		XMLElement ident = md.addElement(dc.tag("identifier"));
		ident.setAttribute("id", bookId);
		ident.addText(identifier);
		XMLElement titleElt = md.addElement(dc.tag("title"));
		titleElt.setAttribute("id", "title");
		titleElt.addText(title);
//		XMLElement date = md.addElement(dc.tag("date"));
//		DateFormat df = new SimpleDateFormat("YYYY-MM-dd");
//		df.setTimeZone(TimeZone.getTimeZone("UTC"));
//		date.addText(df.format(new Date()));
		XMLElement creator = md.addElement(dc.tag("creator"));
		creator.addText(author);
		XMLElement lang = md.addElement(dc.tag("language"));
		lang.addText("en-US");
		XMLElement modified = md.addElement("meta");
		modified.setAttribute("property", "dcterms:modified");
//		modified.setAttribute(opf.attr("event"), "modification");

		DateFormat iso = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss'Z'");
		iso.setTimeZone(TimeZone.getTimeZone("UTC"));
		modified.addText(iso.format(new Date()));
		
		XMLElement toc = man.addElement("item");
		toc.setAttribute("id", "ncx");
		toc.setAttribute("href", "toc.ncx");
		toc.setAttribute("media-type", "application/x-dtbncx+xml");
		
		spine.setAttribute("toc", "ncx");
		
		addFile("toc");
		List<XMLElement> mcn = man.elementChildren();
		mcn.get(mcn.size()-1).setAttribute("properties", "nav");
		List<XMLElement> scn = spine.elementChildren();
		scn.get(scn.size()-1).setAttribute("linear", "yes");

		// guide is deprecated, so let's try and avoid it ...
	}

	public void addFile(String name) {
		XMLElement item = man.addElement("item");
		item.setAttribute("id", name);
		item.setAttribute("href", "Files/" + name + ".xhtml");
		item.setAttribute("media-type", "application/xhtml+xml");
		
		XMLElement ir = spine.addElement("itemref");
		ir.setAttribute("idref", name);
	}
	
	public String makePackage() {
		return pkg.serialize();
	}
}
