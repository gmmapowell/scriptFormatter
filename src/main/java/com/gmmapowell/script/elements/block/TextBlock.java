package com.gmmapowell.script.elements.block;

import com.gmmapowell.script.elements.Block;

public class TextBlock implements Block {
	private final String style;
	private final String text;

	public TextBlock(String style, String text) {
		this.style = style;
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}
}
