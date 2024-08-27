package com.gmmapowell.script.processor.configured;

public class StandardLineProcessor implements ProcessingHandler {
	private final ConfiguredState state;

	public StandardLineProcessor(ConfiguredState state) {
		this.state = state;
	}

	@Override
	public void process(String s) {
		System.out.println("processing line ...");
		state.ensurePara();
//		if (!state.inPara()) {
//			if (state.blockquote)
//				state.newPara("blockquote");
//			else if (state.inRefComment)
//				state.newPara("refComment");
//			else if (this.scanmode == ScanMode.DETAILS && (state.scanMode == ScanMode.OVERVIEW || state.scanMode == ScanMode.CONCLUSION))
//				state.newPara("text", "bold");
//			else
//				state.newPara("text");
//		} else if (joinspace) {
//			if (!state.inSpan())
//				state.newSpan();
//			state.op(new BreakingSpace());
//		}
		state.processText(s);
		state.observeBlanks();
	}

}
