package com.gmmapowell.script.elements.block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;

public class TextBlock implements SpanBlock {
	private final String blockStyle;
	private final List<Span> spans = new ArrayList<>();

	public TextBlock(String blockStyle) {
		this.blockStyle = blockStyle;
	}

	@Override
	public String getStyle() {
		return blockStyle;
	}
	
	public void addSpan(Span span) {
		spans.add(span);
	}
	
	@Override
	public Iterator<Span> iterator() {
		return spans.iterator();
	}
}
