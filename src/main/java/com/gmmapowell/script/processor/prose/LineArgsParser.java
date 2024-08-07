package com.gmmapowell.script.processor.prose;

import java.util.Map;

public interface LineArgsParser {
	boolean hasMore();
	String readString();
	String readArg();
	void argsDone();
	Map<String, String> readParams(String... allowedStrings);
	String asString();
}
