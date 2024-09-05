package com.gmmapowell.script.modules.doc.scanmode;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class ScanningModeIgnorer implements ProcessingScanner {
	private final ScanmodeState sm;

	public ScanningModeIgnorer(ConfiguredState state) {
		sm = state.global().requireState(ScanmodeState.class);
	}
	
	@Override
	public boolean handleLine(String s) {
		if ("@Conclusion".equals(s)) {
			sm.scanMode(ScanMode.CONCLUSION);
			return false; // and allow it to propagate
		}
		
		if (sm.ignoring()) {
			System.out.println("details-ignored");
			return true;
		} else
			return false;
	}

}
