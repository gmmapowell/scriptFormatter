package com.gmmapowell.script.modules.processors.doc;

import java.util.regex.Pattern;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class FieldSpotter implements ProcessingScanner {
	private final Pattern fieldStart = Pattern.compile("([a-z][a-z0-9A-Z_]*)=(.*)");
	private final ScannerAtState ats;
	
	public FieldSpotter(ConfiguredState state) {
		this.ats = state.require(ScannerAtState.class);
	}

	@Override
	public boolean handleLine(String s) {
		if (fieldStart.matcher(s).matches() && ats.wantFields()) {
			System.out.println("is-field");
			return true;
		}
		
		ats.wantFields(false);
		return false;
	}

}
