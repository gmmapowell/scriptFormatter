package com.gmmapowell.script.modules.processors.movie;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingHandler;

public class FlushProcessor implements ProcessingHandler {
	private final MovieMode mode;

	public FlushProcessor(ConfiguredState state) {
		mode = state.require(MovieMode.class);
	}

	@Override
	public void process(String s) {
		try {
			System.out.println("blank");
			mode.flush();
		} catch (Exception ex) {
			throw WrappedException.wrap(ex);
		}
	}

}
