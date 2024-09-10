package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class NeedBreakAmp implements AmpCommandHandler {
	private ConfiguredState sink;

	public NeedBreakAmp(ScannerAmpState state) {
		this.sink = state.state();
	}
	
	@Override
	public String name() {
		return "needbreak";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		this.sink.newPara("break");
		this.sink.endPara();

	}

}
