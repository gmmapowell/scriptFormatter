package com.gmmapowell.script.processor.prose;

public class DocState extends CurrentState {
	public DocCommand cmd;
	public int chapter;
	public int section;
	public boolean commentary;

	public void reset() {
		cmd = null;
		curr = null;
		section = 0;
		commentary = false;
		nextFnMkr = 1;
		nextFnText = 1;
	}
}
