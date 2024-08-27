package com.gmmapowell.script.processor.configured;

public class InlineCommandState {
	private final ConfiguredState state;
	private int nextFnMkr = 1;

	public InlineCommandState(ConfiguredState state) {
		this.state = state;
	}

	public ConfiguredState state() {
		return state;
	}

	public int nextFootnoteMarker() {
		return nextFnMkr++;
	}
}
