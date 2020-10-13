package com.gmmapowell.script.sink.pdf;

public class AcceptToken {
	private final Float onl;

	public AcceptToken() {
		this.onl = null;
	}
	
	public AcceptToken(Float onl) {
		this.onl = onl;
	}

	public boolean forcedNewLine() {
		return onl != null;
	}

	public float getOverflow() {
		return onl;
	}
}
