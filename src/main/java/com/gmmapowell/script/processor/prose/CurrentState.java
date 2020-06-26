package com.gmmapowell.script.processor.prose;

import com.gmmapowell.script.elements.SpanBlock;

public abstract class CurrentState {
	public SpanBlock curr;
	protected int nextFnMkr = 1;
	protected int nextFnText = 1;

	public int nextFootnoteMarker() {
		return nextFnMkr++;
	}
	
	public int nextFootnoteText() {
		return nextFnText++;
	}

	public abstract void line(int lineNumber);
	public abstract String location();
}
