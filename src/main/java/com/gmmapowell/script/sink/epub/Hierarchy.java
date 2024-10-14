package com.gmmapowell.script.sink.epub;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.xml.XMLElement;

interface Contents {
	void addTo(XMLElement ctag);
}

public class Hierarchy implements Contents {

	public class StringContents implements Contents {
		private final String text;

		public StringContents(String text) {
			this.text = text;
		}
		
		@Override
		public void addTo(XMLElement ctag) {
			ctag.addText(text);
		}
	}

	private final Hierarchy parent;
	
	// The only things that should be in styles here are the ones that parent *doesn't* have.
	private final List<String> styles;
	private final List<Contents> contents = new ArrayList<>();

	public Hierarchy(List<String> styles) {
		this(null, styles);
	}

	public Hierarchy(Hierarchy parent, List<String> news) {
		this.parent = parent;
		styles = news;
	}

	@Override
	public void addTo(XMLElement ctag) {
		for (String s : styles) {
			String m = mappedStyle(s);
			if (m != null) {
				ctag = ctag.addElement(m);
			}
		}
		
		for (Contents c : contents) {
			c.addTo(ctag);
		}
	}

	public void flush(XMLElement body) {
		XMLElement ctag;
		if (styles.contains("chapter-title"))
			ctag = body.addElement("h1");
		else
			ctag = body.addElement("p");
		addTo(ctag);
	}

	private String mappedStyle(String s) {
		switch (s) {
		case "chapter-title":
			return null;
		case "bold":
			return "b";
		case "italic":
			return "i";
		default:
			System.out.println("unknown style: " + s);
			return null;
		}
	}

	public boolean hasExactly(List<String> styles) {
		// TODO Auto-generated method stub
		return false;
	}

	public Hierarchy extractParentWithSome(List<String> styles) {
		return parent;
	}

	public Hierarchy push(List<String> styles) {
		ArrayList<String> news = new ArrayList<>(styles);
		news.removeAll(this.styles);
		Hierarchy ret = new Hierarchy(this, news);
		contents.add(ret);
		return ret;
	}

	public void addText(String text) {
		this.contents.add(new StringContents(text));
	}
}
