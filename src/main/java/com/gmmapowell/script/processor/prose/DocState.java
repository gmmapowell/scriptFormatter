package com.gmmapowell.script.processor.prose;

import java.util.Map;

import com.gmmapowell.script.flow.Flow;

public class DocState extends CurrentState {
	public DocCommand cmd;
	public InlineCommand inline;
	public int chapter;
	public int section;
	public boolean commentary;
	public boolean beginComment;
	public boolean inRefComment;
	public boolean wantNumbering;
	public boolean blockquote;

	public DocState(Map<String, Flow> flows) {
		super(flows);
	}

	public void reset(String file) {
		this.file = file;
		cmd = null;
		section = 0;
		commentary = false;
		nextFnMkr = 1;
		nextFnText = 1;
	}

	@Override
	public void line(int lineNumber) {
		this.line = lineNumber;
	}

	public String inputLocation() {
		return file + ":" + line;
	}
	
	public String location() {
		return (chapter-1) + "." + (section-1) + (commentary?"c":"");
	}
}
