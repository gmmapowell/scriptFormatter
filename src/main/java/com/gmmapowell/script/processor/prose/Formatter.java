package com.gmmapowell.script.processor.prose;

public interface Formatter {

	void format(String text, int exdent);

	boolean isBlockIndent(int firstline, int thisline);

}
