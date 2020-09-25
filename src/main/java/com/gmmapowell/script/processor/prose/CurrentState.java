package com.gmmapowell.script.processor.prose;

import java.util.ArrayList;
import java.util.List;

import com.gmmapowell.script.elements.SpanBlock;

public abstract class CurrentState {
	public SpanBlock curr;
	protected int nextFnMkr = 1;
	protected int nextFnText = 1;
	public List<String> defaultSpans = new ArrayList<>();

	public int nextFootnoteMarker() {
		return nextFnMkr++;
	}
	
	public int nextFootnoteText() {
		return nextFnText++;
	}

	public abstract void line(int lineNumber);
	public abstract String location();

	protected boolean trimLine() {
		return true;
	}
}
