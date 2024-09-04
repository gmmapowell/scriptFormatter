package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class CommentCommand implements AtCommandHandler {
	private final ConfiguredState state;

	public CommentCommand(ScannerAtState sas) {
		this.state = sas.state();
	}
	
	@Override
	public String name() {
		return "Comment";
	}

	@Override
	public void invoke(AtCommand cmd) {
		// TODO: should we add "beginRefComment" to the "current styles"?
		state.newPara("beginRefComment");
		state.newSpan("comment-sign");
		state.text("\u25A0");
		state.endSpan();
	}
	
	// TODO: I think this needs an "end" to handle it
}
