package com.gmmapowell.script.sink.epub;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.xml.XMLElement;

interface Contents {
	void addTo(XMLElement ctag);
}

public class Hierarchy implements Contents {
	public class XMLContents implements Contents {
		private final XMLElement xhtml;

		public XMLContents(XMLElement xhtml) {
			this.xhtml = xhtml;
		}

		@Override
		public void addTo(XMLElement ctag) {
			ctag.addElement(xhtml);
		}

	}

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

	private Hierarchy parent;
	
	// The only things that should be in styles here are the ones that parent *doesn't* have.
	private final List<String> styles;
	private final List<Contents> contents = new ArrayList<>();

	public Hierarchy(List<String> styles) {
		this(null, styles);
	}

	public Hierarchy(Hierarchy parent, List<String> news) {
		this.parent = parent;
		styles = new ArrayList<>(news);
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
		if (this.parent != null) {
			this.parent.flush(body);
			return;
		} else {
			XMLElement ctag;
			if (styles.contains("chapter-title"))
				ctag = body.addElement("h1");
			else
				ctag = body.addElement("p");
			addTo(ctag);
		}
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
		return hasAtLeast(styles) && hasNoMoreThan(styles);
	}

	public boolean hasAtLeast(List<String> styles) {
		return this.styles.containsAll(styles);
	}

	public boolean hasNoMoreThan(List<String> styles) {
		return styles.containsAll(this.styles);
	}

	// There are essentially 3 cases:
	//  1. There is an Hnode (me or a parent) with exactly the right styles, or a subset of the desired styles - return it
	//  2. There is an Hnode (me or a parent) which has a split with me with some of them
	//  3. There is no parent and we need to go back to "nothing"
	// So we may need to create a new Parent and link to it, or we may need a parent to do this
	public Hierarchy extractParentWithSome(List<String> styles) {
		Hierarchy p = this;
		Hierarchy top = this;
		ArrayList<String> willNeed = new ArrayList<>(top.allStyles());
		willNeed.retainAll(styles);
		while (p != null) {
			if (p.hasNoMoreThan(styles)) { // it does not have anything we don't want
				if (p.hasExactly(willNeed))
					return p;
				else {
					// need to fabricate a new parent
					Hierarchy np = new Hierarchy(willNeed);
					np.parent = top.parent;
					top.parent = np;
					top.styles.removeAll(willNeed);
					return np;
				}
			} else if (p.styles.isEmpty())
				return p; // it is already root
			else {
				top = p;
				p = p.parent;
			}
		}
		willNeed = new ArrayList<>(top.styles);
		willNeed.retainAll(styles);
		top.parent = new Hierarchy(willNeed);
		top.styles.removeAll(willNeed);
		return top.parent;
	}

	private List<String> allStyles() {
		if (parent == null) {
			return new ArrayList<>(this.styles);
		} else {
			List<String> ret = parent.allStyles();
			ret.addAll(this.styles);
			return ret;
		}
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
	
	public void insert(XMLElement xhtml) {
		this.contents.add(new XMLContents(xhtml));
	}
	
	@Override
	public String toString() {
		return "H{" + this.styles + ":" + contents + "}";
	}
	
}
