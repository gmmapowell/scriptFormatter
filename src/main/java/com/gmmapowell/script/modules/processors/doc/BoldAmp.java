package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class BoldAmp implements AmpCommandHandler {
	private final ConfiguredState state;

	public BoldAmp(ScannerAmpState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "bold";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (!state.inPara())
			state.newPara();
		if (!state.inSpan())
			state.newSpan();
		state.nestSpan("bold");
		state.processTextInSpan(cmd.args.asString());
		state.popSpan();
		state.observeBlanks();
	}
}
