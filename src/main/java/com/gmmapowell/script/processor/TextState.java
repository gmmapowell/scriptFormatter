package com.gmmapowell.script.processor;

import java.util.List;

import com.gmmapowell.script.flow.SpanItem;

public interface TextState {
	String formatAs();

	void newPara(String... formats);
	void newPara(List<String> formats);
	void newSpan(String... formats);
	void newSpan(List<String> formats);
	void nestSpan(String... formats);
	void popSpan();
	void endSpan();
	void endPara();

	void text(String text);
	void op(SpanItem op);

	int nextFootnoteMarker();
	String inputLocation();



}
