package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class AtSpotter implements ProcessingScanner {
	private final ScannerAtState ats;

	public AtSpotter(ConfiguredState state) {
		this.ats = state.require(ScannerAtState.class);
	}

	@Override
	public boolean handleLine(String s) {
		if (s.startsWith("@")) {
			System.out.println("is-at");
			ats.wantFields(true);
			return true;
		}
			
		return false;
	}

}
