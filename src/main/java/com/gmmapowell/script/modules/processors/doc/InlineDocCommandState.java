package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class InlineDocCommandState {
	private final ConfiguredState state;
	private int nextFnMkr = 1;

	public InlineDocCommandState(ConfiguredState state) {
		this.state = state;
	}

	public ConfiguredState state() {
		return state;
	}

	public int nextFootnoteMarker() {
		return nextFnMkr++;
	}
}
