package com.gmmapowell.script.flow;

import java.util.List;

public class StyledToken {
	public final String flow;
	public final SpanItem it;
	public final List<String> styles;
	private CursorIndex loc;

	public StyledToken(String flow, CursorIndex loc, List<String> styles, SpanItem it) {
		this.flow = flow;
		this.loc = loc;
		this.styles = styles;
		this.it = it;
	}
	
	public void resetMe(Cursor c) {
		c.resetTo(loc);
	}
	
	public String location() {
		return loc.toString();
	}
	
	@Override
	public String toString() {
		return "StyledToken{" + flow + "}" + styles + "[" + it + "]";
	}

}
