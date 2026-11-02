package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.InlineCommandHandler;

public class DollarHandler implements InlineCommandHandler, AmpCommandHandler {
	private final ConfiguredState state;

	public DollarHandler(InlineDocCommandState state) {
		this.state = state.state();
	}
	
	public DollarHandler(ScannerAmpState state) {
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

	@Override
	public void invoke(AmpCommand cmd) {
		state.ensurePara();
		state.newSpan();
		state.text("$");
		state.endSpan();
	}
}
