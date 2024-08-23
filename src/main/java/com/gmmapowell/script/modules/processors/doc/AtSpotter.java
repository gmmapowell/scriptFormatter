package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class AtSpotter implements ProcessingScanner {
	private final ScannerAtState ats;

	public AtSpotter(ConfiguredState state) {
		this.ats = state.require(ScannerAtState.class);
		this.ats.configure(state.extensions());
	}

	@Override
	public boolean handleLine(String s) {
		if (s.startsWith("@")) {
			System.out.println("is-at");
			ats.startCommand(s.substring(1));
			return true;
		}
			
		return false;
	}

}
