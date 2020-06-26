package com.gmmapowell.script.processor.prose;

public class DocState extends CurrentState {
	public DocCommand cmd;
	public InlineCommand inline;
	public int chapter;
	public int section;
	public boolean commentary;

	public void reset(String file) {
		cmd = null;
		curr = null;
		section = 0;
		commentary = false;
		nextFnMkr = 1;
		nextFnText = 1;
	}

	@Override
	public void line(int lineNumber) {
		
	}

	public String location() {
		return (chapter-1) + "." + (section-1) + (commentary?"c":"");
	}
}
