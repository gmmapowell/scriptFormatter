package com.gmmapowell.script.sink.epub;

import org.zinutils.xml.XML;
import org.zinutils.xml.XMLElement;
import org.zinutils.xml.XMLNamespace;

public class TOCCreator {
	private final XMLElement ncx;
	private final XMLElement map;
	private final XMLElement html;
	private final XMLElement ul;

	public TOCCreator(String identifier, String title) {
		XML xml = XML.createNS("1.0", "ncx", "http://www.daisy.org/z3986/2005/ncx/");
		ncx = xml.top();
		ncx.setAttribute("version", "2005-1");
		XMLElement head = ncx.addElement("head");
		XMLElement metaUid = head.addElement("meta");
		metaUid.setAttribute("name", "dtb:uid");
		metaUid.setAttribute("content", identifier);
		XMLElement docTitle = ncx.addElement("docTitle");
		XMLElement docText = docTitle.addElement("text");
		docText.addText(title);
		map = ncx.addElement("navMap");

		html = XML.createNS("1.0", "html", "http://www.w3.org/1999/xhtml").top();
		XMLNamespace epub = html.namespace("epub", "http://www.idpf.org/2007/ops");
		XMLElement hhead = html.addElement("head");
		hhead.addElement("title").addText(title);
		XMLElement body = html.addElement("body");
		body.addElement("h1").addText(title);
		XMLElement hnav = body.addElement("nav");
		hnav.setAttribute("id", "toc");
		hnav.setAttribute(epub.attr("type"), "toc");
		ul = hnav.addElement("ul");
	}

	public void addEntry(String title, String file, int idx) {
		String href = file + ".xhtml";

		XMLElement point = map.addElement("navPoint");
		point.setAttribute("id", "point" + idx);
		point.setAttribute("playOrder", Integer.toString(idx));
		XMLElement nl = point.addElement("navLabel");
		XMLElement nlt = nl.addElement("text");
		nlt.addText(title);
		XMLElement npc = point.addElement("content");
		npc.setAttribute("src", "Files/" + href);
		
		XMLElement li = ul.addElement("li");
		li.setAttribute("style", "list-style-type: none;");
		XMLElement link = li.addElement("a");
		link.setAttribute("href", href);
		link.addText(title);
	}

	public String makeNCX() {
		return ncx.serialize();
	}

	public String makeHTML() {
		return html.serialize();
	}
}
