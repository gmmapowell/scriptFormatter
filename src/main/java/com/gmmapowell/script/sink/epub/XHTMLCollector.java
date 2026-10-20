package com.gmmapowell.script.sink.epub;

import java.util.List;
import java.util.zip.ZipOutputStream;

import org.zinutils.utils.FileUtils;
import org.zinutils.xml.XML;
import org.zinutils.xml.XMLElement;
import org.zinutils.xml.XMLNamespace;

public class XHTMLCollector {
	private final XML xml;
	private final XMLElement head;
	private final XMLElement body;
	private Hierarchy hierarchy;
	private XMLNamespace epub;
	
	public XHTMLCollector() {
		xml = XML.createNSwithDTD("1.0", "html", "http://www.w3.org/1999/xhtml", "xhtml", null, null);
//		xml.addPI("DOCTYPE", "html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\"");
		epub = xml.namespace("epub", "http://www.idpf.org/2007/ops");
		head = xml.top().addElement("head");
		body = xml.top().addElement("body");
	}


	public void addCss(String css) {
		XMLElement cssElt = newElement("link");
		cssElt.setAttribute("rel", "stylesheet");
		cssElt.setAttribute("type", "text/css");
		cssElt.setAttribute("href", "../css/main.css");
		head.addElement(cssElt);
	}

	public XMLElement newElement(String tag) {
		return xml.createElement(tag);
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

	public void epubAttribute(XMLElement applyTo, String attr, String value) {
		applyTo.setAttribute(epub.attr(attr), value);
	}

	public void write(ZipOutputStream zos) {
		FileUtils.writeToStream(xml.asString(false), zos);
	}


	public void noPara() {
		hierarchy.noPara();
	}
}
