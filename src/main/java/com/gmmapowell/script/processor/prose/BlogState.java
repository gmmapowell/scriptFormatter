package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.util.Map;

import com.gmmapowell.script.flow.Flow;

public class BlogState extends CurrentState {
	private File gitdir;
	private String gittag;
	public IncludeCommand include;

	public BlogState(Map<String, Flow> flows, String file) {
		super(flows);
		this.file = file;
	}
	@Override
	public String formatAs() {
		return "blockquote";
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

	public void gittag(String tag) {
		this.gittag = tag;
	}
	
	public String gittag() {
		return this.gittag;
	}
}
