package com.gmmapowell.script.modules.processors.movie;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class CommentIgnorer implements ProcessingScanner {
	private final MovieMode mode;

	public CommentIgnorer(ConfiguredState state) {
		mode = state.require(MovieMode.class);
	}

	@Override
	public boolean handleLine(String s) {
		if (s.startsWith("#")) {
			System.out.println("COMMENT");
			return true;
		} else if (mode.is(MovieMode.Mode.COMMENT)) {
			System.out.println("commenting");
			return true;
		} else {
			return false;
		}
			
	}

}
