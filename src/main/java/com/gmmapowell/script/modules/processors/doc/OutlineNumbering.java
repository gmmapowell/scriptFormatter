package com.gmmapowell.script.modules.processors.doc;

public interface OutlineNumbering {
	public void parseFormats(String tocFormat);
	public String format(int level, String text, String style, String anchor);
}
