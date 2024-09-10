package com.gmmapowell.script.modules.processors.movie;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.modules.processors.movie.MovieMode.Mode;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class SlugHandler implements ProcessingScanner {
	private final MovieMode mode;

	public SlugHandler(ConfiguredState state) {
		mode = state.require(MovieMode.class);
	}
	
	@Override
	public boolean handleLine(String s) {
		try {
			if (mode.is(Mode.SLUG1)) {
				System.out.println("location");
				mode.location(s);
				return true;
			} else if (mode.is(Mode.SLUG2)) {
				System.out.println("time");
				mode.time(s);
				return true;
	//		}
	//		if (s.equals("INT")) {
	//		} else if (s.equals("EXT")) {
	//			System.out.println("exterior");
	//			mode.exterior();
	//			return true;
			} else
				return false;
		} catch (Exception ex) {
			throw WrappedException.wrap(ex);
		}
	}

}
