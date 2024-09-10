package com.gmmapowell.script.modules.processors.presenter;

import org.flasck.flas.blockForm.ContinuedLine;

public class IgnoreNestingProcessor implements LineProcessor {
	@Override
	public LineProcessor process(ContinuedLine currline) {
		// just accept everything and keep ignoring nested content
		System.out.println("ignoring " + currline.text());
		return this;
	}

	@Override
	public void flush() {
	}
}
