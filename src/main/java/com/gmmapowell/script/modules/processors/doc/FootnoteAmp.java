package com.gmmapowell.script.modules.processors.doc;

public class FootnoteAmp implements AmpCommandHandler {
	private final ScannerAmpState state;

	public FootnoteAmp(ScannerAmpState state) {
		this.state = state;
	}
	
	@Override
	public String name() {
		return "footnote";
	}

	@Override
	public void invoke(AmpCommand cmd) {
//		commitCurrentCommand(); // I think this is already handled by the "close-if-not-continued" logic
		/*
		state.switchToFlow("footnotes");
		state.newPara("footnote");
		state.newSpan("footnote-number");
		state.op(new YieldToFlow("main"));
		state.text(Integer.toString(state.nextFootnoteText()) + " ");
		*/
	}

}
