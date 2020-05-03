package com.gmmapowell.script.elements;

public interface Block extends Iterable<Span> {

	void addSpan(Span span);
	String getStyle();

}
