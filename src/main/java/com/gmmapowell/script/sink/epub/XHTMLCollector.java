package com.gmmapowell.script.sink.epub;

import java.util.List;
import java.util.zip.ZipOutputStream;

import org.zinutils.utils.FileUtils;
import org.zinutils.xml.XML;
import org.zinutils.xml.XMLElement;

public class XHTMLCollector {
	private final XML xml;
	private final XMLElement head;
	private final XMLElement body;
	private XMLElement ctag;
	
	public XHTMLCollector() {
		xml = XML.createNS("1.0", "html", "http://www.w3.org/1999/xhtml");
		head = xml.top().addElement("head");
		body = xml.top().addElement("body");
	}
	
	public void styles(List<String> styles) {
		if (ctag == null) {
			if (styles.contains("chapter-title"))
				ctag = body.addElement("h1");
			else
				ctag = body.addElement("p");
		}
	}

	public void title(String title) {
		XMLElement elt = head.addElement("title");
		elt.addText(title);
	}
	
	public void text(String text) {
		ctag.addText(text);
	}

	// write the close tag of the current para tag
	public void closePara() {
		ctag = null;
	}

	public void write(ZipOutputStream zos) {
		FileUtils.writeToStream(xml.asString(false), zos);
	}

}
