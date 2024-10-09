package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.InlineCommandHandler;

public class DollarHandler implements InlineCommandHandler {
	private final ConfiguredState state;

	public DollarHandler(InlineDocCommandState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "dollar";
	}

	@Override
	public void invoke() {
		state.text("$");
	}
}
