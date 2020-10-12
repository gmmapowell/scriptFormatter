package com.gmmapowell.script.flow;

public class TextSpanItem implements SpanItem {
	public final String text;

	public TextSpanItem(String tx) {
		this.text = tx;
	}
	
	@Override
	public String toString() {
		return "Tx[" + text + "]";
	}
}
