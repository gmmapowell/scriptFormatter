package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.util.Map;

import com.gmmapowell.script.flow.Flow;

public class BlogState extends CurrentState implements GitState {
	private File gitdir;
	private String gittag;
	public IncludeCommand include;

	public BlogState(Map<String, Flow> flows, String file) {
		super(flows);
		processingFile(file);
	}
	
	@Override
	public String formatAs() {
		return "blockquote";
	}

	@Override
	protected boolean trimLine() {
		return !blockquote;
	}

	public void gitdir(String dir) {
		this.gitdir = new File(dir);
	}
	
	@Override
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
