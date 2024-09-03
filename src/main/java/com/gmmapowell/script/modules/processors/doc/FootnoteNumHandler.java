package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.flow.SyncAfterFlow;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.InlineCommandHandler;

public class FootnoteNumHandler implements InlineCommandHandler {
	private final ConfiguredState state;
	private final InlineDocCommandState ics;

	public FootnoteNumHandler(InlineDocCommandState state) {
		ics = state;
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "footnote";
	}

	@Override
	public void invoke() {
		state.nestSpan("footnote-number");
		state.text(Integer.toString(ics.nextFootnoteMarker()));
		state.popSpan();
		state.op(new SyncAfterFlow("footnotes"));
	}

}
