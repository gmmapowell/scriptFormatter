package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class SpaceAmp implements AmpCommandHandler {
	private final ConfiguredState state;

	public SpaceAmp(ScannerAmpState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "sp";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (state.inPara()) {
			if (!state.inSpan())
				state.newSpan();
			state.op(new BreakingSpace());
		} else {
			state.newPara("text");
		}
		state.processText(cmd.args.asString());
		state.observeBlanks();
	}

}
