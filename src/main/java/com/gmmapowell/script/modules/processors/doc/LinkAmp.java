package com.gmmapowell.script.modules.processors.doc;

public class LinkAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public LinkAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "link";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		String lk = cmd.args.readString();
		String tx = cmd.args.readString();
		/*
		if (!state.inSpan())
			state.newSpan();
		state.nestSpan("tt");
		state.op(new LinkOp(lk, tx));
		state.popSpan();
		*/
	}

}
