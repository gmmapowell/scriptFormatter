package com.gmmapowell.script.processor.movie;

import java.util.Map;

import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.processor.prose.CurrentState;

public class MovieState extends CurrentState {
	public MovieState(Map<String, Flow> flows) {
		super(flows);
	}

	@Override
	public String formatAs() {
		return "preformatted";
	}
	
	public void reset(String file) {
		processingFile(file);
	}

	public String location() {
		return "undefined";
	}
}
