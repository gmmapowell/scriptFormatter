package com.gmmapowell.script.processor.prose;

public interface LineArgsParser {
	boolean hasMore();
	String readString();
	String readArg();
	void argsDone();
}
