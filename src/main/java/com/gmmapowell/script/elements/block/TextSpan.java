package com.gmmapowell.script.elements.block;

import com.gmmapowell.script.elements.Span;

public class TextSpan implements Span {
	private final String style;
	private final String text;

	public TextSpan(String style, String text) {
		this.style = style;
		this.text = text;
	}

	@Override
	public String getStyle() {
		return style;
	}

	@Override
	public String getText() {
		return text;
	}
}
