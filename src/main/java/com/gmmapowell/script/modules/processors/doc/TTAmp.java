package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class TTAmp implements AmpCommandHandler {
	private final ConfiguredState state;

	public TTAmp(ScannerAmpState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "tt";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (!state.inPara())
			state.newPara();
		if (!state.inSpan())
			state.newSpan();
		state.nestSpan("preformatted");
		state.text(cmd.args.asString());
		state.popSpan();
		state.observeBlanks();
	}

}
