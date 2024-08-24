package com.gmmapowell.script.utils;

import java.util.Map;

public interface LineArgsParser {
	Command readCommand();
	boolean hasMore();
	String readString();
	String readArg();
	void argsDone();
	Map<String, String> readParams(String... allowedStrings);
	String asString();
}
