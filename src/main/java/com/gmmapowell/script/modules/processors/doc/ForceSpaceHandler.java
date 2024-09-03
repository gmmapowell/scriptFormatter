package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.InlineCommandHandler;

public class ForceSpaceHandler implements InlineCommandHandler {
	private final ConfiguredState state;

	public ForceSpaceHandler(InlineDocCommandState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "sp";
	}

	@Override
	public void invoke() {
		state.op(new BreakingSpace());
	}
}
