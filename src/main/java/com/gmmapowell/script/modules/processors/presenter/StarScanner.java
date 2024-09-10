package com.gmmapowell.script.modules.processors.presenter;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class StarScanner implements ProcessingScanner {
	private final PresenterGlobals globs;
	private final ConfiguredState state;

	public StarScanner(ConfiguredState state) {
		this.state = state;
		this.globs = state.global().requireState(PresenterGlobals.class);
	}
	
	@Override
	public boolean handleLine(String s) {
		if (s.startsWith("*")) {
			System.out.println("starline");
			globs.present(state.lineNo(), reapplyTabs(s));
			return true;
		} else 
			return false;
	}

	private String reapplyTabs(String s) {
		String prefix = "";
		while (s.length() > 0 && s.charAt(0) == '*') {
			prefix += "\t";
			s = s.substring(1);
		}
		return prefix + s.trim();
	}
}
