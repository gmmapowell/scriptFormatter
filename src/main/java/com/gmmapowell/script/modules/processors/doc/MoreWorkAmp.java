package com.gmmapowell.script.modules.processors.doc;

public class MoreWorkAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public MoreWorkAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "morework";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		System.out.println("more work is required at " + state.inputLocation() + ": " + cmd.args.asString());
	}
}
