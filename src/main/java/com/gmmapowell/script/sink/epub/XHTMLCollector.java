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
	private Hierarchy hierarchy;
//	private XMLElement ctag;
	
	public XHTMLCollector() {
		xml = XML.createNS("1.0", "html", "http://www.w3.org/1999/xhtml");
		head = xml.top().addElement("head");
		body = xml.top().addElement("body");
	}
	
	public void styles(List<String> styles) {
		if (hierarchy == null)
			hierarchy = new Hierarchy(styles);
		else if (hierarchy.hasExactly(styles))
			;
		else {
			hierarchy = hierarchy.extractParentWithSome(styles);
			if (!hierarchy.hasExactly(styles))
				hierarchy = hierarchy.push(styles);
		}
	}

	public void title(String title) {
		XMLElement elt = head.addElement("title");
		elt.addText(title);
	}
	
	public void text(String text) {
		hierarchy.addText(text);
	}

	// write the close tag of the current para tag
	public void closePara() {
		if (hierarchy != null)
			hierarchy.flush(body);
	}

	public void write(ZipOutputStream zos) {
		FileUtils.writeToStream(xml.asString(false), zos);
	}
}
