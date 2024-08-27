package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.InlineCommandHandler;
import com.gmmapowell.script.processor.configured.InlineCommandState;

public class ForceSpaceHandler implements InlineCommandHandler {
	private final ConfiguredState state;

	public ForceSpaceHandler(InlineCommandState state) {
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
