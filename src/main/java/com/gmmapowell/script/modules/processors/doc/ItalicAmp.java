package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class ItalicAmp implements AmpCommandHandler {
	private final ConfiguredState state;

	public ItalicAmp(ScannerAmpState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "italic";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (!state.inPara())
			state.newPara();
		if (!state.inSpan())
			state.newSpan();
		state.nestSpan("italic");
		state.processTextInSpan(cmd.args.asString());
		state.popSpan();
		state.observeBlanks();
	}
}
