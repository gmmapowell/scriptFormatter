package com.gmmapowell.script.modules.processors.movie;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class SlugSpotter implements ProcessingScanner {
	private final MovieMode mode;

	public SlugSpotter(ConfiguredState state) {
		mode = state.require(MovieMode.class);
	}
	
	@Override
	public boolean handleLine(String s) {
		if (s.equals("INT")) {
			System.out.println("interior");
			mode.interior();
			return true;
		} else if (s.equals("EXT")) {
			System.out.println("exterior");
			mode.exterior();
			return true;
		} else
			return false;
	}

}
