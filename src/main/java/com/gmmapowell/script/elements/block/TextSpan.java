package com.gmmapowell.script.elements.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gmmapowell.script.elements.Span;

public class TextSpan implements Span {
	private final List<String> formats;
	private final String text;

	public TextSpan(String style, String text) {
		this.formats = style == null ? new ArrayList<>() : Arrays.asList(style);
		this.text = text;
	}

	public TextSpan(List<String> formats, String text) {
		this.formats = new ArrayList<>(formats);
		this.text = text;
	}

	@Override
	public List<String> getStyles() {
		return formats;
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
