package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class AtBlankSpotter implements ProcessingScanner {
	private final ScannerAtState ats;
	
	public AtBlankSpotter(ConfiguredState state) {
		this.ats = state.require(ScannerAtState.class);
	}

	@Override
	public boolean handleLine(String s) {
		if (ats.hasPendingCommand() && s.trim().length() == 0) {
			System.out.println("blank line after command");
			ats.handleAtCommand();
			ats.ignoreNextBlanks();
			return true;
		}
		return false;
	}

	@Override
	public void placeDone() {
		if (ats.hasPendingCommand()) {
			System.out.println("executing command at end of file");
			ats.handleAtCommand();
		}
	}
}
