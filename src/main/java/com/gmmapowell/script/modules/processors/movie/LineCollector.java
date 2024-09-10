package com.gmmapowell.script.modules.processors.movie;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingHandler;

public class LineCollector implements ProcessingHandler {
	private final MovieMode mode;

	public LineCollector(ConfiguredState state) {
		mode = state.require(MovieMode.class);
	}

	@Override
	public void process(String s) {
		try {
			if (mode.isSpeech(s)) {
				System.out.println("speaker");
				mode.flush();
			} else
				System.out.println("text");
			mode.appendText(s);
		} catch (Exception ex) {
			throw WrappedException.wrap(ex);
		}
	}
}
