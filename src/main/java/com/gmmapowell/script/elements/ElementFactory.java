package com.gmmapowell.script.elements;

public interface ElementFactory {

	Block block(String string);
	Span span(String format, String text);

}
