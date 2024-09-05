package com.gmmapowell.script.modules.processors.doc;

public class BlockquoteState {
	private boolean inBlockquote = false;
	
	public void toggle() {
		inBlockquote = !inBlockquote;
	}
	
	public boolean active() {
		return inBlockquote;
	}
}
