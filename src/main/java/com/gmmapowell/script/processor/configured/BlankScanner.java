package com.gmmapowell.script.processor.configured;

public class BlankScanner implements ProcessingScanner {
	private final ProcessingHandler handler;

	public BlankScanner(ProcessingHandler handler) {
		this.handler = handler;
	}

	@Override
	public boolean handleLine(String s) {
		if (s.length() == 0) {
			this.handler.process(s);
			return true;
		}
		return false;
	}
}
