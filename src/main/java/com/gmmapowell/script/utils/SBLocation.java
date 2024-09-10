package com.gmmapowell.script.utils;

public class SBLocation implements FileWithLocation {
	private String file;
	private int line;

	protected void processingFile(String file) {
		this.file = file;
	}

	public void line(int lineNumber) {
		this.line = lineNumber;
	}

	public int lineNo() {
		return line;
	}

	public String inputLocation() {
		return file + ":" + line;
	}
}
