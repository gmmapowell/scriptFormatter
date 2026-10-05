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
	
	public XHTMLCollector() {
		xml = XML.createNSwithDTD("1.0", "html", "http://www.w3.org/1999/xhtml", "xhtml", "-//W3C//DTD XHTML 1.1//EN", "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd");
//		xml.addPI("DOCTYPE", "html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\"");
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

	public void insert(XMLElement xhtml) {
		hierarchy.insert(xhtml);
	}

	public void entity(String entity) {
		hierarchy.entity(entity);
	}

	// write the close tag of the current para tag
	public void closePara() {
		if (hierarchy != null) {
			hierarchy.flush(body);
			hierarchy = null;
		}
	}

	public void write(ZipOutputStream zos) {
		FileUtils.writeToStream(xml.asString(false), zos);
	}
}
