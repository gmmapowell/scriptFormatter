package com.gmmapowell.script.flow;

public class NestedSpan implements SpanItem {
	public final HorizSpan nested;

	public NestedSpan(HorizSpan nested) {
		this.nested = nested;
	}

}
