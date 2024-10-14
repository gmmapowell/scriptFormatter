package com.gmmapowell.script.sink.epub;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.xml.XMLElement;

public class Hierarchy {
	public interface Contents {
		void addTo(XMLElement ctag);
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

	private final Hierarchy parent;
	private final List<String> styles;
	private final List<Contents> contents = new ArrayList<>();

	public Hierarchy(List<String> styles) {
		this.parent = null;
		this.styles = styles;
	}

	public void flush(XMLElement body) {
		XMLElement ctag;
		if (styles.contains("chapter-title"))
			ctag = body.addElement("h1");
		else
			ctag = body.addElement("p");
		
		for (Contents c : contents) {
			c.addTo(ctag);
		}
	}

	public boolean hasExactly(List<String> styles) {
		// TODO Auto-generated method stub
		return false;
	}

	public Hierarchy extractParentWithSome(List<String> styles) {
		// TODO Auto-generated method stub
		return null;
	}

	public Hierarchy push(List<String> styles) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addText(String text) {
		this.contents.add(new StringContents(text));
	}

}
