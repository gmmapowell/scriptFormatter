package com.gmmapowell.script.elements;

import java.util.List;

public interface ElementFactory {

	SpanBlock block(String format);
	Span html(String html);
	Span span(String format, String text);
	Span lspan(List<String> formats, String text);
	Group group();
	Break adbreak();
	Break commentarybreak();

}
