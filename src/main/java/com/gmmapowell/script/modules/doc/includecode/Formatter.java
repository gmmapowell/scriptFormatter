package com.gmmapowell.script.modules.doc.includecode;

public interface Formatter {

	void format(String text, int exdent);

	boolean isBlockIndent(int firstline, int thisline);

}
