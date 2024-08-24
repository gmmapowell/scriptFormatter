package com.gmmapowell.script.modules.processors.doc;

public class FutureAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public FutureAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "future";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (!cmd.args.hasMore())
			throw new RuntimeException("&future command needs a comment");
		System.out.println(state.inputLocation() + ": in the future, " + cmd.args.readString());
	}

}
