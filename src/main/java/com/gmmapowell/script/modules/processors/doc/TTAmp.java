package com.gmmapowell.script.modules.processors.doc;

public class TTAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public TTAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "tt";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		/*
		if (!state.inPara())
			state.newPara();
		if (!state.inSpan())
			state.newSpan();
		state.nestSpan("preformatted");
		state.text(p.asString());
		state.popSpan();
		*/
	}

}
