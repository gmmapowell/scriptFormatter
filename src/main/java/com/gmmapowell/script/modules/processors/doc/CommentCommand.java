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
		state.newPara("beginRefComment");
		state.newSpan("comment-sign");
		state.text("\u25A0");
		state.endSpan();
		// TODO: backwards compatibility.  Remove this after the refactoring
		state.endPara();
		// TODO: end backwards compatibility section
		state.pushFormat("refComment");
	}

	@Override
	public void onEnd(AtCommand cmd) {
		state.popFormat("refComment");
		state.newPara("endRefComment");
		state.newSpan("comment-sign");
		state.text("\u25A1");
		state.endSpan();
		state.endPara();
	}
}
