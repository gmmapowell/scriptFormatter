package com.gmmapowell.script.modules.processors.doc;

public class ReviewAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public ReviewAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "review";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (!cmd.args.hasMore())
			throw new RuntimeException("&review command needs something to review");
		System.out.println("review in " + state.inputLocation() + ": " + cmd.args.readString());
		cmd.args.argsDone();
	}

}
