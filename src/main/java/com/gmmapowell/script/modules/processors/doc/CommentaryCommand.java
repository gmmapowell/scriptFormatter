package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.prose.CommentaryBreak;

public class CommentaryCommand implements AtCommandHandler {
	private final ScannerAtState state;

	public CommentaryCommand(ScannerAtState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "Commentary";
	}

	@Override
	public void invoke(AtCommand cmd) {
		/*
		state.endSpan();
		state.newPara("break");
		state.newSpan();
		state.op(new CommentaryBreak());
		state.endPara();
		state.commentary = true;
		state.section = 1;
		*/
	}

}
