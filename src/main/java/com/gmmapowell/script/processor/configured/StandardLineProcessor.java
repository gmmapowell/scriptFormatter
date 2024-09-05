package com.gmmapowell.script.processor.configured;

import com.gmmapowell.script.flow.BreakingSpace;

public class StandardLineProcessor implements ProcessingHandler {
	private final ConfiguredState state;

	public StandardLineProcessor(ConfiguredState state) {
		this.state = state;
	}

	@Override
	public void process(String s) {
		System.out.println("processing line ...");
		if (state.joinspace() && state.inPara()) {
			if (!state.inSpan())
				state.newSpan();
			state.op(new BreakingSpace());
		}
		state.ensurePara();
		state.processText(s);
		state.observeBlanks();
	}
}
