package com.gmmapowell.script.styles.simple;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;

public class SimpleStyleCatalog implements StyleCatalog {
	private final Map<String, Style> catalog = new TreeMap<>();
	private final Style defaultStyle = new SimpleStyle(this);
	private final Set<String> missed = new TreeSet<>();
	
	public SimpleStyleCatalog() {
		catalog.put("title", new SimpleStyle(this));
	}
	
	@Override
	public Style get(String style) {
		if (!catalog.containsKey(style)) {
			if (!missed.contains(style)) {
				System.out.println("There is no style " + style);
				missed.add(style);
			}
			return defaultStyle;
		}
		return catalog.get(style);
	}

}
