package com.gmmapowell.script.elements;

public interface SpanBlock extends Block, Iterable<Span> {

	void addSpan(Span span);
	String getStyle();

}
