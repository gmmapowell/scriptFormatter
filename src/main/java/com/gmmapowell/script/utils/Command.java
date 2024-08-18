package com.gmmapowell.script.utils;

public interface Command {
	int depth();
	String name();
	LineArgsParser line();
}
