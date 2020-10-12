package com.gmmapowell.script.sink.pdf;

import java.util.List;

import com.gmmapowell.script.flow.SpanItem;

public class StyledToken {
	private final SpanItem it;
	private final List<String> styles;

	public StyledToken(List<String> styles, SpanItem it) {
		this.styles = styles;
		this.it = it;
	}
	
	@Override
	public String toString() {
		return "StyledToken" + styles + "[" + it + "]";
	}

}
