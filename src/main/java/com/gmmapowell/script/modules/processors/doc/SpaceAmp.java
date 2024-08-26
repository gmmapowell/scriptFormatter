package com.gmmapowell.script.modules.processors.doc;

public class SpaceAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public SpaceAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "sp";
	}

	@Override
	public void invoke(AmpCommand cmd) {
	}

}
