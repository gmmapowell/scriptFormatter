package com.gmmapowell.script.processor;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingHandler;

public class NewParaProcessor implements ProcessingHandler {
	private final ConfiguredState state;

	public NewParaProcessor(ConfiguredState state) {
		this.state = state;
	}

	@Override
	public void process(String s) {
		if (!state.ignoringBlanks()) {
			state.endPara();
			state.ignoreNextBlanks();
		}
	}
}
