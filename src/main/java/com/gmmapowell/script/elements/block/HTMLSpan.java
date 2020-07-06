package com.gmmapowell.script.elements.block;

import java.util.List;

import com.gmmapowell.script.elements.Span;

public class HTMLSpan implements Span {
	private final String text;

	public HTMLSpan(String text) {
		this.text = text;
	}

	@Override
	public List<String> getStyles() {
		return null;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void addSpan(Span sub) {
		throw new RuntimeException("Not Implemented");
	}
}
