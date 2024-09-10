package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.flow.LinkOp;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class LinkAmp implements AmpCommandHandler {
	private final ConfiguredState state;

	public LinkAmp(ScannerAmpState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "link";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		String lk = cmd.args.readString();
		String tx = cmd.args.readString();
		state.ensurePara();
		if (!state.inSpan())
			state.newSpan();
		state.nestSpan("tt");
		state.op(new LinkOp(lk, tx));
		state.popSpan();
		state.observeBlanks();
	}

}
