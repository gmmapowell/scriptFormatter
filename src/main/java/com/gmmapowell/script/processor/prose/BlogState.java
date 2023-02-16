package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.util.Map;

import com.gmmapowell.script.flow.Flow;

public class BlogState extends CurrentState {
	private File gitdir;

	public BlogState(Map<String, Flow> flows, String file) {
		super(flows);
		this.file = file;
	}

	@Override
	public void line(int lineNumber) {
		this.line = lineNumber;
	}

	@Override
	protected boolean trimLine() {
		return !blockquote;
	}

	@Override
	public String inputLocation() {
		return file + ":" + line;
	}

	public void gitdir(String dir) {
		this.gitdir = new File(dir);
	}
	
	public File gitdir() {
		return this.gitdir;
	}
}
