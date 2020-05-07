package com.gmmapowell.script.elements;

import java.util.List;

public interface Span {
	List<String> getStyles();
	String getText();
	void addSpan(Span sub);
}
