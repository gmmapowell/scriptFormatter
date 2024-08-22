package com.gmmapowell.script.processor.configured;

public class AlwaysScanner implements ProcessingScanner {
	private final ProcessingHandler handler;

	public AlwaysScanner(ProcessingHandler handler) {
		this.handler = handler;
	}

	@Override
	public boolean handleLine(String s) {
		handler.process(s);
		return true;
	}

}
