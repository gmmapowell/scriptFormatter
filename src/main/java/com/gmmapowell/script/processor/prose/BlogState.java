package com.gmmapowell.script.processor.prose;

public class BlogState extends CurrentState {
	private final String file;
	public boolean blockquote;
	private int line;

	public BlogState(String file) {
		this.file = file;
	}

	@Override
	public void line(int lineNumber) {
		this.line = lineNumber;
	}

	@Override
	public String location() {
		return file + ":" + line;
	}
}
