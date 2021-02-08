package com.gmmapowell.script.sink.epub;

import org.zinutils.xml.XML;
import org.zinutils.xml.XMLElement;

public class TOCCreator {
	private final XMLElement ncx;
	private final XMLElement map;

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
	}

	public void addEntry(String title, String file, int idx) {
		XMLElement point = map.addElement("navPoint");
		point.setAttribute("id", "point" + idx);
		point.setAttribute("playOrder", Integer.toString(idx));
		XMLElement nl = point.addElement("navLabel");
		XMLElement nlt = nl.addElement("text");
		nlt.addText(title);
		XMLElement npc = point.addElement("content");
		npc.setAttribute("src", "Files/" + file + ".xhtml");
	}

	public String makeTOC() {
		return ncx.serialize();
	}
}
