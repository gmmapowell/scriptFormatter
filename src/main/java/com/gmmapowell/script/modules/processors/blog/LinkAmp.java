package com.gmmapowell.script.modules.processors.blog;

import com.gmmapowell.script.flow.LinkOp;
import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
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
		state.op(new LinkOp(lk, tx));
		state.observeBlanks();
	}

}
