package com.gmmapowell.script.elements;

public interface ElementFactory {

	SpanBlock block(String string);
	Span span(String format, String text);
	Group group();

}
