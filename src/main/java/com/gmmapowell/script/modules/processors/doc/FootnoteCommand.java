package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.flow.ReleaseFlow;
import com.gmmapowell.script.flow.YieldToFlow;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class FootnoteCommand implements AtCommandHandler {
	private final ScannerAtState ats;
	private final ConfiguredState state;

	public FootnoteCommand(ScannerAtState ats) {
		this.ats = ats;
		this.state = ats.state();
	}
	
	@Override
	public String name() {
		return "Footnote";
	}

	@Override
	public void invoke(AtCommand cmd) {
//		commitCurrentCommand(); // I think this is already handled by the "close-if-not-continued" logic
		state.switchToFlow("footnotes");
		state.newPara("footnote");
		state.newSpan("footnote-number");
		state.op(new YieldToFlow("main"));
		state.text(Integer.toString(ats.nextFootnoteText()) + " ");
	}

	@Override
	public void onEnd(AtCommand cmd) {
		state.newSpan();
		state.op(new ReleaseFlow("main"));
		state.switchToFlow("main");
	}
}
