package com.gmmapowell.script.modules.processors.blog;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
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
		state.processTextInSpan(cmd.args.asString());
		state.popSpan();
		state.observeBlanks();
	}

}
