package com.gmmapowell.script.processor.prose;

import com.gmmapowell.script.elements.SpanBlock;

public class CurrentState {
	public SpanBlock curr;
	protected int nextFnMkr = 1;
	protected int nextFnText = 1;

	public int nextFootnoteMarker() {
		return nextFnMkr++;
	}
	
	public int nextFootnoteText() {
		return nextFnText++;
	}

}
