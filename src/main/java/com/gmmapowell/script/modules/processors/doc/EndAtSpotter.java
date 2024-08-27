package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class EndAtSpotter implements ProcessingScanner {
	private final ScannerAtState ats;

	public EndAtSpotter(ConfiguredState state) {
		this.ats = state.require(ScannerAtState.class);
		this.ats.configure(state);
	}

	@Override
	public boolean handleLine(String s) {
		if (s.startsWith("@/")) {
			System.out.println("is-at-end");
			ats.popAtCommand();
			return true;
		}
			
		return false;
	}

}
