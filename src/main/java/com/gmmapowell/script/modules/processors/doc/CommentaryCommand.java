package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.elements.block.CommentaryBreak;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class CommentaryCommand implements AtCommandHandler {
	private final ScannerAtState sas;
	private final ConfiguredState state;

	public CommentaryCommand(ScannerAtState sas) {
		this.sas = sas;
		this.state = sas.state();
	}
	
	@Override
	public String name() {
		return "Commentary";
	}

	@Override
	public void invoke(AtCommand cmd) {
		state.endSpan();
		state.newPara("break");
		state.newSpan();
		state.op(new CommentaryBreak());
		state.endPara();
		sas.outlineEntry(3, null, null, null);
	}

}
