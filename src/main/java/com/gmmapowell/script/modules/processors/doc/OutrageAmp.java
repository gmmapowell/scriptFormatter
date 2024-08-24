package com.gmmapowell.script.modules.processors.doc;

public class OutrageAmp implements AmpCommandHandler {

	private ScannerAmpState state;

	public OutrageAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "outrageousclaim";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		System.out.println("There is an outrageous claim at " + state.inputLocation());
	}
}
