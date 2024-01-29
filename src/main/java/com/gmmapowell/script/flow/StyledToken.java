package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StyledToken {
	public final String flow;
	public final SpanItem it;
	public final List<String> styles;
	private int para;
	private int span;
	private List<Integer> item;

	public StyledToken(String flow, int para, int span, List<AtomicInteger> item, List<String> styles, SpanItem it) {
		this.flow = flow;
		this.para = para;
		this.span = span;
		this.item = new ArrayList<Integer>();
		for (AtomicInteger ai : item)
			this.item.add(ai.get());
		this.styles = styles;
		this.it = it;
	}
	
	public void resetMe(Cursor c) {
		c.resetTo(para, span, item);
	}
	
	@Override
	public String toString() {
		return "StyledToken{" + flow + "}" + styles + "[" + it + "]";
	}

	public String location() {
		return para +"." + span + "." + item;
	}

}
