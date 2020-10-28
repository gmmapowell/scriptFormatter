package com.gmmapowell.script.processor;

import com.gmmapowell.script.flow.SpanItem;

public interface TextState {

	void newSpan(String... formats);
	void nestSpan(String... formats);
	void popSpan();
	void endSpan();

	void text(String text);
	void op(SpanItem op);

	int nextFootnoteMarker();
	String inputLocation();
}
